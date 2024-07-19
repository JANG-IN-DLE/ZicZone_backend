package org.zerock.ziczone.service.myPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.application.*;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.exception.mypage.ResumeAlreadyExistsException;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;
import org.zerock.ziczone.service.storage.StorageService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final CertificateRepository certificateRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final CurriculumRepository curriculumRepository;
    private final EtcRepository etcRepository;
    private final PortfolioRepository portfolioRepository;
    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final StorageService storageService;
    private final ArchiveRepository archiveRepository;
    private final PersonalUserRepository personalUserRepository;

    final static String BUCKET_NAME = "ziczone-bucket-jangindle-optimizer";

    @Transactional
    @Override
    public ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        Long personalId = resumeDTO.getPersonalId();
        validatePersonalId(personalId);

        Resume existingResume = resumeRepository.findByPersonalUser_PersonalId(personalId);
        if (existingResume != null) {
            throw new ResumeAlreadyExistsException("Resume already exists for personal ID: " + personalId);
        }

        String resumePhotoUrl = uploadFile(resumePhoto, "resumePhoto");
        String personalStateUrl = uploadFile(personalState, "personalState");

        List<Map<String, String>> portfolioFiles = uploadPortfolios(portfolios);
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        Resume resume = buildResume(resumeDTO, resumePhoto, personalState, resumePhotoUrl, personalStateUrl, personalUser);
        resumeRepository.save(resume);
        saveRelatedEntities(resume, resumeDTO, portfolioFiles);

        return buildResumeDTO(resume, resumeDTO, portfolioFiles);
    }


    @Transactional
    @Override
    public void updateResume(Long userId, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        if (resumeDTO.getResumeId() == null) {
            throw new IllegalArgumentException("Resume ID cannot be null");
        }

        Resume existingResume = resumeRepository.findById(resumeDTO.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeDTO.getResumeId()));

        // 로그 추가 - 시작
        log.info("Updating resume for userId: {}, resumeId: {}", userId, resumeDTO.getResumeId());

        // 처리할 파일들
        String resumePhotoUrl = processFileUpdate(existingResume.getResumePhotoUrl(), existingResume.getResumePhotoUuid(), resumePhoto, "resumePhoto");
        String personalStateUrl = processFileUpdate(existingResume.getPersonalStateUrl(), existingResume.getPersonalStateUuid(), personalState, "personalState");
        List<Map<String, String>> portfolioFiles = processPortfoliosUpdate(resumeDTO.getResumeId(), portfolios);

        // 엔티티 업데이트
        existingResume = updateResumeEntity(existingResume, resumeDTO, resumePhoto, personalState, resumePhotoUrl, personalStateUrl);

        // 연관 엔티티 저장
        saveRelatedEntities(existingResume, resumeDTO, portfolioFiles);

        // 이력서 저장
        resumeRepository.save(existingResume);

        // 로그 추가 - 완료
        log.info("Resume updated successfully for userId: {}, resumeId: {}", userId, resumeDTO.getResumeId());
    }



    @Transactional
    @Override
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));

        List<Portfolio> portfolios = portfolioRepository.findByResume_ResumeId(resumeId);
        if (portfolios != null && !portfolios.isEmpty()) {
            portfolios.forEach(portfolio -> {
                if (portfolio.getPortFileUuid() != null && !portfolio.getPortFileUuid().isEmpty()) {
                    storageService.deleteFile(BUCKET_NAME, "portfolio", portfolio.getPortFileUuid());
                }
            });
            portfolioRepository.deleteByResumeResumeId(resumeId);
        }

        if (resume.getResumePhotoUuid() != null && !resume.getResumePhotoUuid().isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, "resumePhoto", resume.getResumePhotoUuid());
        }
        if (resume.getPersonalStateUuid() != null && !resume.getPersonalStateUuid().isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, "personalState", resume.getPersonalStateUuid());
        }

        deleteRelatedEntities(resumeId, resume.getPersonalUser().getPersonalId());
        resumeRepository.deleteById(resumeId);
    }

    private void validatePersonalId(Long personalId) {
        if (!personalUserRepository.existsById(personalId)) {
            throw new IllegalArgumentException("Invalid personal ID: " + personalId);
        }
    }

    private String uploadFile(MultipartFile file, String folderName) {
        if (file != null && !file.isEmpty()) {
            String fileUUID = UUID.randomUUID().toString();
            return storageService.uploadFile(file, folderName, fileUUID, BUCKET_NAME);
        }
        return "";
    }

    private Resume buildResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, String resumePhotoUrl, String personalStateUrl, PersonalUser personalUser) {
        return resumeDTO.toEntity().toBuilder()
                .resumePhotoUrl(Optional.ofNullable(resumePhotoUrl).orElse(""))
                .resumePhotoUuid(Optional.ofNullable(resumePhoto).map(f -> UUID.randomUUID().toString()).orElse(""))
                .resumePhotoFileName(Optional.ofNullable(resumePhoto).map(MultipartFile::getOriginalFilename).orElse(""))
                .personalStateUrl(Optional.ofNullable(personalStateUrl).orElse(""))
                .personalStateUuid(Optional.ofNullable(personalState).map(f -> UUID.randomUUID().toString()).orElse(""))
                .personalStateFileName(Optional.ofNullable(personalState).map(MultipartFile::getOriginalFilename).orElse(""))
                .personalUser(personalUser)
                .build();
    }

    private List<Map<String, String>> uploadPortfolios(List<MultipartFile> portfolios) {
        return portfolios.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    String portfolioUUID = UUID.randomUUID().toString();
                    String url = storageService.uploadFile(file, "portfolio", portfolioUUID, BUCKET_NAME);
                    Map<String, String> fileData = new HashMap<>();
                    fileData.put("url", url);
                    fileData.put("originalFilename", file.getOriginalFilename());
                    fileData.put("uuid", portfolioUUID);
                    return fileData;
                })
                .collect(Collectors.toList());
    }

    private void saveRelatedEntities(Resume existingResume, ResumeDTO resumeDTO, List<Map<String, String>> portfolioFiles) {
        saveCertificates(existingResume, resumeDTO.getCertificates());
        saveEducations(existingResume, resumeDTO.getEducations());
        saveCareers(existingResume, resumeDTO.getCareers());
        saveCurriculums(existingResume, resumeDTO.getCurriculums());
        saveEtcs(existingResume, resumeDTO.getEtcs());
        savePortfolios(existingResume, portfolioFiles);
        saveJobPositions(existingResume.getPersonalUser(), resumeDTO.getJobPositions());
        saveTechStacks(existingResume.getPersonalUser(), resumeDTO.getTechStacks());
        saveArchive(existingResume, resumeDTO.getArchive());
    }

    private ResumeDTO buildResumeDTO(Resume resume, ResumeDTO resumeDTO, List<Map<String, String>> portfolioFiles) {
        return ResumeDTO.fromEntity(resume).toBuilder()
                .archive(resumeDTO.getArchive() != null ? resumeDTO.getArchive() : new ArchiveDTO())
                .certificates(Optional.ofNullable(resumeDTO.getCertificates()).orElse(Collections.emptyList()))
                .educations(Optional.ofNullable(resumeDTO.getEducations()).orElse(Collections.emptyList()))
                .careers(Optional.ofNullable(resumeDTO.getCareers()).orElse(Collections.emptyList()))
                .curriculums(Optional.ofNullable(resumeDTO.getCurriculums()).orElse(Collections.emptyList()))
                .etcs(Optional.ofNullable(resumeDTO.getEtcs()).orElse(Collections.emptyList()))
                .jobPositions(Optional.ofNullable(resumeDTO.getJobPositions()).orElse(Collections.emptyList()))
                .techStacks(Optional.ofNullable(resumeDTO.getTechStacks()).orElse(Collections.emptyList()))
                .portfolios(portfolioFiles.stream()
                        .map(fileData -> PortfolioDTO.builder().portFileUrl(fileData.get("url")).build())
                        .collect(Collectors.toList()))
                .build();
    }


    private String processFileUpdate(String existingFileUrl, String existingFileUUID, MultipartFile newFile, String folderName) {
        // 새로운 파일이 없으면 기존 파일 삭제
        if (newFile == null || newFile.isEmpty()) {
            if (existingFileUUID != null && !existingFileUUID.isEmpty()) {
                storageService.deleteFile(BUCKET_NAME, folderName, existingFileUUID);
            }
            return ""; // URL을 공백으로 설정
        }

        // 새로운 파일 업로드
        String newFileUUID = UUID.randomUUID().toString();
        String newFileUrl = storageService.uploadFile(newFile, folderName, newFileUUID, BUCKET_NAME);

        // 기존 파일 삭제
        if (existingFileUUID != null && !existingFileUUID.isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, folderName, existingFileUUID);
        }

        return newFileUrl;
    }


    private List<Map<String, String>> processPortfoliosUpdate(Long resumeId, List<MultipartFile> newPortfolios) {
        List<Portfolio> existingPortfolios = portfolioRepository.findByResume_ResumeId(resumeId);

        if (existingPortfolios != null && !existingPortfolios.isEmpty()) {
            existingPortfolios.forEach(portfolio -> {
                if (portfolio.getPortFileUuid() != null && !portfolio.getPortFileUuid().isEmpty()) {
                    storageService.deleteFile(BUCKET_NAME, "portfolio", portfolio.getPortFileUuid());
                }
            });
            portfolioRepository.deleteByResumeResumeId(resumeId);
        }

        return newPortfolios.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    String portfolioUUID = UUID.randomUUID().toString();
                    String url = storageService.uploadFile(file, "portfolio", portfolioUUID, BUCKET_NAME);
                    Map<String, String> fileData = new HashMap<>();
                    fileData.put("url", url);
                    fileData.put("originalFilename", file.getOriginalFilename());
                    fileData.put("uuid", portfolioUUID);
                    return fileData;
                })
                .collect(Collectors.toList());
    }

    private Resume updateResumeEntity(Resume existingResume, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, String resumePhotoUrl, String personalStateUrl) {
        log.info("Updating resume entity for resumeId: {}", existingResume.getResumeId());
        return existingResume.toBuilder()
                .resumeName(Optional.ofNullable(resumeDTO.getResumeName()).orElse(existingResume.getResumeName()))
                .resumeDate(Optional.ofNullable(resumeDTO.getResumeDate()).orElse(existingResume.getResumeDate()))
                .phoneNum(Optional.ofNullable(resumeDTO.getPhoneNum()).orElse(existingResume.getPhoneNum()))
                .resumeEmail(Optional.ofNullable(resumeDTO.getResumeEmail()).orElse(existingResume.getResumeEmail()))
                .resumePhotoUrl(resumePhotoUrl != null ? resumePhotoUrl : "")
                .resumePhotoUuid((resumePhotoUrl != null && !resumePhotoUrl.isEmpty()) ? UUID.randomUUID().toString() : "")
                .resumePhotoFileName(resumePhoto != null ? resumePhoto.getOriginalFilename() : "")
                .personalStateUrl(personalStateUrl != null ? personalStateUrl : "")
                .personalStateUuid((personalStateUrl != null && !personalStateUrl.isEmpty()) ? UUID.randomUUID().toString() : "")
                .personalStateFileName(personalState != null ? personalState.getOriginalFilename() : "")
                .resumeUpdate(LocalDateTime.now())
                .build();
    }


    private void saveCertificates(Resume existingResume, List<CertificateDTO> certificates) {
        if (certificates != null) {
            certificateRepository.deleteByResumeResumeId(existingResume.getResumeId());
            certificates.forEach(certDTO -> {
                Certificate certificate = Certificate.builder()
                        .cert(Optional.ofNullable(certDTO.getCert()).orElse(""))
                        .certDate(Optional.ofNullable(certDTO.getCert_date()).orElse(""))
                        .resume(existingResume)
                        .build();
                certificateRepository.save(certificate);
            });
        }
    }

    private void saveEducations(Resume resume, List<EducationDTO> educations) {
        if (educations != null) {
            educationRepository.deleteByResumeResumeId(resume.getResumeId());
            educations.forEach(eduDTO -> {
                Education education = Education.builder()
                        .edu(Optional.ofNullable(eduDTO.getEdu()).orElse(""))
                        .credit(Optional.ofNullable(eduDTO.getCredit()).orElse(""))
                        .eduDate(Optional.ofNullable(eduDTO.getEdu_date()).orElse(""))
                        .resume(resume)
                        .build();
                educationRepository.save(education);
            });
        }
    }

    private void saveCareers(Resume resume, List<CareerDTO> careers) {
        if (careers != null) {
            careerRepository.deleteByResumeResumeId(resume.getResumeId());
            careers.forEach(careerDTO -> {
                Career career = Career.builder()
                        .careerName(Optional.ofNullable(careerDTO.getCareer_name()).orElse(""))
                        .careerJob(Optional.ofNullable(careerDTO.getCareer_job()).orElse(""))
                        .careerPosition(Optional.ofNullable(careerDTO.getCareer_position()).orElse(""))
                        .careerDate(Optional.ofNullable(careerDTO.getCareer_date()).orElse(""))
                        .resume(resume)
                        .build();
                careerRepository.save(career);
            });
        }
    }

    private void saveCurriculums(Resume resume, List<CurriculumDTO> curriculums) {
        if (curriculums != null) {
            curriculumRepository.deleteByResumeResumeId(resume.getResumeId());
            curriculums.forEach(curriDTO -> {
                Curriculum curriculum = Curriculum.builder()
                        .curriCompany(Optional.ofNullable(curriDTO.getCurri_company()).orElse(""))
                        .curriContent(Optional.ofNullable(curriDTO.getCurri_content()).orElse(""))
                        .curriDate(Optional.ofNullable(curriDTO.getCurri_date()).orElse(""))
                        .resume(resume)
                        .build();
                curriculumRepository.save(curriculum);
            });
        }
    }

    private void saveEtcs(Resume resume, List<EtcDTO> etcs) {
        if (etcs != null) {
            etcRepository.deleteByResumeResumeId(resume.getResumeId());
            etcs.forEach(etcDTO -> {
                Etc etc = Etc.builder()
                        .etcContent(Optional.ofNullable(etcDTO.getEtc_content()).orElse(""))
                        .etcDate(Optional.ofNullable(etcDTO.getEtc_date()).orElse(""))
                        .resume(resume)
                        .build();
                etcRepository.save(etc);
            });
        }
    }

    private void savePortfolios(Resume resume, List<Map<String, String>> portfolioFiles) {
        if (portfolioFiles != null && !portfolioFiles.isEmpty()) {
            portfolioRepository.deleteByResumeResumeId(resume.getResumeId());
            portfolioFiles.forEach(fileData -> {
                Portfolio portfolio = Portfolio.builder()
                        .portFileUrl(fileData.get("url"))
                        .portFileUuid(fileData.get("uuid"))
                        .portFileFileName(fileData.get("originalFilename"))
                        .resume(resume)
                        .build();
                portfolioRepository.save(portfolio);
            });
        }
    }

    private void saveJobPositions(PersonalUser personalUser, List<JobPositionDTO> jobPositions) {
        if (jobPositions != null) {
            jobPositionRepository.deleteByPersonalUserPersonalId(personalUser.getPersonalId());
            jobPositions.forEach(jobPosDTO -> {
                JobPosition jobPosition = JobPosition.builder()
                        .job(jobPosDTO.getJob().toEntity())
                        .personalUser(personalUser)
                        .build();
                jobPositionRepository.save(jobPosition);
            });
        }
    }

    private void saveTechStacks(PersonalUser personalUser, List<TechStackDTO> techStacks) {
        if (techStacks != null) {
            techStackRepository.deleteByPersonalUserPersonalId(personalUser.getPersonalId());
            techStacks.forEach(techStackDTO -> {
                TechStack techStack = TechStack.builder()
                        .tech(techStackDTO.getTech().toEntity())
                        .personalUser(personalUser)
                        .build();
                techStackRepository.save(techStack);
            });
        }
    }

    private void saveArchive(Resume resume, ArchiveDTO archiveDTO) {
        if (archiveDTO != null) {
            archiveRepository.deleteByResumeResumeId(resume.getResumeId());
            Archive archive = Archive.builder()
                    .archGit(Optional.ofNullable(archiveDTO.getArchGit()).orElse(""))
                    .archNotion(Optional.ofNullable(archiveDTO.getArchNotion()).orElse(""))
                    .archBlog(Optional.ofNullable(archiveDTO.getArchBlog()).orElse(""))
                    .resume(resume)
                    .build();
            archiveRepository.save(archive);
        }
    }



    @Override
    public ResumeDTO getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));
        return convertToDto(resume);
    }

    @Override
    public ResumeDTO getResumeByUserId(Long userId) {
        Optional<Resume> optionalResume = Optional.ofNullable(resumeRepository.findByPersonalUser_PersonalId(userId));
        return optionalResume.map(this::convertToDto).orElse(null);
    }

    @Override
    public List<ResumeDTO> getAllResumes() {
        return resumeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ResumeDTO convertToDto(Resume resume) {
        List<CertificateDTO> certificates = certificateRepository.findByResume(resume).stream()
                .map(CertificateDTO::fromEntity)
                .collect(Collectors.toList());

        List<EducationDTO> educations = educationRepository.findByResume(resume).stream()
                .map(EducationDTO::fromEntity)
                .collect(Collectors.toList());

        List<CareerDTO> careers = careerRepository.findByResume(resume).stream()
                .map(CareerDTO::fromEntity)
                .collect(Collectors.toList());

        List<CurriculumDTO> curriculums = curriculumRepository.findByResume(resume).stream()
                .map(CurriculumDTO::fromEntity)
                .collect(Collectors.toList());

        List<EtcDTO> etcs = etcRepository.findByResume(resume).stream()
                .map(EtcDTO::fromEntity)
                .collect(Collectors.toList());

        List<PortfolioDTO> portfolios = portfolioRepository.findByResume(resume).stream()
                .map(PortfolioDTO::fromEntity)
                .collect(Collectors.toList());

        List<JobPositionDTO> jobPositions = jobPositionRepository.findByPersonalUser(resume.getPersonalUser()).stream()
                .map(JobPositionDTO::fromEntity)
                .collect(Collectors.toList());

        List<TechStackDTO> techStacks = techStackRepository.findByPersonalUser(resume.getPersonalUser()).stream()
                .map(TechStackDTO::fromEntity)
                .collect(Collectors.toList());
        ArchiveDTO archive = archiveRepository.findByResume(resume)
                .map(ArchiveDTO::fromEntity)
                .orElse(null);

        return ResumeDTO.builder()
                .resumeId(resume.getResumeId())
                .resumeName(resume.getResumeName())
                .resumeDate(resume.getResumeDate())
                .phoneNum(resume.getPhoneNum())
                .resumePhotoUrl(resume.getResumePhotoUrl())
                .resumePhotoUUID(resume.getResumePhotoUuid())
                .resumePhotoFileName(resume.getResumePhotoFileName())
                .resumeEmail(resume.getResumeEmail())
                .resumeCreate(resume.getResumeCreate())
                .resumeUpdate(resume.getResumeUpdate())
                .personalStateUrl(resume.getPersonalStateUrl())
                .personalStateUUID(resume.getPersonalStateUuid())
                .personalStateFileName(resume.getPersonalStateFileName())
                .personalId(resume.getPersonalUser().getPersonalId())
                .certificates(certificates)
                .educations(educations)
                .careers(careers)
                .curriculums(curriculums)
                .etcs(etcs)
                .portfolios(portfolios)
                .jobPositions(jobPositions)
                .techStacks(techStacks)
                .archive(archive)
                .build();
    }

    private void deleteRelatedEntities(Long resumeId, Long personalUserId) {
        jobPositionRepository.deleteByPersonalUserPersonalId(personalUserId);
        techStackRepository.deleteByPersonalUserPersonalId(personalUserId);
        certificateRepository.deleteByResumeResumeId(resumeId);
        educationRepository.deleteByResumeResumeId(resumeId);
        careerRepository.deleteByResumeResumeId(resumeId);
        curriculumRepository.deleteByResumeResumeId(resumeId);
        etcRepository.deleteByResumeResumeId(resumeId);
        portfolioRepository.deleteByResumeResumeId(resumeId);
        archiveRepository.deleteByResumeResumeId(resumeId);
    }

    private String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
