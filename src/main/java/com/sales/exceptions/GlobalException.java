package com.sales.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.sales.dto.ErrorDto;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @Transactional
    @ExceptionHandler(value = PermissionDeniedDataAccessException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorDto permissionDeniedDataAccessException (PermissionDeniedDataAccessException ex , WebRequest request){
        logger.error("PermissionDeniedDataAccessException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage(), 403);
    }

    @Transactional
    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDto notFoundException (NotFoundException ex , WebRequest request){
        logger.error("NotFoundException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage(), 404);
    }

    @Transactional
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorDto illegalArgumentException (IllegalArgumentException ex , WebRequest request){
        logger.error("IllegalArgumentException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage(), 406);
    }

    @Transactional
    @ExceptionHandler(value = JsonMappingException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorDto jsonMappingExceptionHandle (JsonMappingException ex , WebRequest request){
        logger.error("JsonMappingException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage(), 406);
    }

    @Transactional
    @ExceptionHandler(value = {MultipartException.class})
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorDto noMultipartException (MultipartException ex , WebRequest request){
        logger.error("MultipartException: {}", ex.getMessage());
        return new ErrorDto("This is not a multipart request", 400);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDto fileNotFound(FileNotFoundException e,WebRequest request) {
        logger.error("FileNotFoundException: {}", e.getMessage());
        ErrorDto errorDto = new ErrorDto(e.getMessage(),400);
        return errorDto;
    }

    @Transactional
    @ExceptionHandler(value = {ObjectNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDto resourceNotFoundException(ObjectNotFoundException ex, WebRequest request) {
        logger.error("ObjectNotFoundException: {}", ex.getMessage());
        String errorMessage = ex.getMessage();
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto message = new ErrorDto(errorMessage,404);
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto resourceNotFoundException(SQLIntegrityConstraintViolationException ex, WebRequest request) {
        logger.error("SQLIntegrityConstraintViolationException: {}", ex.getMessage());
        String errorMessage = ex.getMessage();
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto message = new ErrorDto(errorMessage,500);
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto resourceNotFoundException(NullPointerException ex, WebRequest request) {
        logger.error("NullPointerException: {}", ex.getMessage());
        ErrorDto message = new ErrorDto("Something went wrong there is a null pointer exception.",400);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorDto dataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        logger.error("DataIntegrityViolationException: {}", ex.getMessage());
        String errorMessage = getCauseMessage(ex);
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto err = new ErrorDto(errorMessage,409);
        return err;
    }

    @Transactional
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorDto httpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        logger.error("HttpMessageNotReadableException: {}", ex.getMessage());
        ErrorDto message = new ErrorDto(ex.getMessage(),406);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {MyException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto myException(MyException ex, WebRequest request) {
        logger.error("MyException: {}", ex.getMessage());
        String errorMessage = ex.getMessage();
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto message = new ErrorDto(errorMessage,500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {UserException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorDto userException(UserException ex, WebRequest request) {
        logger.error("UserException: {}", ex.getMessage());
        String errorMessage = ex.getMessage();
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto message = new ErrorDto(errorMessage,500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {UnexpectedRollbackException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto unexpectedRollbackException(UnexpectedRollbackException ex, WebRequest request) {
        logger.error("UnexpectedRollbackException: {}", ex.getMessage());
        String errorMessage = ex.getLocalizedMessage();
        errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
        ErrorDto message = new ErrorDto(errorMessage,500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto resourceNotFoundException(Exception ex, WebRequest request) {
        logger.error("Exception: {}", ex.getMessage());
        ErrorDto message = null;
        try{
            String errorMessage = getCauseMessage(ex);
            errorMessage = errorMessage.contains(";") ? errorMessage.substring(0, errorMessage.indexOf(";")) : errorMessage;
            message =new ErrorDto(errorMessage,500);
        }catch (Exception e) {
            String errorMessage = ex.getMessage();
            errorMessage = errorMessage.contains(";") ? errorMessage.substring(0,errorMessage.indexOf(";")) : errorMessage;
            message = new ErrorDto(errorMessage, 500);
        }
        return message;
    }

    private String getCauseMessage(Throwable t){
        return t.getCause().getCause().getLocalizedMessage();
    }

}
