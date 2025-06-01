package spring.flink.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.flink.apiPayload.exception.GeneralException;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.domain.enums.MemberStatus;
import spring.flink.security.auth.MemberDetailService;
import spring.flink.security.jwt.JwtProperties;
import spring.flink.security.jwt.JwtTokenProvider;
import spring.flink.converter.MemberConverter;
import spring.flink.domain.Member;
import spring.flink.repository.MemberRepository;
import spring.flink.web.dto.MemberRequestDTO;
import spring.flink.web.dto.MemberResponseDTO;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;

    //private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redistemplate;
    private final JwtProperties jwtProperties;
    private final MemberDetailService memberDetailService;

    private int number;
    private String accessToken;
    private String refreshToken;

    // 일반 회원 가입
    public MemberResponseDTO.MemberJoinResponseDTO joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception{
        // 이메일, 전화번호 중복 여부 확인
        if(memberRepository.existsByEmail(request.getEmail())){
            throw new GeneralException(ErrorStatus.EMAIL_EXIST);
        }
        if(memberRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new GeneralException(ErrorStatus.PHONENUMBER_EXIST);
        }
        Member member = MemberConverter.toMember(request);

        // 비밀번호 암호화
        member.encodePassword(bCryptPasswordEncoder.encode(member.getPassword()));
        memberRepository.save(member);

        MemberResponseDTO.MemberJoinResponseDTO result = MemberConverter.toJoinResultDTO(member);
        return result;
    }

    // 이메일 인증번호 생성
    public void sendMessage(String email){
        // 6자리 인증번호 생성
        number = (int)(Math.random() * 90000)+100000;
        // 메시지 생성
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            message.setFrom(new InternetAddress("flink@flink.com", "FLINK"));
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("Flink 이메일 인증번호");
            String body = "";
            body += "<h3>" + "요청하신 인증번호입니다."+"</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "화면으로 돌아가 인증번호를 입력해주세요" + "</h3>";
            body += "<h3>" + "감사합니다" + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch(MessagingException | UnsupportedEncodingException e){
            e.printStackTrace();
        }
        // 메시지 전송
        javaMailSender.send(message);
        // redis에 인증번호 3분간 저장
        ValueOperations<String, Object> ops = redistemplate.opsForValue();
        ops.set("EmailCode"+email, number+"", 180, TimeUnit.SECONDS);
    }

    // 이메일 인증번호 확인
    public void verifyEmailCode(String email, String userCode){
        ValueOperations<String, Object> ops = redistemplate.opsForValue();
        String code = (String) ops.get("EmailCode"+email);
        if(!code.equals(userCode)){
            throw new GeneralException(ErrorStatus.EMAIL_WRONG);
        }
    }

    // 로그인
    public void login(MemberRequestDTO.MemberLoginDTO request){
        // 아이디와 비밀번호가 맞는지 확인
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_WRONG));

        if(!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new GeneralException(ErrorStatus.PASSWORD_WRONG);
        }
        // JWT 토큰 생성
        accessToken = jwtTokenProvider.makeToken(member.getId(), member.getEmail(),1);
        refreshToken = jwtTokenProvider.makeToken(member.getId(), member.getEmail(),0);

        // redis에 refresh 토큰 저장
        ValueOperations<String, Object> ops = redistemplate.opsForValue();
        ops.set(request.getEmail(), refreshToken, jwtProperties.getRefreshExpireMS(), TimeUnit.MILLISECONDS);
    }

    // 로그아웃
    public void logoutMember(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        // 토큰이 존재하는지 확인
        if(!jwtTokenProvider.validateToken(token)){
            throw new GeneralException(ErrorStatus.NOT_VALID_TOKEN);
        }
        // 해당 액세스 토큰 로그아웃 처리하기
        redistemplate.opsForValue().set(token, "logout");
    }

    // 회원 탈퇴
    public void deleteMember(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        if(!jwtTokenProvider.validateToken(token)){
            throw new GeneralException(ErrorStatus.NOT_VALID_TOKEN);
        }
        String email = jwtTokenProvider.getEmail(token);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_WRONG));

        member.setMemberStatus(MemberStatus.INACTIVE);
    }


}
