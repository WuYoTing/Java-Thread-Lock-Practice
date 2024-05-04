package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "lock_log")
public class LockLog {

    @Id
    private Long id;

    private String lockName;

    private boolean locked;

    private long lockTime;
}
