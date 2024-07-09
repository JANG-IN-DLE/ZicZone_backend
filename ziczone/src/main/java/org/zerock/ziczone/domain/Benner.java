package org.zerock.ziczone.domain;

import lombok.*;
import org.zerock.ziczone.dto.BennerDTO;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Benner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bennerId; // id

    @Column(nullable = false, length = 2048)
    private String bennerImg; // 스토리지 이미지경로

    @Column(nullable = false)
    private String bennerText; // 베너 문구

    @Column(nullable = false , length = 2048)
    private String bennerUrl; // 베너 클릭시 사용할 URL 주소

}
