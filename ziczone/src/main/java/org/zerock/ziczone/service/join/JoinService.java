package org.zerock.ziczone.service.join;

import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.join.TechDTO;

import java.util.List;

public interface JoinService {

    //회원가입시 기술스택선택에 필요
    List<TechDTO> getAllTechs();

    //개인회원가입
    String personalSignUp(PersonalUserJoinDTO personalUserJoinDTO);

    //기업회원가입
    String companyJoin(CompanyUserJoinDTO companyUserJoinDTO);

    //해당 이메일을 가진 유저가 있는지 검사
    User EmailDuplication(String email);
}
