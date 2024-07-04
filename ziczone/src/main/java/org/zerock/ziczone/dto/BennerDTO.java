package org.zerock.ziczone.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Data
public class BennerDTO {
    private Long bennerId;
    private String bennerImg;
    private String bennerText;
    private String bennerUrl;

}
