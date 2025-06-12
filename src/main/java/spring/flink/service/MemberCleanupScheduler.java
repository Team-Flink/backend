package spring.flink.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import spring.flink.domain.Member;
import spring.flink.domain.enums.MemberStatus;
import spring.flink.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCleanupScheduler {

    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *") //
    @Transactional
    public void deleteInactiveMembers() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        List<Member> expired = memberRepository.findByStatusAndInactivatedDateBefore(MemberStatus.INACTIVE, threeDaysAgo);
        memberRepository.deleteAll(expired);
    }

}
