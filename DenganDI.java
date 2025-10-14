class Mesin {
    public void nyalakan(){
        System.out.println("Mesin dinyalakan...");
    }
}

class MesinListrik extends Mesin {
    @Override
    public void nyalakan(){
        System.out.println("Mesin listrik menyala...");
    }
}

// class mobil dengan DI
class Mobil {
    private Mesin mesin;

    // injeksi lewat konstruktor
    public Mobil(Mesin mesin) {
        this.mesin = mesin;
    }

    // jalan
    public void jalan(){
        mesin.nyalakan();
        System.out.println("Mobil berjalan...");
    }
}

public class DenganDI {
    public static void main(String[] args) {
        // bikin mesin
        Mesin mesinBensin = new Mesin();
        MesinListrik mesinListrik = new MesinListrik();

        // buat mobil
        Mobil mobil1 = new Mobil(mesinBensin);
        Mobil mobil2 = new Mobil(mesinListrik);

        // jalan
        mobil1.jalan();
        mobil2.jalan();
    }
}


// dengan DI >> kelas Mobil jadi tidak dependen ke kelas Mesin, bisa Mesin atau MesinListrik
// kita bisa melakukan variasi objek Mobil tanpa melakukan perubahan pada kelas mobil