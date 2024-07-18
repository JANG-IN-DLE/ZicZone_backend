package org.zerock.ziczone.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.alarm.Alarm;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    public List<Alarm> findByUser_UserId(Long userId);

}
