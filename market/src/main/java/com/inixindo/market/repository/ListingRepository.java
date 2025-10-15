package com.inixindo.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inixindo.market.model.Listing;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Integer> {
    
}
