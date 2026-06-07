package com.firstclub.interview.repository;

import com.firstclub.interview.entity.UserMembership;
import com.firstclub.interview.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    Optional<UserMembership> findByUserIdAndStatus(Long userId, MembershipStatus status);
    Page<UserMembership> findByUserId(Long userId, Pageable pageable);
}
