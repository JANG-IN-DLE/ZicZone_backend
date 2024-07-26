package org.zerock.ziczone.exception;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zerock.ziczone.dto.error.ErrorResponse;
import org.zerock.ziczone.exception.board.BoardNotFoundException;
import org.zerock.ziczone.exception.mypage.*;
import org.zerock.ziczone.exception.payhisotry.PayHistoryNotFoundException;
import org.zerock.ziczone.exception.payment.PaymentNotFoundException;
import org.zerock.ziczone.exception.resume.ResumeDataIntegrityViolationException;
import org.zerock.ziczone.exception.resume.ResumeNotFoundException;
import org.zerock.ziczone.service.mainPage.MainPageServiceImpl;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    private final MainPageServiceImpl mainPageServiceImpl;

    public GlobalExceptionHandler(MainPageServiceImpl mainPageServiceImpl) {
        this.mainPageServiceImpl = mainPageServiceImpl;
    }

    // 회사 정보를 찾지 못했을 때 예외 처리
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFoundException(CompanyNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 개인 정보를 찾지 못했을 때 예외 처리
    @ExceptionHandler(PersonalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonalNotFoundException(PersonalNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 사용자 정보를 찾지 못했을 때 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 유효하지 않은 사용자 ID 예외 처리
    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserIdException(InvalidUserIdException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    // 유효하지 않은 비밀번호 예외 처리
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    // 결제 정보를 찾지 못했을 때 예외 처리
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 인증 실패 예외 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 리소스를 찾지 못했을 때 예외 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 지원서 찾지 못했을 때 예외
    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResumeNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 게시글 찾지 못 했을 때
    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBoardNotFoundException(ResourceNotFoundException e){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // PayHistory가 없을 때
    @ExceptionHandler(PayHistoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePayHistoryNotFoundException(PayHistoryNotFoundException e){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // 유효하지 않는 지원서 생성 요청
    @ExceptionHandler(ResumeDataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleResumeDataIntegrityViolationException(ResumeDataIntegrityViolationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
