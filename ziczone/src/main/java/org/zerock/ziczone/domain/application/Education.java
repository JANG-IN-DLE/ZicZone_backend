package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eduId;

    @Column(length = 100, nullable = false)
    private String edu;

    @Column(length = 100)
    private String credit;

    @Column(length = 100)
    private String eduDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id")
    private Resume resume;

}
