package spring.flink.apiPayload.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    EMAIL_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 이메일입니다."),
    PHONENUMBER_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4004", "이미 존재하는 휴대폰 번호입니다."),
    EMAIL_WRONG(HttpStatus.BAD_REQUEST, "MEMBER4005", "가입하지 않은 이메일 주소입니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "MEMBER4006", "비밀번호가 틀렸습니다."),
    NOT_VALID_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4007", "유효한 토큰이 아닙니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4008", "유효한 토큰이 아닙니다"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "MEMBER4009", "토큰이 만료되었습니다. 로그인하세요"),
    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다.");

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
