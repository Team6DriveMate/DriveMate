package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SemiRouteInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semiRouteInfo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semiRoute_id")
    private SemiRoute semiRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driveReport_id")
    private DriveReport driveReport;

    private String lineName;
    private String lineStartName;
    private String lineEndName;

    private String lineDisturbance;
    private String lineDescription;
    private Boolean isLineDisturbance = false;

    private String lineCongestion;
    private Boolean isLineCongestion = false;

    private Integer lineDirection;

    private String lineRoadType;
    private Boolean isLineRoadType = false;

    private String lineDistance;
    private String lineTime;

    private String pointDescription;
}
