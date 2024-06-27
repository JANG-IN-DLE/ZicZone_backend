package org.zerock.ziczone.service.pick;

import org.zerock.ziczone.dto.pick.PickCardDTO;

import java.util.List;

public interface PickService {

    List<PickCardDTO> getPickCards();
}
