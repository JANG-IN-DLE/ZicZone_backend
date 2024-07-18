package org.zerock.ziczone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zerock.ziczone.dto.error.ErrorResponse;
import org.zerock.ziczone.exception.mypage.*;

@RestControllerAdvice
public class GlobalExceptionHandler  {


    /**
     * CompanyNotFoundException 예외를 처리하는 메서드.
     * 주어진 회사 ID로 회사를 찾을 수 없는 경우 발생.
     *
     * @param e CompanyNotFoundException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 NOT_FOUND(404)
     */
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFoundException(CompanyNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * PersonalNotFoundException 예외를 처리하는 메서드.
     * 주어진 개인 ID로 개인 사용자를 찾을 수 없는 경우 발생.
     *
     * @param e PersonalNotFoundException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 NOT_FOUND(404)
     */
    @ExceptionHandler(PersonalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonalNotFoundException(PersonalNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * UserNotFoundException 예외를 처리하는 메서드.
     * 주어진 사용자 ID로 사용자를 찾을 수 없는 경우 발생.
     *
     * @param ex UserNotFoundException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 NOT_FOUND(404)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * InvalidUserIdException 예외를 처리하는 메서드.
     * 유효하지 않은 사용자 ID가 제공된 경우 발생.
     *
     * @param ex InvalidUserIdException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 BAD_REQUEST(400)
     */
    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserIdException(InvalidUserIdException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * InvalidPasswordException 예외를 처리하는 메서드.
     * 유효하지 않은 비밀번호가 제공된 경우 발생.
     *
     * @param ex InvalidPasswordException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 BAD_REQUEST(400)
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지원서가 이미 존재하는 경우 발생하는 예외를 처리하는 메서드.
     *
     * @param ex ResumeAlreadyExistsException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 CONFLICT(409)
     */
    @ExceptionHandler(ResumeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResumeAlreadyExistsException(ResumeAlreadyExistsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * IllegalArgumentException 예외를 처리하는 메서드.
     * 잘못된 인수가 제공된 경우 발생.
     *
     * @param ex IllegalArgumentException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 BAD_REQUEST(400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * IllegalStateException 예외를 처리하는 메서드.
     * 메서드 호출이 객체 상태와 일치하지 않는 경우 발생.
     *
     * @param ex IllegalStateException 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 CONFLICT(409)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * 처리되지 않은 모든 예외를 처리하는 메서드.
     *
     * @param ex Exception 예외
     * @return 에러 응답 객체와 HTTP 상태 코드 INTERNAL_SERVER_ERROR(500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
