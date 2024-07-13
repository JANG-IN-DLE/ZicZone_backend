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
                .user(user)
                .board(board)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return commentUserRead(savedComment);
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
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
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
                            .corrId(board.getCorrId())
                            .build();
                })
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void commentDelete(Long userId, Long commId) {
        Optional<Comment> result = commentRepository.findById(commId);

        Comment comment = result.orElseThrow(() -> new IllegalArgumentException("댓글 ID가 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.deleteById(commId);
    }

    // Comment 객체를 받아 userId를 이용해 관련 사용자 정보(이름, 경력)을 조회하여 새로운 DTO 객체에 설정
    @Transactional
    public CommentDTO commentUserRead(Comment comment) {
        User user = userRepository.findById(comment.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 없습니다."));
        PersonalUser personalUser = user.getPersonalUser();

        return CommentDTO.builder()
                .commId(comment.getCommId())
                .commContent(comment.getCommContent())
                .commSelection(comment.isCommSelection())
                .userId(comment.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(user.getUserName())
                .personalCareer(personalUser != null ? personalUser.getPersonalCareer() : null)
                .corrId(comment.getBoard().getCorrId())
                .gender(personalUser != null ? personalUser.getGender() : null)
                .build();
    }

}
