    package org.zerock.ziczone.dto.mypage;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.zerock.ziczone.domain.tech.TechStack;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class TechStackDTO {
        private Long userTechId;
        private TechDTO tech;

        public TechStack toEntity() {
            return TechStack.builder()
                    .userTechId(userTechId)
                    .tech(tech.toEntity())
                    .build();
        }

        public static TechStackDTO fromEntity(TechStack techStack) {
            return TechStackDTO.builder()
                    .userTechId(techStack.getUserTechId())
                    .tech(TechDTO.fromEntity(techStack.getTech()))
                    .build();
        }
    }