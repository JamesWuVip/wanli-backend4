package com.wanli.service;

import com.wanli.dto.CourseCreateDto;
import com.wanli.dto.CourseResponseDto;
import com.wanli.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 创建课程
     */
    CourseResponseDto createCourse(CourseCreateDto courseCreateDto, UUID createdBy);

    /**
     * 根据条件分页查询课程
     */
    Page<CourseResponseDto> getCourses(
            Course.GradeLevel gradeLevel,
            Course.Subject subject,
            String search,
            Boolean isActive,
            Pageable pageable
    );

    /**
     * 根据ID获取课程详情
     */
    CourseResponseDto getCourseById(UUID courseId);

    /**
     * 更新课程信息
     */
    CourseResponseDto updateCourse(UUID courseId, CourseCreateDto courseCreateDto, UUID updatedBy);

    /**
     * 删除课程（软删除）
     */
    void deleteCourse(UUID courseId, UUID deletedBy);

    /**
     * 激活/停用课程
     */
    CourseResponseDto toggleCourseStatus(UUID courseId, Boolean isActive, UUID updatedBy);

    /**
     * 检查课程是否存在
     */
    boolean existsByCourseId(UUID courseId);

    /**
     * 生成课程代码
     */
    String generateCourseCode();

    /**
     * 根据学生ID获取其班级的课程列表
     */
    Page<CourseResponseDto> getCoursesByStudentId(UUID studentId, Pageable pageable);
}