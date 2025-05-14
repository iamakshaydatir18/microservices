package com.microservices.jobservice.request.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendNotificationRequest {
    private String userId;
    private String offerId;
    private String message;
    private String advertId;
}
