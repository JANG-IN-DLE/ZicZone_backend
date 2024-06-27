package org.zerock.ziczone.domain.application;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    @Column(length = 100, nullable = false)
    private String resumeName;

    @Column(length = 100, nullable = false)
    private String resumeDate;

    @Column(length = 100, nullable = false)
    private String phoneNum;

    @Column(length = 255)
    private String resumePhoto;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime resumeCreate;

    @UpdateTimestamp
    @Column(nullable = false, updatable = true)
    private LocalDateTime resumeUpdate;

    @Column(length = 255)
    private String personalState;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;
}
