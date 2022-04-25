package id.ac.ui.cs.workout_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;

import id.ac.ui.cs.workout_tracker.model.User;
import id.ac.ui.cs.workout_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * DatabaseService
 */

@Service
public class DatabaseService {

    @Autowired
    UserRepository userRepository;
    
    public int regLineID(String userId, String displayName){

        User user = new User(userId, displayName);
        userRepository.save(user);

        return 0;

    }

    
    
}