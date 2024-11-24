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

    private Double avgSightDegree = 0.0;

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
        this.weakPoints.put("tension", 0);      // 긴장도 (구간) (전체)
        this.weakPoints.put("weather", 0);      // 날씨 (전체)
        this.weakPoints.put("laneStaying", 0);  // 차선 유지 (구간)
        this.weakPoints.put("laneSwitch", 0);   // 차선 변경 (구간)
        this.weakPoints.put("laneConfusion", 0);// 차선 혼동 (구간)
        this.weakPoints.put("trafficLaws", 0);  // 교통 법규 준수 (구간)
        this.weakPoints.put("situationDecision", 0); // 상황 판단 (구간)
        this.weakPoints.put("sightDegree", 0);  // 시야각 (전체) - 평균 시야각을 저장해놓아야 할까?
        this.weakPoints.put("trafficCongestion", 0); // 혼잡도 (구간)
        this.weakPoints.put("roadType", 0);     // 도로 유형
    }

    public List<String> getTop3WeakPoints() {
        // weakPoints를 value 기준으로 내림차순 정렬
        return this.weakPoints.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3) // 상위 3개만 추출
                .map(Map.Entry::getKey) // key 값만 가져옴
                .collect(Collectors.toList()); // List로 반환
    }

    public void setAvgSightDegree(){
        Double tmp = 0.0;
        for (DriveReport driveReport : this.getDriveReportList()){
            tmp += driveReport.getSurvey().getSightDegree();
        }
        tmp = tmp / driveReportList.size();
        this.setAvgSightDegree(tmp);
    }

    public void setInitialTitle(){
        Title title1= new Title();
        title1.setName("병아리 운전자");  // 기본 지급
        title1.setObtained(true);
        title1.setObtainedTime("");
        title1.setUser(this);

        Title title2 = new Title();
        title2.setName("도로 위 무법자"); // congestion 수치가 5 이상
        title2.setUser(this);

        Title title3 = new Title();
        title3.setName("평정심"); // laneStaying 수치가 5 이상
        title3.setUser(this);

        Title title4 = new Title();
        title4.setName("날씨의 아이");   // weather 수치가 5 이상
        title4.setUser(this);

        Title title5 = new Title();
        title5.setName("눈이 두개지요");   // sightDegree 수치가 5 이상
        title5.setUser(this);

        Title title6 = new Title();
        title6.setName("사팔뜨기");   // sightMirror 수치가 5 이상
        title6.setUser(this);

        Title title7 = new Title();
        title7.setName("준법 시민");    // trafficLaws 수치가 5 이상
        title7.setUser(this);

        Title title8 = new Title();
        title8.setName("드리블의 귀재"); // switchLight 수치가 5 이상
        title8.setUser(this);

        Title title9 = new Title();
        title9.setName("그대 참치마요"); // tension 수치가 5 이상
        title9.setUser(this);
    }

    public void updateTitle(){
        if (this.weakPoints.get("trafficCongestion") >= 5) {
            titleList.get(1).setObtained(true);
            titleList.get(1).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("laneStaying") >= 5) {
            titleList.get(2).setObtained(true);
            titleList.get(2).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("weather") >= 5) {
            titleList.get(3).setObtained(true);
            titleList.get(3).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("sightDegree") >= 5) {
            titleList.get(4).setObtained(true);
            titleList.get(4).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("sightMirror") >= 5) {
            titleList.get(5).setObtained(true);
            titleList.get(5).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("trafficLaws") >= 5) {
            titleList.get(6).setObtained(true);
            titleList.get(6).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("switchLight") >= 5) {
            titleList.get(7).setObtained(true);
            titleList.get(7).setObtainedTime(""); // 현재 시간
        }
        if (this.weakPoints.get("tension") >= 5) {
            titleList.get(8).setObtained(true);
            titleList.get(8).setObtainedTime(""); // 현재 시간
        }

    }

    public void updateExperienceByRoute(){
        this.experience += 50;
    }

    public void updateExperienceByCheck(){
        this.experience += 25;
    }

    public void expToLevel(){
        this.setLevel(this.level + this.experience / 100);
        this.setExperience(this.experience % 100);
    }
}
