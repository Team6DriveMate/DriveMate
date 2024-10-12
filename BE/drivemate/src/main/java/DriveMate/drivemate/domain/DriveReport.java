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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="survey_id")
    private Survey survey;

    @OneToOne(mappedBy = "driveReport", fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    void setUser(User user){
        this.user = user;
        user.getDriveReportList().add(this);
    }

    void setSurvey(Survey survey){
        this.survey = survey;
        survey.setDriveReport(this);
    }

    void setRoute(Route route){
        this.route = route;
        route.setDriveReport(this);
    }
}
