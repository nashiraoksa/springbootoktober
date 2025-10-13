package springbootoktober;

import java.util.Scanner;

public class KasirSederhana {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        String[] barang = {"Sayur Bayam", "Tempe", "Minyak 1L", "Gula 1kg", "Telur 1kg"};
        int[] harga = {3000, 4000, 17000, 15000, 27000};

        System.out.println("=== Kasir Toko Saya ===");
        for (int i = 0; i < barang.length; i++) {
            System.out.println((i + 1) + ". " + barang[i] + " - Rp" + harga[i]);
        }

        int totalBelanja = 0;
        String lanjut = "y";

        do {
            System.out.print("\nMasukkan nomor barang yang ingin dibeli: ");
            int pilihan = input.nextInt();

            if (pilihan < 1 || pilihan > barang.length) {
                System.out.println("Nomor barang tidak valid!");
                continue;
            }

            System.out.print("Masukkan jumlah yang ingin dibeli: ");
            int jumlah = input.nextInt();

            int subtotal = harga[pilihan - 1] * jumlah;
            totalBelanja += subtotal;

            System.out.println("Anda membeli " + jumlah + " " + barang[pilihan - 1] + " seharga Rp" + subtotal);

            System.out.print("Apakah ingin membeli barang lain? (y/n): ");
            lanjut = input.next();
        } while (lanjut.equalsIgnoreCase("y"));

        System.out.println("\n=== Struk Belanja ===");
        System.out.println("Total belanja: Rp" + totalBelanja);
        System.out.println("Terima kasih telah berbelanja di Toko Saya!");

        input.close();

    }
}
