package org.zerock.ziczone.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.member.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
}
