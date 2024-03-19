package br.com.money.controller;

import br.com.money.model.dto.*;
import br.com.money.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/getAll")
    public ResponseEntity<List<ActivityResponseDto>> getAll(Pageable pageable, HttpServletRequest request) {
        return new ResponseEntity<List<ActivityResponseDto>>(this.activityService.getAllPagination(pageable, request), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ActivityResponseDto>> filters(Pageable pageable ,LocalDate oneDate, LocalDate secondDate, String typeValue, HttpServletRequest request) {
        return new ResponseEntity<>(this.activityService.filters(pageable ,oneDate, secondDate, typeValue, request), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ActivityResponseDto> addActivity(@RequestBody ActivityRequestDto activityRequestDto, HttpServletRequest request) {
        return new ResponseEntity<>(this.activityService.addActivity(activityRequestDto, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam Long id, HttpServletRequest request) {
        this.activityService.deleteActivity(id, request);
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> balance(Pageable pageable ,HttpServletRequest request) {
        return new ResponseEntity<Double>(this.activityService.balance(pageable ,request), HttpStatus.OK);
    }
}
