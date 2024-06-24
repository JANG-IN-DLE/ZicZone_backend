package org.zerock.ziczone.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerId;

    @Column(length = 100)
    private String careerName;

    @Column(length = 100)
    private String careerJob;

    @Column(length = 100)
    private String careerPosition;

    @Column(length = 100)
    private String careerDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id")
    private Resume resume;

}
