package com.inixindo.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "listing")
public class Listing {
    // atribut
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String judul;
    private String deskripsi;
    private double harga;
    private String kategori;
    private String username;
    private String nohp;

    // @Lob
    // private byte[] foto;

    // public Integer getId() {
    //   return this.Id;
    // }
    // public void setId(Integer value) {
    //   this.Id = value;
    // }

    // public String getJudul() {
    //   return this.judul;
    // }
    // public void setJudul(String value) {
    //   this.judul = value;
    // }

    // public String getDeskripsi() {
    //   return this.deskripsi;
    // }
    // public void setDeskripsi(String value) {
    //   this.deskripsi = value;
    // }

    // public double getHarga() {
    //   return this.harga;
    // }
    // public void setHarga(double value) {
    //   this.harga = value;
    // }

    // public String getKategori() {
    //   return this.kategori;
    // }
    // public void setKategori(String value) {
    //   this.kategori = value;
    // }

    // public String getUsername() {
    //   return this.username;
    // }
    // public void setUsername(String value) {
    //   this.username = value;
    // }

    // public String getNohp() {
    //   return this.nohp;
    // }
    // public void setNohp(String value) {
    //   this.nohp = value;
    // }
}
