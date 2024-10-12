package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Coordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)// 엔티티 ID
    @JoinColumn(name="semiRoute_id")
    private SemiRoute semiRoute;

    private double first;
    private double second;

    void setSemiRoute(SemiRoute semiRoute){
        this.semiRoute = semiRoute;
        semiRoute.getCoordinateList().add(this);
    }
}
