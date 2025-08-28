package com.wanli.repository;

import com.wanli.entity.Institution;
import com.wanli.entity.InstitutionStatus;
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
 * 教育机构数据访问层接口
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {

    /**
     * 根据机构名称查找机构
     * @param name 机构名称
     * @return 机构信息
     */
    Optional<Institution> findByName(String name);

    /**
     * 根据机构状态查找机构列表
     * @param status 机构状态
     * @return 机构列表
     */
    List<Institution> findByStatus(InstitutionStatus status);

    /**
     * 根据机构状态分页查找机构
     * @param status 机构状态
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByStatus(InstitutionStatus status, Pageable pageable);

    /**
     * 根据创建者ID查找机构列表
     * @param createdBy 创建者ID
     * @return 机构列表
     */
    List<Institution> findByCreatedBy(String createdBy);

    /**
     * 根据创建者ID分页查找机构
     * @param createdBy 创建者ID
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * 根据机构名称模糊查询
     * @param name 机构名称关键字
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 根据机构名称和状态查询
     * @param name 机构名称关键字
     * @param status 机构状态
     * @param pageable 分页参数
     * @return 分页机构列表
     */
    Page<Institution> findByNameContainingIgnoreCaseAndStatus(String name, InstitutionStatus status, Pageable pageable);

    /**
     * 检查机构名称是否已存在
     * @param name 机构名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查机构名称是否已存在（排除指定ID）
     * @param name 机构名称
     * @param id 排除的机构ID
     * @return 是否存在
     */
    boolean existsByNameAndIdNot(String name, UUID id);

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
     * 查询活跃机构列表
     * @return 活跃机构列表
     */
    @Query("SELECT i FROM Institution i WHERE i.status = 'ACTIVE' ORDER BY i.createdAt DESC")
    List<Institution> findActiveInstitutions();

    /**
     * 根据联系邮箱查找机构
     * @param contactEmail 联系邮箱
     * @return 机构信息
     */
    Optional<Institution> findByContactEmail(String contactEmail);

    /**
     * 根据联系电话查找机构
     * @param contactPhone 联系电话
     * @return 机构信息
     */
    Optional<Institution> findByContactPhone(String contactPhone);
}