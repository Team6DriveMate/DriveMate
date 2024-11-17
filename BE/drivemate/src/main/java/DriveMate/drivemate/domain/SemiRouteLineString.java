package DriveMate.drivemate.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("LineString")
public class SemiRouteLineString extends SemiRoute{

    @Transient
    private String semiRouteType = "LineString";

    private Integer lineIndex;
    private String name;
    private String description;
    private Integer distance; // Integer 인가?
    private Double time; // Integer 인가? <--- 여기서 오류 날수도 있어요
    private Integer roadType;
    private Integer facilityType;
}
