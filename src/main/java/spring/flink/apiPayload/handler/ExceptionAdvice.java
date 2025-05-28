package spring.flink.apiPayload.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // 에러 응답 생성 - String
    private ResponseEntity<Object> handleExceptionInternal(ErrorStatus errorStatus, String message) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorStatus, message);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(body);
    }

    // 에러 응답 생성 - Map<String, String>
    private ResponseEntity<Object> handleExceptionInternalArgs(ErrorStatus errorStatus, Map<?,?> map) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorStatus, map);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(body);
    }

    // ConstraintViolationException 핸들링
    // 커스텀 애노테이션 사용 시 발생하는 예외
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintsViolation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternal(ErrorStatus.valueOf(errorMessage),errorMessage);
    }

    // MethodArgumentNotValidException 핸들링
    // @Valid로 DTO 필드의 유효성을 검사할 때 발생하는 예외
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

        return handleExceptionInternalArgs(ErrorStatus._BAD_REQUEST, errors);
    }

    // HttpMessageNotReadableException 핸들링
    // JSON을 입력받아 DTO로 변환하기 전 형식이나 타입이 유효하지 않을 떄 발생하는 예외
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Throwable cause = ex.getCause();
        String details;

        if (cause instanceof InvalidFormatException ife) {
            List<JsonMappingException.Reference> path = ife.getPath();
            if (!path.isEmpty()) {
                String fieldName = path.getFirst().getFieldName();
                String targetType = ife.getTargetType().getSimpleName();
                details = String.format("'%s' 필드의 입력값은 '%s' 타입이어야 합니다.", fieldName, targetType);
            } else {
                log.error("파싱 에러 cause: ", cause);
                details = "파싱 에러 cause: " + cause;
            }
        } else if (cause instanceof MismatchedInputException mie) {
            List<JsonMappingException.Reference> path = mie.getPath();
            if (!path.isEmpty()) {
                String fieldName = path.getFirst().getFieldName();
                details = fieldName + " 필드의 형식이 맞지 않거나 값이 누락되었습니다.";
            } else {
                log.error("파싱 에러 cause: ", cause);
                details = "파싱 에러 cause: " + cause;
            }
        } else if (cause instanceof JsonParseException) {
            details = "잘못된 JSON 문법입니다.";
        } else {
            details = "요청 본문이 올바르지 않습니다.";
        }
        return handleExceptionInternal(ErrorStatus._BAD_REQUEST, details);
    }

    // 기타 에러 핸들링
    // 따로 설정하지 않은 모든 예외
    @ExceptionHandler
    public ResponseEntity<Object> handleOtherException(Exception e, WebRequest request) {
        e.printStackTrace();

        return handleExceptionInternal(ErrorStatus._INTERNAL_SERVER_ERROR, e.getClass() + ": " + e.getMessage());
    }

    // GeneralException 핸들링
    // throw new GeneralException(ErrorStatus.*)로 발생하는 모든 예외
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
        return handleExceptionInternal(e.getErrorStatus(),e.getMessage());
    }
}
