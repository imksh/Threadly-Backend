package com.example.journal.controller;

import com.example.journal.entity.User;
import com.example.journal.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController
{

    @Autowired
    private UserServices userServices;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//    @GetMapping
//    public List<User> getAll()
//    {
//        return new ArrayList<>(userServices.getAll());
//    }
//
//    @GetMapping("/{id}")
//    public Optional<User> getById(@PathVariable ObjectId id)
//    {
//        return userServices.getById(id);
//    }



    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userdb = userServices.findByUsername(username);
        userdb.setUsername(user.getUsername());
        userdb.setPassword(passwordEncoder.encode(user.getPassword()));
        userServices.saveUser(userdb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
