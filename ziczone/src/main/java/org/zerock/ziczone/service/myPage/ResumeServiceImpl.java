package org.zerock.ziczone.service.myPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.application.*;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.tech.Tech;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.exception.mypage.UserNotFoundException;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;
import org.zerock.ziczone.service.storage.StorageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ArchiveRepository archiveRepository;
    private final EtcRepository etcRepository;
    private final CurriculumRepository curriculumRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final PersonalUserRepository personalUserRepository;
    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final StorageService storageService;


    /**
     * 지원서 생성
     * 지원서와 관련된 자격증, 학력, 아카이브, 경력사항, 교육사항, 기타사항들을 같이 생성한다.
     * 만약 테이블에 조회된 값이 없을 경우엔 새로 생성을 해준다.
     * @param resumeDTO
     * @param userId
     * @return ResumeDTO
     */
    @Override
    @Transactional
    public ResumeDTO createResume(ResumeDTO resumeDTO, Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new PersonalNotFoundException("Personal user not found");
        }
        Resume resume = resumeDTO.toEntity();
        resume = resume.toBuilder().personalUser(personalUser).build();
        Resume savedResume = resumeRepository.save(resume);

        saveOrUpdateArchive(resumeDTO.getArchive(), savedResume);
        saveOrUpdateEtcs(resumeDTO.getEtcs(), savedResume);
        saveOrUpdateCurriculums(resumeDTO.getCurriculums(), savedResume);
        saveOrUpdateCareers(resumeDTO.getCareers(), savedResume);
        saveOrUpdateEducations(resumeDTO.getEducations(), savedResume);
        saveOrUpdateCertificates(resumeDTO.getCertificates(), savedResume);

        return ResumeDTO.fromEntity(savedResume);
    }

    /**
     * 지원서 조회
     * 지원서 조회시 없을 경우 지원서 생성 메서드를 가져와서 저장한다.그리고 조회해서 값을 불러온다.
     * @param userId
     * @return resumeDTO
     */
    @Override
    @Transactional
    public ResumeDTO getResume(Long userId) {
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new PersonalNotFoundException("Personal user not found");
        }
        Optional<List<Resume>> resumesOptional = resumeRepository.findByPersonalUser(personalUser);

        Resume resume;
        if (resumesOptional.isPresent() && !resumesOptional.get().isEmpty()) {
            resume = resumesOptional.get().get(0);
        } else {
            resume = createEmptyResume(personalUser);
            resume = resumeRepository.save(resume);
        }

        ResumeDTO resumeDTO = ResumeDTO.fromEntity(resume);
        ArchiveDTO archiveDTO = getOrCreateArchive(resume);
        List<EtcDTO> etcs = getOrCreateEtcs(resume);
        List<CurriculumDTO> curriculums = getOrCreateCurriculums(resume);
        List<CareerDTO> careers = getOrCreateCareers(resume);
        List<EducationDTO> educations = getOrCreateEducations(resume);
        List<CertificateDTO> certificates = getOrCreateCertificates(resume);

        List<JobPositionDTO> jobPositions = getJobPositionsByPersonalId(personalUser.getPersonalId());
        log.info("jobPositions : {}",jobPositions);
        List<TechStackDTO> techStacks = getTechStacksByPersonalId(personalUser.getPersonalId());
        log.info("techStacks : {}",techStacks);



        resumeDTO = resumeDTO.toBuilder()
                .archive(archiveDTO)
                .etcs(etcs)
                .curriculums(curriculums)
                .careers(careers)
                .educations(educations)
                .certificates(certificates)
                .jobPositions(jobPositions)
                .techStacks(techStacks)
                .build();

        log.info("resumeDTO : {}", resumeDTO);
        return resumeDTO;
    }

    /**
     *  지원서 수정
     *  지원서 수정시 필요한 부분만 요청을 보내면 수정이 가능하다. 해당 테이블들의 id값은 필수로 필요하다. (조회시 값을 전달 함)
     * @param userId
     * @param resumeDTO
     * @return
     */
    @Override
    @Transactional
    public ResumeDTO updateResume(Long userId, ResumeDTO resumeDTO, MultipartFile resumePhotoFile) {
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new PersonalNotFoundException("Personal user not found");
        }
        Optional<List<Resume>> resumesOptional = resumeRepository.findByPersonalUser(personalUser);

        if (resumesOptional.isPresent() && !resumesOptional.get().isEmpty()) {
            Resume resume = resumesOptional.get().get(0);

            // 사진 업로드 처리
            String resumePhotoUrl = resume.getResumePhoto();
            if (resumePhotoFile != null && !resumesOptional.get().isEmpty()) {
                // 클라우드 오브젝트 스토리지 버켓 관련 설정
                String bucketName = "ziczone-bucket";
                String folderName = "CorrPdf/";
                String objectName = folderName + resumePhotoFile.getOriginalFilename();
                resumePhotoUrl = storageService.uploadFile(resumePhotoFile, folderName, objectName, bucketName);

            }

            Resume.ResumeBuilder resumeBuilder = resume.toBuilder();
            resumeBuilder.resumeName(getValueOrEmpty(resumeDTO.getResumeName(), resume.getResumeName()));
            resumeBuilder.resumeDate(getValueOrEmpty(resumeDTO.getResumeDate(), resume.getResumeDate()));
            resumeBuilder.phoneNum(getValueOrEmpty(resumeDTO.getPhoneNum(), resume.getPhoneNum()));
            resumeBuilder.resumePhoto(resumePhotoUrl);
            resumeBuilder.resumeCreate(getValueOrEmpty(resumeDTO.getResumeCreate(), resume.getResumeCreate()));
            resumeBuilder.resumeUpdate(LocalDateTime.now()); // Update timestamp
            resumeBuilder.personalState(getValueOrEmpty(resumeDTO.getPersonalState(), resume.getPersonalState()));
            resumeBuilder.resumeEmail(getValueOrEmpty(resumeDTO.getResumeEmail(), resume.getResumeEmail()));

            Resume updatedResume = resumeRepository.save(resumeBuilder.personalUser(personalUser).build());

            saveOrUpdateArchive(resumeDTO.getArchive(), updatedResume);
            saveOrUpdateEtcs(resumeDTO.getEtcs(), updatedResume);
            saveOrUpdateCurriculums(resumeDTO.getCurriculums(), updatedResume);
            saveOrUpdateCareers(resumeDTO.getCareers(), updatedResume);
            saveOrUpdateEducations(resumeDTO.getEducations(), updatedResume);
            saveOrUpdateCertificates(resumeDTO.getCertificates(), updatedResume);
            saveOrUpdateJobPositions(resumeDTO.getJobPositions(), personalUser);
            saveOrUpdateTechStacks(resumeDTO.getTechStacks(), personalUser);

            return getResumeDTOWithDefaults(updatedResume);
        } else {
            throw new IllegalArgumentException("Resume not found for the given user.");
        }
    }


    /**
     * 지원서 삭제
     * 지원서 삭제시 관련된 테이블들의 모든 데이터를 전부 삭제한다.
     * @param userId
     */
    @Transactional
    @Override
    public void deleteResume(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new PersonalNotFoundException("Personal user not found");
        }

        List<Resume> resumes = resumeRepository.findByPersonalUser(personalUser)
                .orElseThrow(() -> new IllegalArgumentException("No Resume entity for the given user."));

        Resume resume = resumes.get(0); // assuming we take the first resume if multiple exist

        try {
            archiveRepository.deleteByResume_ResumeId(resume.getResumeId());
            etcRepository.deleteByResume_ResumeId(resume.getResumeId());
            curriculumRepository.deleteByResume_ResumeId(resume.getResumeId());
            careerRepository.deleteByResume_ResumeId(resume.getResumeId());
            educationRepository.deleteByResume_ResumeId(resume.getResumeId());
            certificateRepository.deleteByResume_ResumeId(resume.getResumeId());
            resumeRepository.deleteById(resume.getResumeId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete resume and related entities", e);
        }
    }


    /**
     *   아래부터는 Helper Method
     *
     */


    private ResumeDTO getResumeDTOWithDefaults(Resume resume) {
        ResumeDTO resumeDTO = ResumeDTO.fromEntity(resume);
        resumeDTO = resumeDTO.toBuilder()
                .archive(getOrCreateArchive(resume))
                .etcs(getOrCreateEtcs(resume))
                .curriculums(getOrCreateCurriculums(resume))
                .careers(getOrCreateCareers(resume))
                .educations(getOrCreateEducations(resume))
                .certificates(getOrCreateCertificates(resume))
                .jobPositions(getOrCreateJobPositionsDTO(resume.getPersonalUser().getPersonalId()))
                .techStacks(getOrCreateTechStacksDTO(resume.getPersonalUser().getPersonalId()))
                .build();
        return resumeDTO;
    }

    private void saveOrUpdateArchive(ArchiveDTO archiveDTO, Resume resume) {
        if (archiveDTO == null) {
            archiveDTO = ArchiveDTO.builder().arch_git("").arch_notion("").arch_blog("").build();
        }
        Archive archive = archiveRepository.findByResume_ResumeId(resume.getResumeId())
                .stream().findFirst().orElse(new Archive());
        archiveRepository.save(archive.toBuilder()
                .archGit(getValueOrEmpty(archiveDTO.getArch_git(), archive.getArchGit()))
                .archNotion(getValueOrEmpty(archiveDTO.getArch_notion(), archive.getArchNotion()))
                .archBlog(getValueOrEmpty(archiveDTO.getArch_blog(), archive.getArchBlog()))
                .resume(resume)
                .build());
    }

    private void saveOrUpdateEtcs(List<EtcDTO> etcs, Resume resume) {
        etcRepository.deleteByResume_ResumeId(resume.getResumeId());
        if (etcs == null) {
            etcs = List.of(EtcDTO.builder().etc_content("").etc_date("").build());
        }
        for (EtcDTO etc : etcs) {
            etcRepository.save(Etc.builder()
                    .etcId(etc.getEtc_id())
                    .etcContent(getValueOrEmpty(etc.getEtc_content(), ""))
                    .etcDate(getValueOrEmpty(etc.getEtc_date(), ""))
                    .resume(resume)
                    .build());
        }
    }

    private void saveOrUpdateCurriculums(List<CurriculumDTO> curriculums, Resume resume) {
        curriculumRepository.deleteByResume_ResumeId(resume.getResumeId());
        if (curriculums == null) {
            curriculums = List.of(CurriculumDTO.builder().curri_content("").curri_company("").curri_date("").build());
        }
        for (CurriculumDTO curriculum : curriculums) {
            curriculumRepository.save(Curriculum.builder()
                    .curriId(curriculum.getCurri_id())
                    .curriContent(getValueOrEmpty(curriculum.getCurri_content(), ""))
                    .curriCompany(getValueOrEmpty(curriculum.getCurri_company(), ""))
                    .curriDate(getValueOrEmpty(curriculum.getCurri_date(), ""))
                    .resume(resume)
                    .build());
        }
    }

    private void saveOrUpdateCareers(List<CareerDTO> careers, Resume resume) {
        careerRepository.deleteByResume_ResumeId(resume.getResumeId());
        if (careers == null) {
            careers = List.of(CareerDTO.builder().career_name("").career_job("").career_position("").career_date("").build());
        }
        for (CareerDTO career : careers) {
            careerRepository.save(Career.builder()
                    .careerId(career.getCareer_id())
                    .careerName(getValueOrEmpty(career.getCareer_name(), ""))
                    .careerJob(getValueOrEmpty(career.getCareer_job(), ""))
                    .careerPosition(getValueOrEmpty(career.getCareer_position(), ""))
                    .careerDate(getValueOrEmpty(career.getCareer_date(), ""))
                    .resume(resume)
                    .build());
        }
    }

    private void saveOrUpdateEducations(List<EducationDTO> educations, Resume resume) {
        educationRepository.deleteByResume_ResumeId(resume.getResumeId());
        if (educations == null) {
            educations = List.of(EducationDTO.builder().edu("").credit("").edu_date("").build());
        }
        for (EducationDTO education : educations) {
            educationRepository.save(Education.builder()
                    .eduId(education.getEdu_id())
                    .edu(getValueOrEmpty(education.getEdu(), ""))
                    .credit(getValueOrEmpty(education.getCredit(), ""))
                    .eduDate(getValueOrEmpty(education.getEdu_date(), ""))
                    .resume(resume)
                    .build());
        }
    }

    private void saveOrUpdateCertificates(List<CertificateDTO> certificates, Resume resume) {
        certificateRepository.deleteByResume_ResumeId(resume.getResumeId());
        if (certificates == null) {
            certificates = List.of(CertificateDTO.builder().cert("").cert_date("").build());
        }
        for (CertificateDTO certificate : certificates) {
            certificateRepository.save(Certificate.builder()
                    .certId(certificate.getCert_id())
                    .cert(getValueOrEmpty(certificate.getCert(), ""))
                    .certDate(getValueOrEmpty(certificate.getCert_date(), ""))
                    .resume(resume)
                    .build());
        }
    }
    private void saveOrUpdateJobPositions(List<JobPositionDTO> jobPositions, PersonalUser personalUser) {
        jobPositionRepository.deleteByPersonalUser(personalUser);
        if (jobPositions != null) {
            List<JobPosition> jobPositionEntities = jobPositions.stream()
                    .map(jobPositionDTO -> JobPosition.builder()
                            .personalUser(personalUser)
                            .job(Job.builder()
                                    .jobId(jobPositionDTO.getJob().getJobId())
                                    .jobName(jobPositionDTO.getJob().getJobName())
                                    .build())
                            .build())
                    .collect(Collectors.toList());
            jobPositionRepository.saveAll(jobPositionEntities);
        }
    }

    private void saveOrUpdateTechStacks(List<TechStackDTO> techStacks, PersonalUser personalUser) {
        techStackRepository.deleteByPersonalUser(personalUser);
        if (techStacks != null) {
            List<TechStack> techStackEntities = techStacks.stream()
                    .map(techStackDTO -> TechStack.builder()
                            .personalUser(personalUser)
                            .tech(Tech.builder()
                                    .techId(techStackDTO.getTech().getTechId())
                                    .techName(techStackDTO.getTech().getTechName())
                                    .techUrl(techStackDTO.getTech().getTechUrl())
                                    .build())
                            .build())
                    .collect(Collectors.toList());
            techStackRepository.saveAll(techStackEntities);
        }
    }

    private <T> T getValueOrEmpty(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private Resume createEmptyResume(PersonalUser personalUser) {
        return Resume.builder()
                .resumeName("")
                .resumeDate("")
                .phoneNum("")
                .resumePhoto("")
                .resumeCreate(LocalDateTime.now())
                .resumeUpdate(LocalDateTime.now())
                .resumeEmail("")
                .personalState("")
                .personalUser(personalUser)
                .build();
    }

    private ArchiveDTO getOrCreateArchive(Resume resume) {
        List<Archive> archives = archiveRepository.findByResume_ResumeId(resume.getResumeId());
        if (!archives.isEmpty()) {
            return ArchiveDTO.fromEntity(archives.get(0));
        } else {
            Archive archive = Archive.builder()
                    .archGit("")
                    .archNotion("")
                    .archBlog("")
                    .resume(resume)
                    .build();
            return ArchiveDTO.fromEntity(archiveRepository.save(archive));
        }
    }

    private List<EtcDTO> getOrCreateEtcs(Resume resume) {
        List<EtcDTO> etcs = etcRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(EtcDTO::fromEntity)
                .collect(Collectors.toList());
        if (etcs.isEmpty()) {
            Etc emptyEtc = Etc.builder()
                    .etcContent("")
                    .etcDate("")
                    .resume(resume)
                    .build();
            etcs = List.of(EtcDTO.fromEntity(etcRepository.save(emptyEtc)));
        }
        return etcs;
    }

    private List<CurriculumDTO> getOrCreateCurriculums(Resume resume) {
        List<CurriculumDTO> curriculums = curriculumRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(CurriculumDTO::fromEntity)
                .collect(Collectors.toList());
        if (curriculums.isEmpty()) {
            Curriculum emptyCurriculum = Curriculum.builder()
                    .curriContent("")
                    .curriCompany("")
                    .curriDate("")
                    .resume(resume)
                    .build();
            curriculums = List.of(CurriculumDTO.fromEntity(curriculumRepository.save(emptyCurriculum)));
        }
        return curriculums;
    }

    private List<CareerDTO> getOrCreateCareers(Resume resume) {
        List<CareerDTO> careers = careerRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(CareerDTO::fromEntity)
                .collect(Collectors.toList());
        if (careers.isEmpty()) {
            Career emptyCareer = Career.builder()
                    .careerName("")
                    .careerJob("")
                    .careerPosition("")
                    .careerDate("")
                    .resume(resume)
                    .build();
            careers = List.of(CareerDTO.fromEntity(careerRepository.save(emptyCareer)));
        }
        return careers;
    }

    private List<EducationDTO> getOrCreateEducations(Resume resume) {
        List<EducationDTO> educations = educationRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(EducationDTO::fromEntity)
                .collect(Collectors.toList());
        if (educations.isEmpty()) {
            Education emptyEducation = Education.builder()
                    .edu("")
                    .credit("")
                    .eduDate("")
                    .resume(resume)
                    .build();
            educations = List.of(EducationDTO.fromEntity(educationRepository.save(emptyEducation)));
        }
        return educations;
    }

    private List<CertificateDTO> getOrCreateCertificates(Resume resume) {
        List<CertificateDTO> certificates = certificateRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(CertificateDTO::fromEntity)
                .collect(Collectors.toList());
        if (certificates.isEmpty()) {
            Certificate emptyCertificate = Certificate.builder()
                    .cert("")
                    .certDate("")
                    .resume(resume)
                    .build();
            certificates = List.of(CertificateDTO.fromEntity(certificateRepository.save(emptyCertificate)));
        }
        return certificates;
    }

    private List<JobPositionDTO> getOrCreateJobPositionsDTO(Long personalId) {
        return getJobPositionsByPersonalId(personalId);
    }

    private List<TechStackDTO> getOrCreateTechStacksDTO(Long personalId) {
        return getTechStacksByPersonalId(personalId);
    }

    private List<JobPositionDTO> getJobPositionsByPersonalId(Long personalId) {
        List<JobPosition> jobPositions = jobPositionRepository.findByPersonalUser_PersonalId(personalId);

        return jobPositions.stream()
                .map(this::convertToJobPositionDTO)
                .collect(Collectors.toList());
    }

    private List<TechStackDTO> getTechStacksByPersonalId(Long personalId) {
        List<TechStack> techStacks = techStackRepository.findByPersonalUser_PersonalId(personalId);

        return techStacks.stream()
                .map(this::convertToTechStackDTO)
                .collect(Collectors.toList());
    }

    private JobPositionDTO convertToJobPositionDTO(JobPosition jobPosition) {
        return JobPositionDTO.builder()
                .userJobId(jobPosition.getUserJobId())
                .job(JobDTO.builder()
                        .jobId(jobPosition.getJob().getJobId())
                        .jobName(jobPosition.getJob().getJobName())
                        .build())
                .build();
    }

    private TechStackDTO convertToTechStackDTO(TechStack techStack) {
        return TechStackDTO.builder()
                .userTechId(techStack.getUserTechId())
                .tech(TechDTO.builder()
                        .techId(techStack.getTech().getTechId())
                        .techName(techStack.getTech().getTechName())
                        .techUrl(techStack.getTech().getTechUrl())
                        .build())
                .build();
    }

}
