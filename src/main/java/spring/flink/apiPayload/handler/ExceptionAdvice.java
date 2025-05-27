package spring.flink.apiPayload.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.apiPayload.exception.GeneralException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // 에러 응답 생성
    private ResponseEntity<Object> handleExceptionInternal(ErrorStatus errorStatus, Object o) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorStatus, o);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(body);
    }

    // ConstraintViolationException 핸들링
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintsViolation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternal(ErrorStatus.valueOf(errorMessage),null);
    }

    // MethodArgumentNotValidException 핸들링
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.of(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });

        return handleExceptionInternal(ErrorStatus._BAD_REQUEST, errors);
    }

    // 기타 에러 핸들링
    @ExceptionHandler
    public ResponseEntity<Object> handleOtherException(Exception e, WebRequest request) {
        e.getStackTrace();

        return handleExceptionInternal(ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // GeneralException 핸들링
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
        return handleExceptionInternal(e.getErrorStatus(),e.getMessage());
    }
}
