package spring.flink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import spring.flink.domain.Member;
import spring.flink.domain.enums.MemberStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Member> findByEmail(String email);

    List<Member> findByStatusAndInactivatedDateBefore(MemberStatus memberStatus, LocalDateTime threeDaysAgo);
}
