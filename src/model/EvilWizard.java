// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas-kelas yang diperlukan 
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Random;

/**
 * Kelas ini merepresentasikan objek rintangan (Evil Wizard) dalam permainan
 * Objek ini berfungsi sebagai tantangan yang harus dihindari oleh pemain
 */
public class EvilWizard {
    // deklarasi variabel untuk properti objek
    private int x, y, width, height; // posisi (x,y) dan ukuran (lebar, tinggi).
    private double velocityX;        // kecepatan pergerakan horizontal
    private Image image;             // gambar untuk ditampilkan
    private static final Random random = new Random(); // objek untuk menghasilkan nilai acak

    /**
     * konstruktor untuk membuat objek EvilWizard baru
     */
    public EvilWizard(int gameWidth, int gameHeight, boolean moveLeft) {
        // mengatur ukuran default untuk gambar wizard.
        this.width = 120;
        this.height = 120;
        
        // menentukan batas atas dan bawah dari zona tengah tempat wizard akan muncul
        // wizard hanya muncul di antara 25% dan 75% tinggi layar.
        int topZoneBottom = gameHeight / 4;
        int bottomZoneTop = gameHeight * 3 / 4;
        
        // menghitung rentang posisi y (vertikal) yang valid
        int minY = topZoneBottom;
        int maxY = bottomZoneTop - this.height;
        
        // mengatur posisi y secara acak di dalam rentang yang valid
        if (minY < maxY) {
            this.y = random.nextInt(maxY - minY) + minY;
        } else {
            // sebagai fallback jika layar terlalu sempit, posisikan di tengah persis
            this.y = gameHeight / 2 - (this.height / 2);
        }

        // mengatur kecepatan wizard. nilai ini ditingkatkan untuk menambah tantangan
        double speed = 6.5;

        // menentukan posisi awal dan kecepatan berdasarkan arah gerakan.
        if (moveLeft) {
            // jika bergerak ke kiri, mulai dari sisi kanan layar
            this.x = gameWidth;
            this.velocityX = -speed; // kecepatan negatif untuk bergerak ke kiri
            loadImage("penjahat1.png"); // memuat gambar yang menghadap ke kiri
        } else {
            // jika bergerak ke kanan, mulai dari luar sisi kiri layar
            this.x = -this.width;
            this.velocityX = speed; // kecepatan positif untuk bergerak ke kanan
            loadImage("penjahat2.png"); // memuat gambar yang menghadap ke kanan
        }

        /* Karakter Evil Wizard 1 oleh Warren Clark (https://lionheart963.itch.io/wizard) */
        /* Karakter Evil Wizard 2 oleh Luiz Melo (https://luizmelo.itch.io/evil-wizard)*/
    }

    /**
     * Fungsi untuk memuat file gambar dari folder assets
     */
    private void loadImage(String imagePath) {
        try {
            URL url = getClass().getResource("/assets/images/" + imagePath);
            if (url != null) this.image = new ImageIcon(url).getImage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Memperbarui posisi wizard di setiap frame berdasarkan kecepatannya
     * Metode ini dipanggil secara berulang dalam game loop
     */
    public void update() {
        this.x += this.velocityX;
    }
    
    /**
     * Memeriksa apakah objek wizard sudah sepenuhnya keluar dari area layar
     */
    public boolean isOffScreen(int gameWidth) {
        return (this.velocityX < 0 && this.x < -this.width) || (this.velocityX > 0 && this.x > gameWidth);
    }

    /**
     * Mengembalikan area deteksi tabrakan (hitbox) untuk objek ini
     * 'inset' digunakan untuk menyesuaikan ukuran hitbox agar lebih pas
     */
    public Rectangle getBounds() {
        int inset = 12; // mengurangi ukuran hitbox sebesar 12 piksel dari setiap sisi.
        return new Rectangle(this.x + inset, this.y + inset, this.width - (2 * inset), this.height - (2 * inset));
    }

    // getter untuk mendapatkan nilai properti dari luar kelas ini
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public Image getImage() { return this.image; }
}
