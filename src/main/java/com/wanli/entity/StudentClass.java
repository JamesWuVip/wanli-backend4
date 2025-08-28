package com.wanli.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 学生班级关联实体
 */
@Entity
@Table(name = "student_classes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "class_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE student_classes SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class StudentClass extends BaseEntity {

    /**
     * 学生ID
     */
    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    /**
     * 班级ID
     */
    @Column(name = "class_id", nullable = false)
    private UUID classId;

    /**
     * 加入时间
     */
    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    /**
     * 离开时间（可选）
     */
    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    /**
     * 是否激活
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 学生实体关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    /**
     * 班级实体关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
}