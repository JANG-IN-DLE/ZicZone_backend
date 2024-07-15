package org.zerock.ziczone.service.pick;

import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.dto.pick.*;

import java.util.List;

public interface PickService {

    List<PickCardDTO> getPickCards();
    List<PickCardDTO> getPersonalPickCards(Long loggedInPersonalId);
    List<PickCardDTO> getCompanyPickCards(Long loggedInCompanyId);
    List<PickJobDTO> getAllJobs();
    PickDetailDTO getPickCardsById(Long companyId, Long personalId);
    PickPersonalDetailDTO getPickCardsByPersonalId(Long loggedInPersonalId, Long personalId);
    PickResumeDTO getResumeById(Long personalId);
    boolean handlePayment(OpenCardDTO openCardDTO);
    PickAndScrapDTO scrapUser(PickAndScrapDTO pickAndScrapDTO);
    PickAndScrapDTO pickUser(PickAndScrapDTO pickAndScrapDTO);
}
