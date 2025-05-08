package spring.flink.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import spring.flink.domain.common.BaseEntity;
import spring.flink.domain.enums.Occupation;
import spring.flink.domain.enums.ProjectStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String title;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Occupation occupation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false)
    private Long recruitMemberCount;

    @Column(nullable = false)
    private Long registerMemberCount;

    @Column(nullable = false, columnDefinition = "BIGINT default 0")
    private Long views;

    @Column(nullable = false)
    private LocalDateTime recruitStartedAt;

    @Column(nullable = false)
    private LocalDateTime recruitEndedAt;
}
