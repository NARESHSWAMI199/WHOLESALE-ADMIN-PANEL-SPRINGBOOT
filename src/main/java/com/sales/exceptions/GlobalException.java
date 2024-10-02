package com.sales.exceptions;

import com.sales.dto.ErrorDto;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.transaction.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalException {


    @Autowired
    Logger logger;
    @Transactional
    @ExceptionHandler(value = {ObjectNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDto resourceNotFoundException(ObjectNotFoundException ex, WebRequest request) {
        ErrorDto message = new ErrorDto(ex.getMessage(),404);
        logger.info(ex.getMessage());
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto resourceNotFoundException(SQLIntegrityConstraintViolationException ex, WebRequest request) {
        ErrorDto message = new ErrorDto(ex.getMessage(),500);
        logger.info(ex.getMessage());
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto resourceNotFoundException(NullPointerException ex, WebRequest request) {
        ErrorDto message = new ErrorDto("Something went wrong there is a null pointer exception.",500);
        logger.info(ex.getMessage());
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return message;
    }



    @Transactional
    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto dataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        //String message = ex.getMessage().contains("constraint [null]") ? "Required parameters can't be null or a duplicate entry." : ex.getMessage();
        String message = getCauseMessage(ex);
        ErrorDto err = new ErrorDto(message,400);
        logger.info(ex.getMessage());
        return err;
    }


    @Transactional
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto httpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorDto message = new ErrorDto("May be request body is empty or required parameter are missing.",500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        logger.info(ex.getMessage());
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {MyException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto myException(MyException ex, WebRequest request) {
        ErrorDto message = new ErrorDto(ex.getMessage(),500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        logger.info(ex.getMessage());
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {UnexpectedRollbackException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto unexpectedRollbackException(UnexpectedRollbackException ex, WebRequest request) {
        ErrorDto message = new ErrorDto(ex.getLocalizedMessage(),500);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        logger.info(ex.getMessage());
        return message;
    }

    @Transactional
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto resourceNotFoundException(Exception ex, WebRequest request) {
        ErrorDto message = null;
        try{
            message =new ErrorDto(getCauseMessage(ex),500);
        }catch (Exception e) {
            message = new ErrorDto(ex.getMessage(), 500);
        }
        logger.info(ex.getMessage());
        return message;
    }



    private String getCauseMessage(Throwable t){
        return t.getCause().getCause().getLocalizedMessage();
    }

}
