package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class SemiRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="SemiRoute_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)// 엔티티 ID
    @JoinColumn(name="route_id")
    private Route route;

    private Integer numIndex;

    @OneToMany(mappedBy = "semiRoute", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Coordinate> coordinateList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="section_id")
    private Section section;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private SemiRouteRoadInfo semiRouteRoadInfo;

    public void setRoute(Route route){
        this.route = route;
        route.getSemiRouteList().add(this);
    }

    public void addCoordinate(Coordinate coordinate){
        this.coordinateList.add(coordinate);
        coordinate.setSemiRoute(this);
    }
}
