package com.wanli.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 班级实体
 */
@Entity
@Table(name = "classes")
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE classes SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ClassEntity extends BaseEntity {

    /**
     * 班级名称
     */
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    /**
     * 班级代码
     */
    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    /**
     * 年级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "grade_level", nullable = false)
    private GradeLevel gradeLevel;

    /**
     * 班级描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 是否激活
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 班主任ID
     */
    @Column(name = "head_teacher_id")
    private UUID headTeacherId;

    /**
     * 学年
     */
    @Column(name = "academic_year", nullable = false, length = 10)
    private String academicYear;

    /**
     * 学期
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Semester semester;

    /**
     * 班级和课程的多对多关系
     */
    @ManyToMany
    @JoinTable(
            name = "class_course",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    /**
     * 年级枚举
     */
    public enum GradeLevel {
        GRADE_1, GRADE_2, GRADE_3, GRADE_4, GRADE_5, GRADE_6,
        GRADE_7, GRADE_8, GRADE_9, GRADE_10, GRADE_11, GRADE_12
    }

    /**
     * 学期枚举
     */
    public enum Semester {
        SPRING, FALL
    }
}