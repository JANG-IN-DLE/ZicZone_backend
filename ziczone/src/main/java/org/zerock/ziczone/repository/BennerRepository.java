package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Benner;

import java.util.List;

public interface BennerRepository extends JpaRepository<Benner, Long> {
}