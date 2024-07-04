package org.zerock.ziczone.service.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.tech.Tech;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.repository.tech.TechRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class JoinServiceImpl implements JoinService {

    private final TechRepository techRepository;

//  pickzone Job 데이터 가져오는 메서드
    @Override
    public List<TechDTO> getAllTechs() {
        List<Tech> techs = techRepository.findAll();
        return techs.stream()
                .map(tech -> TechDTO.builder()
                        .techId(tech.getTechId())
                        .techName(tech.getTechName())
                        .techUrl(tech.getTechUrl())
                        .build())
                .collect(Collectors.toList());

    }

}
