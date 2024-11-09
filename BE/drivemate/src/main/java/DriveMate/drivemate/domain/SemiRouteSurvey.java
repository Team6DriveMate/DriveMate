package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SemiRouteSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="semiRoutePoint_id")
    private SemiRoutePoint semiRoutePoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="semiRouteLineString_id")
    private SemiRouteLineString semiRouteLineString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="driveReport_id")
    private DriveReport driveReport;

    private Boolean laneSwitch;
    private Boolean laneConfusion;
    private Boolean tension;
    private Boolean trafficLaws;
    private Boolean situationDecision;

}
