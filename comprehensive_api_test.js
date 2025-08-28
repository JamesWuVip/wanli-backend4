const axios = require('axios');
const fs = require('fs');

// 配置
const BASE_URL = 'http://localhost:8080/api';
const TEST_RESULTS = [];
let authToken = null;
let testUserId = null;
let testCourseId = null;
let testLessonId = null;

// 测试结果记录函数
function logResult(testName, success, message, data = null) {
    const result = {
        test: testName,
        success,
        message,
        timestamp: new Date().toISOString(),
        data
    };
    TEST_RESULTS.push(result);
    console.log(`${success ? '✅' : '❌'} ${testName}: ${message}`);
    if (data) {
        console.log('   Data:', JSON.stringify(data, null, 2));
    }
}

// HTTP请求封装
async function makeRequest(method, endpoint, data = null, headers = {}) {
    try {
        const config = {
            method,
            url: `${BASE_URL}${endpoint}`,
            headers: {
                'Content-Type': 'application/json',
                ...headers
            }
        };
        
        if (data) {
            config.data = data;
        }
        
        const response = await axios(config);
        return { success: true, data: response.data, status: response.status };
    } catch (error) {
        return {
            success: false,
            error: error.response?.data || error.message,
            status: error.response?.status
        };
    }
}

// 1. 健康检查测试
async function testHealthCheck() {
    console.log('\n=== 健康检查测试 ===');
    
    const result = await makeRequest('GET', '/health');
    
    if (result.success) {
        logResult('健康检查', true, '服务正常运行', result.data);
    } else {
        logResult('健康检查', false, `服务异常: ${result.error}`);
    }
    
    return result.success;
}

// 2. 用户注册测试
async function testUserRegistration() {
    console.log('\n=== 用户注册测试 ===');
    
    const testUser = {
        username: `testuser_${Date.now()}`,
        email: `test_${Date.now()}@example.com`,
        password: 'Test123456!',
        confirmPassword: 'Test123456!',
        fullName: '测试用户',
        phoneNumber: '13800138000'
    };
    
    const result = await makeRequest('POST', '/auth/register', testUser);
    
    if (result.success) {
        testUserId = result.data.userId;
        logResult('用户注册', true, '注册成功', { userId: testUserId });
        return { success: true, user: testUser };
    } else {
        logResult('用户注册', false, `注册失败: ${JSON.stringify(result.error)}`);
        return { success: false };
    }
}

