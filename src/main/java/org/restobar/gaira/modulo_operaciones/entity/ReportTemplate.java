package org.restobar.gaira.modulo_operaciones.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_templates", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Usuario owner;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "report_type", nullable = false, length = 80)
    private String reportType;

    @Column(name = "selected_fields", nullable = false, columnDefinition = "JSONB")
    private String selectedFields;

    @Builder.Default
    @Column(name = "filters", nullable = false, columnDefinition = "JSONB")
    private String filters = "[]";

    @Column(name = "sort_field", length = 80)
    private String sortField;

    @Builder.Default
    @Column(name = "sort_order", length = 4)
    private String sortOrder = "asc";

    @Builder.Default
    @Column(name = "is_shared", nullable = false)
    private Boolean isShared = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
