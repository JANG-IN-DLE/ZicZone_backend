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
    private Long techId;

    @Column(length = 100)
    private String techName;

    @Column(length = 100)
    private String techUrl;
}
