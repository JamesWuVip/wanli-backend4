package com.wanli.exception;

import com.wanli.common.ApiResponse;
import com.wanli.common.ErrorCode;
import com.wanli.exception.user.DuplicateEmailException;
import com.wanli.exception.user.DuplicateUsernameException;
import com.wanli.exception.user.InvalidPasswordException;
import com.wanli.exception.user.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常
 * 
 * @author wanli
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理用户不存在异常
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
        logger.warn("用户不存在: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理用户名重复异常
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateUsernameException(DuplicateUsernameException e) {
        logger.warn("用户名重复: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理邮箱重复异常
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(DuplicateEmailException e) {
        logger.warn("邮箱重复: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理密码无效异常
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPasswordException(InvalidPasswordException e) {
        logger.warn("密码无效: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<List<ValidationException.FieldError>>> handleValidationException(ValidationException e) {
        logger.warn("验证异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
    }
    
    /**
     * 处理方法参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationException.FieldError>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        logger.warn("方法参数验证失败: {}", e.getMessage());
        
        List<ValidationException.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ValidationException.FieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
            ));
        }
        
        ApiResponse<List<ValidationException.FieldError>> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        response.setMessage(ErrorCode.VALIDATION_ERROR.getMessage());
        response.setData(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<ValidationException.FieldError>>> handleConstraintViolationException(
            ConstraintViolationException e) {
        logger.warn("约束违反异常: {}", e.getMessage());
        
        List<ValidationException.FieldError> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            fieldErrors.add(new ValidationException.FieldError(
                violation.getPropertyPath().toString(),
                violation.getMessage(),
                violation.getInvalidValue()
            ));
        }
        
        ApiResponse<List<ValidationException.FieldError>> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        response.setMessage(ErrorCode.VALIDATION_ERROR.getMessage());
        response.setData(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        logger.warn("认证异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(ErrorCode.UNAUTHORIZED));
    }
    
    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        logger.warn("访问拒绝: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ErrorCode.ACCESS_DENIED));
    }
    
    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e) {
        logger.error("数据访问异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ErrorCode.DATABASE_ERROR));
    }
    
    /**
     * 处理数据完整性违反异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("数据完整性违反: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ErrorCode.DATA_INTEGRITY_VIOLATION));
    }
    
    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        logger.warn("HTTP请求方法不支持: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED));
    }
    
    /**
     * 处理HTTP媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {
        logger.warn("HTTP媒体类型不支持: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ApiResponse.error(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED));
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logger.error("未知异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR));
    }
}