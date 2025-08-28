package com.wanli.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 课程实体类
 */
@Entity
@Table(name = "courses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_level", nullable = false)
    private GradeLevel gradeLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)
    private Subject subject;

    // createdBy字段已在BaseEntity中定义，这里不需要重复定义
    
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;
    
    @Column(name = "course_id", nullable = false)
    private UUID courseId;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "lesson_count")
    private Integer lessonCount = 0;

    /**
     * 多对一关系：课程所属机构
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    /**
     * 课程和班级的多对多关系
     */
    @ManyToMany(mappedBy = "courses")
    private Set<ClassEntity> classes = new HashSet<>();

    /**
     * 年级枚举
     */
    public enum GradeLevel {
        GRADE_1("一年级"),
        GRADE_2("二年级"),
        GRADE_3("三年级"),
        GRADE_4("四年级"),
        GRADE_5("五年级"),
        GRADE_6("六年级");

        private final String displayName;

        GradeLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 学科枚举
     */
    public enum Subject {
        CHINESE("语文"),
        MATH("数学"),
        ENGLISH("英语");

        private final String displayName;

        Subject(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}