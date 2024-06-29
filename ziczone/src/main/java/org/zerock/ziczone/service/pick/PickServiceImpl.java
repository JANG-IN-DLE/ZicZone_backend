package org.zerock.ziczone.service.pick;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
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

    @Override
    public PickCardDTO getPickCardsById(Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getTech().getTechName())
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


}
