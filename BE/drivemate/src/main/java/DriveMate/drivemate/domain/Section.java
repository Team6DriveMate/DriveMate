package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;

    @OneToMany(mappedBy="section")
    private List<SemiRoute> semiRouteList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="driveReport_id")
    private DriveReport driveReport;

    private String sectionName;

    public void setDriveReport(DriveReport driveReport){
        this.driveReport = driveReport;
        driveReport.getSectionList().add(this);
    }

    public void addSemiRouteList(SemiRoute semiRoute){
        this.getSemiRouteList().add(semiRoute);
        semiRoute.setSection(this);
    }
}
