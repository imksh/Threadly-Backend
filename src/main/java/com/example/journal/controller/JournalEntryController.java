package com.example.journal.controller;

import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.services.JournalEntryServices;
import com.example.journal.services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class JournalEntryController
{

    @Autowired
    private JournalEntryServices journalEntryServices;

    @Autowired
    private UserServices userServices;

    @GetMapping()
    public ResponseEntity<?> getAll()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findByUsername(username);
        List<JournalEntry> all = user.getJournalEntries();
        if(all!=null && !all.isEmpty())
        {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry entry)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        entry.setDate(LocalDateTime.now());
        entry.setUsername(username);
        journalEntryServices.saveEntry(username,entry);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }

    @GetMapping("/{myId}")
    public ResponseEntity<?> getById(@PathVariable String myId)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findByUsername(username);
        Optional<JournalEntry> entry1 = journalEntryServices.findById(new ObjectId(myId));
        if(entry1.get().isVisible())
        {
            return new ResponseEntity<>(entry1.get(), HttpStatus.OK);
        }
        else
        {
            List<JournalEntry> list = user.getJournalEntries().stream().filter(x -> x.getId().equals(new ObjectId(myId))).collect(Collectors.toList());
            if(!list.isEmpty())
            {
                Optional<JournalEntry> entry = journalEntryServices.findById(new ObjectId(myId));
                if(entry.isPresent())
                {
                    return new ResponseEntity<>(entry.get(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{myId}")
    public ResponseEntity<?> deleteById(@PathVariable String myId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        journalEntryServices.deleteById(username,new ObjectId(myId));
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{myId}")
    public ResponseEntity<?> updateById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findByUsername(username);
        List<JournalEntry> list = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!list.isEmpty())
        {
            Optional<JournalEntry> old = journalEntryServices.findById(myId);
            if(old.isPresent())
            {
                JournalEntry entry = old.get();
                entry.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : entry.getTitle());
                entry.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("") ? newEntry.getContent() : entry.getContent());
                journalEntryServices.saveEntry(entry);
                return new ResponseEntity<>(entry, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }


}
