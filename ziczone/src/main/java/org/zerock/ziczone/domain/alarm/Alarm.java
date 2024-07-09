package org.zerock.ziczone.domain.alarm;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.zerock.ziczone.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;                   // id

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime alarmCreate;      // 알람 생성 시간

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean readOrNot;              // 읽음(1), 읽지 않음(0)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                      // 유저 테이블

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_content_id")
    private AlarmContent alarmContent;      // 알람 내용 테이블


}
