package DriveMate.drivemate.service;

import DriveMate.drivemate.domain.User;
import DriveMate.drivemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String password, String userNickname){
        User user = User.createUser(username, password, userNickname);
        user.setInitialTitle();
        user.setInitialWeakPoint();
        return user;
    }

    public User findUserByUserName(String username) { return userRepository.findByUserName(username).get(0);}

    @Transactional
    public Long saveUser(User user){
        validateDuplicateUser(user);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public Long updateUser(User user){
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateUser(User user) {
        List<User> findUsers = userRepository.findByUserName(user.getUserName());
        if(!findUsers.isEmpty())
            throw new IllegalStateException("이미 존재하는 회원입니다.");
    }

    public boolean logIn(String userName, String userPW){
         List<User> findUser = userRepository.findByUserName(userName);
         if(!findUser.isEmpty()){
             return findUser.get(0).getUserPW().equals(userPW);
         }
         return false;
    }




}
