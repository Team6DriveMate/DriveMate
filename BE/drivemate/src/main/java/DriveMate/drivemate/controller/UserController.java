package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.*;
import DriveMate.drivemate.domain.Title;
import DriveMate.drivemate.domain.User;
import DriveMate.drivemate.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public SuccessRespondDTO logIn(@RequestBody LoginDTO loginDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        if (userService.logIn(loginDTO.getUserName(), loginDTO.getPassword())){
            successRespondDTO.setSuccess(true);
            return successRespondDTO;
        }
        else{
            successRespondDTO.setSuccess(false);
            return successRespondDTO;
        }
    }

    @PostMapping("/signup")
    public SuccessRespondDTO signIn(@RequestBody SignUpDTO signUpDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        if (signUpDTO.getUserPW().equals(signUpDTO.getConfirmUserPW())) {
            Long id = userService.saveUser(userService.createUser(signUpDTO.getUserName(), signUpDTO.getUserPW(), signUpDTO.getUserNickname()));
            successRespondDTO.setSuccess(id != null);
            return successRespondDTO;
        }
        successRespondDTO.setSuccess(false);
        return successRespondDTO;
    }

    @GetMapping("/info/{username}")
    public UserRespondDTO getUserInfo(@PathVariable String username){
        try {
            User user = userService.findUserByUserName(username);
            UserRespondDTO userRespondDTO = new UserRespondDTO();
            userRespondDTO.setNickname(user.getUserNickname());
            userRespondDTO.setUsername(user.getUserName());
            userRespondDTO.setLevel(user.getLevel());
            userRespondDTO.setExperience(user.getExperience());
            userRespondDTO.setNextLevelExperience(100 - (user.getExperience() % 100));
            userRespondDTO.setMainTitle(user.getMainTitle());
            userRespondDTO.setWeakPoint(user.getTop3WeakPoints());
            for (Title title : user.getTitleList()) {
                if(title.isObtained()){
                    TitleDTO titleDTO = new TitleDTO();
                    titleDTO.setName(title.getName());
                    titleDTO.setDateObtained(title.getObtainedTime());
                    userRespondDTO.addTitleDTO(titleDTO);
                }
            }
            return userRespondDTO;
        } catch (Exception e){
            e.getStackTrace();
            return null;
        }
    }

    @PostMapping("/update/{username}")
    public SuccessRespondDTO updateUser(@PathVariable String username, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        User user = userService.findUserByUserName(username);
        if (user != null) {
            user.setUserNickname(userUpdateRequestDTO.nickname);
            user.setMainTitle(userUpdateRequestDTO.mainTitle);
            userService.updateUser(user);
            successRespondDTO.setSuccess(true);
            return successRespondDTO;
        }
        successRespondDTO.setSuccess(false);
        return successRespondDTO;
    }

}
