package org.zerock.ziczone.service.pick;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.application.Career;
import org.zerock.ziczone.domain.application.Education;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
import org.zerock.ziczone.dto.pick.PickResumeDTO;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PickServiceImpl implements PickService {

    private final PersonalUserRepository personalUserRepository;
    private final TechStackRepository techStackRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final CertificateRepository certificateRepository;
    private final CurriculumRepository curriculumRepository;
    private final EtcRepository etcRepository;
    private final ArchiveRepository archiveRepository;

    //    pickzone 회원 card data 가져오는 메서드
    @Override
    public List<PickCardDTO> getPickCards() {
        List<PersonalUser> users = personalUserRepository.findAll();

        return users.stream().map(user -> {
            List<String> techNames = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechName())
                    .collect(Collectors.toList());
            List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(jobPosition -> jobPosition.getJob().getJobName())
                    .collect(Collectors.toList());
            return PickCardDTO.builder()
                    .userId(user.getUser().getUserId())
                    .personalId(user.getPersonalId())
                    .userName(user.getUser().getUserName())
                    .userIntro(user.getUser().getUserIntro())
                    .gender(user.getGender())
                    .personalCareer(user.getPersonalCareer())
                    .techName(String.join(",", techNames))
                    .jobName(String.join(",", jobNames))
                    .build();
        }).collect(Collectors.toList());

    }
//  pickzone Job 데이터 가져오는 메서드
    @Override
    public List<PickJobDTO> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(job -> PickJobDTO.builder()
                        .jobId(job.getJobId())
                        .jobName(job.getJobName())
                        .build())
                .collect(Collectors.toList());

    }
//  pickDetailzone 회원정보 가져오는 메서드
    @Override
    public PickCardDTO getPickCardsById(Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());

        PickCardDTO pickCardDTO = PickCardDTO.builder()
                .userId(personalUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(personalUser.getUser().getUserName())
                .userIntro(personalUser.getUser().getUserIntro())
                .gender(personalUser.getGender())
                .personalCareer(personalUser.getPersonalCareer())
                .techName(String.join(",", techNames))
                .jobName(String.join(",", jobNames))
                .build();

        return pickCardDTO;
    }
//  pickDetailzone resume data 가져오는 메서드
    @Override
    public PickResumeDTO getResumeById(Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);
        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        Resume resume = resumeRepository.findByPersonalUser_PersonalId(personalId);
        if(resume == null)  {
            throw new RuntimeException("resume not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        List<String> educations = educationRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(education -> education.getEdu() + "," + education.getCredit() + "," + education.getEduDate())
                .collect(Collectors.toList());
        List<String> careers = careerRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(career -> career.getCareerName()+ "," +career.getCareerJob() + "," + career.getCareerPosition() + ","+ career.getCareerDate())
                .collect(Collectors.toList());
        List<String> certificates = certificateRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(certificate -> certificate.getCert()+"," + certificate.getCertDate())
                .collect(Collectors.toList());
        List<String> curriculums = curriculumRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(curriculum -> curriculum.getCurriCompany()+","+curriculum.getCurriContent()+","+curriculum.getCurriDate())
                .collect(Collectors.toList());
        List<String> etcs = etcRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(etc -> etc.getEtcContent()+","+etc.getEtcDate())
                .collect(Collectors.toList());
        List<String> archives = archiveRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(archive -> archive.getArchBlog()+","+archive.getArchGit()+","+archive.getArchNotion())
                .collect(Collectors.toList());
        PickResumeDTO pickResumeDTO = PickResumeDTO.builder()
                .personalId(personalUser.getPersonalId())
                .resumeId(resume.getResumeId())
                .resumeName(resume.getResumeName())
                .resumeDate(resume.getResumeDate())
                .resumePhoto(resume.getResumePhoto())
                .phoneNum(resume.getPhoneNum())
                .personalState(resume.getPersonalState())
                .techName(String.join(",", techNames))
                .jobName(String.join(",", jobNames))
                .educations(educations)
                .careers(careers)
                .curriculums(curriculums)
                .etcs(etcs)
                .archives(archives)
                .certificates(certificates)
                .build();

        return pickResumeDTO;
    }


}
