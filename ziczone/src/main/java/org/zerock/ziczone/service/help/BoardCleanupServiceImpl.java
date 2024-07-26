package org.zerock.ziczone.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.service.alarm.AlarmService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardCleanupServiceImpl implements BoardCleanupService {
    private final BoardRepository boardRepository;
    private final PayHistoryRepository payHistoryRepository;

    private final AlarmService alarmService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    // @Scheduled(cron = "0 * * * * ?") // 테스트 (매 분마다 실행)
    @Transactional
    public void cleanupOldBoards() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<Board> oldBoards = boardRepository.findOldBoardsWithoutComments(sevenDaysAgo);

        for (Board board : oldBoards) {
            PayHistory payHistory = PayHistory.builder()
                    .sellerId(board.getUser().getUserId())
                    .buyerId(board.getUser().getUserId())
                    .berryBucket("+" + board.getCorrPoint())
                    .payHistoryContent("게시물환불")
                    .payHistoryDate(LocalDateTime.now())
                    .personalUser(board.getUser().getPersonalUser())
                    .build();

            payHistoryRepository.save(payHistory);
            alarmService.addAlarm("DELETEBOARD", board.getCorrId(), board.getUser().getUserId());
        }

        int deletedCount = boardRepository.deleteOldBoardsWithoutComments(sevenDaysAgo);

        log.info("Total deleted posts: {}", deletedCount);
    }
}
