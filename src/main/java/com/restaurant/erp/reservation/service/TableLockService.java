package com.restaurant.erp.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TableLockService {

    private final RedissonClient redissonClient;

    public boolean lockTable(Integer tableId, String bookingDateStr, long waitTimeSeconds, long leaseTimeSeconds) {
        String lockKey = "lock:table:" + tableId + ":" + bookingDateStr;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            log.info("Attempting to acquire distributed lock for table {} on {}", tableId, bookingDateStr);
            return lock.tryLock(waitTimeSeconds, leaseTimeSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for table {}", tableId, e);
            return false;
        }
    }

    public void unlockTable(Integer tableId, String bookingDateStr) {
        String lockKey = "lock:table:" + tableId + ":" + bookingDateStr;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.info("Released distributed lock for table {} on {}", tableId, bookingDateStr);
        }
    }
}
