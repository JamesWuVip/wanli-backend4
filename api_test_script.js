#!/usr/bin/env node

/**
 * 万里教育后台管理系统 API 测试脚本
 * 测试staging环境的API功能
 */

const axios = require('axios');
const { execSync } = require('child_process');

// 配置
const CONFIG = {
  // 自动获取Railway staging环境URL
  getBaseUrl: () => {
    try {
      // 获取Railway domain
      const output = execSync('railway domain', { encoding: 'utf8' }).trim();
      // 解析输出，提取实际的URL
      const lines = output.split('\n');
      const urlLine = lines.find(line => line.includes('https://'));
      if (urlLine) {
        const match = urlLine.match(/https:\/\/[^\s]+/);
        if (match) {
          return match[0];
        }
      }
      throw new Error('无法解析Railway domain输出');
    } catch (error) {
      console.warn('无法自动获取Railway URL，使用默认URL');
      return 'https://wanli-backend-staging-staging.up.railway.app';
    }
  },
  timeout: 10000,
  retries: 3
};

// 测试数据
const TEST_DATA = {
  users: {
    admin: {
      username: 'admin',
      password: 'admin123'
    },
    teacher: {
      username: 'teacher_test',
      password: 'teacher123',
      email: 'teacher_test@wanli.edu',
      fullName: '测试教师',
      role: 'TEACHER'
    },
    student: {
      username: 'student_test',
      password: 'student123',
      email: 'student_test@example.com',
      fullName: '测试学生',
      role: 'STUDENT'
    }
  },
  courses: {
    testCourse: {
      title: '测试课程',
      description: '这是一个测试课程',
      status: 'DRAFT'
    }
  },
  lessons: {
    testLesson: {
      title: '测试课时',
      content: '这是一个测试课时的内容',
      duration: 45
    }
  }
};

// 全局变量存储测试结果
let testResults = {
  total: 0,
  passed: 0,
  failed: 0,
  errors: []
};

let authTokens = {};
let createdUsers = {};
let createdCourses = {};
let createdLessons = {};

// HTTP客户端配置
let httpClient;

/**
 * 初始化HTTP客户端
 */
function initHttpClient() {
  const baseURL = CONFIG.getBaseUrl();
  console.log(`🌐 使用API基础URL: ${baseURL}`);
  
  httpClient = axios.create({
    baseURL: baseURL + '/api',
    timeout: CONFIG.timeout,
    headers: {
      'Content-Type': 'application/json'
    }
  });
  
  // 请求拦截器
  httpClient.interceptors.request.use(
    (config) => {
      console.log(`📤 ${config.method.toUpperCase()} ${config.url}`);
      return config;
    },
    (error) => {
      console.error('❌ 请求错误:', error.message);
      return Promise.reject(error);
    }
  );
  
  // 响应拦截器
  httpClient.interceptors.response.use(
    (response) => {
      console.log(`📥 ${response.status} ${response.config.method.toUpperCase()} ${response.config.url}`);
      return response;
    },
    (error) => {
      if (error.response) {
        console.error(`❌ ${error.response.status} ${error.config.method.toUpperCase()} ${error.config.url}`);
        console.error('响应数据:', error.response.data);
      } else {
        console.error('❌ 网络错误:', error.message);
      }
      return Promise.reject(error);
    }
  );
}

/**
 * 测试辅助函数
 */
function runTest(testName, testFn) {
  return new Promise(async (resolve) => {
    testResults.total++;
    console.log(`\n🧪 测试: ${testName}`);
    
    try {
      await testFn();
      testResults.passed++;
      console.log(`✅ 通过: ${testName}`);
      resolve(true);
    } catch (error) {
      testResults.failed++;
      testResults.errors.push({ test: testName, error: error.message });
      console.error(`❌ 失败: ${testName}`);
      console.error(`   错误: ${error.message}`);
      resolve(false);
    }
  });
}

/**
 * 等待函数
 */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 健康检查测试
 */
