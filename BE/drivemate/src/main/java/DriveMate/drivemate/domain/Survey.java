package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="survey_id")
    private Long id;

    private Integer switchLight;
    private Integer sideMirror;
    private Integer tensionLevel;
    private Integer weather;
    private Integer laneStaying;
    private Integer sightDegree;

    private String memo;

    @OneToOne(fetch = FetchType.LAZY)
    private DriveReport driveReport;

    public void setDriveReport(DriveReport driveReport){
        this.driveReport = driveReport;
        driveReport.setSurvey(this);
    }

    // FinalSemiRoute -> 안에 distubance O / X 변수를 넣어야 하나 고민된다.
}
