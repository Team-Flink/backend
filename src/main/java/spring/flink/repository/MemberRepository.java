package spring.flink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import spring.flink.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Member> findByEmail(String email);
}
