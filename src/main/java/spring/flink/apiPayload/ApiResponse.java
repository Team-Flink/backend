package spring.flink.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.apiPayload.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse <T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청 성공 - 201 OK
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    // 리소스 생성 - 201 CREATED
    public static <T> ApiResponse<T> created(T result) {
        return new ApiResponse<>(true, SuccessStatus._CREATED.getCode(), SuccessStatus._CREATED.getMessage(), result);
    }

    // 요청 실패 - ExceptionAdvice에서 사용
    public static <T> ApiResponse<T> onFailure(ErrorStatus errorStatus, T data) {
        return new ApiResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), data);
    }
}
