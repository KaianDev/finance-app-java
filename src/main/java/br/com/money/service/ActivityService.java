package br.com.money.service;

import br.com.money.model.Activity;
import br.com.money.model.dto.ActivityRequestDto;
import br.com.money.model.dto.ActivityResponseDto;
import br.com.money.model.TypeAct;
import br.com.money.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<ActivityResponseDto> getAll() {
        return this.activityRepository.findAll().stream().map(ActivityResponseDto::new).toList();
    }

    public ActivityResponseDto addActivity(ActivityRequestDto activityRequestDto) {
        Activity activity = new Activity();
        activity.setDate(activityRequestDto.date());
        activity.setDescription(activityRequestDto.description());
        activity.setValue(activityRequestDto.value());
        activity.setType(activityRequestDto.type());
        Activity newActivity = this.activityRepository.save(activity);
        return new ActivityResponseDto(newActivity);
    }

    public void deleteActivity(Long id) {
        Activity activity = this.activityRepository.findById(id).get();
        this.activityRepository.delete(activity);
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
}
