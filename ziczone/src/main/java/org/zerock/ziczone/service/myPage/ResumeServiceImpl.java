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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Transactional
    @Override
    public ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        Long personalId = resumeDTO.getPersonalId();
        Resume existingResume = resumeRepository.findByPersonalUser_PersonalId(personalId);

        // personalId가 존재하는지 확인
        if (!personalUserRepository.existsById(personalId)) {
            throw new IllegalArgumentException("Invalid personal ID: " + personalId);
        }
        // 지원서가 존재하는지 확인
        if (existingResume != null) {
            // 지원서가 이미 존재하는 경우 커스텀 예외 발생
            throw new ResumeAlreadyExistsException("Resume already exists for personal ID: " + personalId);
        }

        String bucketName = "ziczone-bucket-jangindle-optimizer";
        String resumePhotoUrl = uploadFileAndDeleteOld(resumePhoto, "resumePhoto/", generateFileName("resumePhoto/", resumePhoto), bucketName, null);
        String personalStateUrl = uploadFileAndDeleteOld(personalState, "personalState/", generateFileName("personalState/", personalState), bucketName, null);
        List<String> portfolioUrls = uploadPortfolios(portfolios, bucketName);

        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        Resume resume = resumeDTO.toEntity().toBuilder()
                .resumePhoto(resumePhotoUrl)
                .personalState(personalStateUrl)
                .personalUser(personalUser)
                .build();

        resume = resumeRepository.save(resume);
        saveRelatedEntities(resume, resumeDTO, portfolioUrls);



        return ResumeDTO.fromEntity(resume).toBuilder()
                .archive(resumeDTO.getArchive() != null ? resumeDTO.getArchive() : new ArchiveDTO())
                .certificates(resumeDTO.getCertificates() != null ? resumeDTO.getCertificates() : Collections.emptyList())
                .educations(resumeDTO.getEducations() != null ? resumeDTO.getEducations() : Collections.emptyList())
                .careers(resumeDTO.getCareers() != null ? resumeDTO.getCareers() : Collections.emptyList())
                .curriculums(resumeDTO.getCurriculums() != null ? resumeDTO.getCurriculums() : Collections.emptyList())
                .etcs(resumeDTO.getEtcs() != null ? resumeDTO.getEtcs() : Collections.emptyList())
                .jobPositions(resumeDTO.getJobPositions() != null ? resumeDTO.getJobPositions() : Collections.emptyList())
                .techStacks(resumeDTO.getTechStacks() != null ? resumeDTO.getTechStacks() : Collections.emptyList())
                .portfolios(portfolioUrls.stream()
                        .map(url -> PortfolioDTO.builder().portFile(url).build())
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
        log.info("기존 이력서: {}", existingResume);
        log.info("1.. resumePhoto.getOriginalFilename ::> {}", resumePhoto.getOriginalFilename());
        log.info("1.. personalState.getOriginalFilename ::> {}", personalState.getOriginalFilename());
        log.info("portfolios.stream.toList ::> {}",portfolios.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList()));
        log.info("1.. size ::> {}", portfolios.size());
        log.info("portfolios.stream().allMatch(MultipartFile::isEmpty) ::> {} ", portfolios.stream().allMatch(MultipartFile::isEmpty));
        log.info("1-2.. existingResume ::> {}", existingResume);



        log.info("2.. resumePhoto.isEmpty() ::> {}", resumePhoto.isEmpty());
        log.info("2.. personalState.isEmpty() ::> {}", personalState.isEmpty());
        log.info("2.. portfolios.isEmpty() ::> {}", portfolios.isEmpty());
        log.info("2.. portfolios.stream().allMatch(MultipartFile::isEmpty) ::> {}", portfolios.stream().allMatch(MultipartFile::isEmpty));

        log.info("-----------------------------------------------------------------------------------------------");


        String bucketName = "ziczone-bucket-jangindle-optimizer";

        String resumePhotoUrl = existingResume.getResumePhoto();
        if (!resumePhoto.isEmpty()) {
            storageService.deleteFile(existingResume.getResumePhoto());
            resumePhotoUrl = uploadFileAndDeleteOld(resumePhoto, "resumePhoto/", generateFileName("resumePhoto/", resumePhoto), bucketName, existingResume.getResumePhoto());
        } else if (resumePhoto.isEmpty() && resumeDTO.getResumePhoto() == null) {
            storageService.deleteFile(existingResume.getResumePhoto());
            resumePhotoUrl = null;
        }

        String personalStateUrl = existingResume.getPersonalState();
        if (!personalState.isEmpty()) {
            storageService.deleteFile(existingResume.getPersonalState());
            personalStateUrl = uploadFileAndDeleteOld(personalState, "personalState/", generateFileName("personalState/", personalState), bucketName, existingResume.getPersonalState());
        } else if (personalState.isEmpty() && resumeDTO.getPersonalState() == null) {
            storageService.deleteFile(existingResume.getPersonalState());
            personalStateUrl = null;
        }

        List<String> portfolioUrls = !portfolios.stream().allMatch(MultipartFile::isEmpty) ? uploadPortfolios(portfolios, bucketName) : Collections.emptyList();


        log.info("1. resumePhotoUrl ::: {}", resumePhotoUrl);
        log.info("1. personalStateUrl ::: {}", personalStateUrl);
        log.info("1. portfolioUrls ::: {}", portfolioUrls);

        log.info("2. resumePhotoUrl.isEmpty() ::: {}", resumePhotoUrl == null || resumePhotoUrl.isEmpty());
        log.info("2. personalStateUrl.isEmpty() ::: {}", personalStateUrl == null || personalStateUrl.isEmpty());
        log.info("2. portfolios.stream().allMatch(MultipartFile::isEmpty) ::: {}", portfolios.stream().allMatch(MultipartFile::isEmpty));

        log.info("3. resumeDTO ::: {}", resumeDTO);

        existingResume = existingResume.toBuilder()
                .resumeName(resumeDTO.getResumeName() != null ? resumeDTO.getResumeName() : existingResume.getResumeName())
                .resumeDate(resumeDTO.getResumeDate() != null ? resumeDTO.getResumeDate() : existingResume.getResumeDate())
                .phoneNum(resumeDTO.getPhoneNum() != null ? resumeDTO.getPhoneNum() : existingResume.getPhoneNum())
                .resumeEmail(resumeDTO.getResumeEmail() != null ? resumeDTO.getResumeEmail() : existingResume.getResumeEmail())
                .resumePhoto(resumePhotoUrl != null ? resumePhotoUrl : "")
                .personalState(personalStateUrl != null ? personalStateUrl : "")
                .resumeUpdate(LocalDateTime.now())
                .build();

        log.info("existingResume :: {}", existingResume);

        saveRelatedEntities(existingResume, resumeDTO, portfolioUrls);

        resumeRepository.save(existingResume);

        log.info("4. resumeDTO :::> {}", resumeDTO);
        log.info("4. existingResume :: {}", existingResume);


        // DateTimeFormatter 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        ResumeDTO.fromEntity(existingResume).toBuilder()
                .resumeName(existingResume.getResumeName() != null ? existingResume.getResumeName() : Collections.emptyList().toString())
                .resumeDate(existingResume.getResumeDate() != null ? existingResume.getResumeDate() : Collections.emptyList().toString())
                .phoneNum(existingResume.getPhoneNum() != null ? existingResume.getPhoneNum() : Collections.emptyList().toString())
                .resumePhoto(existingResume.getResumePhoto() != null ? existingResume.getResumePhoto() : Collections.emptyList().toString())
                .resumeEmail(existingResume.getResumeEmail() != null ? existingResume.getResumeEmail() : Collections.emptyList().toString())
                .resumeUpdate(existingResume.getResumeUpdate() != null ? existingResume.getResumeUpdate() : LocalDateTime.parse(LocalDateTime.now().format(formatter)))
                .personalState(existingResume.getPersonalState() != null ? existingResume.getPersonalState() : Collections.emptyList().toString())
                .archive(resumeDTO.getArchive() != null ? resumeDTO.getArchive() : new ArchiveDTO())
                .certificates(resumeDTO.getCertificates() != null ? resumeDTO.getCertificates() : Collections.emptyList())
                .educations(resumeDTO.getEducations() != null ? resumeDTO.getEducations() : Collections.emptyList())
                .careers(resumeDTO.getCareers() != null ? resumeDTO.getCareers() : Collections.emptyList())
                .curriculums(resumeDTO.getCurriculums() != null ? resumeDTO.getCurriculums() : Collections.emptyList())
                .etcs(resumeDTO.getEtcs() != null ? resumeDTO.getEtcs() : Collections.emptyList())
                .jobPositions(resumeDTO.getJobPositions() != null ? resumeDTO.getJobPositions() : Collections.emptyList())
                .techStacks(resumeDTO.getTechStacks() != null ? resumeDTO.getTechStacks() : Collections.emptyList())
                .portfolios(!portfolioUrls.isEmpty() ? portfolioUrls.stream()
                        .map(url -> PortfolioDTO.builder().portFile(url).build())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    private List<String> uploadPortfolios(List<MultipartFile> portfolios, String bucketName) {
        return portfolios.stream()
                .map(file -> {
                    try {
                        String fileName = generateFileName("portfolio/", file);
                        return storageService.uploadFile(file, "portfolio/", fileName, bucketName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void saveRelatedEntities(Resume existingResume, ResumeDTO resumeDTO, List<String> portfolioUrls) {
        saveCertificates(existingResume, resumeDTO.getCertificates());
        saveEducations(existingResume, resumeDTO.getEducations());
        saveCareers(existingResume, resumeDTO.getCareers());
        saveCurriculums(existingResume, resumeDTO.getCurriculums());
        saveEtcs(existingResume, resumeDTO.getEtcs());
        savePortfolios(existingResume, portfolioUrls);
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

    private void savePortfolios(Resume resume, List<String> portfolioUrls) {
        if (portfolioUrls != null && !portfolioUrls.isEmpty()
                || portfolioUrls.stream().allMatch(url -> url != null && !url.isEmpty())) {
            portfolioRepository.deleteByResumeResumeId(resume.getResumeId());
            portfolioUrls.forEach(url -> {
                Portfolio portfolio = Portfolio.builder()
                        .portFile(url)
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
                storageService.deleteFile(portfolio.getPortFile());
            });
        }

        storageService.deleteFile(resume.getResumePhoto());
        storageService.deleteFile(resume.getPersonalState());
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
                .resumePhoto(resume.getResumePhoto())
                .resumeEmail(resume.getResumeEmail())
                .resumeCreate(resume.getResumeCreate())
                .resumeUpdate(resume.getResumeUpdate())
                .personalState(resume.getPersonalState())
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

    private String generateFileName(String folderName, MultipartFile file) {
        return folderName + file.getOriginalFilename();
    }

    private String uploadFileAndDeleteOld(MultipartFile newFile, String folderName, String fileName, String bucketName, String oldFileUrl) {
        String newFileUrl = storageService.uploadFile(newFile, folderName, fileName, bucketName);
        if (newFileUrl != null && oldFileUrl != null && !oldFileUrl.isEmpty()) {
            storageService.deleteFile(oldFileUrl);
        }
        return newFileUrl;
    }

}
