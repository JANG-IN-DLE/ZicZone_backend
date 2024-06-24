package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
