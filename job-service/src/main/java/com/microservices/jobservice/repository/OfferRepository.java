package com.microservices.jobservice.repository;

import com.microservices.jobservice.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, String> {
    List<Offer> getOffersByUserId(String id);

    List<Offer> getOffersByAdvertId(String id);
}
