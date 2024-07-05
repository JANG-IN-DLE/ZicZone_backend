package org.zerock.ziczone.service.join;

import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.dto.join.TechDTO;

import java.util.List;

public interface JoinService {

    //회원가입시 기술스택선택에 필요
    List<TechDTO> getAllTechs();

    //개인회원가입
    String personalJoin(PersonalUserDTO personalUserDTO);

}
