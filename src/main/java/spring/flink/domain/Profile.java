package spring.flink.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import spring.flink.domain.common.BaseEntity;
import spring.flink.domain.enums.Occupation;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String nickname;

    private String url;

    private String imageName;

    @Column(nullable = false)
    private Long age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Occupation occupation;

    private String portfolioUrl;

    private String portfolioName;

}
