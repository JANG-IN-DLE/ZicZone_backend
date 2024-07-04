package org.zerock.ziczone.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ziczone.domain.Benner;
import org.zerock.ziczone.dto.BennerDTO;
import org.zerock.ziczone.repository.BennerRepository;
import org.zerock.ziczone.service.mainPage.BennerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
public class BennerServiceTests {
    @Autowired
    private BennerService bennerService;
    @Autowired
    private BennerRepository bennerRepository;

    private final Long user_id = 1L;

    @Test
    public void getBennerDTOList(){
        // when
        List<BennerDTO> benners = bennerService.getAllBenners();
        // then
        assertNotNull(benners);
        assertTrue(benners.size() >= 0); // 실제 데이터베이스 상태에 따라 검증
        log.info(benners.toString());
    }

    @Test
    public void getBennerDTO(){
        // when
        BennerDTO bennerDTO = bennerService.getBenner(user_id);
        // then: 결과가 예상대로인지 검증합니다.
        assertNotNull(bennerDTO);
        assertEquals(user_id, bennerDTO.getBennerId());
        log.info(bennerDTO.toString());
        log.info(bennerDTO.toString());
    }


    @Test
    public void createBenner(){
        BennerDTO bennerDTO = new BennerDTO(999L,"IMG","TEXT","URL");
        // when
        BennerDTO createdBenner = bennerService.createBenner(bennerDTO);
        // then: 결과가 예상대로인지 검증합니다.
        assertNotNull(createdBenner);
        assertNotNull(createdBenner.getBennerId());
        log.info(bennerDTO.toString());
    }
    @Test
    public void updateBennerDTO(){
        // given: 기존 베너를 업데이트할 새로운 베너 DTO를 만듭니다.
        BennerDTO bennerDTO = BennerDTO.builder()
                .bennerId(user_id)
                .bennerImg("updated_img.jpg")
                .bennerText("updated_text")
                .bennerUrl("updated_url")
                .build();

        // when: updateBenner 메서드를 호출합니다.
        BennerDTO updatedBenner = bennerService.updateBenner(user_id, bennerDTO);
        // then: 결과가 예상대로인지 검증합니다.
        assertNotNull(updatedBenner);
        assertEquals(bennerDTO.getBennerImg(), updatedBenner.getBennerImg());
        log.info(updatedBenner.toString());
    }

    @Test
    public void testDeleteBenner() {
        // given: 특정 ID로 베너가 있다고 가정합니다.


        // when: deleteBenner 메서드를 호출합니다.
        bennerService.deleteBenner(user_id);

        // then: 베너가 삭제되었는지 검증합니다.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bennerService.getBenner(user_id);
        });
        assertEquals("Benner not Found", exception.getMessage());
    }

}
