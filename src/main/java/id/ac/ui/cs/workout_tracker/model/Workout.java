package id.ac.ui.cs.workout_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="workout")
public class Workout {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name="wo_day")
    private String day;

    @Column(name="wo_name")
    private String name;

    @Column(name="wo_sets")
    private int sets;

    @Column(name="wo_reps")
    private int reps;

    @ManyToOne
    @JoinColumn(name="wo_user", referencedColumnName = "id")
    private User user;

    public Workout(String woDay, String woName, int woSets, int woReps) {
        this.day = woDay;
        this.name = woName;
        this.sets = woSets;
        this.reps = woReps;
    }

    public String getDay() {
        return this.day;
    }

    public String getName() {
        return this.name;
    }

    public int getSets() {
        return this.sets;
    }

    public int getReps() {
        return this.reps;
    }
}
