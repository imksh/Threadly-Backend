package com.example.journal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailServices
{
    @Autowired
    private JavaMailSender javaMailSender;

    private Map<String, Integer> verificationCodes = new HashMap<>();

    private int generateCode() {
        return (int) (Math.random()*9000)+1000;
    }

    public void sendEmail(String email)
    {
        try{
            int code = generateCode();
            verificationCodes.put(email,code);
            String subject = "Verify email for Threadly";
            String body = "Your verification code for threadly is: " + code;
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
        }
        catch (Exception e)
        {
            System.out.println("An error occurred "+ e);
        }
    }
    public boolean verifyCode(String email, int inputCode) {
        Integer code = verificationCodes.get(email);
        if (code != null && code == inputCode) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }
}
