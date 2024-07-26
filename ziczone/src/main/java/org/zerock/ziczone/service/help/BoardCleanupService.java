package org.zerock.ziczone.service.help;

public interface BoardCleanupService {
    // 작성일 7일이 지나도 댓글이 달리지 않는 경우 게시물 삭제, 베리 환불
    void cleanupOldBoards();
}

