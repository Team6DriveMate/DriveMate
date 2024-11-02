package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "route_id")
    private Long id;

    private String startLocation;
    private String endLocation;
    private String startLat;
    private String startLon;
    private String endLat;
    private String endLon;

    private Integer totalDistance;
    private Integer totalTime;

    @OneToOne(fetch = FetchType.LAZY)
    private DriveReport driveReport;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SemiRoute> semiRouteList = new ArrayList<>();

    public void setDriveReport(DriveReport driveReport){
        this.driveReport = driveReport;
        driveReport.setRoute(this);
    }

    public void addSemiRouteList(SemiRoute semiRoute){
        this.semiRouteList.add(semiRoute);
        semiRoute.setRoute(this);
    }

}
