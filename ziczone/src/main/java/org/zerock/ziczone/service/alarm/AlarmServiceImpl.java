package org.zerock.ziczone.service.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.domain.alarm.AlarmContent;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.Alarm.ResponseAlarmDTO;
import org.zerock.ziczone.repository.alarm.AlarmContentRepository;
import org.zerock.ziczone.repository.alarm.AlarmRepository;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.login.JwtService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    private final AlarmRepository alarmRepository;
    private final AlarmContentRepository alarmContentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private final JwtService jwtService;


    //구독
    @Override
    public SseEmitter subscribe(Long userId, String token) {
        if (!jwtService.validateToken(token, jwtService.extractUsername(token))) {
            throw new SecurityException("Invalid token");
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.put(userId, emitter);
        log.info("Subscribe to user id : " + userId);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));

        return emitter;
    }


    //로그이웃
    @Override
    public void logout(Long userId) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            emitter.complete();
            sseEmitters.remove(userId);
        }
    }


    //회원 이름 조회
    public String getUserName(Long id) {
        User user = userRepository.findByUserId(id);
        return user.getUserName();
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

    //유저이름 필터링
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
    public Alarm saveAlarm(String type, Long senderId, Long receiverId) {

        AlarmContent alarmContent = AlarmContent.builder()
                .alarmType(type)
                .senderId(senderId)
                .build();
        alarmContentRepository.save(alarmContent);

        Alarm alarm = Alarm.builder()
                .alarmContent(alarmContent)
                .alarmCreate(LocalDateTime.now())
                .readOrNot(false)
                .user(userRepository.findByUserId(receiverId))
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
                    .receiver(maskUserName(getUserName(userId)))
                    .getBerry(board.getCorrPoint())
                    .alarmCreate(alarm.getAlarmCreate())
                    .readOrNot(alarm.isReadOrNot())
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
                .readOrNot(alarm.isReadOrNot())
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
                .readOrNot(alarm.isReadOrNot())
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
                .readOrNot(alarm.isReadOrNot())
                .build();
    }

    //다른 컨트롤러에서 사용해야함
    //새로 생성된 알람
    @Override
    public ResponseAlarmDTO addAlarm(String type, Long senderId, Long receiverId) {

        Alarm savedAlarm = saveAlarm(type, senderId, receiverId);

        ResponseAlarmDTO responseAlarmDTO = createResponseAlarmDTO(savedAlarm, receiverId);

        if(sseEmitters.get(receiverId) != null){
            sendAlarm(receiverId, responseAlarmDTO);
        }

        return responseAlarmDTO;
    }

    public void sendAlarm(Long userId, ResponseAlarmDTO responseAlarmDTO) {
        SseEmitter emitter = sseEmitters.get(userId);
        // 사용자가 존재하면
        if (emitter != null) {
            try {
                log.info("Sending alarm to user: {}", userId);
                // 'alarm'이벤트를 alarm데이터를 담아서 클라이언트로 전송
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(responseAlarmDTO)); //타입, sender, receiver, berry, 읽음여부
            } catch (IOException e) {
                log.error("Error sending alarm to user: {}", userId, e);
                sseEmitters.remove(userId);
            }
        } else {
            log.warn("No SSE emitter found for user: {}", userId);
        }
    }

    @Override
    public void readAlarm(Long userId){
        List<Alarm> alarmList = alarmRepository.findByUser_UserId(userId);
// 알림의 readOrNot 값을 true로 변경
        List<Alarm> updatedAlarms = alarmList.stream().map(alarm ->
                Alarm.builder()
                        .alarmId(alarm.getAlarmId())
                        .alarmContent(alarm.getAlarmContent())
                        .alarmCreate(alarm.getAlarmCreate())
                        .readOrNot(true) // readOrNot을 true로 설정
                        .user(alarm.getUser())
                        .build()
        ).collect(Collectors.toList());

        // 변경된 알림 저장
        alarmRepository.saveAll(updatedAlarms);

    }
}
