package spring.flink.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.service.MemberService;
import spring.flink.web.dto.MemberRequestDTO;
import spring.flink.web.dto.MemberResponseDTO;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberJoinResponseDTO>> join(@Valid @RequestBody MemberRequestDTO.MemberJoinDTO request) throws Exception{
        MemberResponseDTO.MemberJoinResponseDTO result = memberService.joinMember(request);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(result));
    }

    @PostMapping("/signup/email")
    @Operation(summary = "이메일 인증번호 전송 API")
    public ResponseEntity<ApiResponse<?>> sendEmail(@RequestParam String email) throws Exception{
        // 인증 번호 생성
        memberService.sendMessage(email);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(null));
    }

    @GetMapping("/signup/email/verify")
    @Operation(summary = "이메일 인증번호 확인 API")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam String email, @RequestParam String userNum) throws Exception{
        // 이메일 인증번호 확인
        memberService.verifyEmailCode(email, userNum);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody MemberRequestDTO.MemberLoginDTO request) throws Exception{
        // 로그인 완료 시, 클라이언트에세 액세스 토큰과 리프레시 토큰 제공
        MemberResponseDTO.MemberLoginResultDTO result = memberService.login(request);
        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();
        // 헤더에 액세스, 리프레시 토큰
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Refresh-Token", refreshToken);
        return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess(null));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃  API")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) throws Exception{
        memberService.logoutMember(request);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(null));
    }

    @PostMapping("/delete")
    @Operation(summary = "회원 탈퇴 API")
    public ResponseEntity<ApiResponse<?>> delete(HttpServletRequest request) throws Exception{
        memberService.deleteMember(request);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(null));
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 만료시, 리프레시 토큰으로 액세스 토큰 재발급 API")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestHeader("Refresh-Token") String refreshToken) throws Exception{
        String token = memberService.reissue(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess(null));
    }


}
