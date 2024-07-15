package org.zerock.ziczone.service.alarm;

import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.Alarm.AlarmDTO;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.join.TechDTO;

import java.util.List;

public interface AlarmService {


//    public List<AlarmDTO> getAlarmList(Long userId);

    //알람을 저장
    public String saveAlarm(AlarmDTO alarmDTO);


}
