package spring.flink.apiPayload.exception.handler;

import spring.flink.apiPayload.code.BaseErrorCode;
import spring.flink.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
