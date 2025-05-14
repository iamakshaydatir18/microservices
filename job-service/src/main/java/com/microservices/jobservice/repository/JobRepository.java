package com.microservices.jobservice.repository;

import com.microservices.jobservice.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> getJobsByCategoryId(String id);

    List<Job> getJobsByKeysContainsIgnoreCase(String key);
}
