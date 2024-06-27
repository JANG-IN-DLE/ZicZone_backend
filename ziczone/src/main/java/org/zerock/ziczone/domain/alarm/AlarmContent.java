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
    private Long alarmContentId;

    @Column(length = 100)
    private String alarmType;

}
