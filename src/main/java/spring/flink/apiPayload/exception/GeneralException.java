package spring.flink.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spring.flink.apiPayload.code.BaseErrorCode;
import spring.flink.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
