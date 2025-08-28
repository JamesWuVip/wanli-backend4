package com.wanli.entity;

/**
 * 教育机构状态枚举
 */
public enum InstitutionStatus {
    /**
     * 活跃状态
     */
    ACTIVE("活跃"),
    
    /**
     * 非活跃状态
     */
    INACTIVE("非活跃"),
    
    /**
     * 暂停状态
     */
    SUSPENDED("暂停");

    private final String displayName;

    InstitutionStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取显示名称
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
}