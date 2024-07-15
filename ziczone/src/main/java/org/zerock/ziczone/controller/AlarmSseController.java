package org.zerock.ziczone.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.dto.Alarm.AlarmDTO;
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
        if (emitter != null) {
            try {
                log.info("Sending alarm to user: {}", userId);
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

    // 테스트 알림 전송
    @GetMapping("/test-notification/{userId}")
    public String testNotification(@PathVariable Long userId) {
        try {
            AlarmDTO alarm = new AlarmDTO();
            alarm.setMessage("Test notification");
            sendAlarm(userId, alarm);
            return "Notification sent";
        } catch (Exception e) {
            return "Error sending notification: " + e.getMessage();
        }
    }

}
