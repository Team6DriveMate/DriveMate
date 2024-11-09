package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class DriveReport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="survey_id")
    private Survey survey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToMany(mappedBy = "driveReport", fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    @OneToMany(mappedBy = "driveReport", fetch = FetchType.LAZY)
    private List<SemiRouteSurvey> semiRouteSurveyList = new ArrayList<>();

    public void setUser(User user){
        this.user = user;
        user.getDriveReportList().add(this);
    }

    public void setSurvey(Survey survey){
        this.survey = survey;
        survey.setDriveReport(this);
    }

}
