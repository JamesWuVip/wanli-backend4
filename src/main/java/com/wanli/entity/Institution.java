package com.wanli.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

/**
 * 教育机构实体
 */
@Entity
@Table(name = "institutions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE institutions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Institution extends BaseEntity {

    /**
     * 机构名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 注意：franchises表中只有name和status字段，其他字段已移除以匹配数据库结构

    /**
     * 机构状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InstitutionStatus status = InstitutionStatus.ACTIVE;

    /**
     * 多对一关系：机构创建者
     * 注意：created_by字段在BaseEntity中是String类型，不能直接作为UUID外键
     * 如果需要关联User实体，应该使用单独的UUID字段
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    // @JsonIgnore
    // private User createdByUser;

    /**
     * 一对多关系：机构管理的课程
     */
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    /**
     * 一对多关系：机构的学员
     */
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<User> students = new ArrayList<>();

}