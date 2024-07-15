package org.zerock.ziczone.domain.alarm;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlarmContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmContentId;    // id

    @Column(length = 100)
    private String alarmType;       // 알림 유형

    @Column
    private Long senderId;      //보내는 사람 Id

}
