package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_studies")
@Getter
@Setter
public class CaseStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_study_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private CustomerRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "client_name_display")
    private String clientNameDisplay;

    @Column(name = "challenge", columnDefinition = "TEXT")
    private String challenge;

    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "image_gallery", columnDefinition = "TEXT")
    private String imageGallery;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isFeatured == null) {
            isFeatured = false;
        }
        if (isPublished == null) {
            isPublished = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
