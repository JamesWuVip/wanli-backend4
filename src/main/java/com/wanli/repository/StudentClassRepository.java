package com.wanli.repository;

import com.wanli.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 学生班级关联Repository
 */
@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, UUID> {

    /**
     * 根据学生ID查找所有激活的班级关联
     */
    @Query("SELECT sc FROM StudentClass sc WHERE sc.studentId = :studentId AND sc.isActive = true")
    List<StudentClass> findActiveByStudentId(@Param("studentId") UUID studentId);

    /**
     * 根据班级ID查找所有激活的学生关联
     */
    @Query("SELECT sc FROM StudentClass sc WHERE sc.classId = :classId AND sc.isActive = true")
    List<StudentClass> findActiveByClassId(@Param("classId") UUID classId);

    /**
     * 根据学生ID获取所有激活班级的ID列表
     */
    @Query("SELECT sc.classId FROM StudentClass sc WHERE sc.studentId = :studentId AND sc.isActive = true")
    List<UUID> findActiveClassIdsByStudentId(@Param("studentId") UUID studentId);

    /**
     * 检查学生是否在指定班级中
     */
    @Query("SELECT COUNT(sc) > 0 FROM StudentClass sc WHERE sc.studentId = :studentId AND sc.classId = :classId AND sc.isActive = true")
    boolean existsByStudentIdAndClassId(@Param("studentId") UUID studentId, @Param("classId") UUID classId);

    /**
     * 根据学生ID统计激活的班级数量
     */
    @Query("SELECT COUNT(sc) FROM StudentClass sc WHERE sc.studentId = :studentId AND sc.isActive = true")
    long countActiveByStudentId(@Param("studentId") UUID studentId);
}