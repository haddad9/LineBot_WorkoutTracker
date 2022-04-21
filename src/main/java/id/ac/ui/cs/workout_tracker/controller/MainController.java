package id.ac.ui.cs.workout_tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> homepage(Model model) {

        return ResponseEntity.ok("-Halo dunia");
    }
    
    
}
