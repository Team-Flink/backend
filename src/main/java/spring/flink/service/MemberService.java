package spring.flink.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.flink.apiPayload.code.status.ErrorStatus;
import spring.flink.apiPayload.exception.handler.MemberHandler;
import spring.flink.security.jwt.JwtTokenProvider;
import spring.flink.converter.MemberConverter;
import spring.flink.domain.Member;
import spring.flink.repository.MemberRepository;
import spring.flink.web.dto.MemberRequestDTO;

import java.io.UnsupportedEncodingException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private int number;



    // 일반 회원 가입
    public Member joinMember(MemberRequestDTO.MemberJoinDTO request) throws Exception{
        // 이메일, 전화번호 중복 여부 확인
        if(memberRepository.existsByEmail(request.getEmail())){
            throw new MemberHandler(ErrorStatus.EMAIL_EXIST);
        }
        if(memberRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new MemberHandler(ErrorStatus.PHONENUMBER_EXIST);
        }
        Member member = MemberConverter.toMember(request);

        // 비밀번호 암호화
        member.encodePassword(bCryptPasswordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    public int sendMessage(String email){
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
        return number;
    }

    // 로그인 -> 액세스 토큰만 드려요 지금은
    public String login(MemberRequestDTO.MemberLoginDTO request){
        // 아이디와 비밀번호가 맞는지 확인
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.EMAIL_WRONG));

        if(!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new MemberHandler(ErrorStatus.PASSWORD_WRONG);
        }
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.makeAccessToken(member.getId());

        // JWT 토큰 클라이언트에 전달

        return accessToken;
    }


}
