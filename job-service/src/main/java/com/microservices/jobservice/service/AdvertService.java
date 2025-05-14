package com.microservices.jobservice.service;

import com.microservices.jobservice.client.FileStorageClient;
import com.microservices.jobservice.client.UserServiceClient;
import com.microservices.jobservice.dto.UserDto;
import com.microservices.jobservice.enums.AdvertStatus;
import com.microservices.jobservice.enums.Advertiser;
import com.microservices.jobservice.exc.NotFoundException;
import com.microservices.jobservice.model.Advert;
import com.microservices.jobservice.model.Job;
import com.microservices.jobservice.repository.AdvertRepository;
import com.microservices.jobservice.request.advert.AdvertCreateRequest;
import com.microservices.jobservice.request.advert.AdvertUpdateRequest;
import com.microservices.jobservice.request.notification.SendNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertService {
    private final AdvertRepository advertRepository;
    private final JobService jobService;
    private final UserServiceClient userServiceclient;
    private final FileStorageClient fileStorageClient;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, SendNotificationRequest>  kafkaTemplate;
    private final NewTopic topic;


    public Advert createAdvert(AdvertCreateRequest request, MultipartFile file) {
        String userId = getUserById(request.getUserId()).getId();
        Job job = jobService.getJobById(request.getJobId());

        String imageId = null;

        if (file != null)
            imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();

        Advert toSave = Advert.builder()
                .userId(userId)
                .job(job)
                .name(request.getName())
                .advertiser(request.getAdvertiser())
                .deliveryTime(request.getDeliveryTime())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(AdvertStatus.OPEN)
                .imageId(imageId)
                .build();


         Advert adv = advertRepository.save(toSave);

        SendNotificationRequest notification = SendNotificationRequest.builder()
                .message("User has created Advert...")
                .userId(adv.getUserId())
                .advertId(adv.getId()).build();

        kafkaTemplate.send(topic.name(), notification);

        return adv;
    }

    public List<Advert> getAll() {
        return advertRepository.findAll();
    }

    public Advert getAdvertById(String id) {
        return findAdvertById(id);
    }

    public List<Advert> getAdvertsByUserId(String id, Advertiser type) {
        String userId = getUserById(id).getId();
        return advertRepository.getAdvertsByUserIdAndAdvertiser(userId, type);
    }

    public UserDto getUserById(String id) {
        return Optional.ofNullable(userServiceclient.getUserById(id).getBody())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Advert updateAdvertById(AdvertUpdateRequest request, MultipartFile file) {
        Advert toUpdate = findAdvertById(request.getId());
        String existingImage = toUpdate.getImageId();
        String userID = toUpdate.getUserId();

        modelMapper.map(request, toUpdate);

        if (file != null) {
            String imageId = fileStorageClient.uploadImageToFIleSystem(file).getBody();
            if (imageId != null) {

                if(existingImage != null){
                    fileStorageClient.deleteImageFromFileSystem(existingImage);
                }
                toUpdate.setImageId(imageId);
            }
        }

        toUpdate.setUserId(userID);

        Advert adv = advertRepository.save(toUpdate);

        SendNotificationRequest notification = SendNotificationRequest.builder()
                .message("User has updated Advert...")
                .userId(adv.getUserId())
                .advertId(adv.getId()).build();

        kafkaTemplate.send(topic.name(), notification);

        return adv;
    }

    public void deleteAdvertById(String id) {
        advertRepository.deleteById(id);
    }

    public boolean authorizeCheck(String id, String principal) {
        return getUserById(getAdvertById(id).getUserId()).getUsername().equals(principal);
    }

    protected Advert findAdvertById(String id) {
        return advertRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Advert not found"));
    }
}
