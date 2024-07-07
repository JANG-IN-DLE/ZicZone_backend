package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalUserUpdateDTO {
    private String intro;
    private String personalCareer;
    private String personalUserPassword;
    private boolean isPersonalVisible;
    private boolean isCompanyVisible;
}
