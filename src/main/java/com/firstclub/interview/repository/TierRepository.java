package com.firstclub.interview.repository;

import com.firstclub.interview.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {
    Optional<Tier> findByName(String name);
    List<Tier> findAllByOrderByRankAsc();
}
