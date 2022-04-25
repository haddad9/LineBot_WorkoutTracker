package id.ac.ui.cs.workout_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.workout_tracker.model.Workout;

public interface WorkoutRepository extends JpaRepository<Workout, Integer>{
    Workout findById(int id);
}
