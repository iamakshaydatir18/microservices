package com.microservices.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.jobservice.dto.CategoryDto;
import com.microservices.jobservice.dto.JobDto;
import com.microservices.jobservice.request.category.CategoryCreateRequest;
import com.microservices.jobservice.request.category.CategoryUpdateRequest;
import com.microservices.jobservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/job-service/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CategoryDto> createCategory(@RequestPart String request,
                                               @RequestPart(required = false) MultipartFile file) {
        try{
            ObjectMapper obj = new ObjectMapper();
            CategoryCreateRequest requestObj = obj.readValue(request, CategoryCreateRequest.class);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(modelMapper.map(categoryService.createCategory(requestObj,file), CategoryDto.class));

        }catch (Exception e){
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAll")
    ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class)).toList());
    }

    @GetMapping("/getCategoryById/{id}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(categoryService.getCategoryById(id), CategoryDto.class));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<JobDto> updateCategoryById(@RequestPart String request,
                                              @RequestPart(required = false) MultipartFile file) {

        try{
            ObjectMapper obj = new ObjectMapper();
            CategoryUpdateRequest requestObj = obj.readValue(request, CategoryUpdateRequest.class);

            return ResponseEntity.ok(modelMapper.map(categoryService.updateCategoryById(requestObj,file), JobDto.class));

        }catch (Exception e) {
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteCategoryById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteCategoryById(@PathVariable String id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }
}
