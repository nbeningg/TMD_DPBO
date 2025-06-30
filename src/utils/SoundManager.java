// ini adalah penanda bahwa file ini berada di dalam folder 'utils'
package utils;

// mengimpor kelas-kelas yang diperlukan dari library java untuk mengelola audio
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Kelas ini untuk mengelola semua hal terkait suara
 * Dibuat statis agar metodenya bisa dipanggil dari mana saja tanpa perlu membuat objek baru
 */
public class SoundManager {
    // variabel statis untuk menyimpan klip musik latar
    // statis berarti hanya ada satu musik latar yang bisa berjalan di seluruh game
    private static Clip backgroundMusic;

    /**
     * Metode statis untuk memutar efek suara pendek satu kali
     */
    public static void playSound(String soundFileName) {
        try {
            // mendapatkan lokasi file suara dari folder assets/sounds
            URL url = SoundManager.class.getResource("/assets/sounds/" + soundFileName);
            // jika file tidak ditemukan, tampilkan pesan error dan hentikan proses
            if (url == null) { 
                System.err.println("Suara tidak ditemukan: " + soundFileName); 
                return; 
            }
            // membuka file suara sebagai aliran audio
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // mendapatkan objek klip yang siap untuk diisi suara
            Clip clip = AudioSystem.getClip();
            // membuka aliran audio ke dalam klip
            clip.open(audioIn);
            // mulai memutar klip suara
            clip.start();
        } catch (Exception e) { 
            // menangani error yang mungkin terjadi saat proses memuat atau memutar suara
            e.printStackTrace(); 
        }
    }

    /**
     * Metode statis untuk memutar musik latar secara terus-menerus (loop)
     */
    public static void playMusic(String soundFileName) {
        // hentikan dulu musik yang mungkin sedang berjalan untuk menghindari tumpukan suara
        stopMusic();
        try {
            URL url = SoundManager.class.getResource("/assets/sounds/" + soundFileName);
            if (url == null) { 
                System.err.println("Musik tidak ditemukan: " + soundFileName); 
                return; 
            }
            // mengisi variabel 'backgroundMusic' dengan klip baru
            backgroundMusic = AudioSystem.getClip();
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            backgroundMusic.open(audioIn);
            // mengatur agar musik diputar secara berulang tanpa henti
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Metode statis untuk menghentikan musik latar yang sedang berjalan
     */
    public static void stopMusic() {
        // periksa apakah ada musik yang sedang berjalan sebelum mencoba menghentikannya
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop(); // hentikan pemutaran
            backgroundMusic.close(); // lepaskan sumber daya yang digunakan oleh klip
        }
    }
}
