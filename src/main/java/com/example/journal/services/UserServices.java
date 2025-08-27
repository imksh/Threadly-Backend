package com.example.journal.services;

import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.reposotiory.UserRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserServices
{
    @Autowired
    private UserRepo userRepo;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean saveNewUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepo.save(user);
        return true;
    }

    public boolean saveUser(User user)
    {
        userRepo.save(user);
        return true;
    }

    public boolean saveAdmin(User user)
    {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepo.save(user);
        return true;
    }

    public List<User> getAll()
    {
        return new ArrayList<>(userRepo.findAll());
    }

    public Optional<User> getById(ObjectId id)
    {
        return userRepo.findById(id);
    }

    public boolean delete(ObjectId id)
    {
        userRepo.deleteById(id);
        return true;
    }

    public List<JournalEntry> getUserEntries(String userName)
    {
        User user = findByUsername(userName);
        return user.getJournalEntries();
    }

    public User findByUsername(String userName)
    {
       return userRepo.findByUsername(userName);
    }
    public User findByEmail(String email)
    {
        return userRepo.findByUsername(email);
    }
}
