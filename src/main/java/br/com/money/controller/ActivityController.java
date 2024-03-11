package br.com.money.controller;

import br.com.money.model.Activity;
import br.com.money.model.dto.ActivityRequestDto;
import br.com.money.model.dto.ActivityResponseDto;
import br.com.money.model.dto.BetweenTwoDatesDto;
import br.com.money.model.dto.DateRequestDto;
import br.com.money.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping("/getByDate")
    public ResponseEntity<List<ActivityResponseDto>> getByDate(@RequestBody DateRequestDto dateRequestDto) {
        return new ResponseEntity<>(this.activityService.getByDate(dateRequestDto), HttpStatus.OK);
    }

    @GetMapping("/getBetween")
    public ResponseEntity<List<ActivityResponseDto>> getBetweenDates(@RequestBody BetweenTwoDatesDto betweenTwoDatesDto) {
        this.activityService.getBetweenTwoDates(betweenTwoDatesDto);
        return new ResponseEntity<>(this.activityService.getBetweenTwoDates(betweenTwoDatesDto), HttpStatus.OK);
    }
    @GetMapping("/getByValueType")
    public ResponseEntity<List<ActivityResponseDto>> getByValue(@RequestParam String valueType) {
        return new ResponseEntity<>(this.activityService.getByValue(valueType), HttpStatus.OK);
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
