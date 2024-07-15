package org.zerock.ziczone.service.myPage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.application.*;
import org.zerock.ziczone.dto.mypage.PortfolioDTO;
import org.zerock.ziczone.dto.mypage.ResumeDTO;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.service.storage.StorageService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ResumeServiceImpl 클래스는 이력서(Resume)와 관련된 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl2 implements ResumeService2 {

    private final ResumeRepository resumeRepository;
    private final CertificateRepository certificateRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final CurriculumRepository curriculumRepository;
    private final EtcRepository etcRepository;
    private final PortfolioRepository portfolioRepository;
    private final StorageService storageService;

    @Transactional
    @Override
    public ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {

        String bucketName = "ziczone-bucket-jangindle-optimizer";
        String resumePhotoUrl = storageService.uploadFile(resumePhoto,"resumePhoto/", generateFileName("resumePhoto/", resumePhoto), bucketName);
        String personalStateUrl = storageService.uploadFile(personalState," personalState/", generateFileName("personalState/", personalState), bucketName);
        List<String> portfolioUrls = portfolios.stream()
                .map(file -> storageService.uploadFile(file, "portfolio/", generateFileName("portfolio/",file), bucketName))
                .collect(Collectors.toList());



        Resume resume = resumeDTO.toEntity().toBuilder()
                .resumePhoto(resumePhotoUrl)
                .personalState(personalStateUrl)
                .build();

        resume = resumeRepository.save(resume);
        saveRelatedEntities(resume, resumeDTO, portfolioUrls);
        return ResumeDTO.fromEntity(resume);
    }

    private String resumePhotoUrl(MultipartFile resumePhoto, String folderName, String bucketName) {

    }

    @Transactional
    @Override
    public ResumeDTO updateResume(Long resumeId, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));


        String bucketName = "ziczone-bucket-jangindle-optimizer";
        String resumePhotoUrl = storageService.uploadFile(resumePhoto,"resumePhoto/", generateFileName("resumePhoto/", resumePhoto), bucketName);
        String personalStateUrl = storageService.uploadFile(personalState," personalState/", generateFileName("personalState/", personalState), bucketName);
        List<String> portfolioUrls = portfolios.stream()
                .map(file -> storageService.uploadFile(file, "portfolio/", generateFileName("portfolio/",file), bucketName))
                .collect(Collectors.toList());

        resume = resumeDTO.toEntity().toBuilder()
                .resumeId(resumeId)
                .resumePhoto(resumePhotoUrl)
                .personalState(personalStateUrl)
                .build();

        resume = resumeRepository.save(resume);
        deleteRelatedEntities(resumeId);
        saveRelatedEntities(resume, resumeDTO, portfolioUrls);
        return ResumeDTO.fromEntity(resume);
    }

    @Transactional
    @Override
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));

        storageService.deleteFile(resume.getResumePhoto());
        storageService.deleteFile(resume.getPersonalState());
        deleteRelatedEntities(resumeId);
        resumeRepository.deleteById(resumeId);
    }

    @Override
    public ResumeDTO getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resume ID: " + resumeId));
        return ResumeDTO.fromEntity(resume);
    }

    @Override
    public ResumeDTO getResumeByUserId(Long userId) {
        Optional<Resume> optionalResume = Optional.ofNullable(resumeRepository.findByPersonalUser_PersonalId(userId));
        return optionalResume.map(ResumeDTO::fromEntity).orElse(null);


    }

    @Override
    public List<ResumeDTO> getAllResumes() {
        return resumeRepository.findAll().stream()
                .map(ResumeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void saveRelatedEntities(Resume resume, ResumeDTO resumeDTO, List<String> portfolioUrls) {
        resumeDTO.getCertificates().forEach(certDTO -> {
            Certificate certificate = certDTO.toEntity().toBuilder()
                    .resume(resume)
                    .build();
            certificateRepository.save(certificate);
        });

        resumeDTO.getEducations().forEach(eduDTO -> {
            Education education = eduDTO.toEntity().toBuilder()
                    .resume(resume)
                    .build();
            educationRepository.save(education);
        });

        resumeDTO.getCareers().forEach(carDTO -> {
            Career career = carDTO.toEntity().toBuilder()
                    .resume(resume)
                    .build();
            careerRepository.save(career);
        });

        resumeDTO.getCurriculums().forEach(curDTO -> {
            Curriculum curriculum = curDTO.toEntity().toBuilder()
                    .resume(resume)
                    .build();
            curriculumRepository.save(curriculum);
        });

        resumeDTO.getEtcs().forEach(etcDTO -> {
            Etc etc = etcDTO.toEntity().toBuilder()
                    .resume(resume)
                    .build();
            etcRepository.save(etc);
        });

        for (int i = 0; i < resumeDTO.getPortfolios().size(); i++) {
            PortfolioDTO portDTO = resumeDTO.getPortfolios().get(i);
            String portUrl = portfolioUrls.get(i);
            Portfolio portfolio = portDTO.toEntity().toBuilder()
                    .resume(resume)
                    .portFile(portUrl)
                    .build();
            portfolioRepository.save(portfolio);
        }
    }

    private void deleteRelatedEntities(Long resumeId) {
        certificateRepository.deleteByResumeResumeId(resumeId);
        educationRepository.deleteByResumeResumeId(resumeId);
        careerRepository.deleteByResumeResumeId(resumeId);
        curriculumRepository.deleteByResumeResumeId(resumeId);
        etcRepository.deleteByResumeResumeId(resumeId);
        portfolioRepository.deleteByResumeId(resumeId);
        
    }

    private String generateFileName(String folderName, MultipartFile file){
       return folderName + file.getOriginalFilename();
    }

}