package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "client_todos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "assigned_to")
    private String assignedTo; // e.g. "Everybody", username, or user email

    @Column(name = "created_by")
    private String createdBy; // email or username of the logged-in creator

    @Column(name = "priority")
    private String priority; // "Low", "Medium", "High"

    @Column(name = "product_service")
    private String productService;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "is_done")
    @Builder.Default
    private Boolean done = false;

    @Column(name = "hours")
    @Builder.Default
    private Double hours = 0.0;

    @Column(name = "unbilled_hours")
    @Builder.Default
    private Double unbilled = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.assignedTo == null || this.assignedTo.isBlank()) {
            this.assignedTo = "Everybody";
        }
        if (this.priority == null || this.priority.isBlank()) {
            this.priority = "Low";
        }
        if (this.done == null) {
            this.done = false;
        }
        if (this.hours == null) {
            this.hours = 0.0;
        }
        if (this.unbilled == null) {
            this.unbilled = 0.0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}