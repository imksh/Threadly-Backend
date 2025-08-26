package com.example.journal.reposotiory;

import com.example.journal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepoImpl
{
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUser()
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Karan"));
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }

    public User findByEmail(String email)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class);
        return user;
    }
}
