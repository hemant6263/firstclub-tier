package com.firstclub.interview.repository;

import com.firstclub.interview.entity.MembershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, Long> {
    List<MembershipHistory> findByUserMembershipIdOrderByCreatedAtDesc(Long membershipId);
}
