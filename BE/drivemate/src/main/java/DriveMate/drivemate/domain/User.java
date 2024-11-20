package DriveMate.drivemate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // 이게 무슨 차이였지? AUTO 랑 IDENTITY ?
    @Column(name = "user_id")
    private Long id;

    private String userName;
    private String userPW;

    private String userNickname;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Title> titleList = new ArrayList<>();

    // Title 아닌 String으로 Title의 name만 받아온다
    private String mainTitle = "";

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<DriveReport> driveReportList = new ArrayList<>();

    private Integer level = 1;

    private Integer experience = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_weak_points", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "weak_point")
    @Column(name = "value")
    private Map<String, Integer> weakPoints = new HashMap<>(); // 약점 이름과 점수 매핑

    public static User createUser(String userName, String userPW, String userNickname){
        User user = new User();
        user.setUserName(userName);
        user.setUserPW(userPW);
        user.setUserNickname(userNickname);
        user.setInitialWeakPoint();
        user.setInitialTitle();
        return user;
    }

    public void addDriveReportList(DriveReport driveReport){
        this.getDriveReportList().add(driveReport);
        driveReport.setUser(this);
    }

    public void setInitialWeakPoint(){
        this.weakPoints.put("switchLight", 0);  // 방향등 (전체)
        this.weakPoints.put("sideMirror", 0);   // 사이드미러 (전체)
        this.weakPoints.put("tension", 0); // 긴장도 (구간) (전체)
        this.weakPoints.put("weather", 0);      // 날씨 (전체)
        this.weakPoints.put("laneStaying", 0);  // 차선 유지 (구간)
        this.weakPoints.put("laneSwitch", 0);   // 차선 변경 (구간)
        this.weakPoints.put("laneConfusion", 0);// 차선 혼동 (구간)
        this.weakPoints.put("trafficLaws", 0);  // 교통 법규 준수 (구간)
        this.weakPoints.put("situationDecision", 0);// 상황 판단 (구간)
        this.weakPoints.put("sightDegree", 0);  // 시야각 (전체) - 평균 시야각을 저장해놓아야 할까?
        this.weakPoints.put("trafficCongestion", 0); // 혼잡도 (구간)
    }

    public List<String> getTop3WeakPoints() {
        // weakPoints를 value 기준으로 내림차순 정렬
        return this.weakPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3) // 상위 3개만 추출
                .map(Map.Entry::getKey) // key 값만 가져옴
                .collect(Collectors.toList()); // List로 반환
    }

    public void setInitialTitle(){
        Title title1 = new Title();
        title1.setName("병아리 운전자");
        title1.setUser(this);

        Title title2 = new Title();
        title2.setName("도로 위 무법자");
        title2.setUser(this);

        Title title3 = new Title();
        title3.setName("직각 코너링 마스터");
        title3.setUser(this);
    }
}
