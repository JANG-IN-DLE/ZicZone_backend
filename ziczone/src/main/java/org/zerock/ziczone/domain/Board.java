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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long corrId;

    @Column(length = 100, nullable = false)
    private String corrTitle;

    @Column(length = 255, nullable = false)
    private String corrContent;

    @Column(length = 100, nullable = false)
    private String corrPdf;

    @Column(nullable = false)
    private Integer corrPoint;

    private Integer corrView;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime corrCreate;

    @UpdateTimestamp
    @Column(nullable = false, updatable = true)
    private LocalDateTime corrModify;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
