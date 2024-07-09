package org.zerock.ziczone.domain.tech;

import com.jayway.jsonpath.internal.function.text.Length;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Tech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long techId;  //id

    @Column(length = 2048)
    private String techUrl; // 기술스택 이미지 경로(스토리지)

    @Column(length = 100)
    private String techName; // 기술 스택 이름
}
