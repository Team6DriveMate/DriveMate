package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SemiRouteRoadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "semiRouteRoadInfo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coordinate_id")
    private Coordinate coordinate;

    private Integer infoIndex;
    private String name;
    private String disturbance;
    private String description;
    private String congestion;
    private String direction;
    private String roadType;
    private Integer distance;
    private Double time;
    private Integer speed;


    private String pointDescription;

    public void setCoordinate(Coordinate coordinate){
        this.coordinate = coordinate;
        coordinate.setSemiRouteRoadInfo(this);
    }
}
