package org.zerock.ziczone.domain.tech;

import lombok.*;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTechId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tech_id")
    private Tech tech;
}
