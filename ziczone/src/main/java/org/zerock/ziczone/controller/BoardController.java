package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;
import org.zerock.ziczone.service.help.BoardService;

@RestController
@Log4j2
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 최신순, 조회순, 포인트(베리)순 필터링
    @GetMapping("/filter")
    public PageResponseDTO<BoardDTO> boardFilter(
            @RequestParam String filterType,
            @RequestParam int page,
            @RequestParam int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        return boardService.boardFilter(filterType, pageRequestDTO);
    }
}
