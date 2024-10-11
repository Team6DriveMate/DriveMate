package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="survey_id")
    private Long id;

    private Integer switchLight;
    private Integer sideMirror;
    private Integer tensionLevel;
    private Integer sightDegree;

    // FinalSemiRoute -> 안에 distubance O / X 변수를 넣어야 하나 고민된다.
}
