package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.SessionRepository;
import com.gym.backend.model.Session;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionService {
    @Autowired
    private SessionRepository repository;

    public Session findById(long id){
        return repository.findById(id).orElse(null);
    }

    public List<Session> findAll(){
        List<Session> result = new ArrayList<>();
        repository.findAll().forEach(aSession -> result.add(aSession));
        return result;
    }

    @Transactional
    public Session save(Session aSession){
        return repository.save(aSession);
    }

    @Transactional
    public void delete(long sessionId){
        repository.deleteById(sessionId);
    }
}
