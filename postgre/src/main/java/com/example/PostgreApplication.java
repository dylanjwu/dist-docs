package com.example;

import com.example.entity.File;
import com.example.repository.FileRepository;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
@EnableScheduling
@SpringBootApplication
public class PostgreApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;



    public static void main(String[] args) {


        SpringApplication.run(PostgreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("123");
//
//        User user = new User("Jacky", "123");
//
//        this.userRepository.save(user);

    }
}
