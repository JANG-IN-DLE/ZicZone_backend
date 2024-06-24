package org.zerock.ziczone.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

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
    private Long alarmId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime alarmCreate;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean readOrNot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alarm_content_id")
    private AlarmContent alarmContent;



}
