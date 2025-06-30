// ini adalah penanda bahwa file ini berada di dalam folder 'view'
package view;

// mengimpor semua kelas yang diperlukan dari folder lain dan library java
import viewmodel.GameViewModel;
import viewmodel.StartMenuViewModel;
import model.GameModel;
import javax.swing.*;
import java.awt.*;

/**
 * Kelas ini adalah jendela utama (frame) dari permainan
 * Bertanggung jawab untuk menampung dan beralih antara panel menu utama dan panel permainan
 */
public class MainFrame extends JFrame {
    // konstanta 'static' untuk ukuran jendela permainan, agar bisa diakses dari kelas lain
    public static final int GAME_WIDTH = 1024;
    public static final int GAME_HEIGHT = 576;

    // deklarasi variabel untuk panel-panel yang akan ditampilkan
    private StartMenuPanel startMenuPanel;
    private GamePanel gamePanel;
    private GameViewModel gameViewModel;

    /**
     * konstruktor untuk membuat objek MainFrame baru
     */
    public MainFrame() {
        setTitle("The Last Glyph Collector"); // mengatur judul jendela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // program akan berhenti jika jendela ditutup
        setResizable(false); // ukuran jendela tidak bisa diubah oleh pengguna

        // --- Inisialisasi Arsitektur MVVM ---
        // 1. membuat objek Model yang berisi semua data dan logika permainan
        GameModel gameModel = new GameModel(GAME_WIDTH, GAME_HEIGHT);
        // 2. membuat ViewModel untuk setiap View
        StartMenuViewModel startMenuViewModel = new StartMenuViewModel();
        // 3. membuat ViewModel untuk game, dengan memberikan referensi ke method 'showStartMenu' sebagai callback
        gameViewModel = new GameViewModel(gameModel, this::showStartMenu);

        // membuat objek untuk setiap panel View
        startMenuPanel = new StartMenuPanel(this, startMenuViewModel);
        gamePanel = new GamePanel(gameViewModel);
        
        // menggunakan CardLayout untuk memungkinkan perpindahan antara beberapa panel dalam satu frame
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        // menambahkan panel menu utama dengan nama "MENU"
        mainPanel.add(startMenuPanel, "MENU");
        // menambahkan panel permainan dengan nama "GAME"
        mainPanel.add(gamePanel, "GAME");
        
        // mengatur mainPanel sebagai konten utama dari frame ini
        setContentPane(mainPanel);

        pack(); // mengatur ukuran frame secara otomatis agar pas dengan komponen di dalamnya
        setLocationRelativeTo(null); // menampilkan frame di tengah layar saat pertama kali muncul
        
        // menampilkan menu utama sebagai layar awal
        showStartMenu();
    }

    /**
     * Metode untuk menampilkan panel menu utama
     */
    public void showStartMenu() {
        gamePanel.stopGame(); // menghentikan timer game jika sedang berjalan
        // mendapatkan CardLayout
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        // menampilkan panel yang memiliki nama "MENU"
        cl.show(getContentPane(), "MENU");
        startMenuPanel.loadScores(); // memuat ulang data skor terbaru di tabel leaderboard
        startMenuPanel.requestFocusInWindow(); // meminta fokus agar input keyboard langsung aktif di panel menu
    }

    /**
     * Metode untuk memulai permainan dan beralih ke panel permainan
     */
    public void startGame(String username) {
        gameViewModel.setCurrentUsername(username); // mengatur username saat ini di ViewModel
        gameViewModel.restartGame(); // mereset dan memulai permainan baru
        
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        // menampilkan panel yang memiliki nama "GAME"
        cl.show(getContentPane(), "GAME");
        
        gamePanel.requestFocusInWindow(); // meminta fokus ke panel permainan agar input keyboard aktif
        gamePanel.startGame(); // memulai timer/game loop di GamePanel
    }
}
