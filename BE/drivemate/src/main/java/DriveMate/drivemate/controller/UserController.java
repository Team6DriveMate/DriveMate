package DriveMate.drivemate.controller;

import DriveMate.drivemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/User")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public boolean logIn(@RequestParam String username, @RequestParam String password){
        return userService.logIn(username, password);
    }

    @PostMapping("/create")
    public boolean signIn(@RequestParam String username, @RequestParam String password){
        Long id = userService.saveUser(userService.createUser(username, password));
        return id != null;
    }

}
