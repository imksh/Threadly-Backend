package com.example.journal.services;

import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.reposotiory.JournalEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryServices
{
    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private  UserServices userServices;

    @Transactional
    public void saveEntry(String username, JournalEntry journalEntry)
    {
        try
        {
            User user = userServices.findByUsername(username);
            List<JournalEntry> userEntries = user.getJournalEntries();
            JournalEntry saved = journalEntryRepo.save(journalEntry);
            userEntries.add(saved);
            userServices.saveUser(user);
        }
        catch (Exception e)
        {
            System.out.println(e);
            throw new RuntimeException("An Error occurred: "+e);
        }
    }

    public List<JournalEntry> getAll()
    {
        return journalEntryRepo.findAll();
    }


    public Optional<JournalEntry> findById(ObjectId id)
    {
        return journalEntryRepo.findById(id);
    }

    @Transactional
    public void deleteById(String username,ObjectId id)
    {

        try
        {
            User user = userServices.findByUsername(username);
            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed)
            {
                userServices.saveUser(user);
                journalEntryRepo.deleteById(id);
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
            throw new RuntimeException("An Error occurred: "+e);
        }
    }
}
