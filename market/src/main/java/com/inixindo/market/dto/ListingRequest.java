package com.inixindo.market.dto;

import io.micrometer.common.lang.NonNull;

public record ListingRequest(@NonNull String judul, String deskripsi, double harga, String kategori, String username, String nohp) {
    
}

