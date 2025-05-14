package com.microservices.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.jobservice.dto.JobDto;
import com.microservices.jobservice.request.job.JobCreateRequest;
import com.microservices.jobservice.request.job.JobUpdateRequest;
import com.microservices.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/job-service/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<JobDto> createJob(@RequestPart("request") String requestJson,
                                     @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JobCreateRequest requestObj = objectMapper.readValue(requestJson, JobCreateRequest.class);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(modelMapper.map(jobService.createJob(requestObj, file), JobDto.class));
        } catch (Exception e) {
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/getJobsThatFitYourNeeds/{needs}")
    ResponseEntity<List<JobDto>> getJobsThatFitYourNeeds(@PathVariable String needs) {
        return ResponseEntity.ok(jobService.getJobsThatFitYourNeeds(needs).stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @GetMapping("/getAll")
    ResponseEntity<List<JobDto>> getAll() {
        return ResponseEntity.ok(jobService.getAll().stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @GetMapping("/getJobById/{id}")
    ResponseEntity<JobDto> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(jobService.getJobById(id), JobDto.class));
    }

    @GetMapping("/getJobsByCategoryId/{id}")
    ResponseEntity<List<JobDto>> getJobsByCategoryId(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobsByCategoryId(id).stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<JobDto> updateJob(@RequestPart("request") String requestJson,
                                     @RequestPart(value = "file", required = false) MultipartFile file) {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JobUpdateRequest requestObj = objectMapper.readValue(requestJson, JobUpdateRequest.class);
            return ResponseEntity.ok(modelMapper.map(jobService.updateJob(requestObj, file), JobDto.class));

        }catch(Exception e){
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteJobById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteJobById(@PathVariable String id) {
        jobService.deleteJobById(id);
        return ResponseEntity.ok().build();
    }
}
