package spring.flink.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spring.flink.apiPayload.status.ErrorStatus;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;
}
