package com.wanli.service;

import com.wanli.entity.Institution;
import com.wanli.entity.InstitutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 教育机构服务接口
 */
public interface InstitutionService {

    /**
     * 创建机构
     * @param institution 机构信息
     * @return 创建的机构
     */
    Institution createInstitution(Institution institution);

    /**
     * 根据ID查找机构
     * @param id 机构ID
     * @return 机构信息
     */
    Optional<Institution> findById(UUID id);

    /**
     * 根据名称查找机构
     * @param name 机构名称
     * @return 机构信息
     */
    Optional<Institution> findByName(String name);

    /**
     * 获取所有机构
     * @return 机构列表
     */
    List<Institution> findAll();

    /**
     * 分页获取机构列表
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findAll(Pageable pageable);

    /**
     * 根据状态获取机构列表
     * @param status 机构状态
     * @return 机构列表
     */
    List<Institution> findByStatus(InstitutionStatus status);

    /**
     * 根据状态分页获取机构列表
     * @param status 机构状态
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByStatus(InstitutionStatus status, Pageable pageable);

    /**
     * 根据创建者获取机构列表
     * @param createdBy 创建者ID
     * @return 机构列表
     */
    List<Institution> findByCreatedBy(String createdBy);

    /**
     * 根据创建者分页获取机构列表
     * @param createdBy 创建者ID
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * 根据名称模糊查询机构
     * @param name 机构名称关键字
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> searchByName(String name, Pageable pageable);

    /**
     * 根据名称和状态查询机构
     * @param name 机构名称关键字
     * @param status 机构状态
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> searchByNameAndStatus(String name, InstitutionStatus status, Pageable pageable);

    /**
     * 更新机构信息
     * @param id 机构ID
     * @param institution 更新的机构信息
     * @return 更新后的机构
     */
    Institution updateInstitution(UUID id, Institution institution);

    /**
     * 更新机构状态
     * @param id 机构ID
     * @param status 新状态
     * @return 更新后的机构
     */
    Institution updateStatus(UUID id, InstitutionStatus status);

    /**
     * 删除机构（软删除）
     * @param id 机构ID
     */
    void deleteInstitution(UUID id);

    /**
     * 检查机构名称是否已存在
     * @param name 机构名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查机构名称是否已存在（排除指定ID）
     * @param name 机构名称
     * @param excludeId 排除的机构ID
     * @return 是否存在
     */
    boolean existsByNameExcludeId(String name, UUID excludeId);

    /**
     * 统计指定状态的机构数量
     * @param status 机构状态
     * @return 机构数量
     */
    long countByStatus(InstitutionStatus status);

    /**
     * 统计指定创建者的机构数量
     * @param createdBy 创建者ID
     * @return 机构数量
     */
    long countByCreatedBy(String createdBy);

    /**
     * 获取活跃机构列表
     * @return 活跃机构列表
     */
    List<Institution> findActiveInstitutions();

    /**
     * 验证机构信息
     * @param institution 机构信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    void validateInstitution(Institution institution);
}