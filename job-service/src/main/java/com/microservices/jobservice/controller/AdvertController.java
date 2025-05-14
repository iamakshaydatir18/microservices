package com.microservices.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.jobservice.dto.AdvertDto;
import com.microservices.jobservice.enums.Advertiser;
import com.microservices.jobservice.request.advert.AdvertCreateRequest;
import com.microservices.jobservice.request.advert.AdvertUpdateRequest;
import com.microservices.jobservice.service.AdvertService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/job-service/advert")
@RequiredArgsConstructor
public class AdvertController {
    private final AdvertService advertService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<AdvertDto> createAdvert( @RequestPart("request") String request,
                                                  @RequestPart(required = false) MultipartFile file) {
        try{
            ObjectMapper  objectMapper = new ObjectMapper();
            AdvertCreateRequest requestObj = objectMapper.readValue(request, AdvertCreateRequest.class);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(modelMapper.map(advertService.createAdvert(requestObj, file), AdvertDto.class));

        }catch (Exception e){
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AdvertDto>> getAll() {
        return ResponseEntity.ok(advertService.getAll().stream()
                .map(advert -> modelMapper.map(advert, AdvertDto.class)).toList());
    }

    @GetMapping("/getAdvertById/{id}")
    public ResponseEntity<AdvertDto> getAdvertById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(advertService.getAdvertById(id), AdvertDto.class));
    }

    @GetMapping("/getAdvertsByUserId/{id}")
    public ResponseEntity<List<AdvertDto>> getAdvertsByUserId(@PathVariable String id,
                                                              @RequestParam Advertiser type) {
        return ResponseEntity.ok(advertService.getAdvertsByUserId(id, type).stream()
                .map(advert -> modelMapper.map(advert, AdvertDto.class)).toList());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @advertService.authorizeCheck(#request.id, principal)")
    public ResponseEntity<AdvertDto> updateAdvertById(@RequestPart("request") String request,
                                                      @RequestPart(required = false) MultipartFile file) {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdvertUpdateRequest req = objectMapper.readValue(request , AdvertUpdateRequest.class);

            return ResponseEntity.ok(modelMapper.map(advertService.updateAdvertById(req, file), AdvertDto.class));
        }catch (Exception e){
            System.err.println("Error processing Create request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/deleteAdvertById/{id}")
    @PreAuthorize("hasRole('ADMIN') or @advertService.authorizeCheck(#id, principal)")
    public ResponseEntity<Void> deleteAdvertById(@PathVariable String id) {
        advertService.deleteAdvertById(id);
        return ResponseEntity.ok().build();
    }
}