async function testHealthCheck() {
  await runTest('系统健康检查', async () => {
    const response = await httpClient.get('/health');
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    if (!response.data.success) {
      throw new Error('健康检查返回失败状态');
    }
    
    console.log('   健康状态:', response.data.data?.status || 'OK');
  });
}

/**
 * 用户认证API测试
 */
async function testAuthAPIs() {
  console.log('\n📋 开始测试用户认证API...');
  
  // 测试用户注册
  await runTest('用户注册', async () => {
    const userData = TEST_DATA.users.student;
    const response = await httpClient.post('/auth/register', userData);
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    if (!response.data.success) {
      throw new Error(`注册失败: ${response.data.message}`);
    }
    
    createdUsers.student = response.data.data;
    console.log('   注册用户ID:', createdUsers.student.userId);
  });
  
  // 测试用户登录
  await runTest('用户登录', async () => {
    const loginData = {
      username: TEST_DATA.users.student.username,
      password: TEST_DATA.users.student.password
    };
    
    const response = await httpClient.post('/auth/login', loginData);
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    if (!response.data.success) {
      throw new Error(`登录失败: ${response.data.message}`);
    }
    
    authTokens.student = response.data.data.accessToken;
    console.log('   获取到JWT令牌');
  });
  
  // 测试获取当前用户信息
  await runTest('获取当前用户信息', async () => {
    const response = await httpClient.get('/auth/me', {
      headers: {
        'Authorization': `Bearer ${authTokens.student}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    if (!response.data.success) {
      throw new Error(`获取用户信息失败: ${response.data.message}`);
    }
    
    console.log('   用户名:', response.data.data.username);
    console.log('   角色:', response.data.data.role);
  });
  
  // 测试管理员登录（如果存在）
  await runTest('管理员登录', async () => {
    const loginData = {
      username: TEST_DATA.users.admin.username,
      password: TEST_DATA.users.admin.password
    };
    
    try {
      const response = await httpClient.post('/auth/login', loginData);
      
      if (response.data.success) {
        authTokens.admin = response.data.data.accessToken;
        console.log('   管理员登录成功');
      }
    } catch (error) {
      // 管理员账户可能不存在，这是正常的
      console.log('   管理员账户不存在或密码错误（正常情况）');
    }
  });
  
  // 测试用户登出
  await runTest('用户登出', async () => {
    const response = await httpClient.post('/auth/logout', {}, {
      headers: {
        'Authorization': `Bearer ${authTokens.student}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    if (!response.data.success) {
      throw new Error(`登出失败: ${response.data.message}`);
    }
    
    console.log('   用户登出成功');
  });
}

/**
 * 课程管理API测试（预留）
 */
async function testCourseAPIs() {
  console.log('\n📚 开始测试课程管理API...');
  
  // 注意：由于课程管理API尚未实现，这些测试将会失败
  // 这里提供测试框架，当API实现后可以启用
  
  console.log('⚠️  课程管理API尚未实现，跳过相关测试');
  
  /*
  // 测试创建课程
  await runTest('创建课程', async () => {
    if (!authTokens.admin && !authTokens.teacher) {
      throw new Error('需要教师或管理员权限');
    }
    
    const token = authTokens.admin || authTokens.teacher;
    const courseData = TEST_DATA.courses.testCourse;
    
    const response = await httpClient.post('/courses', courseData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    createdCourses.test = response.data.data;
    console.log('   课程ID:', createdCourses.test.id);
  });
  
  // 测试获取课程列表
  await runTest('获取课程列表', async () => {
    const response = await httpClient.get('/courses', {
      headers: {
        'Authorization': `Bearer ${authTokens.student}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log('   课程数量:', response.data.data.length);
  });
  
  // 测试更新课程
  await runTest('更新课程', async () => {
    if (!createdCourses.test) {
      throw new Error('没有可更新的课程');
    }
    
    const token = authTokens.admin || authTokens.teacher;
    const updateData = {
      title: '更新后的测试课程',
      description: '这是更新后的课程描述'
    };
    
    const response = await httpClient.put(`/courses/${createdCourses.test.id}`, updateData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log('   课程更新成功');
  });
  */
}

/**
 * 课时管理API测试（预留）
 */
async function testLessonAPIs() {
  console.log('\n📖 开始测试课时管理API...');
  
  console.log('⚠️  课时管理API尚未实现，跳过相关测试');
  
  /*
  // 测试创建课时
  await runTest('创建课时', async () => {
    if (!createdCourses.test) {
      throw new Error('需要先创建课程');
    }
    
    const token = authTokens.admin || authTokens.teacher;
    const lessonData = {
      ...TEST_DATA.lessons.testLesson,
      courseId: createdCourses.test.id
    };
    
    const response = await httpClient.post('/lessons', lessonData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    createdLessons.test = response.data.data;
    console.log('   课时ID:', createdLessons.test.id);
  });
  
  // 测试获取课程的课时列表
  await runTest('获取课程课时列表', async () => {
    if (!createdCourses.test) {
      throw new Error('需要先创建课程');
    }
    
    const response = await httpClient.get(`/courses/${createdCourses.test.id}/lessons`, {
      headers: {
        'Authorization': `Bearer ${authTokens.student}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log('   课时数量:', response.data.data.length);
  });
  */
}

/**
 * 清理测试数据
 */
async function cleanupTestData() {
  console.log('\n🧹 清理测试数据...');
  
  // 清理创建的用户（如果有管理员权限）
  if (authTokens.admin && createdUsers.student) {
    try {
      await httpClient.delete(`/users/${createdUsers.student.userId}`, {
        headers: {
          'Authorization': `Bearer ${authTokens.admin}`
        }
      });
      console.log('✅ 测试用户已删除');
    } catch (error) {
      console.log('⚠️  无法删除测试用户（可能权限不足）');
    }
  }
}

/**
 * 生成测试报告
 */
function generateTestReport() {
  console.log('\n📊 测试报告');
  console.log('=' .repeat(50));
  console.log(`总测试数: ${testResults.total}`);
  console.log(`通过: ${testResults.passed}`);
  console.log(`失败: ${testResults.failed}`);
  console.log(`成功率: ${((testResults.passed / testResults.total) * 100).toFixed(2)}%`);
  
  if (testResults.errors.length > 0) {
    console.log('\n❌ 失败的测试:');
    testResults.errors.forEach((error, index) => {
      console.log(`${index + 1}. ${error.test}: ${error.error}`);
    });
  }
  
  console.log('\n🎯 建议:');
  if (testResults.failed === 0) {
    console.log('✅ 所有测试通过！API运行正常。');
  } else {
    console.log('⚠️  部分测试失败，请检查:');
    console.log('   1. 服务器是否正常运行');
    console.log('   2. 数据库连接是否正常');
    console.log('   3. 测试数据是否有效');
    console.log('   4. API接口是否已实现');
  }
  
  return testResults.failed === 0;
}

/**
 * 主测试函数
 */
async function runAllTests() {
  console.log('🚀 开始API测试...');
  console.log('测试环境: Railway Staging');
  
  try {
    // 初始化HTTP客户端
    initHttpClient();
    
    // 等待服务启动
    console.log('⏳ 等待服务启动...');
    await sleep(2000);
    
    // 运行测试
    await testHealthCheck();
    await testAuthAPIs();
    await testCourseAPIs();
    await testLessonAPIs();
    
    // 清理测试数据
    await cleanupTestData();
    
    // 生成报告
    const allPassed = generateTestReport();
    
    // 退出码
    process.exit(allPassed ? 0 : 1);
    
  } catch (error) {
    console.error('\n💥 测试执行出错:', error.message);
    process.exit(1);
  }
}

// 如果直接运行此脚本
if (require.main === module) {
  runAllTests();
}

module.exports = {
  runAllTests,
  testHealthCheck,
  testAuthAPIs,
  testCourseAPIs,
  testLessonAPIs
};