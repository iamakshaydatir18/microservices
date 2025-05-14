package com.microservices.jobservice.request.advert;

import com.microservices.jobservice.enums.Advertiser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdvertCreateRequest {
    @NotBlank(message = "Advert name is required")
    private String name;
    private String description;
    @NotNull(message = "Delivery time is required")
    private int deliveryTime;
    @NotNull(message = "Price is required")
    private int price;
    @NotBlank(message = "Advertiser is required")
    private Advertiser advertiser;
    @NotBlank(message = "User id is required")
    private String userId;
    @NotBlank(message = "Job id is required")
    private String jobId;
}
