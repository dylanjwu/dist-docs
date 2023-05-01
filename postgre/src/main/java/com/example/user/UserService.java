package com.example.user;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        try{
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setPassword(userRequest.getPassword());
            user.setCreateAt(LocalDateTime.now());
            userRepository.save(user);

            UserResponse userResponse = new UserResponse(HttpStatus.CREATED.value(), null);
            return userResponse;

        } catch (Exception e){

            // for dylan
            UserResponse userResponse = new UserResponse(HttpStatus.CONFLICT.value(), null);
            return userResponse;
        }
    }
}
