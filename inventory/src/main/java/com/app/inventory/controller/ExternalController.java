package com.app.inventory.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// anotasi rest controller agar dapat dibaca oleh main program
@RestController
@RequestMapping("/api/bean")
public class ExternalController {
    // gunakan bean
    private final String pesan;

    // bean diambil berdasarkan tipe data/objek, di appconfig didefine tipe data string, kemudian di parameter pesan juga tipe datanya string, otomatis akan mengambil fungsi apppesan di appconfig
    public ExternalController(@Qualifier("appPesan") String pesan){
        this.pesan = pesan;
    }

    @GetMapping
    public String getPesan(){
        return pesan;
    }
}
