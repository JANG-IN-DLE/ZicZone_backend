package org.zerock.ziczone.service.alarm;

import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.dto.Alarm.RequestAlarmDTO;
import org.zerock.ziczone.dto.Alarm.ResponseAlarmDTO;

import java.util.List;

public interface AlarmService {

    //알람을 저장
    public Alarm saveAlarm(RequestAlarmDTO requestAlarmDTO);

    public List<ResponseAlarmDTO> AlarmList(Long userId);

    public ResponseAlarmDTO sendAlarm(RequestAlarmDTO requestAlarmDTO);

}
