package org.zerock.ziczone.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.board.Comment;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.board.CommentRepository;
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
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final PersonalUserRepository personalUserRepository;

    /**
     * 댓글 등록
     *
     * @param commentDTO 등록할 댓글 정보
     * @return CommentDTO 등록된 댓글 정보
     * @throws IllegalArgumentException 회원 ID가 없거나 기업 회원이 댓글을 등록하려고 할 때 발생
     */
    @Override
    @Transactional
    public CommentDTO commentRegister(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 없습니다."));
        Board board = boardRepository.findById(commentDTO.getCorrId())
                .orElseThrow(() -> new IllegalArgumentException("게시물 ID가 없습니다."));

        if (user.getUserType() != UserType.PERSONAL) {
            throw new IllegalArgumentException("기업 회원은 댓글을 등록할 수 없습니다.");
        }

        Comment comment = Comment.builder()
                .commContent(commentDTO.getCommContent())
                .commModify(commentDTO.getCommModify())
                .user(user)
                .board(board)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return commentUserRead(savedComment);
    }

    /**
     * 특정 게시물의 모든 댓글 조회
     *
     * @param corrId 게시물 ID
     * @return List<CommentDTO> 특정 게시물에 대한 모든 댓글 정보
     * @throws IllegalArgumentException 게시물 ID가 유효하지 않을 때 발생
     */
    @Override
    public List<CommentDTO> boardReadAllComment(Long corrId) {
        List<Comment> comments = commentRepository.findByBoardCorrId(corrId);

        return comments.stream()
                .map(comment -> {
                    User user = comment.getUser();
                    PersonalUser personalUser = user.getPersonalUser();
                    Board board = comment.getBoard();

                    return CommentDTO.builder()
                            .commId(comment.getCommId())
                            .commContent(comment.getCommContent())
                            .commSelection(comment.isCommSelection())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .gender(personalUser.getGender())
                            .personalId(personalUser.getPersonalId())
                            .corrPoint(board.getCorrPoint())
                            .corrId(board.getCorrId())
                            .commModify(comment.getCommModify())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     *
     * @param commentDTO 수정할 댓글 정보
     * @return CommentDTO 수정된 댓글 정보
     * @throws IllegalArgumentException 댓글 ID가 없거나 작성자가 아닌 사용자가 수정하려고 할 때 발생
     */
    @Override
    @Transactional
    public CommentDTO commentModify(CommentDTO commentDTO) {
        Optional<Comment> result = commentRepository.findById(commentDTO.getCommId());
        Comment comment = result.orElseThrow(() -> new IllegalArgumentException("댓글 ID가 없습니다."));

        if (!comment.getUser().getUserId().equals(commentDTO.getUserId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        comment.change(commentDTO.getCommContent());

        commentRepository.save(comment);

        return commentUserRead(comment);
    }

    /**
     * 댓글 삭제
     *
     * @param userId 사용자 ID
     * @param commId 삭제할 댓글 ID
     * @throws IllegalArgumentException 댓글 ID가 없거나 작성자가 아닌 사용자가 삭제하려고 할 때 발생
     */
    @Override
    @Transactional
    public void commentDelete(Long userId, Long commId) {
        Optional<Comment> result = commentRepository.findById(commId);
        Comment comment = result.orElseThrow(() -> new IllegalArgumentException("댓글 ID가 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.deleteById(commId);
    }

    /**
     * 정보 추가
     *
     * @param comment 조회할 댓글
     * @return CommentDTO 조회된 댓글 정보
     * @throws IllegalArgumentException 회원 ID가 없을 경우 발생
     */
    @Override
    public CommentDTO commentUserRead(Comment comment) {
        User user = userRepository.findById(comment.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 없습니다."));
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(user.getUserId());
        Board board = comment.getBoard();

        return CommentDTO.builder()
                .commId(comment.getCommId())
                .commContent(comment.getCommContent())
                .commSelection(comment.isCommSelection())
                .userId(comment.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .corrId(comment.getBoard().getCorrId())
                .gender(personalUser.getGender())
                .corrPoint(board.getCorrPoint())
                .commModify(comment.getCommModify())
                .build();
    }

    /**
     * 댓글 채택
     *
     * @param commId 채택할 댓글 ID
     * @param userId 채택을 수행하는 사용자 ID
     * @throws IllegalArgumentException 댓글 ID가 없거나, 게시물 작성자가 아니거나, 자신의 댓글을 채택하려고 하거나, 이미 채택된 댓글이 있는 경우 발생
     */
    @Override
    @Transactional
    public void selectComment(Long commId, Long userId) {
        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 ID가 없습니다."));
        Board board = comment.getBoard();

        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시물 작성자만 댓글을 채택할 수 있습니다.");
        }

        if (comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 댓글은 채택할 수 없습니다.");
        }

        List<Comment> comments = commentRepository.findByBoardCorrId(board.getCorrId());
        for (Comment c : comments) {
            if (c.isCommSelection()) {
                throw new IllegalArgumentException("이미 채택된 댓글이 있습니다.");
            }
        }

        comment.changeSelection(true);

        commentRepository.save(comment);
    }

    @Transactional
    public List<CommentDTO> userReadAllComment(Long userId) {
        List<Comment> comments = commentRepository.findByUserUserId(userId);

        return comments.stream()
                .map(comment -> {
                    User user = comment.getUser();
                    PersonalUser personalUser = user.getPersonalUser();
                    Board board = comment.getBoard();

                    return CommentDTO.builder()
                            .commId(comment.getCommId())
                            .commContent(comment.getCommContent())
                            .commSelection(comment.isCommSelection())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .corrId(board.getCorrId())
                            .personalId(personalUser.getPersonalId())
                            .corrPoint(board.getCorrPoint())
                            .gender(personalUser.getGender())
                            .commModify(comment.getCommModify())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
