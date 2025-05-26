package spring.flink.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.converter.MemberConverter;
import spring.flink.domain.Member;
import spring.flink.service.MemberService;
import spring.flink.web.dto.MemberRequestDTO;
import spring.flink.web.dto.MemberResponseDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final Map<String, Integer> emailCodeMap = new ConcurrentHashMap<>();


    @PostMapping("/signup")
    @Operation(summary = "회원가입 API")
    public ApiResponse<MemberResponseDTO.MemberJoinResponseDTO> join(@Valid @RequestBody MemberRequestDTO.MemberJoinDTO request) throws Exception{
        Member member = memberService.joinMember(request);
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @PostMapping("/signup/email")
    @Operation(summary = "이메일 인증번호 전송 API")
    public ApiResponse<?> sendEmail(@RequestParam String email) throws Exception{
        int code = memberService.sendMessage(email);
        emailCodeMap.put(email, code);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/signup/email/verify")
    @Operation(summary = "이메일 인증번호 확인 API")
    public ApiResponse<?> verifyEmail(@RequestParam String email, @RequestParam int userNum) throws Exception{
        // 해시 맵 기반으로 구현 -> 스프링 자체 캐시 / redis?
        int correctCode = emailCodeMap.get(email);
        boolean isRight = true;
        if(correctCode != userNum){
            isRight = false;
        }
        // false인 경우 아니라고 해야함
        System.out.println("userNum = " + userNum);
        return ApiResponse.onSuccess(isRight);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ApiResponse<MemberResponseDTO.MemberLoginResultDTO> login(@RequestBody MemberRequestDTO.MemberLoginDTO request) throws Exception{
        // 로그인 완료 시, 클라이언트에세 액세스 토큰과 리프레시 토큰 제공
        String token = memberService.login(request); // 왜 login이 static이어야하지?
        return ApiResponse.onSuccess(MemberConverter.toTokenDTO(token));
    }

}
