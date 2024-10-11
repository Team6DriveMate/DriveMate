package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class DriveReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "driveReport", fetch = FetchType.LAZY)
    private List<SemiRouteInfo> semiRouteInfoList = new ArrayList<>();
}
