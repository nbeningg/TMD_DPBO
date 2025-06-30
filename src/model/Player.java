// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas-kelas yang diperlukan dari library java swing dan awt
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

/**
 * Kelas ini merepresentasikan objek pemain (Penyihir) yang dikontrol oleh pengguna
 */
public class Player {
    // deklarasi variabel untuk properti objek
    private int x, y, width, height; // variabel untuk posisi (x,y) dan ukuran (lebar, tinggi) pemain
    private Image image;             // variabel untuk menyimpan gambar visual pemain
    private int speed = 6;           // variabel untuk kecepatan gerak pemain
    private GlyphBall heldBall = null; // variabel untuk menyimpan bola yang sedang dipegang, null jika tidak memegang apa-apa

    /**
     * konstruktor untuk membuat objek Player baru
     */
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.width = 85;  // mengatur lebar default pemain
        this.height = 85; // mengatur tinggi default pemain
        loadImage("penyihir.png");
        /* Asset Penyihir oleh Lucas Godoy @pixelgodoy (https://x.com/pixelgodoy/status/1889306862215913814) */
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
     * Metode untuk menggerakkan pemain dan memastikan tidak keluar dari batas area permainan
     */
    public void move(int dx, int dy, int gameWidth, int gameHeight) {
        // menghitung posisi baru berdasarkan input arah (dx, dy) dan kecepatan
        int newX = this.x + dx * speed;
        int newY = this.y + dy * speed;

        // memeriksa dan membatasi pergerakan horizontal agar tidak keluar dari sisi kiri dan kanan layar
        if (newX < 0) {
            newX = 0;
        }
        if (newX > gameWidth - this.width) {
            newX = gameWidth - this.width;
        }

        // menentukan batas atas dan bawah dari area aman tempat pemain bisa bergerak
        int topBoundary = gameHeight / 4;
        int bottomBoundary = gameHeight * 3 / 4;

        // memeriksa dan membatasi pergerakan vertikal agar tidak keluar dari area aman tersebut
        if (newY < topBoundary) {
            newY = topBoundary;
        }
        if (newY > bottomBoundary - this.height) { 
            newY = bottomBoundary - this.height; 
        }

        // menerapkan posisi baru ke pemain
        this.x = newX;
        this.y = newY;
        
        // jika pemain sedang memegang bola
        if (this.heldBall != null) {
            // pastikan posisi bola yang dipegang selalu mengikuti posisi pemain
            this.heldBall.setPosition(this.x + (this.width / 2), this.y + 15);
        }
    }

    /**
     * Mengembalikan pemain ke kondisi dan posisi awal saat permainan di-reset
     */
    public void reset(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.heldBall = null; // melepaskan bola yang mungkin sedang dipegang
    }

    /**
     * Mengembalikan area deteksi tabrakan (hitbox) untuk pemain
     */
    public Rectangle getBounds() {
        int insetX = 18;
        int insetY = 12;
        return new Rectangle(this.x + insetX, this.y + insetY, this.width - (2 * insetX), this.height - (2 * insetY));
    }

    // fungsi getter dan setter untuk mengakses dan mengubah properti dari kelas lain
    public void setHeldBall(GlyphBall ball) { this.heldBall = ball; } 
    public GlyphBall getHeldBall() { return this.heldBall; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Image getImage() { return image; }
}
