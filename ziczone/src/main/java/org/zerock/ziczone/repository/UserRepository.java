package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
