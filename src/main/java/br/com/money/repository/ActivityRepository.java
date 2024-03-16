package br.com.money.repository;

import br.com.money.model.Activity;
import br.com.money.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByDate(LocalDate localDate);
    List<Activity> findByDateAndUser(LocalDate localDate, User user);
    List<Activity> findAllActivitiesByUser(User user);
}
