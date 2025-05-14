package com.microservices.jobservice.service;

import com.microservices.jobservice.client.FileStorageClient;
import com.microservices.jobservice.exc.NotFoundException;
import com.microservices.jobservice.model.Category;
import com.microservices.jobservice.repository.CategoryRepository;
import com.microservices.jobservice.request.category.CategoryCreateRequest;
import com.microservices.jobservice.request.category.CategoryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileStorageClient fileStorageClient;
    private final ModelMapper modelMapper;

    public Category createCategory(CategoryCreateRequest request, MultipartFile file) {
        String imageId = null;

        if (file != null)
            imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();

        return categoryRepository.save(
                Category.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .imageId(imageId)
                        .build());
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        System.out.println("Inside Finding category with id"+ id);
        return findCategoryById(id);
    }

    public Category updateCategoryById(CategoryUpdateRequest request, MultipartFile file) {
        System.out.println("getting category with ID given - " + request.getId());
        Category toUpdate = findCategoryById(request.getId());

        System.out.println("found Catgory .." + toUpdate.toString());

        modelMapper.map(request, toUpdate);

        System.out.println("After modelMapper Catgory .." + toUpdate.toString());

        if (file != null) {
            String imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();

            System.out.println("created Image ID - " + imageId );
            if (imageId != null) {

                System.out.println("to update Image all ready ID - " + toUpdate.getImageId() );
                if(toUpdate.getImageId() != null){
                    fileStorageClient.deleteImageFromFileSystem(toUpdate.getImageId());
                }

                System.out.println("Updating image id as its null when created..");
                toUpdate.setImageId(imageId);
            }
        }

        return categoryRepository.save(toUpdate);
    }

    public void deleteCategoryById(String id) {
        categoryRepository.deleteById(id);
    }

    protected Category findCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
