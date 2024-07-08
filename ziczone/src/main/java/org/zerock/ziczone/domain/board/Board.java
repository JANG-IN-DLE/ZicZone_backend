package org.zerock.ziczone.domain.board;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(length = 500, nullable = false)
    private String corrContent;

    @Column(length = 2048, nullable = false)
    private String corrPdf;

    @Column(nullable = false)
    private Integer corrPoint;

    @Builder.Default
    @Column(nullable = false)
    private Integer corrView = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime corrCreate;

    @UpdateTimestamp
    @Column(nullable = false, updatable = true)
    private LocalDateTime corrModify;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public void change(String corrTitle, String corrContent, String corrPdf) {
        this.corrTitle = corrTitle;
        this.corrContent = corrContent;
        this.corrPdf = corrPdf;
    }
}
