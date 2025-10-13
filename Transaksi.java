package springbootoktober;

import java.util.ArrayList;

public class Transaksi {
    private ArrayList<Produk> daftarProduk = new ArrayList<>();
    private double totalHarga = 0;

    // tambahkan ke keranjang
    public void tambahKeranjang(Produk p, int jml){
        // hitung
        double subtotal = p.getHarga() * jml;

        // tambahkan ke total price
        totalHarga += subtotal;

        // kasih info
        System.out.println("Keranjang = "+p.getNama()+" x "+jml+" = Rp"+subtotal);
    }

    // hitung total harga
    public void tampilTotalHarga(){
        // tampilkan tanpa diskon
        System.out.println("Total belanja Anda: Rp. "+totalHarga);
    }
}
