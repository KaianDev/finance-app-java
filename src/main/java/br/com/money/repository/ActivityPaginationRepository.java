package br.com.money.repository;

import br.com.money.model.Activity;
import br.com.money.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ActivityPaginationRepository extends PagingAndSortingRepository<Activity, Long> {
    List<Activity> findAllByUser(Pageable pageable, User user);
}
