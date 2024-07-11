package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Certificate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDTO {
    private Long cert_id;
    private String cert;
    private String cert_date;

    // DTO to Entity
    public Certificate toEntity() {
        return Certificate.builder()
                .certId(this.cert_id)
                .cert(this.cert)
                .certDate(this.cert_date)
                .build();
    }

    // Entity to DTO
    public static CertificateDTO fromEntity(Certificate entity) {
        return CertificateDTO.builder()
                .cert_id(entity.getCertId())
                .cert(entity.getCert())
                .cert_date(entity.getCertDate())
                .build();
    }

}
