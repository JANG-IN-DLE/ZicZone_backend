package org.zerock.ziczone.service.myPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
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

        // 파일 업로드
        Map<String, String> resumePhotoData = uploadFile(resumePhoto, "resumePhoto");
        Map<String, String> personalStateData = uploadFile(personalState, "personalState");

        log.info("resumePhotoData {}",resumePhotoData);
        log.info("personalStateData {}",personalStateData);
        List<Map<String, String>> portfolioFiles = uploadPortfolios(portfolios);
        log.info("portfolioFiles {}", portfolioFiles);
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        // Resume 객체 생성
        Resume resume = buildResume(resumeDTO, resumePhotoData, personalStateData, personalUser);
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
        log.info("Updating resume Start(Before): {}, DTO: {}", userId, resumeDTO);

        // 파일 처리
        Map<String, String> resumePhotoData = processFileUpdate(existingResume.getResumePhotoUrl(), existingResume.getResumePhotoUuid(), resumePhoto, "resumePhoto");
        Map<String, String> personalStateData = processFileUpdate(existingResume.getPersonalStateUrl(), existingResume.getPersonalStateUuid(), personalState, "personalState");
        List<Map<String, String>> portfolioFiles = processPortfoliosUpdate(resumeDTO.getResumeId(), portfolios);

        log.info("portfolioFiles.stream().toList() {}", portfolioFiles.stream().toList());
        log.info("resumePhotoData {}", resumePhotoData);
        log.info("personalStateData {}", personalStateData);
        // 엔티티 업데이트
        existingResume = updateResumeEntity(existingResume, resumeDTO, resumePhotoData, personalStateData);


        // 연관 엔티티 삭제
        log.info("연관 엔티티 삭제 보내기 전 existingResume {}", existingResume);
        deleteRelatedEntities(existingResume.getResumeId(), existingResume.getPersonalUser().getPersonalId());
        // 연관 엔티티 저장
        log.info("연관 엔티티 저장 보내기 전 existingResume {}", existingResume);
        saveRelatedEntities(existingResume, resumeDTO, portfolioFiles);

        // 이력서 저장
        resumeRepository.save(existingResume);


        Resume afterResume = resumeRepository.findById(resumeDTO.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeDTO.getResumeId()));

        log.info("이력서 저장 후 afterResume {}", afterResume);

        // 로그 추가 - 완료
        log.info("Resume updated successfully for userId: {}, resumeId: {}", userId, resumeDTO.getResumeId());
    }

    @Transactional
    @Override
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));

        // 포트폴리오 파일 삭제
        List<Portfolio> portfolios = portfolioRepository.findByResume_ResumeId(resumeId);
        if (portfolios != null && !portfolios.isEmpty()) {
            portfolios.forEach(portfolio -> {
                if (portfolio.getPortFileUuid() != null && !portfolio.getPortFileUuid().isEmpty()) {
                    storageService.deleteFile(BUCKET_NAME, "portfolio", portfolio.getPortFileUuid());
                }
            });
            portfolioRepository.deleteByResumeResumeId(resumeId);
        }

        // 이력서 사진 및 자기소개서 파일 삭제
        if (resume.getResumePhotoUuid() != null && !resume.getResumePhotoUuid().isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, "resumePhoto", resume.getResumePhotoUuid());
        }
        if (resume.getPersonalStateUuid() != null && !resume.getPersonalStateUuid().isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, "personalState", resume.getPersonalStateUuid());
        }

        // 연관 엔티티 삭제
        deleteRelatedEntities(resumeId, resume.getPersonalUser().getPersonalId());

        // 이력서 삭제
        resumeRepository.deleteById(resumeId);
    }

    // 개인 ID 유효성 검증
    private void validatePersonalId(Long personalId) {
        if (!personalUserRepository.existsById(personalId)) {
            throw new IllegalArgumentException("Invalid personal ID: " + personalId);
        }
    }

    // 파일 업로드
    private Map<String, String> uploadFile(MultipartFile file, String folderName) {
        if (file != null && !file.isEmpty()) {
            return storageService.uploadFile(file, folderName, BUCKET_NAME);
        }
        return Collections.emptyMap();
    }

    // Resume 객체 생성
    private Resume buildResume(ResumeDTO resumeDTO, Map<String, String> resumePhotoData, Map<String, String> personalStateData, PersonalUser personalUser) {
        return resumeDTO.toEntity().toBuilder()
                .resumePhotoUrl(resumePhotoData.getOrDefault("fileUrl", ""))
                .resumePhotoUuid(resumePhotoData.getOrDefault("fileUUID", ""))
                .resumePhotoFileName(resumePhotoData.getOrDefault("fileOriginalFileName", ""))
                .personalStateUrl(personalStateData.getOrDefault("fileUrl", ""))
                .personalStateUuid(personalStateData.getOrDefault("fileUUID", ""))
                .personalStateFileName(personalStateData.getOrDefault("fileOriginalFileName" , ""))
                .personalUser(personalUser)
                .build();
    }

    // 포트폴리오 파일 업로드
    private List<Map<String, String>> uploadPortfolios(List<MultipartFile> portfolios) {
        return portfolios.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> storageService.uploadFile(file, "portfolio", BUCKET_NAME))
                .collect(Collectors.toList());
    }

    // 연관 엔티티 저장
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

    // ResumeDTO 객체 생성
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
                        .map(fileData -> PortfolioDTO.builder().portFileUrl(fileData.get("fileUrl")).build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 파일 업데이트 처리
    private Map<String, String> processFileUpdate(String existingFileUrl, String existingFileUUID, MultipartFile newFile, String folderName) {
        Map<String, String> fileData = new HashMap<>();

        // 새로운 파일이 없는 경우
        if (newFile == null || newFile.isEmpty()) {
            if (existingFileUUID != null && !existingFileUUID.isEmpty()) {
                storageService.deleteFile(BUCKET_NAME, folderName, existingFileUUID);
            }
            fileData.put("fileUrl", existingFileUrl != null ? existingFileUrl : "");
            fileData.put("fileUUID", existingFileUUID != null ? existingFileUUID : "");
            fileData.put("fileOriginalFileName", newFile != null ? newFile.getOriginalFilename() : "");

            log.info("fileData >>>  {}", fileData);
            return fileData;
        }

        // 새로운 파일 업로드
        fileData = storageService.uploadFile(newFile, folderName, BUCKET_NAME);

        // 기존 파일 삭제
        if (existingFileUUID != null && !existingFileUUID.isEmpty()) {
            storageService.deleteFile(BUCKET_NAME, folderName, existingFileUUID);
        }

        return fileData;
    }

    // 포트폴리오 파일 업데이트 처리
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
        for (MultipartFile file : newPortfolios) {
            log.info("File Name: {}, File Size: {}", file.getOriginalFilename(), file.getSize());
        }

        if (newPortfolios == null) {
            return Collections.emptyList();
        }
        return newPortfolios.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> storageService.uploadFile(file, "portfolio", BUCKET_NAME))
                .collect(Collectors.toList());
    }

    // 이력서 엔티티 업데이트
    private Resume updateResumeEntity(Resume existingResume, ResumeDTO resumeDTO, Map<String, String> resumePhotoData, Map<String, String> personalStateData) {
        log.info("Updating resume entity for resumeId: {}", existingResume.getResumeId());
        log.info("Updating resume : {}", existingResume);

        return existingResume.toBuilder()
                .resumeName(Optional.ofNullable(resumeDTO.getResumeName()).orElse(existingResume.getResumeName()))
                .resumeDate(Optional.ofNullable(resumeDTO.getResumeDate()).orElse(existingResume.getResumeDate()))
                .phoneNum(Optional.ofNullable(resumeDTO.getPhoneNum()).orElse(existingResume.getPhoneNum()))
                .resumeEmail(Optional.ofNullable(resumeDTO.getResumeEmail()).orElse(existingResume.getResumeEmail()))
                .resumeEmail(Optional.ofNullable(resumeDTO.getResumeEmail()).orElse(existingResume.getResumeEmail()))

//                .resumePhotoUrl(resumePhotoData.get("fileUrl").isEmpty() ? null : resumePhotoData.get("fileUrl"))
//                .resumePhotoUuid(resumePhotoData.get("fileUUID").isEmpty() ? null : resumePhotoData.get("fileUUID"))
//                .resumePhotoFileName(resumePhotoData.get("fileOriginalFileName").isEmpty() ? null : resumePhotoData.get("fileOriginalFileName"))
//                .personalStateUrl(personalStateData.get("fileUrl").isEmpty() ? null : personalStateData.get("fileUrl"))
//                .personalStateUuid(personalStateData.get("fileUUID").isEmpty() ? null : personalStateData.get("fileUUID"))
//                .personalStateFileName(personalStateData.get("fileOriginalFileName").isEmpty() ? null : personalStateData.get("fileOriginalFileName"))
//

                .resumePhotoUrl(resumePhotoData.get("fileUrl").isEmpty() ? "" : resumePhotoData.get("fileUrl"))
                .resumePhotoUuid(resumePhotoData.get("fileUUID").isEmpty() ? "" : resumePhotoData.get("fileUUID"))
                .resumePhotoFileName(resumePhotoData.get("fileOriginalFileName").isEmpty() ? "" : resumePhotoData.get("fileOriginalFileName"))
                .personalStateUrl(personalStateData.get("fileUrl").isEmpty() ? "" : personalStateData.get("fileUrl"))
                .personalStateUuid(personalStateData.get("fileUUID").isEmpty() ? "" : personalStateData.get("fileUUID"))
                .personalStateFileName(personalStateData.get("fileOriginalFileName").isEmpty() ? "" : personalStateData.get("fileOriginalFileName"))


                .resumeUpdate(LocalDateTime.now())
                .build();
    }

    // 연관 엔티티 저장 메서드들
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
                        .portFileUrl(fileData.get("fileUrl"))
                        .portFileUuid(fileData.get("fileUUID"))
                        .portFileName(fileData.get("fileOriginalFileName"))
                        .resume(resume)
                        .build();
                portfolioRepository.save(portfolio);
            });
        }
    }

    private void saveJobPositions(PersonalUser personalUser, List<JobPositionDTO> jobPositions) {
        if (jobPositions != null) {
            log.info("jobPositions : {} ",jobPositions);
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
                .resumePhotoUuid(resume.getResumePhotoUuid())
                .resumePhotoFileName(resume.getResumePhotoFileName())
                .resumeEmail(resume.getResumeEmail())
                .resumeCreate(resume.getResumeCreate())
                .resumeUpdate(resume.getResumeUpdate())
                .personalStateUrl(resume.getPersonalStateUrl())
                .personalStateUuid(resume.getPersonalStateUuid())
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

    // 연관 엔티티 삭제
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

}
