package com.example.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;
import com.example.repository.UserRepository;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserService userService;

    @PostMapping("/user/createUser")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) {
        String body = String.valueOf(userService.createUser(userRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/user/getUserInfo")
    public ResponseEntity<UserInfo> getUserById(@RequestParam Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getPassword(), user.getCreatedAt(), user.getSharedFiles());
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }



    @PostMapping("/user/login")
    public ResponseEntity<UserInfo> login(@RequestBody UserRequest userRequest) {
        String userName = userRequest.getUsername();
        String password = userRequest.getPassword();
        List<User> users = userRepository.findAll();

        for(User user: users){
            if(user.getUsername().equals(userName)){
                if (user.getPassword().equals(password)){
                    UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getPassword(), user.getCreatedAt(), user.getSharedFiles());
                    return new ResponseEntity<>(userInfo, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(new UserInfo(), HttpStatus.UNAUTHORIZED);
                }
            }
        }
        return new ResponseEntity<>(new UserInfo(), HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @GetMapping("/user/getAllUsers")
    public ResponseEntity<List<Map<String, String>>> getAllUsers(){
//        List<String> userIds = userRepository.findAll()
//                .stream()
//                .map(User::getUsername)
//                .collect(Collectors.toList());

        List<Map<String, String>> users = userRepository.findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(user -> {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("id", String.valueOf(user.getId()));
                    userMap.put("username", user.getUsername());
                    return userMap;
                })
                .collect(Collectors.toList());
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}