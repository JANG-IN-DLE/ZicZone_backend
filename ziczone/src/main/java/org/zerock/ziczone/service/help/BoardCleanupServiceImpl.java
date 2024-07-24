package org.zerock.ziczone.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.repository.board.BoardRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardCleanupServiceImpl implements BoardCleanupService {
    private final BoardRepository boardRepository;

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 * * * * ?") // 테스트 (매 분마다 실행)
    @Transactional
    public void cleanupOldBoards() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        int deletedCount = boardRepository.deleteOldBoards(sevenDaysAgo);

        log.info("Total deleted posts: {}", deletedCount);
    }
}