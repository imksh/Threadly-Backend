package com.example.journal.controller;

import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.services.UserServices;
import com.example.journal.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController
{

    @Autowired
    private UserServices userServices;

    @Autowired
    private JwtUtil jwtUtil;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public List<User> getAll()
    {
        return new ArrayList<>(userServices.getAll());
    }

    @GetMapping("/journal/{username}")
    public List<JournalEntry> getUserJournals(@PathVariable String username)
    {
        return new ArrayList<>(userServices.getUserEntries(username));
    }

    @GetMapping("id/{id}")
    public Optional<User> getById(@PathVariable ObjectId id)
    {
        return userServices.getById(id);
    }

    @GetMapping("/{username}")
    public User getByUsername(@PathVariable String username)
    {
        return userServices.findByUsername(username);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/search-user")
    public User searchUser(@RequestBody Map<String,String> req)
    {
        User user = userServices.findByUsername(req.get("username"));
        return user;
    }


    @PutMapping
public ResponseEntity<?> updateUser(@RequestBody User user, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User userdb = userServices.findByUsername(username);

    userdb.setUsername(user.getUsername());
    userdb.setName(user.getName());
    userdb.setEmail(user.getEmail());
    userdb.setBio(user.getBio());

    if (!user.getPassword().isEmpty()) {
        userdb.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    String jwt = jwtUtil.generateToken(user.getUsername());

    // Set cookie with SameSite=None for cross-origin
    response.addHeader("Set-Cookie",
        String.format("token=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                      jwt, 24*60*60)
    );

    userServices.saveUser(userdb);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}
}
