package DriveMate.drivemate.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SemiRouteLineString extends SemiRoute{

    @Transient
    private String semiRouteType = "LineString";

    private Integer lineIndex;
    private String name;
    private String description;
    private Integer distance; // Integer 인가?
    private Integer time; // Integer 인가?
    private Integer roadType;
    private Integer facilityType;
}
