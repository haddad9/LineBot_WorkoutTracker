package id.ac.ui.cs.workout_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.workout_tracker.model.User;


public interface UserRepository extends JpaRepository<User, Integer>{
    User findByUserId(String string);
}