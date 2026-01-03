package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CaseStudyDTO {
    private Long id;
    private Long requestId;
    private String requestCode;
    private Long serviceTypeId;
    private String serviceTypeName;
    private String title;
    private String slug;
    private String clientNameDisplay;
    private String challenge;
    private String solution;
    private String result;
    private String thumbnailUrl;
    private String imageGallery;
    private Boolean isFeatured;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
