package spring.flink.converter;

import spring.flink.domain.Member;
import spring.flink.web.dto.MemberRequestDTO;
import spring.flink.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;

public class MemberConverter {

    public static Member toMember(MemberRequestDTO.MemberJoinDTO request){
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    public static MemberResponseDTO.MemberJoinResponseDTO toJoinResultDTO(Member member){
        return MemberResponseDTO.MemberJoinResponseDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

//    public static MemberResponseDTO.MemberLoginResultDTO toTokenDTO(String accessToken, String refreshToken){
//        return MemberResponseDTO.MemberLoginResultDTO.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

}
