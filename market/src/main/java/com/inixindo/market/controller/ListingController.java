package com.inixindo.market.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inixindo.market.model.Listing;
import com.inixindo.market.service.ListingService;

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    // service listing
    ListingService listingService;

    // post data
    @PostMapping("path")
    public String simpanListing(@RequestBody Listing entity){
        return listingService.tambah(entity);
    }
    
}
