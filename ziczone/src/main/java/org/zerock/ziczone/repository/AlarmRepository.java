package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
