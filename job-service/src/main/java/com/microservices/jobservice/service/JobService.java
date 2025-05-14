package com.microservices.jobservice.service;

import com.microservices.jobservice.client.FileStorageClient;
import com.microservices.jobservice.exc.NotFoundException;
import com.microservices.jobservice.model.Category;
import com.microservices.jobservice.model.Job;
import com.microservices.jobservice.repository.JobRepository;
import com.microservices.jobservice.request.job.JobCreateRequest;
import com.microservices.jobservice.request.job.JobUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final CategoryService categoryService;
    private final FileStorageClient fileStorageClient;
    private final ModelMapper modelMapper;

    public Job createJob(JobCreateRequest request, MultipartFile file) {
        Category category = categoryService.getCategoryById(request.getCategoryId());

        String imageId = null;

        if (file != null)
            imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();

        return jobRepository.save(Job.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .keys(Optional.of(List.of(request.getKeys()))
                        .orElse(new ArrayList<>()))
                .imageId(imageId)
                .build());
    }

    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    public Job getJobById(String id) {
        return findJobById(id);
    }

    public Job updateJob(JobUpdateRequest request, MultipartFile file) {
        System.out.println("Mapped model into JobUpdateRequest "+ request.toString());

        Job toUpdate = findJobById(request.getId());

        String existingImageId =  toUpdate.getImageId();
        modelMapper.map(request, toUpdate);

        if (file != null) {
            String imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();
            if (imageId != null) {
                if(existingImageId != null){
                    fileStorageClient.deleteImageFromFileSystem(existingImageId);
                }
                toUpdate.setImageId(imageId);
            }
        }

        return jobRepository.save(toUpdate);
    }

    public void deleteJobById(String id) {
        jobRepository.deleteById(id);
    }

    public List<Job> getJobsByCategoryId(String id) {
        return jobRepository.getJobsByCategoryId(id);
    }

    public List<Job> getJobsThatFitYourNeeds(String needs) {
        String[] keys = needs.replaceAll("\"", "").split(" ");
        HashMap<String, Integer> map = new HashMap<>();
        Arrays.stream(keys).forEach(key -> jobRepository.getJobsByKeysContainsIgnoreCase(key)
                .forEach(job -> {
                    if (map.containsKey(job.getId())) {
                        int count = map.get(job.getId());
                        map.put(job.getId(), count + 1);
                    } else map.put(job.getId(), 1);
                }));
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> findJobById(entry.getKey()))
                .collect(Collectors.toList());
    }

    protected Job findJobById(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found"));
    }
}
