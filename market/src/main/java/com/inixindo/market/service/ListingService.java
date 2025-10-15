package com.inixindo.market.service;

import org.springframework.stereotype.Service;

import com.inixindo.market.model.Listing;
import com.inixindo.market.repository.ListingRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ListingService {
    // repo
    private final ListingRepository repo;

    // fungsi tambah data
    @Transactional
    public String tambah(Listing request){
        Listing baru = new Listing();
        baru.setJudul(request.getJudul());
        baru.setDeskripsi(request.getDeskripsi());
        baru.setHarga(request.getHarga());
        baru.setKategori(request.getKategori());
        baru.setUsername(request.getUsername());
        baru.setNohp(request.getNohp());

        // simpan
        repo.save(baru);

        return "berhasil input data";
    }
}
