// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas-kelas yang diperlukan dari library java swing dan awt
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

/**
 * Kelas ini merepresentasikan objek Portal (atau Keranjang) yang berfungsi sebagai tujuan akhir untuk memasukkan bola sihir
 */
public class Portal {
    // deklarasi variabel untuk properti objek
    private int x, y, width, height; // variabel untuk posisi (x,y) dan ukuran (lebar, tinggi)
    private Image image;             // variabel untuk menyimpan gambar visual portal

    /**
     * konstruktor untuk membuat objek Portal baru
     */
    public Portal(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 110;  // mengatur lebar default portal
        this.height = 110; // mengatur tinggi default portal
        loadImage("portal.png");
        /* Asset Portal oleh @klyaksun di canva */
    }

    /**
     * Fungsi untuk memuat file gambar dari folder assets
     */
    private void loadImage(String imagePath) {
        try {
            // mencoba mendapatkan lokasi file gambar dari dalam folder
            URL url = getClass().getResource("/assets/images/" + imagePath);
            if (url != null) {
                // jika gambar ditemukan, muat gambar tersebut
                this.image = new ImageIcon(url).getImage();
            } else {
                // jika gambar 'portal.png' tidak ditemukan, coba cari gambar cadangan (fallback)
                System.err.println("Aset 'portal.png' tidak ditemukan, mencoba 'keranjang.png'");
                url = getClass().getResource("/assets/images/keranjang.png");
                if (url != null) {
                    this.image = new ImageIcon(url).getImage();
                }
            }
        } catch (Exception e) {
            // mencetak error ke console jika terjadi masalah saat memuat gambar
            e.printStackTrace();
        }
    }

    // fungsi getter untuk mendapatkan nilai properti dari luar kelas ini
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public Image getImage() { return this.image; }
    
    /**
     * Mengembalikan area deteksi tabrakan (hitbox) untuk objek ini
     */
    public Rectangle getBounds() { 
        return new Rectangle(this.x, this.y, this.width, this.height); 
    }
}
