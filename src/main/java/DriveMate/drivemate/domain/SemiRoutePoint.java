package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

@Entity
@Getter @Setter
public class SemiRoutePoint extends SemiRoute {

    private Integer pointIndex;
    private String name;
    private String description;
    private String nextRoadName;
    private Integer turnType;
    private String pointType; // S (start) or E (end)
}
