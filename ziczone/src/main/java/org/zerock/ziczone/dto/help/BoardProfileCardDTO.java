package org.zerock.ziczone.dto.help;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardProfileCardDTO {
    private Long userId; // 게시물 작성자 회원 ID

    private Long personalId; // 게시물 작성자 개인 회원 ID

    private Long corrPoint; // 게시물 베리 포인트

    private String jobName; // 직무 이름

    private Gender gender; // 성별

    private String userName; // 게시물 작성자 이름

    private String personalCareer; // 경력

    private Integer berryPoint; // payment.berryPoint 아님

    private String userIntro; // 한줄 소개

    private String techUrl; // 기술 이름
}
