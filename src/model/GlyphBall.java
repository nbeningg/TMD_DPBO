// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas-kelas yang diperlukan dari library java swing dan awt
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Random;

/**
 * Kelas ini merepresentasikan objek bola sihir (Glyph Ball) yang menjadi target untuk dikumpulkan oleh pemain dalam permainan
 */
public class GlyphBall {
    // deklarasi variabel untuk properti objek
    private int x, y, scoreValue, radius; // posisi (x,y), nilai skor, dan radius tabrakan
    private double velocityX;             // kecepatan pergerakan horizontal
    private Image image;                  // gambar untuk ditampilkan
    private boolean isBeingPulled;        // status untuk menandakan apakah bola sedang ditarik
    private double displayRadius;         // radius untuk ditampilkan

    // objek untuk menghasilkan nilai acak
    private static final Random random = new Random();
    // daftar nama file gambar untuk bola, akan dipilih secara acak
    private static final String[] BALL_ASSETS = {"bola_sihir1.png", "bola_sihir2.png", "bola_sihir3.png", "bola_sihir4.png"};

    /* Asset Glyph Ball atau Bola Sihir oleh @klyaksun di canva */

    /**
     * konstuktor untuk membuat objek GlyphBall baru
     */
    public GlyphBall(int gameWidth, int gameHeight, boolean spawnInTop) {
        // mengatur properti awal untuk setiap bola
        this.radius = 30;
        this.displayRadius = this.radius;
        this.scoreValue = 10 + random.nextInt(91); // menghasilkan skor acak antara 10 dan 100
        
        // menentukan batas vertikal untuk zona kemunculan bola
        int topZoneEndY = gameHeight / 4;
        int bottomZoneMinY = gameHeight * 3 / 4;
        
        // mengatur kecepatan dasar pergerakan bola
        double baseSpeed = 3.0;

        if (spawnInTop) {
            // jika muncul di atas, bola akan bergerak dari kanan ke kiri
            int minY = this.radius;
            int maxY = topZoneEndY - this.radius;
            if (minY < maxY) this.y = random.nextInt(maxY - minY) + minY;
            else this.y = minY;
            this.x = gameWidth + radius;
            this.velocityX = -baseSpeed; // kecepatan negatif untuk bergerak ke kiri
        } else {
            // jika muncul di bawah, bola akan bergerak dari kiri ke kanan
            int minY = bottomZoneMinY + this.radius;
            int maxY = gameHeight - this.radius;
            if (minY < maxY) this.y = random.nextInt(maxY - minY) + minY;
            else this.y = minY;
            this.x = -radius;
            this.velocityX = baseSpeed; // kecepatan positif untuk bergerak ke kanan
        }
        // memilih gambar bola secara acak dari daftar aset
        loadImage(BALL_ASSETS[random.nextInt(BALL_ASSETS.length)]);
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
     * Memperbarui posisi bola di setiap frame
     * Bola hanya bergerak jika tidak sedang dalam status ditarik
     */
    public void update() { 
        if (!this.isBeingPulled) {
            this.x += this.velocityX; 
        }
    }
    
    // Kumpulan fungsi getter dan setter untuk mengakses dan mengubah properti bola dari kelas lain
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public double getDisplayRadius() { return this.displayRadius; }
    public void setDisplayRadius(double newRadius) { this.displayRadius = newRadius; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getScoreValue() { return scoreValue; }
    public int getRadius() { return radius; }
    public Image getImage() { return image; }
    public Rectangle getBounds() { return new Rectangle(x - radius, y - radius, 2 * radius, 2 * radius); }
    public void setBeingPulled(boolean pulled) { this.isBeingPulled = pulled; }
}