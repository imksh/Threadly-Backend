package com.example.journal.controller;

import com.example.journal.entity.User;
import com.example.journal.reposotiory.UserRepoImpl;
import com.example.journal.services.EmailServices;
import com.example.journal.services.UserDetailsServiceImpl;
import com.example.journal.services.UserServices;
import com.example.journal.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/public")
public class PublicController
{

    @Autowired
    private UserServices userServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private UserRepoImpl userRepo;


    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "Ok";
    }

    @PostMapping("/signup")
    public boolean signup(@RequestBody User user)
    {
        userServices.saveNewUser(user);
        return true;
    }

    @PostMapping("/check-email")
    public boolean checkEmail(@RequestBody Map<String, String> input)
    {
        User user = userRepo.findByEmail(input.get("email"));
        return user==null;
    }
    @PostMapping("/check-username")
    public boolean checkUsername(@RequestBody Map<String, String> input)
    {
        User user = userServices.findByUsername(input.get("username"));
        return user==null;
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody Map<String, String> input) {
        emailServices.sendEmail(input.get("email"));
        return "Verification code sent to " + input.get("email");
    }

    @PostMapping("/verify-email")
    public boolean verifyEmail(@RequestBody Map<String, String> body)
    {
        String email = body.get("email");
        Integer code = Integer.parseInt(body.get("code"));
        return emailServices.verifyCode(email,code);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Create secure HttpOnly cookie
            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);   // JS can't read this cookie
            cookie.setSecure(true);     // Only HTTPS (for dev, you can set false)
            cookie.setPath("/");        // accessible for all endpoints
            cookie.setMaxAge(24 * 60 * 60); // 1 day expiry

            response.addCookie(cookie);

            // Optionally also return success message
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Incorrect username or password");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/validate-user")
    public ResponseEntity<?> validateUser(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token found");
        }

        try {
            // Extract username
            String username = jwtUtil.extractUsername(jwt);

            // Validate token against user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                return ResponseEntity.ok(Map.of("valid", true, "username", username));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}
