package spring.flink.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


public class MemberRequestDTO {

    @Getter
    public static class MemberJoinDTO{

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String password;

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        String nickname;

        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        String phoneNumber;
    }

    @Getter
    public static class MemberLoginDTO{

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String password;
    }
}
