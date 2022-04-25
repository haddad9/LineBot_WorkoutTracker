package id.ac.ui.cs.workout_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

/**
 * User
 */

 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user")
public class User {
    @Column(name="line_id")
    private String lineId;

    @Column(name="display_name")
    String displayName; 

    @Id
    @Column(name="user_id")
    private String userId;

    @Column(name="berat")
    private double berat;

    @Column(name="tinggi")
    private double tinggi; 

    @Column (name="is_female")
    private boolean isFemale;

    @OneToMany(mappedBy = "user")
    private List<Workout> listWorkout;

      public User(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
      return this.userId;
    }

    public List<Workout> getListWorkout() {
      return this.listWorkout;
    }

    public void setListWorkout(List<Workout> newListWorkout) {
      this.listWorkout = newListWorkout;
  }
}