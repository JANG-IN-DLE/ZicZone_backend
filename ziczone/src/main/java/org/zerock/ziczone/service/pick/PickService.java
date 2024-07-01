package org.zerock.ziczone.service.pick;

import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
import org.zerock.ziczone.dto.pick.PickResumeDTO;

import java.util.List;

public interface PickService {

    List<PickCardDTO> getPickCards();
    List<PickJobDTO> getAllJobs();
    PickCardDTO getPickCardsById(Long personalId);
    PickResumeDTO getResumeById(Long personalId);
}
