package org.zerock.ziczone.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commId;

    private String commContent;

    private boolean commSelection;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime commCreate;

    @UpdateTimestamp
    @Column(nullable = false, updatable = true)
    private LocalDateTime commModify;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corr_id")
    private Board board;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
