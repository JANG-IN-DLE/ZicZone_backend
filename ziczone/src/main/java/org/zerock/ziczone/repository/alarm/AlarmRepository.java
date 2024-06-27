package org.zerock.ziczone.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.alarm.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
