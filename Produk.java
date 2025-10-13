package springbootoktober;

// kelas Produk untuk menyimpan data produk, merepresentasikan objek produk
public class Produk {
    // atribut
    private String nama;
    private double harga;
    private int stok;

    // method / fungsi
    // konstruktor
    public Produk(String nama, double harga, int stok) {
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    // method umum di java:
    // setter dan getter >> untuk berinteraksi dan modifikasi data di dalam objek

    // getter ambil nama produk
    public String getNama() {
        return nama;
    }

    // getter ambil harga
    public double getHarga(){
        return harga;
    }

    // getter ambil stok
    public int getStok(){
        return stok;
    }

    // getter getInfo, mengembalikan informasi terkait proyek yang kita buat, digunakan untuk debugging saat development
    public void showInfo(){
        System.out.println(nama+" - Rp " + harga + " ("+stok+" tersedia)");
    }

    // setter set stok, mengembalikan nilai stok dikurangi 1, tidak mengurangi stok secara rill (tidak ada modifikasi data)
    // public int decStok() {
    //     return stok - 1;
    // }

    // setter set stok, mengurangi secara rill (modifikasi beneran)
    public int decStok() {
        this.stok = this.stok - 1;
        return this.stok;
    }

    // NOTES: this. merupakan sintaks yang digunakan untuk merujuk variabel ke atribut objek (yg dinisiasi pertama kali di atas), jika tanpa this. berarti dia variabel dari parameter
}
