package com.wanli.service.impl;

import com.wanli.entity.Institution;
import com.wanli.entity.InstitutionStatus;
import com.wanli.repository.InstitutionRepository;
import com.wanli.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 教育机构服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;

    @Override
    @Transactional
    public Institution createInstitution(Institution institution) {
        log.info("Creating new institution: {}", institution.getName());
        
        // 验证机构信息
        validateInstitution(institution);
        
        // 检查名称是否已存在
        if (existsByName(institution.getName())) {
            throw new IllegalArgumentException("机构名称已存在: " + institution.getName());
        }
        
        // 设置ID和创建时间
        institution.setId(UUID.randomUUID());
        institution.setCreatedAt(OffsetDateTime.now());
        institution.setUpdatedAt(OffsetDateTime.now());
        
        // 设置默认状态
        if (institution.getStatus() == null) {
            institution.setStatus(InstitutionStatus.ACTIVE);
        }
        
        Institution savedInstitution = institutionRepository.save(institution);
        log.info("Institution created successfully with ID: {}", savedInstitution.getId());
        
        return savedInstitution;
    }

    @Override
    public Optional<Institution> findById(UUID id) {
        log.debug("Finding institution by ID: {}", id);
        return institutionRepository.findById(id);
    }

    @Override
    public Optional<Institution> findByName(String name) {
        log.debug("Finding institution by name: {}", name);
        return institutionRepository.findByName(name);
    }

    @Override
    public List<Institution> findAll() {
        log.info("Finding all institutions - starting query");
        try {
            List<Institution> institutions = institutionRepository.findAll();
            log.info("Found {} institutions", institutions.size());
            return institutions;
        } catch (Exception e) {
            log.error("Error finding all institutions", e);
            throw e;
        }
    }

    @Override
    public Page<Institution> findAll(Pageable pageable) {
        log.debug("Finding all institutions with pagination: {}", pageable);
        return institutionRepository.findAll(pageable);
    }

    @Override
    public List<Institution> findByStatus(InstitutionStatus status) {
        log.debug("Finding institutions by status: {}", status);
        return institutionRepository.findByStatus(status);
    }

    @Override
    public Page<Institution> findByStatus(InstitutionStatus status, Pageable pageable) {
        log.debug("Finding institutions by status: {} with pagination: {}", status, pageable);
        return institutionRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Institution> findByCreatedBy(String createdBy) {
        log.debug("Finding institutions by creator: {}", createdBy);
        return institutionRepository.findByCreatedBy(createdBy);
    }

    @Override
    public Page<Institution> findByCreatedBy(String createdBy, Pageable pageable) {
        log.debug("Finding institutions by creator: {} with pagination: {}", createdBy, pageable);
        return institutionRepository.findByCreatedBy(createdBy, pageable);
    }

    @Override
    public Page<Institution> searchByName(String name, Pageable pageable) {
        log.debug("Searching institutions by name: {} with pagination: {}", name, pageable);
        return institutionRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Institution> searchByNameAndStatus(String name, InstitutionStatus status, Pageable pageable) {
        log.debug("Searching institutions by name: {} and status: {} with pagination: {}", name, status, pageable);
        return institutionRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
    }

    @Override
    @Transactional
    public Institution updateInstitution(UUID id, Institution institution) {
        log.info("Updating institution with ID: {}", id);
        
        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));
        
        // 验证更新的机构信息
        validateInstitution(institution);
        
        // 检查名称是否与其他机构冲突
        if (!existingInstitution.getName().equals(institution.getName()) && 
            existsByNameExcludeId(institution.getName(), id)) {
            throw new IllegalArgumentException("机构名称已存在: " + institution.getName());
        }
        
        // 更新字段 - 只更新Institution实体类中存在的字段
        existingInstitution.setName(institution.getName());
        existingInstitution.setStatus(institution.getStatus());
        existingInstitution.setUpdatedAt(OffsetDateTime.now());
        existingInstitution.setUpdatedBy(institution.getUpdatedBy());
        
        Institution updatedInstitution = institutionRepository.save(existingInstitution);
        log.info("Institution updated successfully: {}", updatedInstitution.getId());
        
        return updatedInstitution;
    }

    @Override
    @Transactional
    public Institution updateStatus(UUID id, InstitutionStatus status) {
        log.info("Updating institution status: {} to {}", id, status);
        
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));
        
        institution.setStatus(status);
        institution.setUpdatedAt(OffsetDateTime.now());
        
        Institution updatedInstitution = institutionRepository.save(institution);
        log.info("Institution status updated successfully: {}", updatedInstitution.getId());
        
        return updatedInstitution;
    }

    @Override
    @Transactional
    public void deleteInstitution(UUID id) {
        log.info("Deleting institution with ID: {}", id);
        
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));
        
        // 软删除
        institution.setDeletedAt(OffsetDateTime.now());
        institution.setUpdatedAt(OffsetDateTime.now());
        
        institutionRepository.save(institution);
        log.info("Institution deleted successfully: {}", id);
    }

    @Override
    public boolean existsByName(String name) {
        return institutionRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameExcludeId(String name, UUID excludeId) {
        return institutionRepository.existsByNameAndIdNot(name, excludeId);
    }

    @Override
    public long countByStatus(InstitutionStatus status) {
        return institutionRepository.countByStatus(status);
    }

    @Override
    public long countByCreatedBy(String createdBy) {
        return institutionRepository.countByCreatedBy(createdBy);
    }

    @Override
    public List<Institution> findActiveInstitutions() {
        log.debug("Finding active institutions");
        return institutionRepository.findActiveInstitutions();
    }

    @Override
    public void validateInstitution(Institution institution) {
        if (institution == null) {
            throw new IllegalArgumentException("机构信息不能为空");
        }
        
        if (!StringUtils.hasText(institution.getName())) {
            throw new IllegalArgumentException("机构名称不能为空");
        }
        
        if (institution.getName().length() > 100) {
            throw new IllegalArgumentException("机构名称长度不能超过100个字符");
        }
        
        // Institution实体类中只有name和status字段，移除其他字段的验证
    }
}