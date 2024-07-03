package org.zerock.ziczone.domain.member;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;


    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String userName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime userCreate;

    @Column(length = 255, nullable = false)
    private String userIntro;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PersonalUser personalUser;
}
