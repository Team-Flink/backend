package spring.flink.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import spring.flink.domain.common.BaseEntity;
import spring.flink.domain.enums.MemberStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String nickname;

    @Column(nullable = false, columnDefinition = "varchar(30)")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long reports;

    private LocalDateTime inactivatedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

}
