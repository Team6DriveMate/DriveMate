package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public abstract class SemiRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SemiRoute_id")
    private Long id; //
    @ManyToOne(fetch = FetchType.LAZY)// 엔티티 ID
    @JoinColumn(name="route_id")
    private Route route;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="semiRouteInfo_id")
    private SemiRouteInfo semiRouteInfo;

    private Integer numIndex;

    @OneToMany(mappedBy = "semiRoute", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coordinate> coordinateList = new ArrayList<>();

    public void setRoute(Route route){
        this.route = route;
        route.getSemiRouteList().add(this);
    }

    public void addCoordinate(Coordinate coordinate){
        this.coordinateList.add(coordinate);
        coordinate.setSemiRoute(this);
    }
}
