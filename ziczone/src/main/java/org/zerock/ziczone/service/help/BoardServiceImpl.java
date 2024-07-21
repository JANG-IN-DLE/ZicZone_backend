package org.zerock.ziczone.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.help.BoardProfileCardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.board.CommentRepository;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;
import org.zerock.ziczone.service.storage.StorageService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PersonalUserRepository personalUserRepository;
    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final PaymentRepository paymentRepository;
    private final CommentRepository commentRepository;
    private final StorageService storageService;

    final String BUCKETNAME = "ziczone-bucket-jangindle";

    /**
     * 게시물 등록
     *
     * @param corrPoint  게시물 포인트
     * @param corrTitle  게시물 제목
     * @param corrContent 게시물 내용
     * @param corrPdf    첨부 파일 (MultipartFile 형태)
     * @param userId     사용자 ID
     * @return Long      생성된 게시물 ID
     * @throws IllegalArgumentException 회원 ID가 없거나, 기업 회원이 게시물을 등록하려고 할 때 발생
     */
    @Override
    @Transactional
    public Long boardRegister(int corrPoint, String corrTitle, String corrContent, MultipartFile corrPdf, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 userId"));
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(user.getUserId());
        Payment payment = paymentRepository.findByPersonalUser(personalUser);

        if (user.getUserType() != UserType.PERSONAL) {
            throw new IllegalArgumentException("개인 회원만 게시물을 등록할 수 있습니다.");
        }
        if (payment.getBerryPoint() < corrPoint) {
            throw new IllegalArgumentException("보유한 베리 포인트가 부족합니다.");
        } else {
            payment.deductionBoardBerryPoint(corrPoint);
            paymentRepository.save(payment);
        }

        String corrPdfUUID = UUID.randomUUID().toString();
        Map<String, String> corrPdfUrl = storageService.uploadFile(corrPdf, "CorrPdf", BUCKETNAME);

        Board board = Board.builder()
                .corrPoint(corrPoint)
                .corrTitle(corrTitle)
                .corrContent(corrContent)
                .corrPdfUuid(corrPdfUUID)
                .corrPdfFileName(corrPdf.getOriginalFilename())
                .corrPdfUrl(corrPdfUrl.get("fileUrl"))
                .corrPdfFileName(corrPdf.getOriginalFilename())
                .corrPdfUuid(corrPdfUUID)
                .corrView(0)
                .user(user)
                .build();

        boardRepository.save(board);

        return board.getCorrId();
    }

    /**
     * [등록, 수정] 게시물 작성자 프로필 카드 조회
     * (게시물 작성 시 사용자 프로필 정보를 조회하여 DTO로 반환)
     *
     * @param userId 사용자 ID
     * @return BoardProfileCardDTO 조회된 사용자 프로필 정보
     * @throws IllegalArgumentException 회원 ID가 없거나, 개인 사용자 프로필이 없을 때 발생
     */
    @Override
    @Transactional
    public BoardProfileCardDTO registerUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 userId"));
        PersonalUser personalUser = user.getPersonalUser();
        if (personalUser == null) {
            throw new IllegalArgumentException("개인 사용자 프로필이 없습니다.");
        }
        List<String> jobNames = jobPositionRepository.findByPersonalUser(personalUser).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        Payment payment = paymentRepository.findByPersonalUser(personalUser);
        List<String> techUrls = techStackRepository.findByPersonalUser(personalUser).stream()
                .map(techStack -> techStack.getTech().getTechUrl())
                .collect(Collectors.toList());

        BoardProfileCardDTO boardProfileCardDTO = BoardProfileCardDTO.builder()
                .userId(user.getUserId())
                .personalId(personalUser.getPersonalId())
                .jobName(String.join(",", jobNames))
                .gender(personalUser.getGender())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .berryPoint(payment.getBerryPoint())
                .userIntro(user.getUserIntro())
                .techUrl(String.join(",", techUrls))
                .build();

        return boardProfileCardDTO;
    }

    /**
     * 게시물 조회
     *
     * @param corrId 게시물 ID
     * @return BoardDTO 조회된 게시물 정보
     */
    @Override
    public BoardDTO boardReadOne(Long corrId) {
        Optional<Board> result = boardRepository.findById(corrId);
        Board board = result.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 corrId"));
        User user = board.getUser();
        PersonalUser personalUser = user.getPersonalUser();
        boolean isCommentSelected = commentRepository.existsByBoardCorrIdAndCommSelection(board.getCorrId(), true);

        return BoardDTO.builder()
                .corrId(board.getCorrId())
                .corrPoint(board.getCorrPoint())
                .corrTitle(board.getCorrTitle())
                .corrContent(board.getCorrContent())
                .corrPdfFileName(board.getCorrPdfFileName())
                .corrPdfUrl(board.getCorrPdfUrl())
                .corrPdfUuid(board.getCorrPdfUuid())
                .corrView(board.getCorrView())
                .corrModify(board.getCorrModify())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .commSelection(isCommentSelected)
                .build();
    }

    /**
     * [조회] 게시물 작성자 프로필 카드 조회
     * (게시물 조회 시 사용자 프로필 정보를 조회하여 DTO로 반환)
     *
     * @param corrId 게시물 ID
     * @return BoardProfileCardDTO 게시물 작성자의 프로필 카드 정보
     * @throws IllegalArgumentException 게시물을 찾을 수 없을 때 발생
     */
    @Override
    public BoardProfileCardDTO boardUserProfile(Long corrId) {
        Board board = boardRepository.findById(corrId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 corrId"));
        User user = board.getUser();
        PersonalUser personalUser = user.getPersonalUser();
        Payment payment = paymentRepository.findByPersonalUser(personalUser);
        List<String> jobNames = jobPositionRepository.findByPersonalUser(personalUser).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        List<String> techUrls = techStackRepository.findByPersonalUser(personalUser).stream()
                .map(techStack -> techStack.getTech().getTechUrl())
                .collect(Collectors.toList());

        BoardProfileCardDTO boardProfileCardDTO = BoardProfileCardDTO.builder()
                .userId(user.getUserId())
                .personalId(personalUser.getPersonalId())
                .corrPoint(Long.valueOf(board.getCorrPoint()))
                .jobName(String.join(",", jobNames))
                .gender(personalUser.getGender())
                .userName(user.getUserName())
                .berryPoint(payment.getBerryPoint())
                .personalCareer(personalUser.getPersonalCareer())
                .userIntro(user.getUserIntro())
                .techUrl(String.join(",", techUrls))
                .build();

        return boardProfileCardDTO;
    }

    /**
     * HELP존 리스트 정렬(최신순, 조회순, 베리순)
     *
     * @param filterType 정렬 기준 (latest: 최신순, views: 조회순, berry: 베리순)
     * @param pageRequestDTO 페이지 요청 정보 (페이지 번호 및 크기)
     * @param showSelect 채택된 게시물 제외 여부
     * @return PageResponseDTO<BoardDTO> 페이지 응답 DTO
     */
    @Override
    public PageResponseDTO<BoardDTO> boardFilter(String filterType, PageRequestDTO pageRequestDTO, boolean showSelect) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize());
        Page<Board> result;

        if (showSelect) {
            switch (filterType) {
                case "latest":
                    result = boardRepository.findAllByOrderByCorrCreateDescAndCommSelectionFalse(pageable);
                    break;
                case "views":
                    result = boardRepository.findAllByOrderByCorrViewDescAndCommSelectionFalse(pageable);
                    break;
                case "berry":
                    result = boardRepository.findAllByOrderByCorrPointDescAndCommSelectionFalse(pageable);
                    break;
                default:
                    throw new IllegalArgumentException("유효하지 않은 filter type: " + filterType);
            }
        } else {
            switch (filterType) {
                case "latest":
                    result = boardRepository.findAllByOrderByCorrCreateDesc(pageable);
                    break;
                case "views":
                    result = boardRepository.findAllByOrderByCorrViewDesc(pageable);
                    break;
                case "berry":
                    result = boardRepository.findAllByOrderByCorrPointDesc(pageable);
                    break;
                default:
                    throw new IllegalArgumentException("유효하지 않은 filter type: " + filterType);
            }
        }

        List<BoardDTO> dtoList = result.stream()
                .map(board -> {
                    User user = board.getUser();
                    PersonalUser personalUser = user.getPersonalUser();
                    boolean isCommentSelected = commentRepository.existsByBoardCorrIdAndCommSelection(board.getCorrId(), true);

                    return BoardDTO.builder()
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .corrTitle(board.getCorrTitle())
                            .corrContent(board.getCorrContent())
                            .corrPdfUuid(board.getCorrPdfUuid())
                            .corrPdfFileName(board.getCorrPdfFileName())
                            .corrPdfUrl(board.getCorrPdfUrl())
                            .corrView(board.getCorrView())
                            .corrModify(board.getCorrModify())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .commSelection(isCommentSelected)
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    /**
     * 게시물 수정
     *
     * @param corrId            게시물 ID
     * @param userId            사용자 ID
     * @param corrTitle         게시물 제목
     * @param corrContent       게시물 내용
     * @param corrPdf           첨부 파일 (MultipartFile 형태, 선택사항)
     * @param existingFileName  기존 파일 이름 (선택사항, 새로운 파일이 업로드되지 않은 경우 사용)
     * @return Long             수정된 게시물 ID
     * @throws IllegalArgumentException 유효하지 않은 corrId이거나 작성자가 아닐 경우 발생
     */
    @Override
    @Transactional
    public Long boardModify(Long corrId, Long userId, String corrTitle, String corrContent, MultipartFile corrPdf, String existingFileName) {
        Board board = boardRepository.findById(corrId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 corrId"));

        if (!board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        String corrPdfUUID = board.getCorrPdfUuid();
        String corrPdfFileName = board.getCorrPdfFileName();
        String corrPdfUrl = board.getCorrPdfUrl();

        if (corrPdf != null && !corrPdf.isEmpty()) {
            corrPdfUUID = UUID.randomUUID().toString();
            Map<String, String> uploadedFile = storageService.uploadFile(corrPdf, "CorrPdf", BUCKETNAME);
            corrPdfFileName = corrPdf.getOriginalFilename();
            corrPdfUrl = uploadedFile.get("fileUrl");
        } else if (existingFileName != null) {
            corrPdfFileName = existingFileName;
        }

        board.change(corrTitle, corrContent, corrPdfUUID, corrPdfFileName, corrPdfUrl);

        boardRepository.save(board);

        return board.getCorrId();
    }

    /**
     * 게시물 삭제
     *
     * @param userId 사용자 ID
     * @param corrId 게시물 ID
     * @throws IllegalArgumentException 유효하지 않은 corrId이거나 작성자가 아닐 경우 발생
     */
    @Override
    @Transactional
    public void boardDelete(Long userId, Long corrId) {
        Optional<Board> result = boardRepository.findById(corrId);
        Board board = result.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 corrId"));

        if (board.getUser() == null || !board.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        boardRepository.deleteById(corrId);
    }

    /**
     * 정보 추가
     *
     * @param board 게시물 객체
     * @return BoardDTO 게시물 정보
     */
    @Override
    public BoardDTO boardUserRead(Board board) {
        User user = userRepository.findById(board.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 없습니다."));
        PersonalUser personalUser = personalUserRepository.findById(board.getUser().getPersonalUser().getPersonalId())
                .orElseThrow(() -> new IllegalArgumentException("개인 회원 ID가 없습니다."));

        return BoardDTO.builder()
                .corrId(board.getCorrId())
                .corrPoint(board.getCorrPoint())
                .corrTitle(board.getCorrTitle())
                .corrContent(board.getCorrContent())
                .corrPdfFileName(board.getCorrPdfFileName())
                .corrPdfUrl(board.getCorrPdfUrl())
                .corrPdfUuid(board.getCorrPdfUuid())
                .corrView(board.getCorrView())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .personalCareer(personalUser.getPersonalCareer())
                .build();
    }

    /**
     * 게시물 조회수 증가
     *
     * @param userId  사용자 ID
     * @param corrId  게시물 ID
     * @throws IllegalArgumentException 게시물 ID가 없을 때 발생
     */
    @Override
    @Transactional
    public void boardViewCount(Long userId, Long corrId) {
        Board board = boardRepository.findById(corrId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 ID가 없습니다."));

        if (board.getUser() == null || !board.getUser().getUserId().equals(userId)) {
            boardRepository.boardViewCount(corrId);
        }
    }

    @Override
    public List<BoardDTO> userReadAll(Long userId) {
        List<Board> boards = boardRepository.findByUserUserId(userId);
        User userCheck = userRepository.findByUserId(userId);
        PersonalUser personalUserCheck = userCheck.getPersonalUser();
        if (personalUserCheck == null){
            throw new PersonalNotFoundException("Personal User Not Found");
        }
        return boards.stream()
                .map(board -> {
                    User user = board.getUser();
                    PersonalUser personalUser = user.getPersonalUser();
                    boolean isCommentSelected = commentRepository.existsByBoardCorrIdAndCommSelection(board.getCorrId(), true);

                    return BoardDTO.builder()
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .corrTitle(board.getCorrTitle())
                            .corrContent(board.getCorrContent())
                            .corrPdfUuid(board.getCorrPdfUuid())
                            .corrPdfFileName(board.getCorrPdfFileName())
                            .corrPdfUrl(board.getCorrPdfUrl())
                            .corrView(board.getCorrView())
                            .commSelection(isCommentSelected)
                            .corrModify(board.getCorrModify())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .commentList(null)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
