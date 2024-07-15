package org.zerock.ziczone.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.dto.Alarm.AlarmDTO;
import org.zerock.ziczone.service.alarm.AlarmService;
import org.zerock.ziczone.service.login.JwtService;

import java.io.IOException;
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
    @CrossOrigin(origins = "http://localhost:3000")
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
    public void sendAlarm(Long userId, AlarmDTO alarm) {
        SseEmitter emitter = sseEmitters.get(userId);
        // 사용자가 존재하면
        if (emitter != null) {
            try {
                log.info("Sending alarm to user: {}", userId);
                // 'alarm'이벤트를 alarm데이터를 담아서 클라이언트로 전송
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(alarm));
            } catch (IOException e) {
                log.error("Error sending alarm to user: {}", userId, e);
                sseEmitters.remove(userId);
            }
        } else {
            log.warn("No SSE emitter found for user: {}", userId);
        }
    }

    //알림요청
    @PostMapping("/send")
    public ResponseEntity<String> Notification(@RequestBody AlarmDTO alarm) {
        try {

            alarmService.saveAlarm(alarm); //알림내용 저장
            sendAlarm(alarm.getReceiverId(), alarm); //

            return ResponseEntity.ok("Notification sent to user: " + alarm.getReceiverId() + " SUCCESS");

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error sending notification: " + e.getMessage());
        }
    }
}
