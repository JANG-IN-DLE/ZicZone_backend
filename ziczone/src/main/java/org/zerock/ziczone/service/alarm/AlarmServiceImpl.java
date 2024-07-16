package org.zerock.ziczone.service.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.domain.alarm.AlarmContent;
import org.zerock.ziczone.dto.Alarm.AlarmDTO;
import org.zerock.ziczone.repository.alarm.AlarmContentRepository;
import org.zerock.ziczone.repository.alarm.AlarmRepository;
import org.zerock.ziczone.repository.member.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final AlarmContentRepository alarmContentRepository;
    private final UserRepository userRepository;

//    @Override
//    public List<AlarmDTO> getAlarmList() {
//        return List.of();
//    }

    @Override
    public String saveAlarm(AlarmDTO alarmDTO) {

        AlarmContent alarmContent = AlarmContent.builder()
                .alarmType(alarmDTO.getType())
                .senderId(alarmDTO.getSenderId())
                .build();
        alarmContentRepository.save(alarmContent);

        Alarm alarm = Alarm.builder()
                .alarmContent(alarmContent)
                .alarmCreate(LocalDateTime.now())
                .readOrNot(false)
                .user(userRepository.findByUserId(alarmDTO.getReceiverId()))
                .build();
        alarmRepository.save(alarm);

        return "Alarm saved";
    }
}
