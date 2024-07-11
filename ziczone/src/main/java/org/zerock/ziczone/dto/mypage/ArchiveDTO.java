package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Archive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveDTO {
    private Long arch_id;
    private String arch_git;
    private String arch_notion;
    private String arch_blog;

    // DTO to Entity
    public Archive toEntity() {
        return Archive.builder()
                .archId(this.arch_id)
                .archGit(this.arch_git)
                .archNotion(this.arch_notion)
                .archBlog(this.arch_blog)
                .build();
    }

    // Entity to DTO
    public static ArchiveDTO fromEntity(Archive entity) {
        return ArchiveDTO.builder()
                .arch_id(entity.getArchId())
                .arch_git(entity.getArchGit())
                .arch_notion(entity.getArchNotion())
                .arch_blog(entity.getArchBlog())
                .build();
    }
}
