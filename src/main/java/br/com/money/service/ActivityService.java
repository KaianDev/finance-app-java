package br.com.money.service;

import br.com.money.exception.*;
import br.com.money.model.Activity;
import br.com.money.model.dto.*;
import br.com.money.model.TypeAct;
import br.com.money.repository.ActivityPaginationRepository;
import br.com.money.repository.ActivityRepository;
import br.com.money.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityPaginationRepository paginationRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenConvert tokenConvert;

    public List<ActivityResponseDto> getAllPagination(Pageable pageable ,HttpServletRequest request) {
        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var user = this.userRepository.findByEmail(email);

        if(!user.getStatus()) {
            throw new RandomException("User inactive");
        }

        return this.paginationRepository.findAllByUser(pageable ,user).stream().map(ActivityResponseDto::new).toList();
    }

    public List<ActivityResponseDto> filters(Pageable pageable ,LocalDate oneDate, LocalDate secondDate, String typeValue, HttpServletRequest request) {
        if(oneDate != null && secondDate == null && typeValue == null) {
            return this.getByDate(oneDate, request);
        } else if(oneDate != null && secondDate != null && typeValue == null) {
            return this.getBetweenTwoDates(oneDate, secondDate, request);
        } else if(oneDate != null && secondDate == null && typeValue != null){
            return this.getByValue(oneDate, typeValue, request);
        } else if(oneDate == null && secondDate == null && typeValue != null) {
            return this.getByValueType(pageable, typeValue, request);
        }
        throw new RandomException("invalid data");
    }

    public List<ActivityResponseDto> getByDate(LocalDate oneDate, HttpServletRequest request) {
        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var user = this.userRepository.findByEmail(email);

        if(!user.getStatus()) {
            throw new RandomException("User inactive");
        }

        return this.activityRepository.findByDateAndUser(oneDate, user).stream().map(ActivityResponseDto::new).toList();
    }

    public List<ActivityResponseDto> getBetweenTwoDates(LocalDate oneDate, LocalDate secondDate, HttpServletRequest request) {
        long dias = ChronoUnit.DAYS.between(oneDate, secondDate);
        List<Activity> activityList = new ArrayList<>();
        List<Activity> activitiesLoop = new ArrayList<>();
        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var user = this.userRepository.findByEmail(email);

        if(!user.getStatus()) {
            throw new RandomException("User inactive");
        }

        for(int i = 0; i <= dias; i++) {
            activitiesLoop = this.activityRepository.findByDateAndUser(oneDate.plusDays(i), user);
            for(Activity custom : activitiesLoop) {
                activityList.add(custom);
            }
        }
        return activityList.stream().map(ActivityResponseDto::new).toList();
    }

    public List<ActivityResponseDto> getByValue(LocalDate oneDate, String typeValue, HttpServletRequest request) {
        List<ActivityResponseDto> listValue = this.getByDate(oneDate, request);
        List<ActivityResponseDto> listBalance = new ArrayList<>();

        if(listValue.isEmpty()) {
            throw new RandomException("There are no activities related to the passed parameter yet");
        }

        if(!typeValue.equals("expense") && !typeValue.equals("revenue")) {
            throw new RandomException("Unavailable parameter");
        }
        if(typeValue.equals("expense")) {
            for(ActivityResponseDto custom : listValue) {
                if(custom.type().getTypeValue().equals(typeValue)) {
                    listBalance.add(custom);
                }
            }
        } else {
            for(ActivityResponseDto custom : listValue) {
                if(custom.type().getTypeValue().equals(typeValue)) {
                    listBalance.add(custom);
                }
            }
        }
        return listBalance;
    }

    public List<ActivityResponseDto> getByValueType(Pageable pageable,String typeValue, HttpServletRequest request) {
        List<ActivityResponseDto> listValue = this.getAllPagination(pageable ,request);
        List<ActivityResponseDto> listBalance = new ArrayList<>();

        if(listValue.isEmpty()) {
            throw new RandomException("There are no activities related to the passed parameter yet");
        }

        if(!typeValue.equals("expense") && !typeValue.equals("revenue")) {
            throw new RandomException("Unavailable parameter");
        }
        if(typeValue.equals("expense")) {
            for(ActivityResponseDto custom : listValue) {
                if(custom.type().getTypeValue().equals(typeValue)) {
                    listBalance.add(custom);
                }
            }
        } else {
            for(ActivityResponseDto custom : listValue) {
                if(custom.type().getTypeValue().equals(typeValue)) {
                    listBalance.add(custom);
                }
            }
        }
        return listBalance;
    }

    public ActivityResponseDto addActivity(ActivityRequestDto activityRequestDto, HttpServletRequest request) {

        if(!validFields(activityRequestDto)) {
            throw new ValidFieldsException("Fill in all fields");
        }

        if(activityRequestDto.value() < 0.01) {
            throw new ValueZeroException("value less than or equal to zero");
        }

        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var user = this.userRepository.findByEmail(email);

        if(!user.getStatus()) {
            throw new RandomException("User inactive");
        }

        Activity activity = new Activity();
        activity.setDate(activityRequestDto.date());
        activity.setDescription(activityRequestDto.description());
        activity.setValue(activityRequestDto.value());
        activity.setType(activityRequestDto.type());
        activity.setUser(user);
        Activity newActivity = this.activityRepository.save(activity);
        return new ActivityResponseDto(newActivity);
    }

    public void deleteActivity(Long id, HttpServletRequest request) {
        var email = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var user = this.userRepository.findByEmail(email);

        if(!user.getStatus()) {
            throw new RandomException("User inactive");
        }

        Optional<Activity> activity = this.activityRepository.findById(id);
        if(activity.isEmpty()) {
            throw new UserNotFoundException("Not found activity");
        }
        if(user.getId() != activity.get().getUser().getId()) {
            throw new RandomException("Activity not exist");
        }
        this.activityRepository.delete(activity.get());
    }

    public Double balance(Pageable pageable ,HttpServletRequest request) {
        List<ActivityResponseDto> listBalance = this.getAllPagination(pageable ,request);
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
