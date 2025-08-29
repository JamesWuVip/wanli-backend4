package com.wanli.controller;

import com.wanli.dto.InstitutionCreateDTO;
import com.wanli.dto.InstitutionQueryDTO;
import com.wanli.dto.InstitutionResponseDTO;
import com.wanli.dto.InstitutionUpdateDTO;
import com.wanli.entity.Institution;
import com.wanli.entity.InstitutionStatus;
import com.wanli.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 教育机构管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
@Validated
public class InstitutionController {

    private final InstitutionService institutionService;

    /**
     * 创建机构
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInstitution(@Valid @RequestBody InstitutionCreateDTO createDTO) {
        log.info("Creating institution: {}", createDTO.getName());
        
        try {
            // 转换DTO到实体
            Institution institution = new Institution();
            BeanUtils.copyProperties(createDTO, institution);
            
            // 设置创建者信息（临时使用固定值，实际应从认证上下文获取）
            institution.setCreatedBy("admin");
            
            // 创建机构
            Institution createdInstitution = institutionService.createInstitution(institution);
            
            // 转换为响应DTO
            InstitutionResponseDTO responseDTO = convertToResponseDTO(createdInstitution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "机构创建成功");
            response.put("data", responseDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create institution: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error creating institution", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建机构失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取机构详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInstitutionById(@PathVariable UUID id) {
        log.info("Getting institution by ID: {}", id);
        
        try {
            Institution institution = institutionService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));
            
            InstitutionResponseDTO responseDTO = convertToResponseDTO(institution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTO);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Institution not found: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting institution", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取机构信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 分页查询机构列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getInstitutions(@Valid InstitutionQueryDTO queryDTO) {
        log.info("Getting institutions with query: {} - controller method called", queryDTO);
        
        try {
            // 构建分页参数
            Sort sort = Sort.by("desc".equalsIgnoreCase(queryDTO.getSortDirection()) ? 
                    Sort.Direction.DESC : Sort.Direction.ASC, queryDTO.getSortBy());
            Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);
            
            Page<Institution> institutionPage;
            
            // 根据查询条件获取数据
            if (StringUtils.hasText(queryDTO.getName()) && queryDTO.getStatus() != null) {
                institutionPage = institutionService.searchByNameAndStatus(
                        queryDTO.getName(), queryDTO.getStatus(), pageable);
            } else if (StringUtils.hasText(queryDTO.getName())) {
                institutionPage = institutionService.searchByName(queryDTO.getName(), pageable);
            } else if (queryDTO.getStatus() != null) {
                institutionPage = institutionService.findByStatus(queryDTO.getStatus(), pageable);
            } else if (StringUtils.hasText(queryDTO.getCreatedBy())) {
                institutionPage = institutionService.findByCreatedBy(queryDTO.getCreatedBy(), pageable);
            } else {
                institutionPage = institutionService.findAll(pageable);
            }
            
            log.info("Service returned {} institutions", institutionPage.getContent().size());
            
            // 转换为响应DTO
            List<InstitutionResponseDTO> responseDTOs = institutionPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Converted to {} response DTOs", responseDTOs.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            response.put("totalElements", institutionPage.getTotalElements());
            response.put("totalPages", institutionPage.getTotalPages());
            response.put("currentPage", institutionPage.getNumber());
            response.put("pageSize", institutionPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error getting institutions", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取机构列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新机构信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateInstitution(
            @PathVariable UUID id, @Valid @RequestBody InstitutionUpdateDTO updateDTO) {
        log.info("Updating institution: {}", id);
        
        try {
            // 转换DTO到实体
            Institution institution = new Institution();
            BeanUtils.copyProperties(updateDTO, institution);
            
            // 更新机构
            Institution updatedInstitution = institutionService.updateInstitution(id, institution);
            
            // 转换为响应DTO
            InstitutionResponseDTO responseDTO = convertToResponseDTO(updatedInstitution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "机构更新成功");
            response.put("data", responseDTO);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update institution: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error updating institution", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新机构失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新机构状态
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateInstitutionStatus(
            @PathVariable UUID id, @RequestParam InstitutionStatus status) {
        log.info("Updating institution status: {} to {}", id, status);
        
        try {
            Institution updatedInstitution = institutionService.updateStatus(id, status);
            InstitutionResponseDTO responseDTO = convertToResponseDTO(updatedInstitution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "机构状态更新成功");
            response.put("data", responseDTO);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update institution status: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error updating institution status", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新机构状态失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除机构（软删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteInstitution(@PathVariable UUID id) {
        log.info("Deleting institution: {}", id);
        
        try {
            institutionService.deleteInstitution(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "机构删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete institution: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error deleting institution", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除机构失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取活跃机构列表
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveInstitutions() {
        log.info("Getting active institutions");
        
        try {
            List<Institution> activeInstitutions = institutionService.findActiveInstitutions();
            List<InstitutionResponseDTO> responseDTOs = activeInstitutions.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTOs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error getting active institutions", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取活跃机构列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取机构统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInstitutionStatistics() {
        log.info("Getting institution statistics");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalCount", institutionService.findAll().size());
            statistics.put("activeCount", institutionService.countByStatus(InstitutionStatus.ACTIVE));
            statistics.put("inactiveCount", institutionService.countByStatus(InstitutionStatus.INACTIVE));
            statistics.put("suspendedCount", institutionService.countByStatus(InstitutionStatus.SUSPENDED));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error getting institution statistics", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取机构统计信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 转换实体为响应DTO
     */
    private InstitutionResponseDTO convertToResponseDTO(Institution institution) {
        InstitutionResponseDTO responseDTO = new InstitutionResponseDTO();
        
        // 手动设置字段以避免类型转换问题
        responseDTO.setId(institution.getId().toString());
        responseDTO.setName(institution.getName());
        responseDTO.setDescription(institution.getDescription());
        responseDTO.setContactEmail(institution.getContactEmail());
        responseDTO.setContactPhone(institution.getContactPhone());
        responseDTO.setAddress(institution.getAddress());
        responseDTO.setStatus(institution.getStatus());
        responseDTO.setCreatedBy(institution.getCreatedBy());
        responseDTO.setUpdatedBy(institution.getUpdatedBy());
        responseDTO.setCreatedAt(institution.getCreatedAt());
        responseDTO.setUpdatedAt(institution.getUpdatedAt());
        responseDTO.setDeletedAt(institution.getDeletedAt());
        
        // 这里可以添加关联数据的统计，如用户数、课程数、学员数
        // 由于当前没有实现这些关联，暂时设置为0
        responseDTO.setUserCount(0L);
        responseDTO.setCourseCount(0L);
        responseDTO.setStudentCount(0L);
        
        return responseDTO;
    }
}