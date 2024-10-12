package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 이게 무슨 차이였지? AUTO 랑 IDENTITY ?
    @Column(name = "user_id")
    private Long id;

    private String userName;
    private String userPW;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DriveReport> driveReportList = new ArrayList<>();

    public static User createUser(String userName, String userPW){
        User user = new User();
        user.setUserName(userName);
        user.setUserPW(userPW);
        return user;
    }

    void addDriveReportList(DriveReport driveReport){
        this.getDriveReportList().add(driveReport);
        driveReport.setUser(this);
    }
}
