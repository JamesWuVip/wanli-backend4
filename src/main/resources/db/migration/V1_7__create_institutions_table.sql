-- V1.7: 创建教育机构表

-- 创建institutions表
CREATE TABLE institutions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(36) NOT NULL,
    updated_by VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- 添加表注释
COMMENT ON TABLE institutions IS '教育机构表';
COMMENT ON COLUMN institutions.id IS '机构ID';
COMMENT ON COLUMN institutions.name IS '机构名称';
COMMENT ON COLUMN institutions.description IS '机构描述';
COMMENT ON COLUMN institutions.contact_email IS '联系邮箱';
COMMENT ON COLUMN institutions.contact_phone IS '联系电话';
COMMENT ON COLUMN institutions.address IS '地址';
COMMENT ON COLUMN institutions.status IS '机构状态';
COMMENT ON COLUMN institutions.created_by IS '创建者ID';
COMMENT ON COLUMN institutions.updated_by IS '更新者ID';
COMMENT ON COLUMN institutions.created_at IS '创建时间';
COMMENT ON COLUMN institutions.updated_at IS '更新时间';
COMMENT ON COLUMN institutions.deleted_at IS '删除时间';

-- 创建索引
CREATE INDEX idx_institutions_name ON institutions(name);
CREATE INDEX idx_institutions_status ON institutions(status);
CREATE INDEX idx_institutions_created_by ON institutions(created_by);
CREATE INDEX idx_institutions_created_at ON institutions(created_at);
CREATE INDEX idx_institutions_deleted_at ON institutions(deleted_at);

-- 添加外键约束
ALTER TABLE institutions 
ADD CONSTRAINT fk_institutions_created_by 
FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE institutions 
ADD CONSTRAINT fk_institutions_updated_by 
FOREIGN KEY (updated_by) REFERENCES users(id);

-- 添加状态检查约束
ALTER TABLE institutions 
ADD CONSTRAINT chk_institutions_status 
CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'));

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 创建触发器
CREATE TRIGGER update_institutions_updated_at
    BEFORE UPDATE ON institutions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 插入初始数据
INSERT INTO institutions (id, name, description, contact_email, contact_phone, address, status, created_by) VALUES
('550e8400-e29b-41d4-a716-446655440001', '万里书院总部', '万里书院教育集团总部机构', 'admin@wanli.edu', '400-123-4567', '北京市朝阳区教育大厦', 'ACTIVE', 
    (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)),
('550e8400-e29b-41d4-a716-446655440002', '万里书院北京分校', '万里书院北京地区分校', 'beijing@wanli.edu', '010-12345678', '北京市海淀区中关村大街', 'ACTIVE', 
    (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)),
('550e8400-e29b-41d4-a716-446655440003', '万里书院上海分校', '万里书院上海地区分校', 'shanghai@wanli.edu', '021-12345678', '上海市浦东新区陆家嘴金融区', 'ACTIVE', 
    (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1));