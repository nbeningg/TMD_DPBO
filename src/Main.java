/* Saya Nuansa Bening Aura Jelita dengan NIM 2301410 mengerjakan evaluasi 
Tugas Masa Depan dalam mata kuliah Desain dan Pemrograman Berorientasi Objek 
untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
 */

// mengimpor kelas yang diperlukan dari folder lain dan library java
import view.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Kelas ini adalah titik masuk utama (entry point) dari seluruh permainan
 * Tugasnya hanya satu, yaitu membuat dan menampilkan jendela utama (MainFrame)
 */
public class Main {
    /**
     * Metode main adalah metode pertama yang akan dijalankan saat program dieksekusi
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // membuat instance baru dari MainFrame, yang akan memulai seluruh alur permainan
            MainFrame mainFrame = new MainFrame();
            // menampilkan jendela utama ke layar
            mainFrame.setVisible(true);
        });
    }
}