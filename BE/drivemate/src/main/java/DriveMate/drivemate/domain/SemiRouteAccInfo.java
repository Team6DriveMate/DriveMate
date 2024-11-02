package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SemiRouteAccInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "semiRouteAccInfo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coordinate_id")
    private Coordinate coordinate;

    private Integer accIndex;
    private String description;
    private String accidentUpperCode;
    private String accidentUpperName;
    private String accidentDetailCode;
    private String accidentDetailName;

}
