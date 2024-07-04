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
    private Long bennerId;

    @Column(nullable = false)
    private String bennerImg;

    @Column(nullable = false)
    private String bennerText;

    @Column(nullable = false)
    private String bennerUrl;

}
