package org.zerock.ziczone.service.mainPage;

import org.zerock.ziczone.domain.Benner;
import org.zerock.ziczone.dto.BennerDTO;
import org.zerock.ziczone.dto.mypage.UserDTO;

import java.util.List;

public interface BennerService {
    List<BennerDTO> getAllBenners();
    BennerDTO getBenner(Long id);
    BennerDTO createBenner(BennerDTO bennerDTO);
    BennerDTO updateBenner(Long id, BennerDTO bennerDTO);
    void deleteBenner(Long id);


}
