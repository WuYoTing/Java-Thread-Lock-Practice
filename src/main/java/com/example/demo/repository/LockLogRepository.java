package com.example.demo.repository;

import com.example.demo.entity.LockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LockLogRepository extends JpaRepository<LockLog, Long> {

    LockLog findByLockNameAndLockedIsFalse(String lockName);

    LockLog findByLockNameAndLockedIsTrue(String lockName);
}
