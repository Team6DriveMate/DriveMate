package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Coordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)// 엔티티 ID
    @JoinColumn(name="semiRoute_id")
    private SemiRoute semiRoute;

    @OneToOne(mappedBy="coordinate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SemiRouteRoadInfo semiRouteRoadInfo;

    private double first;   // Lon
    private double second;  // Lat

    public void setSemiRoute(SemiRoute semiRoute){
        this.semiRoute = semiRoute;
        semiRoute.getCoordinateList().add(this);
    }


    /*
    public void setSemiRouteInfo(SemiRouteRoadInfo semiRouteRoadInfo){
        this.semiRouteRoadInfo = semiRouteRoadInfo;
        semiRouteRoadInfo.setCoordinate(this);
    }

     */
}
