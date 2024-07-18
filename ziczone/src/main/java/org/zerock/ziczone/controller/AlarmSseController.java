package org.zerock.ziczone.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.dto.Alarm.RequestAlarmDTO;
import org.zerock.ziczone.dto.Alarm.ResponseAlarmDTO;
import org.zerock.ziczone.service.alarm.AlarmService;
import org.zerock.ziczone.service.login.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@Log4j2
public class AlarmSseController {

    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final JwtService jwtService;

    private final AlarmService alarmService;

    // 클라이언트 구독
    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable Long userId, @RequestParam("token") String token) {
        // 토큰 검증
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

    //알람보냄
    public void sendAlarm(Long userId, ResponseAlarmDTO responseAlarmDTO) {
        SseEmitter emitter = sseEmitters.get(userId);
        // 사용자가 존재하면
        if (emitter != null) {
            try {
                log.info("Sending alarm to user: {}", userId);
                // 'alarm'이벤트를 alarm데이터를 담아서 클라이언트로 전송
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(responseAlarmDTO)); //타입, sender, receiver, berry
            } catch (IOException e) {
                log.error("Error sending alarm to user: {}", userId, e);
                sseEmitters.remove(userId);
            }
        } else {
            log.warn("No SSE emitter found for user: {}", userId);
        }
    }

    // 알림요청
    // SELECTION : 게시글 작성자(게시물ID) / 댓글 작성자(회원ID)
    // COMMENT   : 댓글 작성자(게시물ID) / 게시글 작성자(회원ID)
    // PICK      : 기업 회원(회원ID) / 개인 회원(회원ID)
    // SCRAP     : 기업 회원(회원ID) / 개인 회원(회원ID)
    // BUYRESUME : 이력서 구매자(회원ID) / 이력서 소유자(회원ID)
    @PostMapping("/send")
    public ResponseEntity<ResponseAlarmDTO> Notification(@RequestBody RequestAlarmDTO alarm) {

        ResponseAlarmDTO responseAlarmDTO = alarmService.sendAlarm(alarm);

        sendAlarm(alarm.getReceiverId(), responseAlarmDTO); //프론트로 알림보냄

        return ResponseEntity.ok(responseAlarmDTO);
    }

    // 로그인시에 알람 불러오기
    @GetMapping("/initAlarm/{userId}")
    public ResponseEntity<List<ResponseAlarmDTO>> initAlarm(@PathVariable Long userId) {
        List<ResponseAlarmDTO> responseAlarmDTO = alarmService.AlarmList(userId);
        log.info("Initial alarm list : {}", responseAlarmDTO);

        return ResponseEntity.ok(responseAlarmDTO);
    }
}
