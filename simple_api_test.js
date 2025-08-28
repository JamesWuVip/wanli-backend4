#!/usr/bin/env node

/**
 * 简化的API测试脚本
 * 直接使用固定URL测试staging环境
 */

const axios = require('axios');

// 配置
const BASE_URL = 'https://wanli-backend-staging-staging.up.railway.app/api';
const TIMEOUT = 15000;

// 创建HTTP客户端
const httpClient = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 测试结果
let testResults = {
  total: 0,
  passed: 0,
  failed: 0,
  errors: []
};

/**
 * 运行单个测试
 */
async function runTest(testName, testFn) {
  testResults.total++;
  console.log(`\n🧪 测试: ${testName}`);
  
  try {
    await testFn();
    testResults.passed++;
    console.log(`✅ 通过: ${testName}`);
    return true;
  } catch (error) {
    testResults.failed++;
    testResults.errors.push({ test: testName, error: error.message });
    console.error(`❌ 失败: ${testName}`);
    console.error(`   错误: ${error.message}`);
    if (error.response) {
      console.error(`   状态码: ${error.response.status}`);
      console.error(`   响应: ${JSON.stringify(error.response.data, null, 2)}`);
    }
    return false;
  }
}

/**
 * 测试健康检查
 */
async function testHealth() {
  await runTest('健康检查', async () => {
    console.log(`请求URL: ${BASE_URL}/health`);
    const response = await httpClient.get('/health');
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log(`   响应: ${JSON.stringify(response.data, null, 2)}`);
  });
}

/**
 * 测试用户注册
 */
async function testUserRegistration() {
  await runTest('用户注册', async () => {
    const userData = {
      username: 'test_user_' + Date.now(),
      password: 'test123456',
      confirmPassword: 'test123456',
      email: 'test' + Date.now() + '@example.com',
      fullName: '测试用户',
      role: 'STUDENT'
    };
    
    console.log(`请求URL: ${BASE_URL}/auth/register`);
    console.log(`请求数据: ${JSON.stringify(userData, null, 2)}`);
    
    const response = await httpClient.post('/auth/register', userData);
    
    if (response.status !== 201 && response.status !== 200) {
      throw new Error(`期望状态码200或201，实际${response.status}`);
    }
    
    console.log(`   响应: ${JSON.stringify(response.data, null, 2)}`);
    
    // 保存用户信息供后续测试使用
    global.testUser = {
      ...userData,
      userId: response.data.data?.userId
    };
  });
}

/**
 * 测试用户登录
 */
async function testUserLogin() {
  await runTest('用户登录', async () => {
    if (!global.testUser) {
      throw new Error('需要先注册用户');
    }
    
    const loginData = {
      username: global.testUser.username,
      password: global.testUser.password
    };
    
    console.log(`请求URL: ${BASE_URL}/auth/login`);
    console.log(`请求数据: ${JSON.stringify(loginData, null, 2)}`);
    
    const response = await httpClient.post('/auth/login', loginData);
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log(`   响应: ${JSON.stringify(response.data, null, 2)}`);
    
    // 保存token供后续测试使用
    global.authToken = response.data.data?.accessToken;
    
    if (!global.authToken) {
      throw new Error('未获取到访问令牌');
    }
  });
}

/**
 * 测试获取用户信息
 */
async function testGetUserInfo() {
  await runTest('获取用户信息', async () => {
    if (!global.authToken) {
      throw new Error('需要先登录获取令牌');
    }
    
    console.log(`请求URL: ${BASE_URL}/auth/me`);
    
    const response = await httpClient.get('/auth/me', {
      headers: {
        'Authorization': `Bearer ${global.authToken}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log(`   响应: ${JSON.stringify(response.data, null, 2)}`);
  });
}

/**
 * 测试用户登出
 */
async function testUserLogout() {
  await runTest('用户登出', async () => {
    if (!global.authToken) {
      throw new Error('需要先登录获取令牌');
    }
    
    console.log(`请求URL: ${BASE_URL}/auth/logout`);
    
    const response = await httpClient.post('/auth/logout', {}, {
      headers: {
        'Authorization': `Bearer ${global.authToken}`
      }
    });
    
    if (response.status !== 200) {
      throw new Error(`期望状态码200，实际${response.status}`);
    }
    
    console.log(`   响应: ${JSON.stringify(response.data, null, 2)}`);
  });
}

/**
 * 生成测试报告
 */
function generateReport() {
  console.log('\n📊 测试报告');
  console.log('=' .repeat(50));
  console.log(`API基础URL: ${BASE_URL}`);
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
  
  return testResults.failed === 0;
}

/**
 * 主测试函数
 */
async function runTests() {
  console.log('🚀 开始简化API测试...');
  console.log(`API基础URL: ${BASE_URL}`);
  console.log(`超时时间: ${TIMEOUT}ms`);
  
  try {
    // 等待服务稳定
    console.log('⏳ 等待服务稳定...');
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // 运行测试
    await testHealth();
    await testUserRegistration();
    await testUserLogin();
    await testGetUserInfo();
    await testUserLogout();
    
    // 生成报告
    const allPassed = generateReport();
    
    console.log('\n🎯 测试完成!');
    if (allPassed) {
      console.log('✅ 所有测试通过！');
    } else {
      console.log('⚠️  部分测试失败，请检查服务状态。');
    }
    
    process.exit(allPassed ? 0 : 1);
    
  } catch (error) {
    console.error('\n💥 测试执行出错:', error.message);
    process.exit(1);
  }
}

// 运行测试
if (require.main === module) {
  runTests();
}

module.exports = { runTests };