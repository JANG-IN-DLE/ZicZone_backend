package org.zerock.ziczone.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Etc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long etcId;

    @Column(length = 100)
    private String etcContent;

    @Column(length = 100)
    private String etcDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id")
    private Resume resume;
}