// 3. 用户登录测试
async function testUserLogin(user) {
    console.log('\n=== 用户登录测试 ===');
    
    if (!user) {
        logResult('用户登录', false, '没有可用的测试用户');
        return false;
    }
    
    const loginData = {
        username: user.username,
        password: user.password
    };
    
    const result = await makeRequest('POST', '/auth/login', loginData);
    
    console.log('登录响应调试:', JSON.stringify(result, null, 2));
    
    if (result.success && result.data && result.data.data && result.data.data.accessToken) {
        authToken = result.data.data.accessToken;
        logResult('用户登录', true, '登录成功', { token: authToken.substring(0, 20) + '...' });
        return true;
    } else {
        logResult('用户登录', false, `登录失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 4. 获取用户信息测试
async function testGetUserInfo() {
    console.log('\n=== 获取用户信息测试 ===');
    
    if (!authToken) {
        logResult('获取用户信息', false, '没有有效的认证令牌');
        return false;
    }
    
    const result = await makeRequest('GET', '/auth/me', null, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        logResult('获取用户信息', true, '获取成功', result.data);
        return true;
    } else {
        logResult('获取用户信息', false, `获取失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 5. 创建课程测试
async function testCreateCourse() {
    console.log('\n=== 创建课程测试 ===');
    
    if (!authToken) {
        logResult('创建课程', false, '没有有效的认证令牌');
        return false;
    }
    
    const courseData = {
        courseName: `测试课程_${Date.now()}`,
        courseDescription: '这是一个测试课程',
        gradeLevel: 'GRADE_1',
        subject: 'CHINESE'
    };
    
    const result = await makeRequest('POST', '/courses', courseData, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        testCourseId = result.data.id;
        logResult('创建课程', true, '创建成功', { courseId: testCourseId });
        return true;
    } else {
        logResult('创建课程', false, `创建失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 6. 查询课程列表测试
async function testGetCourses() {
    console.log('\n=== 查询课程列表测试 ===');
    
    if (!authToken) {
        logResult('查询课程列表', false, '没有有效的认证令牌');
        return false;
    }
    
    const result = await makeRequest('GET', '/courses', null, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        const courses = result.data.content || result.data; // 处理分页响应
        const count = Array.isArray(courses) ? courses.length : (result.data.totalElements || 0);
        logResult('查询课程列表', true, `查询成功，共${count}门课程`, 
                 { count: count });
        return true;
    } else {
        logResult('查询课程列表', false, `查询失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 7. 查询单个课程测试
async function testGetCourse() {
    console.log('\n=== 查询单个课程测试 ===');
    
    if (!authToken || !testCourseId) {
        logResult('查询单个课程', false, '没有有效的认证令牌或课程ID');
        return false;
    }
    
    const result = await makeRequest('GET', `/courses/${testCourseId}`, null, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        logResult('查询单个课程', true, '查询成功', result.data);
        return true;
    } else {
        logResult('查询单个课程', false, `查询失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 8. 更新课程测试
async function testUpdateCourse() {
    console.log('\n=== 更新课程测试 ===');
    
    if (!authToken || !testCourseId) {
        logResult('更新课程', false, '没有有效的认证令牌或课程ID');
        return false;
    }
    
    const updateData = {
        courseName: `更新的测试课程_${Date.now()}`,
        courseDescription: '这是一个更新后的测试课程',
        gradeLevel: 'GRADE_2',
        subject: 'MATH'
    };
    
    const result = await makeRequest('PUT', `/courses/${testCourseId}`, updateData, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        logResult('更新课程', true, '更新成功', result.data);
        return true;
    } else {
        logResult('更新课程', false, `更新失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 9. 创建课时测试
async function testCreateLesson() {
    console.log('\n=== 创建课时测试 ===');
    
    if (!authToken || !testCourseId) {
        logResult('创建课时', false, '没有有效的认证令牌或课程ID');
        return false;
    }
    
    const lessonData = {
        title: `测试课时_${Date.now()}`,
        content: '这是一个测试课时的内容',
        duration: 30,
        orderIndex: 1,
        courseId: testCourseId
    };
    
    const result = await makeRequest('POST', '/lessons', lessonData, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        testLessonId = result.data.id;
        logResult('创建课时', true, '创建成功', { lessonId: testLessonId });
        return true;
    } else {
        logResult('创建课时', false, `创建失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 10. 查询课时列表测试
async function testGetLessons() {
    console.log('\n=== 查询课时列表测试 ===');
    
    if (!testCourseId) {
        logResult('查询课时列表', false, '没有可用的测试课程ID');
        return false;
    }
    
    const result = await makeRequest('GET', `/courses/${testCourseId}/lessons`);
    
    if (result.success) {
        logResult('查询课时列表', true, `查询成功，共${result.data.length}个课时`, 
                 { count: result.data.length });
        return true;
    } else {
        logResult('查询课时列表', false, `查询失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 11. 更新课时测试
async function testUpdateLesson() {
    console.log('\n=== 更新课时测试 ===');
    
    if (!authToken || !testLessonId) {
        logResult('更新课时', false, '没有有效的认证令牌或课时ID');
        return false;
    }
    
    const updateData = {
        title: `更新的测试课时_${Date.now()}`,
        content: '这是一个更新后的测试课时内容',
        duration: 45
    };
    
    const result = await makeRequest('PUT', `/lessons/${testLessonId}`, updateData, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success) {
        logResult('更新课时', true, '更新成功', result.data);
        return true;
    } else {
        logResult('更新课时', false, `更新失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 12. 删除课时测试
async function testDeleteLesson() {
    console.log('\n=== 删除课时测试 ===');
    
    if (!authToken || !testLessonId) {
        logResult('删除课时', false, '没有有效的认证令牌或课时ID');
        return false;
    }
    
    const result = await makeRequest('DELETE', `/lessons/${testLessonId}`, null, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success || result.status === 204) {
        logResult('删除课时', true, '删除成功');
        return true;
    } else {
        logResult('删除课时', false, `删除失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 13. 删除课程测试
async function testDeleteCourse() {
    console.log('\n=== 删除课程测试 ===');
    
    if (!authToken || !testCourseId) {
        logResult('删除课程', false, '没有有效的认证令牌或课程ID');
        return false;
    }
    
    const result = await makeRequest('DELETE', `/courses/${testCourseId}`, null, {
        'Authorization': `Bearer ${authToken}`
    });
    
    if (result.success || result.status === 204) {
        logResult('删除课程', true, '删除成功');
        return true;
    } else {
        logResult('删除课程', false, `删除失败: ${JSON.stringify(result.error)}`);
        return false;
    }
}

// 生成测试报告
function generateReport() {
    const totalTests = TEST_RESULTS.length;
    const passedTests = TEST_RESULTS.filter(r => r.success).length;
    const failedTests = totalTests - passedTests;
    const successRate = ((passedTests / totalTests) * 100).toFixed(2);
    
    const report = {
        summary: {
            totalTests,
            passedTests,
            failedTests,
            successRate: `${successRate}%`,
            testDate: new Date().toISOString()
        },
        results: TEST_RESULTS
    };
    
    // 保存报告到文件
    fs.writeFileSync('comprehensive_api_test_report.json', JSON.stringify(report, null, 2));
    
    console.log('\n=== 测试报告 ===');
    console.log(`总测试数: ${totalTests}`);
    console.log(`通过测试: ${passedTests}`);
    console.log(`失败测试: ${failedTests}`);
    console.log(`成功率: ${successRate}%`);
    console.log('\n详细报告已保存到: comprehensive_api_test_report.json');
    
    return report;
}

// 主测试函数
async function runAllTests() {
    console.log('开始运行完整的API测试套件...');
    console.log(`测试目标: ${BASE_URL}`);
    
    try {
        // 1. 健康检查
        await testHealthCheck();
        
        // 2. 用户认证流程
        const registrationResult = await testUserRegistration();
        let loginSuccess = false;
        
        if (registrationResult.success) {
            loginSuccess = await testUserLogin(registrationResult.user);
        }
        
        if (loginSuccess) {
            await testGetUserInfo();
        }
        
        // 3. 课程管理流程
        const courseCreated = await testCreateCourse();
        await testGetCourses();
        
        if (courseCreated) {
            await testGetCourse();
            await testUpdateCourse();
            
            // 4. 课时管理流程
            const lessonCreated = await testCreateLesson();
            await testGetLessons();
            
            if (lessonCreated) {
                await testUpdateLesson();
                await testDeleteLesson();
            }
            
            await testDeleteCourse();
        }
        
    } catch (error) {
        console.error('测试过程中发生错误:', error);
        logResult('测试执行', false, `执行错误: ${error.message}`);
    } finally {
        // 生成最终报告
        generateReport();
    }
}

// 运行测试
if (require.main === module) {
    runAllTests().then(() => {
        console.log('\n所有测试完成!');
        process.exit(0);
    }).catch(error => {
        console.error('测试运行失败:', error);
        process.exit(1);
    });
}

module.exports = {
    runAllTests,
    generateReport
};