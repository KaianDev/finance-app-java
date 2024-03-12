package br.com.money.service;

import br.com.money.exception.*;
import br.com.money.model.Activity;
import br.com.money.model.dto.*;
import br.com.money.model.TypeAct;
import br.com.money.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<ActivityResponseDto> getAll() {
        return this.activityRepository.findAll().stream().map(ActivityResponseDto::new).toList();
    }

    public List<ActivityResponseDto> filters(FilterDto filterDto) {
        if(filterDto.oneDate() != null && filterDto.secondDate() == null && filterDto.typeValue() == null) {
            return this.getByDate(filterDto);
        } else if(filterDto.oneDate() != null && filterDto.secondDate() != null && filterDto.typeValue() == null) {
            return this.getBetweenTwoDates(filterDto);
        } else if(filterDto.oneDate() != null && filterDto.secondDate() == null && filterDto.typeValue() != null){
            return this.getByValue(filterDto);
        }
        throw new RandomException("invalid data");
    }

    public List<ActivityResponseDto> getByDate(FilterDto filterDto) {

        List<ActivityResponseDto> list = this.activityRepository.findByDate(filterDto.oneDate()).stream().map(ActivityResponseDto::new).toList();
        if(list.isEmpty()) {
            throw new DateNotFoundException("The date given is not available");
        }
        return list;
    }

    public List<ActivityResponseDto> getBetweenTwoDates(FilterDto filterDto) {
        long dias = ChronoUnit.DAYS.between(filterDto.oneDate(), filterDto.secondDate());
        List<Activity> activityList = new ArrayList<>();
        List<Activity> activitiesLoop = null;
        for(int i = 0; i <= dias; i++) {
            activitiesLoop = this.activityRepository.findByDate(filterDto.oneDate().plusDays(i));
            for(Activity custom : activitiesLoop) {
                activityList.add(custom);
            }
        }
        if(activityList.isEmpty()) {
            throw new DateNotFoundException("There are no dates available in the range provided");
        }
        return activityList.stream().map(ActivityResponseDto::new).toList();
    }

    public List<ActivityResponseDto> getByValue(FilterDto filterDto) {
        List<ActivityResponseDto> listValue = this.getByDate(filterDto);
        List<ActivityResponseDto> listBalance = new ArrayList<>();

        if(listValue.isEmpty()) {
            throw new RandomException("There are no activities related to the passed parameter yet");
        }

        if(!filterDto.typeValue().equals("Despesa") && !filterDto.typeValue().equals("Receita")) {
            throw new RandomException("Unavailable parameter");
        }
        if(filterDto.typeValue().equals("Despesa")) {
            for(ActivityResponseDto custom : listValue) {
                if(custom.type().getTypeValue().equals(filterDto.typeValue())) {
                    listBalance.add(custom);
                }
            }
        }
        for(ActivityResponseDto custom : listValue) {
            if(custom.type().getTypeValue().equals(filterDto.typeValue())) {
                listBalance.add(custom);
            }
        }
        if(listBalance.isEmpty()) {
            throw new RandomException("There are no activities related to the passed parameter yet");
        }
        return listBalance;
    }

    public ActivityResponseDto addActivity(ActivityRequestDto activityRequestDto) {

        if(!validFields(activityRequestDto)) {
            throw new ValidFieldsException("Fill in all fields");
        }

        if(activityRequestDto.value() < 0.01) {
            throw new ValueZeroException("value less than or equal to zero");
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
        if(activityRequestDto.description() == null || activityRequestDto.description().isEmpty()) {
            return false;
        }
        if(activityRequestDto.date() == null || activityRequestDto.value() == null || activityRequestDto.type() == null) {
            return false;
        }
        return true;
    }
}
