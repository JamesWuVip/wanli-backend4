const https = require('https');
const http = require('http');

// 测试配置
const BASE_URL = 'https://wanli-backend-staging-staging.up.railway.app';
const TEST_PATHS = [
    '/health',
    '/api/health',
    '/api/api/health',
    '/',
    '/api/',
    '/actuator/health'
];

// HTTP请求函数
function makeRequest(url, options = {}) {
    return new Promise((resolve, reject) => {
        const urlObj = new URL(url);
        const isHttps = urlObj.protocol === 'https:';
        const client = isHttps ? https : http;
        
        const requestOptions = {
            hostname: urlObj.hostname,
            port: urlObj.port || (isHttps ? 443 : 80),
            path: urlObj.pathname + urlObj.search,
            method: options.method || 'GET',
            headers: {
                'User-Agent': 'Debug-Test-Client/1.0',
                'Accept': 'application/json, text/plain, */*',
                'Connection': 'close',
                ...options.headers
            },
            timeout: 10000
        };

        console.log(`\n🔍 测试请求: ${url}`);
        console.log(`   方法: ${requestOptions.method}`);
        console.log(`   主机: ${requestOptions.hostname}:${requestOptions.port}`);
        console.log(`   路径: ${requestOptions.path}`);
        
        const req = client.request(requestOptions, (res) => {
            let data = '';
            
            console.log(`   状态码: ${res.statusCode}`);
            console.log(`   状态消息: ${res.statusMessage}`);
            console.log(`   响应头:`);
            Object.entries(res.headers).forEach(([key, value]) => {
                console.log(`     ${key}: ${value}`);
            });
            
            res.on('data', (chunk) => {
                data += chunk;
            });
            
            res.on('end', () => {
                console.log(`   响应体长度: ${data.length} 字节`);
                if (data.length > 0 && data.length < 1000) {
                    console.log(`   响应内容: ${data}`);
                } else if (data.length >= 1000) {
                    console.log(`   响应内容(前500字符): ${data.substring(0, 500)}...`);
                }
                
                resolve({
                    statusCode: res.statusCode,
                    statusMessage: res.statusMessage,
                    headers: res.headers,
                    data: data,
                    success: res.statusCode >= 200 && res.statusCode < 300
                });
            });
        });

        req.on('error', (error) => {
            console.log(`   ❌ 请求错误: ${error.message}`);
            resolve({
                statusCode: 0,
                statusMessage: 'Request Error',
                headers: {},
                data: '',
                error: error.message,
                success: false
            });
        });

        req.on('timeout', () => {
            console.log(`   ⏰ 请求超时`);
            req.destroy();
            resolve({
                statusCode: 0,
                statusMessage: 'Timeout',
                headers: {},
                data: '',
                error: 'Request timeout',
                success: false
            });
        });

        if (options.body) {
            req.write(options.body);
        }
        
        req.end();
    });
}

// 网络连接测试
async function testNetworkConnectivity() {
    console.log('\n🌐 网络连接测试');
    console.log('=' .repeat(50));
    
    // 测试DNS解析
    try {
        const dns = require('dns');
        const hostname = 'wanli-backend-staging-staging.up.railway.app';
        
        await new Promise((resolve, reject) => {
            dns.lookup(hostname, (err, address, family) => {
                if (err) {
                    console.log(`❌ DNS解析失败: ${err.message}`);
                    reject(err);
                } else {
                    console.log(`✅ DNS解析成功: ${hostname} -> ${address} (IPv${family})`);
                    resolve();
                }
            });
        });
    } catch (error) {
        console.log(`❌ DNS测试异常: ${error.message}`);
    }
    
    // 测试基本HTTP连接
    try {
        const result = await makeRequest('https://httpbin.org/get');
        if (result.success) {
            console.log('✅ 基本HTTP连接正常');
        } else {
            console.log('❌ 基本HTTP连接异常');
        }
    } catch (error) {
        console.log(`❌ HTTP连接测试异常: ${error.message}`);
    }
}

// 路径测试
async function testAllPaths() {
    console.log('\n🔍 API路径测试');
    console.log('=' .repeat(50));
    
    const results = [];
    
    for (const path of TEST_PATHS) {
        const url = BASE_URL + path;
        const result = await makeRequest(url);
        
        results.push({
            path: path,
            url: url,
            statusCode: result.statusCode,
            success: result.success,
            error: result.error,
            responseLength: result.data ? result.data.length : 0
        });
        
        // 添加延迟避免过于频繁的请求
        await new Promise(resolve => setTimeout(resolve, 1000));
    }
    
    return results;
}

// Railway特定测试
async function testRailwaySpecific() {
    console.log('\n🚂 Railway特定测试');
    console.log('=' .repeat(50));
    
    // 测试Railway健康检查
    const railwayHealthUrl = 'https://wanli-backend-staging-staging.up.railway.app';
    console.log('\n测试Railway根路径...');
    const rootResult = await makeRequest(railwayHealthUrl);
    
    // 测试带User-Agent的请求
    console.log('\n测试带特定User-Agent的请求...');
    const uaResult = await makeRequest(BASE_URL + '/api/health', {
        headers: {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        }
    });
    
    // 测试POST请求
    console.log('\n测试POST请求到健康检查端点...');
    const postResult = await makeRequest(BASE_URL + '/api/health', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
    });
    
    return {
        root: rootResult,
        userAgent: uaResult,
        post: postResult
    };
}

// 主测试函数
async function runDebugTests() {
    console.log('🔧 Railway API Debug 测试工具');
    console.log('=' .repeat(50));
    console.log(`测试目标: ${BASE_URL}`);
    console.log(`测试时间: ${new Date().toISOString()}`);
    
    try {
        // 网络连接测试
        await testNetworkConnectivity();
        
        // 路径测试
        const pathResults = await testAllPaths();
        
        // Railway特定测试
        const railwayResults = await testRailwaySpecific();
        
        // 生成总结报告
        console.log('\n📊 测试总结');
        console.log('=' .repeat(50));
        
        console.log('\n路径测试结果:');
        pathResults.forEach(result => {
            const status = result.success ? '✅' : '❌';
            console.log(`${status} ${result.path} -> ${result.statusCode} (${result.responseLength} bytes)`);
            if (result.error) {
                console.log(`   错误: ${result.error}`);
            }
        });
        
        console.log('\nRailway特定测试结果:');
        console.log(`根路径: ${railwayResults.root.statusCode}`);
        console.log(`User-Agent测试: ${railwayResults.userAgent.statusCode}`);
        console.log(`POST测试: ${railwayResults.post.statusCode}`);
        
        // 保存详细报告
        const report = {
            timestamp: new Date().toISOString(),
            baseUrl: BASE_URL,
            pathResults: pathResults,
            railwayResults: railwayResults
        };
        
        require('fs').writeFileSync('debug_api_test_report.json', JSON.stringify(report, null, 2));
        console.log('\n📄 详细报告已保存到: debug_api_test_report.json');
        
    } catch (error) {
        console.error('❌ 测试过程中发生错误:', error.message);
        console.error(error.stack);
    }
}

// 运行测试
if (require.main === module) {
    runDebugTests().then(() => {
        console.log('\n🎉 Debug测试完成!');
        process.exit(0);
    }).catch(error => {
        console.error('❌ Debug测试失败:', error.message);
        process.exit(1);
    });
}

module.exports = { runDebugTests, makeRequest };