package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.AlarmContent;

public interface AlarmContentRepository extends JpaRepository<AlarmContent, Long> {
    AlarmContent findByAlarmContentId(Long alarmContentId);
}
