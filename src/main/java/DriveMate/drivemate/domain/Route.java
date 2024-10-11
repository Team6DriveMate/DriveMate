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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long id;

    private String startLocation;
    private String endLocation;
    private String startLat;
    private String startLon;
    private String endLat;
    private String endLon;

    private String totalDistance;
    private String totalTime;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SemiRoute> semiRouteList = new ArrayList<>();

}
