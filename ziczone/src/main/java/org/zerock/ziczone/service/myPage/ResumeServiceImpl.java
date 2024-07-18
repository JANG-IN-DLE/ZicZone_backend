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
import java.time.format.DateTimeFormatter;
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

    final String BUCKETNAME = "ziczone-bucket-jangindle-optimizer";

    @Transactional
    @Override
    public ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        Long personalId = resumeDTO.getPersonalId();
        Resume existingResume = resumeRepository.findByPersonalUser_PersonalId(personalId);
        String oldFileUUID = null;

        if (!personalUserRepository.existsById(personalId)) {
            throw new IllegalArgumentException("Invalid personal ID: " + personalId);
        }

        if (existingResume != null) {
            oldFileUUID = existingResume.getResumePhotoUUID();
            throw new ResumeAlreadyExistsException("Resume already exists for personal ID: " + personalId);
        }

        String photoUUID = UUID.randomUUID().toString();
        String stateUUID = UUID.randomUUID().toString();

        String resumePhotoUrl = uploadFileAndDeleteOld(resumePhoto, "resumePhoto/", photoUUID, BUCKETNAME, oldFileUUID);
        String personalStateUrl = uploadFileAndDeleteOld(personalState, "personalState/", stateUUID, BUCKETNAME, oldFileUUID);
        List<Map<String, String>> portfolioFiles = uploadPortfolios(portfolios);

        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        Resume resume = resumeDTO.toEntity().toBuilder()
                .resumePhotoUrl(resumePhotoUrl)
                .resumePhotoUUID(photoUUID)
                .resumePhotoFileName(resumePhoto.getOriginalFilename())
                .personalStateUrl(personalStateUrl)
                .personalStateUUID(stateUUID)
                .personalStateFileName(personalState.getOriginalFilename())
                .personalUser(personalUser)
                .build();

        resume = resumeRepository.save(resume);

        saveRelatedEntities(resume, resumeDTO, portfolioFiles);

        return ResumeDTO.fromEntity(resume).toBuilder()
                .archive(resumeDTO.getArchive() != null ? resumeDTO.getArchive() : new ArchiveDTO())
                .certificates(resumeDTO.getCertificates() != null ? resumeDTO.getCertificates() : Collections.emptyList())
                .educations(resumeDTO.getEducations() != null ? resumeDTO.getEducations() : Collections.emptyList())
                .careers(resumeDTO.getCareers() != null ? resumeDTO.getCareers() : Collections.emptyList())
                .curriculums(resumeDTO.getCurriculums() != null ? resumeDTO.getCurriculums() : Collections.emptyList())
                .etcs(resumeDTO.getEtcs() != null ? resumeDTO.getEtcs() : Collections.emptyList())
                .jobPositions(resumeDTO.getJobPositions() != null ? resumeDTO.getJobPositions() : Collections.emptyList())
                .techStacks(resumeDTO.getTechStacks() != null ? resumeDTO.getTechStacks() : Collections.emptyList())
                .portfolios(portfolioFiles.stream()
                        .map(fileData -> PortfolioDTO.builder().portFileUrl(fileData.get("url")).build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    @Override
    public void updateResume(Long userId, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        if (resumeDTO.getResumeId() == null) {
            throw new IllegalArgumentException("Resume ID cannot be null");
        }

        Resume existingResume = resumeRepository.findById(resumeDTO.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeDTO.getResumeId()));

        String photoUUID = UUID.randomUUID().toString();
        String resumePhotoUrl = existingResume.getResumePhotoUrl();
        if (!resumePhoto.isEmpty()) {
            storageService.deleteFile(BUCKETNAME, existingResume.getResumePhotoUUID());
            resumePhotoUrl = uploadFileAndDeleteOld(resumePhoto, "resumePhoto/", photoUUID, BUCKETNAME, existingResume.getResumePhotoUUID());
        } else if (resumePhoto.isEmpty() && resumeDTO.getResumePhotoUrl() == null) {
            storageService.deleteFile(BUCKETNAME, existingResume.getResumePhotoUUID());
            resumePhotoUrl = null;
        }

        String personalStateUrl = existingResume.getPersonalStateUrl();
        String stateUUID = UUID.randomUUID().toString();
        if (!personalState.isEmpty()) {
            storageService.deleteFile(BUCKETNAME, existingResume.getPersonalStateUUID());
            personalStateUrl = uploadFileAndDeleteOld(personalState, "personalState/", stateUUID, BUCKETNAME, existingResume.getPersonalStateUUID());
        } else if (personalState.isEmpty() && resumeDTO.getPersonalStateUrl() == null) {
            storageService.deleteFile(BUCKETNAME, existingResume.getPersonalStateUUID());
            personalStateUrl = null;
        }

        List<Map<String, String>> portfolioFiles = !portfolios.stream().allMatch(MultipartFile::isEmpty) ? uploadPortfolios(portfolios) : Collections.emptyList();

        existingResume = existingResume.toBuilder()
                .resumeName(resumeDTO.getResumeName() != null ? resumeDTO.getResumeName() : existingResume.getResumeName())
                .resumeDate(resumeDTO.getResumeDate() != null ? resumeDTO.getResumeDate() : existingResume.getResumeDate())
                .phoneNum(resumeDTO.getPhoneNum() != null ? resumeDTO.getPhoneNum() : existingResume.getPhoneNum())
                .resumeEmail(resumeDTO.getResumeEmail() != null ? resumeDTO.getResumeEmail() : existingResume.getResumeEmail())
                .resumePhotoUrl(resumePhotoUrl != null ? resumePhotoUrl : "")
                .resumePhotoUUID(existingResume.getResumePhotoUUID() != null ? photoUUID : "")
                .resumePhotoFileName(existingResume.getResumePhotoFileName() != null ? resumePhoto.getOriginalFilename() : "")
                .personalStateUrl(personalStateUrl != null ? personalStateUrl : "")
                .personalStateUUID(existingResume.getPersonalStateUUID() != null ? stateUUID : "")
                .personalStateFileName(existingResume.getPersonalStateFileName() != null ? personalState.getOriginalFilename() : "")
                .resumeUpdate(LocalDateTime.now())
                .build();

        saveRelatedEntities(existingResume, resumeDTO, portfolioFiles);

        resumeRepository.save(existingResume);
    }

    private List<Map<String, String>> uploadPortfolios(List<MultipartFile> portfolios) {
        return portfolios.stream()
                .map(file -> {
                    try {
                        String portfolioUUID = generateFileName();
                        String url = storageService.uploadFile(file, "portfolio/", portfolioUUID, BUCKETNAME);
                        Map<String, String> fileData = new HashMap<>();
                        fileData.put("url", url);
                        fileData.put("originalFilename", file.getOriginalFilename());
                        return fileData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
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

    private void saveCertificates(Resume existingResume, List<CertificateDTO> certificates) {
        if (certificates != null) {
            certificateRepository.deleteByResumeResumeId(existingResume.getResumeId());
            certificates.forEach(certDTO -> {
                Certificate certificate = Certificate.builder()
                        .cert(certDTO.getCert() != null ? certDTO.getCert() : "")
                        .certDate(certDTO.getCert_date() != null ? certDTO.getCert_date() : "")
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
                        .edu(eduDTO.getEdu() != null ? eduDTO.getEdu() : "")
                        .credit(eduDTO.getCredit() != null ? eduDTO.getCredit() : "")
                        .eduDate(eduDTO.getEdu_date() != null ? eduDTO.getEdu_date() : "")
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
                        .careerName(careerDTO.getCareer_name() != null ? careerDTO.getCareer_name() : "")
                        .careerJob(careerDTO.getCareer_job() != null ? careerDTO.getCareer_job() : "")
                        .careerPosition(careerDTO.getCareer_position() != null ? careerDTO.getCareer_position() : "")
                        .careerDate(careerDTO.getCareer_date() != null ? careerDTO.getCareer_date() : "")
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
                        .curriCompany(curriDTO.getCurri_company() != null ? curriDTO.getCurri_company() : "")
                        .curriContent(curriDTO.getCurri_content() != null ? curriDTO.getCurri_content() : "")
                        .curriDate(curriDTO.getCurri_date() != null ? curriDTO.getCurri_date() : "")
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
                        .etcContent(etcDTO.getEtc_content() != null ? etcDTO.getEtc_content() : "")
                        .etcDate(etcDTO.getEtc_date() != null ? etcDTO.getEtc_date() : "")
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
                        .portFileUUID(generateFileName())
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
                    .archGit(archiveDTO.getArchGit() != null ? archiveDTO.getArchGit() : "")
                    .archNotion(archiveDTO.getArchNotion() != null ? archiveDTO.getArchNotion() : "")
                    .archBlog(archiveDTO.getArchBlog() != null ? archiveDTO.getArchBlog() : "")
                    .resume(resume)
                    .build();
            archiveRepository.save(archive);
        }
    }

    @Transactional
    @Override
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));

        List<Portfolio> portfolios = portfolioRepository.findByResume_ResumeId(resumeId);
        if (portfolios != null && !portfolios.isEmpty()) {
            portfolios.forEach(portfolio -> {
                storageService.deleteFile(BUCKETNAME, portfolio.getPortFileUUID());
            });
        }

        storageService.deleteFile(BUCKETNAME, resume.getResumePhotoUUID());
        storageService.deleteFile(BUCKETNAME, resume.getPersonalStateUUID());
        deleteRelatedEntities(resumeId, resume.getPersonalUser().getPersonalId());
        resumeRepository.deleteById(resumeId);
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
                .resumePhotoUUID(resume.getResumePhotoUUID())
                .resumePhotoFileName(resume.getResumePhotoFileName())
                .resumeEmail(resume.getResumeEmail())
                .resumeCreate(resume.getResumeCreate())
                .resumeUpdate(resume.getResumeUpdate())
                .personalStateUrl(resume.getPersonalStateUrl())
                .personalStateUUID(resume.getPersonalStateUUID())
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

    private String generateFileName() {
        return UUID.randomUUID().toString();
    }

    private String uploadFileAndDeleteOld(MultipartFile newFile, String folderName, String fileName, String bucketName, String oldFileUUID) {
        String newFileUrl = storageService.uploadFile(newFile, folderName, fileName, bucketName);
        if (newFileUrl != null && oldFileUUID != null && !oldFileUUID.isEmpty()) {
            storageService.deleteFile(BUCKETNAME, oldFileUUID);
        }
        return newFileUrl;
    }

}
