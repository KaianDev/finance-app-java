package br.com.money.service;

import br.com.money.exception.TypeException;
import br.com.money.exception.UserNotFoundException;
import br.com.money.exception.ValidFieldsException;
import br.com.money.model.Activity;
import br.com.money.model.dto.ActivityRequestDto;
import br.com.money.model.dto.ActivityResponseDto;
import br.com.money.model.TypeAct;
import br.com.money.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<ActivityResponseDto> getAll() {
        return this.activityRepository.findAll().stream().map(ActivityResponseDto::new).toList();
    }

    public ActivityResponseDto addActivity(ActivityRequestDto activityRequestDto) {

        if(activityRequestDto.type() != TypeAct.REVENUE && activityRequestDto.type() != TypeAct.EXPENSE) {
            throw new TypeException("Invalid input type");
        }
        if(!validFields(activityRequestDto)) {
            throw new ValidFieldsException("Fill in all fields");
        }
        Activity activity = new Activity();
        activity.setDate(activityRequestDto.date());
        activity.setDescription(activityRequestDto.description());
        activity.setValue(activityRequestDto.value());
        activity.setType(activityRequestDto.type());
        Activity newActivity = this.activityRepository.save(activity);
        return new ActivityResponseDto(newActivity);
    }

    public void deleteActivity(Long id) {
        Optional<Activity> activity = this.activityRepository.findById(id);
        if(activity.isEmpty()) {
            throw new UserNotFoundException("Not found User");
        }
        this.activityRepository.delete(activity.get());
    }

    public Double balance() {
        List<ActivityResponseDto> listBalance = this.getAll();
        Double sum = 0.0;
        for(ActivityResponseDto x : listBalance) {
            if(x.type() == TypeAct.REVENUE) {
                sum += x.value();
            }
            if(x.type() == TypeAct.EXPENSE) {
                sum -= x.value();
            }
        }
        return sum;
    }

    private boolean validFields(ActivityRequestDto activityRequestDto) {
        if(activityRequestDto.description() == null || activityRequestDto.date() == null || activityRequestDto.value() == null) {
            return false;
        }
        return true;
    }
}
