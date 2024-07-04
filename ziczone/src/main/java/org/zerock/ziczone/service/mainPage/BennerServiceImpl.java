package org.zerock.ziczone.service.mainPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.Benner;
import org.zerock.ziczone.dto.BennerDTO;
import org.zerock.ziczone.repository.BennerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BennerServiceImpl implements BennerService {


    private final BennerRepository bennerRepository;

    /**
     * toDTO 형변환
     * @param benner
     * @return
     */
    private BennerDTO convertToDTO(Benner benner) {
        return BennerDTO.builder()
                .bennerId(benner.getBennerId())
                .bennerImg(benner.getBennerImg())
                .bennerText(benner.getBennerText())
                .bennerUrl(benner.getBennerUrl())
                .build();
    }

    /**
     * toEntity 형변환
     * @param bennerDTO
     * @return
     */
    private Benner convertToEntity(BennerDTO bennerDTO) {
        return Benner.builder()
                .bennerId(bennerDTO.getBennerId())
                .bennerImg(bennerDTO.getBennerImg())
                .bennerText(bennerDTO.getBennerText())
                .bennerUrl(bennerDTO.getBennerUrl())
                .build();
    }

    /**
     * List<DTO>로 형변환
     * @return List
     */
    public List<BennerDTO> getAllBenners(){
        List<Benner> benners = bennerRepository.findAll();
        return benners.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 베너 조회
     * @param id
     * @return BennerDTO
     */
    @Override
    public BennerDTO getBenner(Long id) {
        Benner benner = bennerRepository.findById(id).orElseThrow(()-> new RuntimeException("Benner not Found"));
        return convertToDTO(benner);
    }

    /**
     * 베너 생성
     * @param bennerDTO
     * @return saveBennerDTO
     */
    @Override
    public BennerDTO createBenner(BennerDTO bennerDTO) {
        Benner benner = convertToEntity(bennerDTO);
        Benner savedBenner = bennerRepository.save(benner);
        return convertToDTO(savedBenner);
    }

    /**
     * 베너 업데이트
     * @param id
     * @param bennerDTO
     * @return updatedBennerDTO
     */
    @Override
    public BennerDTO updateBenner(Long id, BennerDTO bennerDTO) {
        Benner existingBenner = bennerRepository.findById(id).orElseThrow(()-> new RuntimeException("Benner not Found"));
        Benner updatedBenner = Benner.builder()
                .bennerId(existingBenner.getBennerId())
                .bennerImg(bennerDTO.getBennerImg())
                .bennerText(bennerDTO.getBennerText())
                .bennerUrl(bennerDTO.getBennerUrl())
                .build();
                bennerRepository.save(updatedBenner);
        return convertToDTO(updatedBenner);
    }

    /**
     * 선택 베너 삭제
     * @param id
     */
    @Override
    public void deleteBenner(Long id) {
        bennerRepository.deleteById(id);
    }


}
