package spring.flink.apiPayload.exception.handler;

import spring.flink.apiPayload.code.status.ErrorStatus;
import spring.flink.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
