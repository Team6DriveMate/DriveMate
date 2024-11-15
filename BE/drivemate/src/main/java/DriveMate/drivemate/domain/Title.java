package DriveMate.drivemate.domain;

import DriveMate.drivemate.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Title {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JoinColumn(name = "title_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String obtainedTime = "";

    private boolean isObtained = false;

    void setUser(User user){
        this.user = user;
        user.getTitleList().add(this);
    }

}
