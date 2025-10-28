package com.app.inventory.model;

public class Item {
    // sesuaikan atribut yang mau kita olah/butuhkan saja, tidak perlu semua kolom dari tabel database di-define di sini

    // atribut
    private Long id;
    private String nama;
    private int jumlah;
    private String lokasi;

    // constructor
    public Item(){}

    public Item(Long id, String nama, int jumlah, String lokasi){
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.lokasi = lokasi;
    }

    // setter getter
    public Long getId() {
      return this.id;
    }
    public void setId(Long value) {
      this.id = value;
    }

    public String getNama() {
      return this.nama;
    }
    public void setNama(String value) {
      this.nama = value;
    }

    public int getJumlah() {
      return this.jumlah;
    }
    public void setJumlah(int value) {
      this.jumlah = value;
    }

    public String getLokasi() {
      return this.lokasi;
    }
    public void setLokasi(String value) {
      this.lokasi = value;
    }
}
