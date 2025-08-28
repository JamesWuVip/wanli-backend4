package com.wanli.controller;

import com.wanli.common.ApiResponse;
import com.wanli.dto.CourseCreateDto;
import com.wanli.dto.CourseResponseDto;
import com.wanli.entity.Course;
import com.wanli.security.CustomUserDetails;
import com.wanli.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CourseController {

    private final CourseService courseService;

    /**
     * 创建课程
     */
    @PostMapping
    @PreAuthorize("hasRole('HQ_TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<CourseResponseDto>> createCourse(
            @Valid @RequestBody CourseCreateDto courseCreateDto) {
        
        log.info("Creating course: {}", courseCreateDto.getCourseName());
        
        UUID currentUserId = getCurrentUserId();
        CourseResponseDto courseResponse = courseService.createCourse(courseCreateDto, currentUserId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("课程创建成功", courseResponse));
    }

    /**
     * 获取课程列表
     */
    @GetMapping
    @PreAuthorize("hasRole('HQ_TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<CourseResponseDto>>> getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) Course.GradeLevel gradeLevel,
            @RequestParam(required = false) Course.Subject subject,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        
        log.info("Getting courses - page: {}, size: {}, gradeLevel: {}, subject: {}, search: {}, isActive: {}", 
                page, size, gradeLevel, subject, search, isActive);
        
        // 解析排序参数
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        
        // 所有用户都可以看到所有激活的课程
        Page<CourseResponseDto> coursePage = courseService.getCourses(
                gradeLevel, subject, search, isActive, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("获取成功", coursePage));
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('HQ_TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<CourseResponseDto>> getCourseById(
            @PathVariable UUID courseId) {
        
        log.info("Getting course by ID: {}", courseId);
        
        CourseResponseDto courseResponse = courseService.getCourseById(courseId);
        
        return ResponseEntity.ok(ApiResponse.success("获取成功", courseResponse));
    }

    /**
     * 更新课程
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('HQ_TEACHER')")
    public ResponseEntity<ApiResponse<CourseResponseDto>> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseCreateDto courseCreateDto) {
        
        log.info("Updating course: {}", courseId);
        
        UUID currentUserId = getCurrentUserId();
        CourseResponseDto courseResponse = courseService.updateCourse(courseId, courseCreateDto, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("课程更新成功", courseResponse));
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('HQ_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable UUID courseId) {
        
        log.info("Deleting course: {}", courseId);
        
        UUID currentUserId = getCurrentUserId();
        courseService.deleteCourse(courseId, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("课程删除成功", null));
    }

    /**
     * 切换课程状态
     */
    @PatchMapping("/{courseId}/status")
    @PreAuthorize("hasRole('HQ_TEACHER')")
    public ResponseEntity<ApiResponse<CourseResponseDto>> toggleCourseStatus(
            @PathVariable UUID courseId,
            @RequestParam Boolean isActive) {
        
        log.info("Toggling course status: {} to {}", courseId, isActive);
        
        UUID currentUserId = getCurrentUserId();
        CourseResponseDto courseResponse = courseService.toggleCourseStatus(courseId, isActive, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("课程状态更新成功", courseResponse));
    }

    /**
     * 获取当前登录用户ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}