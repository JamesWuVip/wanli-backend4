package com.wanli.repository;

import com.wanli.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 课程数据访问层
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * 根据课程代码查找课程
     */
    Optional<Course> findByCourseCode(String courseCode);

    /**
     * 根据条件分页查询课程
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(:gradeLevel IS NULL OR c.gradeLevel = :gradeLevel) AND " +
           "(:subject IS NULL OR c.subject = :subject) AND " +
           "(:search IS NULL OR c.title LIKE :search OR c.description LIKE :search) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive) " +
           "ORDER BY c.createdAt DESC")
    Page<Course> findCoursesWithFilters(
            @Param("gradeLevel") Course.GradeLevel gradeLevel,
            @Param("subject") Course.Subject subject,
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    /**
     * 检查课程名称是否已存在
     */
    boolean existsByTitle(String title);

    /**
     * 根据创建者查找课程
     */
    Page<Course> findByCreatedBy(UUID createdBy, Pageable pageable);

    /**
     * 统计激活课程数量
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.isActive = true")
    long countActiveCourses();

    /**
     * 根据班级ID列表查询课程
     */
    @Query("SELECT DISTINCT c FROM Course c " +
           "JOIN c.classes cl " +
           "WHERE cl.id IN :classIds AND c.isActive = true")
    Page<Course> findByClassIds(@Param("classIds") List<UUID> classIds, Pageable pageable);
}