package springbootoktober;

import java.util.ArrayList;
import java.util.Scanner;

public class MainKasir {
    public static void main(String[] args) {
        // buat inputan menggunakan scanner
        Scanner input = new Scanner(System.in);

        // buat daftar produk
        ArrayList<Produk> daftarProduk = new ArrayList<>();
        
        // isi produk
        daftarProduk.add(new Produk("Bayam", 2000, 10));
        daftarProduk.add(new Produk("Pepaya", 5400, 10));
        daftarProduk.add(new Produk("Jengkol", 4000, 10));
        daftarProduk.add(new Produk("Tempe", 3000, 10));

        // buat transaksi
        Transaksi trx = new Transaksi();

        // munculkan menu
        System.out.println("=== KASIR BPD DIY ===");

        boolean belanja = true;

        while(belanja){
            System.out.println("\nDaftar Produk: ");
            for (Produk p: daftarProduk){
                p.showInfo();
            }

            // instruksi
            System.out.println("\nMasukan nama produk (atau ketik 'selesai'): ");
            String namaProduk = input.nextLine(); // ambil input nama produk

            // cocokan
            if(namaProduk.equalsIgnoreCase("selesai")){
                belanja = false;
            }else {
                System.out.println("Masukkan jumlah beli");
                int jml = Integer.parseInt(input.nextLine());

                // cari produk
                Produk ditemukan = null;
                for(Produk p: daftarProduk){
                    if(p.getNama().equalsIgnoreCase(namaProduk)){
                        ditemukan = p;
                        break;
                    }
                }

                if(ditemukan != null){
                    trx.tambahKeranjang(ditemukan, jml);
                }else{
                    System.out.println("Produk tidak ditemukan");
                }
            }
        }

        // tampilkan hasil
        trx.tampilTotalHarga();
        System.out.println("Terima kasih telah berbelanja, ditunggu kedatangan selanjutnya...");
    }
}
