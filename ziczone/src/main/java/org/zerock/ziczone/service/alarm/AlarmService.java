package org.zerock.ziczone.service.alarm;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.dto.Alarm.RequestAlarmDTO;
import org.zerock.ziczone.dto.Alarm.ResponseAlarmDTO;

import java.util.List;

public interface AlarmService {

    //구독
    SseEmitter subscribe(Long userId, String token);

    //로그이웃
    void logout(Long userId);

    //알람을 저장
    public Alarm saveAlarm(String type, Long senderId, Long receiverId);

    public List<ResponseAlarmDTO> AlarmList(Long userId);

    public ResponseAlarmDTO addAlarm(String type, Long senderId, Long receiverId);


    void readAlarm(Long userId);

}
