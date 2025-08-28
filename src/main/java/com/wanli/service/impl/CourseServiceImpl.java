package com.wanli.service.impl;

import com.wanli.dto.CourseCreateDto;
import com.wanli.dto.CourseResponseDto;
import com.wanli.entity.Course;
import com.wanli.exception.BusinessException;
import com.wanli.repository.CourseRepository;
import com.wanli.repository.StudentClassRepository;
import com.wanli.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 课程服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final StudentClassRepository studentClassRepository;

    @Override
    public CourseResponseDto createCourse(CourseCreateDto courseCreateDto, UUID createdBy) {
        log.info("Creating course: {}", courseCreateDto.getCourseName());

        // 检查课程名称是否已存在
        if (courseRepository.existsByTitle(courseCreateDto.getCourseName())) {
            throw new BusinessException("DUPLICATE_COURSE_NAME", "课程名称已存在: " + courseCreateDto.getCourseName());
        }

        // 创建课程实体
        Course course = new Course();
        
        // 设置课程基本信息
        course.setCourseCode(generateCourseCode());
        course.setCourseName(courseCreateDto.getCourseName());
        course.setTitle(courseCreateDto.getCourseName());
        course.setDescription(courseCreateDto.getCourseDescription());
        course.setGradeLevel(courseCreateDto.getGradeLevel());
        course.setSubject(courseCreateDto.getSubject());
        course.setCreatedBy(createdBy.toString());
        course.setCreatorId(createdBy);
        course.setCourseId(UUID.randomUUID());
        course.setIsActive(true);
        course.setLessonCount(0);

        // 保存课程
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());

        return CourseResponseDto.fromEntity(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getCourses(
            Course.GradeLevel gradeLevel,
            Course.Subject subject,
            String search,
            Boolean isActive,
            Pageable pageable) {
        
        log.info("Querying courses with filters - gradeLevel: {}, subject: {}, search: {}, isActive: {}", 
                gradeLevel, subject, search, isActive);

        // 处理搜索参数，添加通配符
        String processedSearch = null;
        if (search != null && !search.trim().isEmpty()) {
            processedSearch = "%" + search.trim() + "%";
        }

        Page<Course> coursePage = courseRepository.findCoursesWithFilters(
                gradeLevel, subject, processedSearch, isActive, pageable);

        return coursePage.map(CourseResponseDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseById(UUID courseId) {
        log.info("Getting course by ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("COURSE_NOT_FOUND", "课程不存在: " + courseId));

        return CourseResponseDto.fromEntity(course);
    }

    @Override
    public CourseResponseDto updateCourse(UUID courseId, CourseCreateDto courseCreateDto, UUID updatedBy) {
        log.info("Updating course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("COURSE_NOT_FOUND", "课程不存在: " + courseId));

        // 检查课程名称是否已被其他课程使用
        if (!course.getTitle().equals(courseCreateDto.getCourseName()) &&
            courseRepository.existsByTitle(courseCreateDto.getCourseName())) {
            throw new BusinessException("DUPLICATE_COURSE_NAME", "课程名称已存在: " + courseCreateDto.getCourseName());
        }

        // 更新课程信息
        course.setCourseName(courseCreateDto.getCourseName());
        course.setTitle(courseCreateDto.getCourseName());
        course.setDescription(courseCreateDto.getCourseDescription());
        course.setGradeLevel(courseCreateDto.getGradeLevel());
        course.setSubject(courseCreateDto.getSubject());

        // 如果年级或学科发生变化，重新生成课程代码
        String newCourseCode = generateCourseCode();
        if (!course.getCourseCode().equals(newCourseCode)) {
            course.setCourseCode(newCourseCode);
        }

        Course savedCourse = courseRepository.save(course);
        log.info("Course updated successfully: {}", courseId);

        return CourseResponseDto.fromEntity(savedCourse);
    }

    @Override
    public void deleteCourse(UUID courseId, UUID deletedBy) {
        log.info("Deleting course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("COURSE_NOT_FOUND", "课程不存在: " + courseId));

        // 软删除：设置为非激活状态
        course.setIsActive(false);

        courseRepository.save(course);
        log.info("Course deleted successfully: {}", courseId);
    }

    @Override
    public CourseResponseDto toggleCourseStatus(UUID courseId, Boolean isActive, UUID updatedBy) {
        log.info("Toggling course status: {} to {}", courseId, isActive);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("COURSE_NOT_FOUND", "课程不存在: " + courseId));

        course.setIsActive(isActive);

        Course savedCourse = courseRepository.save(course);
        log.info("Course status toggled successfully: {}", courseId);

        return CourseResponseDto.fromEntity(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCourseId(UUID courseId) {
        return courseRepository.existsById(courseId);
    }

    @Override
    public String generateCourseCode() {
        // 生成格式: C + 时间戳后6位
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "C" + timestamp.substring(timestamp.length() - 6);
    }

    @Override
    public Page<CourseResponseDto> getCoursesByStudentId(UUID studentId, Pageable pageable) {
        // 获取学生所在的所有激活班级ID
        List<UUID> classIds = studentClassRepository.findActiveClassIdsByStudentId(studentId);
        
        if (classIds.isEmpty()) {
            // 如果学生不在任何班级中，返回空页面
            return Page.empty(pageable);
        }
        
        // 根据班级ID列表查询课程
        Page<Course> coursePage = courseRepository.findByClassIds(classIds, pageable);
        
        // 转换为DTO
        return coursePage.map(CourseResponseDto::fromEntity);
    }
}