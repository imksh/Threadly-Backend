package com.example.journal.controller;

import com.example.journal.entity.User;
import com.example.journal.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    private UserServices userServices;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUser()
    {
        List<User> userList = userServices.getAll();
        if(userList !=null && !userList.isEmpty())
        {
            return new ResponseEntity<>(userList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add-admin")
    public ResponseEntity<?> addAdmin(@RequestBody User user)
    {
        userServices.saveAdmin(user);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
