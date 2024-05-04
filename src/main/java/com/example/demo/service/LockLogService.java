package com.example.demo.service;


import com.example.demo.entity.LockLog;
import com.example.demo.repository.LockLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000,
        rollbackFor = Exception.class)
public class LockLogService {

    @Autowired
    private LockLogRepository lockLogRepository;

    public boolean tryLock(String lockName) {
        LockLog lockLog = lockLogRepository.findByLockNameAndLockedIsFalse(lockName);
        if (lockLog != null) {
            lockLog.setLocked(true);
            lockLog.setLockTime(System.currentTimeMillis());
            lockLogRepository.save(lockLog);
            return true;
        }
        return false;
    }

    public void unlock(String lockName) {
        LockLog lockLog = lockLogRepository.findByLockNameAndLockedIsTrue(lockName);
        if (lockLog != null) {
            lockLogRepository.delete(lockLog);
        }
    }

    public boolean isLocked(String lockName) {
        LockLog lockLog = lockLogRepository.findByLockNameAndLockedIsTrue(lockName);
        return lockLog != null;
    }
}
