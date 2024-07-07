package org.zerock.ziczone.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PersonalUserRepository personalUserRepository;

    @Override
    @Transactional
    public Long boardRegister(BoardDTO boardDTO) {
        User user = userRepository.findById(boardDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 없습니다."));

        if (user.getUserType() != UserType.PERSONAL) {
            throw new IllegalArgumentException("기업 회원은 게시물을 등록할 수 없습니다.");
        }

        Board board = Board.builder()
                .corrPoint(boardDTO.getCorrPoint())
                .corrTitle(boardDTO.getCorrTitle())
                .corrContent(boardDTO.getCorrContent())
                .corrPdf(boardDTO.getCorrPdf())
                .corrView(0)
                .corrModify(boardDTO.getCorrModify())
                .user(user)
                .build();

        boardRepository.save(board);

        BoardDTO updatedDTO = boardUserRead(boardDTO);
        log.info(updatedDTO);

        return boardDTO.getCorrId();
    }

    @Override
    public BoardDTO boardReadOne(Long corrId) {
        Optional<Board> result = boardRepository.findById(corrId);

        Board board = result.orElseThrow(() -> new IllegalArgumentException("게시물 ID가 없습니다."));

        User user = board.getUser();
        PersonalUser personalUser = user.getPersonalUser();

        return BoardDTO.builder()
                .corrId(board.getCorrId())
                .corrPoint(board.getCorrPoint())
                .corrTitle(board.getCorrTitle())
                .corrContent(board.getCorrContent())
                .corrPdf(board.getCorrPdf())
                .corrView(board.getCorrView())
                .corrModify(board.getCorrModify())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .build();
    }

    @Override
    public List<BoardDTO> userReadAll(Long userId) {
        List<Board> boards = boardRepository.findByUserUserId(userId);

        return boards.stream()
                .map(board -> {
                    User user = board.getUser();
                    PersonalUser personalUser = user.getPersonalUser();

                    return BoardDTO.builder()
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .corrTitle(board.getCorrTitle())
                            .corrContent(board.getCorrContent())
                            .corrPdf(board.getCorrPdf())
                            .corrView(board.getCorrView())
                            .corrModify(board.getCorrModify())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageResponseDTO<BoardDTO> boardFilter(String filterType, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize());
        Page<Board> result;

        switch (filterType) {
            case "latest":   // 최신순
                result = boardRepository.findAllByOrderByCorrCreateDesc(pageable);
                break;
            case "views":    // 조회순
                result = boardRepository.findAllByOrderByCorrViewDesc(pageable);
                break;
            case "berry":    // 포인트(베리)순
                result = boardRepository.findAllByOrderByCorrPointDesc(pageable);
                break;
            default:
                throw new IllegalArgumentException("Invalid filter type: " + filterType);
        }

        List<BoardDTO> dtoList = result.stream()
                .map(board -> {
                    User user = board.getUser();
                    PersonalUser personalUser = user.getPersonalUser();

                    return BoardDTO.builder()
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .corrTitle(board.getCorrTitle())
                            .corrContent(board.getCorrContent())
                            .corrPdf(board.getCorrPdf())
                            .corrView(board.getCorrView())
                            .corrModify(board.getCorrModify())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public void boardModify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getCorrId());

        Board board = result.orElseThrow(() -> new IllegalArgumentException("게시물 ID가 없습니다."));

        if(!board.getUser().getUserId().equals(boardDTO.getUserId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        board.change(boardDTO.getCorrTitle(), boardDTO.getCorrContent(), boardDTO.getCorrPdf());

        boardRepository.save(board);

        BoardDTO updatedDTO = boardUserRead(boardDTO);
        log.info(updatedDTO);
    }

    @Override
    @Transactional
    public void boardDelete(Long userId, Long corrId) {
        Optional<Board> result = boardRepository.findById(corrId);

        Board board = result.orElseThrow(() -> new IllegalArgumentException("게시물 ID가 없습니다."));

        if (board.getUser() == null || !board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        boardRepository.deleteById(corrId);
    }

    @Override
    public BoardDTO boardUserRead(BoardDTO boardDTO) {
        User user = userRepository.findByUserId(boardDTO.getUserId());
        PersonalUser personalUser = personalUserRepository.findByPersonalId(boardDTO.getUserId());

        return BoardDTO.builder()
                .corrId(boardDTO.getCorrId())
                .corrPoint(boardDTO.getCorrPoint())
                .corrTitle(boardDTO.getCorrTitle())
                .corrContent(boardDTO.getCorrContent())
                .corrPdf(boardDTO.getCorrPdf())
                .corrView(boardDTO.getCorrView())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .build();
    }
}
