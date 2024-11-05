package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.LoginDTO;
import DriveMate.drivemate.DTO.RegisterDTO;
import DriveMate.drivemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public boolean logIn(@RequestBody LoginDTO loginDTO){
        return userService.logIn(loginDTO.getUserName(), loginDTO.getPassword());
    }

    @PostMapping("/register")
    public boolean signIn(@RequestBody RegisterDTO registerDTO){
        Long id = userService.saveUser(userService.createUser(registerDTO.getUserName(), registerDTO.getPassword()));
        return id != null;
    }

}
