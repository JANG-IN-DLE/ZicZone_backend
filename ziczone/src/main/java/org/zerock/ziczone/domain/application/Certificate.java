package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certId;

    @Column(length = 100)
    private String cert;

    @Column(length = 100)
    private String certDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id")
    private Resume resume;
    
}
