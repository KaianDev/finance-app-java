package br.com.money.controller;

import br.com.money.model.dto.*;
import br.com.money.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/getAll")
    public ResponseEntity<List<ActivityResponseDto>> getAll() {
        return new ResponseEntity<List<ActivityResponseDto>>(this.activityService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ActivityResponseDto>> filters(@RequestBody FilterDto filterDto) {
        return new ResponseEntity<>(this.activityService.filters(filterDto), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ActivityResponseDto> addActivity(@RequestBody ActivityRequestDto activityRequestDto) {
        return new ResponseEntity<>(this.activityService.addActivity(activityRequestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam Long id) {
        this.activityService.deleteActivity(id);
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> balance() {
        return new ResponseEntity<Double>(this.activityService.balance(), HttpStatus.OK);
    }
}
