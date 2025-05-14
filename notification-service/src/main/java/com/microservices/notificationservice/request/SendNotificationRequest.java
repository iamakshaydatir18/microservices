package com.microservices.notificationservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor // Add this annotation
@AllArgsConstructor
public class SendNotificationRequest {
    private String userId;
    private String offerId;
    private String message;
    private String advertId;
}
