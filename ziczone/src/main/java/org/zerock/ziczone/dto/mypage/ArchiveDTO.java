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
    private Long archId;
    private String archGit;
    private String archNotion;
    private String archBlog;

    // DTO to Entity
    public Archive toEntity() {
        return Archive.builder()
                .archId(this.archId)
                .archGit(this.archGit)
                .archNotion(this.archNotion)
                .archBlog(this.archBlog)
                .build();
    }

    // Entity to DTO
    public static ArchiveDTO fromEntity(Archive entity) {
        return ArchiveDTO.builder()
                .archId(entity.getArchId())
                .archGit(entity.getArchGit())
                .archNotion(entity.getArchNotion())
                .archBlog(entity.getArchBlog())
                .build();
    }
}
