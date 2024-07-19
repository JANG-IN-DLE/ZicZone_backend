package org.zerock.ziczone.service.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.domain.alarm.AlarmContent;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.Alarm.RequestAlarmDTO;
import org.zerock.ziczone.dto.Alarm.ResponseAlarmDTO;
import org.zerock.ziczone.repository.alarm.AlarmContentRepository;
import org.zerock.ziczone.repository.alarm.AlarmRepository;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.member.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final AlarmContentRepository alarmContentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    //회원 이름 조회
    public String getUserName(Long id) {
        User user = userRepository.findByUserId(id);
        return maskUserName(user.getUserName());
    }

    //게시글 제목 조회
    public String getPostName(Long id) {
        Optional<Board> board = boardRepository.findById(id);
// return boardRepository.findById(id)
                      // .map(Board::getCorrTitle)
                      // .orElse("Unknown Post");
        
        if (board.isPresent()) {
            return board.get().getCorrTitle();
        } else {
            // 게시글이 없을 경우 대체 텍스트 반환
            return "Unknown Post";
        }
    }


    public String maskUserName(String userName) {
        if(userName == null || userName.isEmpty()) {
            return "Unknown User";
        }

        int length = userName.length();

        //두글지 아하일 경우
        if(length == 1 || length == 2){
            return userName.charAt(0) + "*";
        } else{
            return userName.charAt(0) + "*" + userName.charAt(length - 1);
        }
    }
    //알림저장
    @Override
    public Alarm saveAlarm(RequestAlarmDTO requestAlarmDTO) {

        AlarmContent alarmContent = AlarmContent.builder()
                .alarmType(requestAlarmDTO.getType())
                .senderId(requestAlarmDTO.getSenderId())
                .build();
        alarmContentRepository.save(alarmContent);

        Alarm alarm = Alarm.builder()
                .alarmContent(alarmContent)
                .alarmCreate(LocalDateTime.now())
                .readOrNot(false)
                .user(userRepository.findByUserId(requestAlarmDTO.getReceiverId()))
                .build();
        alarmRepository.save(alarm);

        return alarm;
    }

    //초기에 보낼 알림 리스트
    @Override
    public List<ResponseAlarmDTO> AlarmList(Long userId) {
        List<Alarm> alarmList = alarmRepository.findByUser_UserId(userId);
        return alarmList.stream()
                .map(alarm -> createResponseAlarmDTO(alarm, userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    //ResponseAlarmDTO 만들기
    private ResponseAlarmDTO createResponseAlarmDTO(Alarm alarm, Long userId) {
        String alarmType = alarm.getAlarmContent().getAlarmType();
        return switch (alarmType) {
            case "SELECTION" -> createSelectionAlarmDTO(alarm, userId);
            case "COMMENT" -> createCommentAlarmDTO(alarm, userId);
            case "PICK", "SCRAP" -> createPickOrScrapAlarmDTO(alarm, userId);
            case "BUYRESUME" -> createBuyResumeAlarmDTO(alarm, userId);
            default -> {
                log.warn("Unknown alarm type: " + alarmType);
                yield null;
            }
        };
    }

    // 채택알림
    private ResponseAlarmDTO createSelectionAlarmDTO(Alarm alarm, Long userId) {
        Optional<Board> boardOpt = boardRepository.findById(alarm.getAlarmContent().getSenderId());
        if (boardOpt.isPresent()) {
            Board board = boardOpt.get();
            return ResponseAlarmDTO.builder()
                    .Type("SELECTION")
                    .sender(getPostName(alarm.getAlarmContent().getSenderId()))
                    .receiver(getUserName(userId))
                    .getBerry(board.getCorrPoint())
                    .alarmCreate(alarm.getAlarmCreate())
                    .build();
        } else {
            log.warn("Board not found for userId: " + userId);
            return null;
        }
    }

    // 댓글알림
    private ResponseAlarmDTO createCommentAlarmDTO(Alarm alarm, Long userId) {
        return ResponseAlarmDTO.builder()
                .Type("COMMENT")
                .sender(getPostName(alarm.getAlarmContent().getSenderId()))
                .receiver(getUserName(userId))
                .getBerry(null)
                .alarmCreate(alarm.getAlarmCreate())
                .build();
    }

    // pick or serap 알림
    private ResponseAlarmDTO createPickOrScrapAlarmDTO(Alarm alarm, Long userId) {
        return ResponseAlarmDTO.builder()
                .Type(alarm.getAlarmContent().getAlarmType())
                .sender(getUserName(alarm.getAlarmContent().getSenderId()))
                .receiver(getUserName(userId))
                .getBerry(null)
                .alarmCreate(alarm.getAlarmCreate())
                .build();
    }

    // 이력서 구매 알림
    private ResponseAlarmDTO createBuyResumeAlarmDTO(Alarm alarm, Long userId) {
        return ResponseAlarmDTO.builder()
                .Type("BUYRESUME")
                .sender(getUserName(alarm.getAlarmContent().getSenderId()))
                .receiver(getUserName(userId))
                .getBerry(50)
                .alarmCreate(alarm.getAlarmCreate())
                .build();
    }



    //새로 생성된 알람
    @Override
    public ResponseAlarmDTO sendAlarm(RequestAlarmDTO requestAlarmDTO) {

        Alarm savedAlarm = saveAlarm(requestAlarmDTO);

        return createResponseAlarmDTO(savedAlarm, savedAlarm.getUser().getUserId());
    }
}
