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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="survey_id")
    private Survey survey;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToMany(mappedBy = "driveReport", fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    @OneToMany(mappedBy = "driveReport", fetch = FetchType.LAZY)
    private List<SemiRouteSurvey> semiRouteSurveyList = new ArrayList<>();

    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;

    public void setUser(User user){
        this.user = user;
        user.getDriveReportList().add(this);
    }

    public void addSemiRouteSurvey(SemiRouteSurvey semiRouteSurvey){
        semiRouteSurvey.setDriveReport(this);
        this.semiRouteSurveyList.add(semiRouteSurvey);
    }

}
