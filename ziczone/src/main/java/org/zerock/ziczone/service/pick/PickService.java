package org.zerock.ziczone.service.pick;

import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.dto.pick.*;

import java.util.List;

public interface PickService {

    List<PickCardDTO> getPickCards();
    List<PickCardDTO> getPersonalPickCards(Long loggedInUserId);
    List<PickCardDTO> getCompanyPickCards(Long loggedInUserId);
    List<PickJobDTO> getAllJobs();
    PickDetailDTO getPickCardsById(Long loggedInUserId, Long personalId);
    PickPersonalDetailDTO getPickCardsByPersonalId(Long loggedInUserId, Long personalId);
    PickResumeDTO getResumeById(Long personalId);
    boolean handlePayment(OpenCardDTO openCardDTO);
    PickAndScrapDTO scrapUser(PickAndScrapDTO pickAndScrapDTO);
    PickAndScrapDTO pickUser(PickAndScrapDTO pickAndScrapDTO);
}
