// ini adalah penanda bahwa file ini berada di dalam folder 'viewmodel'
package viewmodel;

// mengimpor semua kelas yang diperlukan dari folder lain dan library java
import model.*;
import database.DatabaseManager;
import model.GameModel.GameOverStatus;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Kelas ini adalah ViewModel untuk GamePanel, berfungsi sebagai jembatan antara GamePanel (View) dan GameModel (logika dan data permainan)
 * Tujuannya adalah memisahkan logika tampilan dari logika inti permainan
 */
public class GameViewModel {
    // deklarasi variabel untuk properti ViewModel
    private GameModel gameModel;        // referensi ke Model yang berisi semua data dan logika game
    private String currentUsername;     // menyimpan nama pengguna yang sedang bermain
    private PropertyChangeSupport support; // objek untuk memberitahu View jika ada perubahan data
    private Runnable quitToMenuCallback;   // sebuah fungsi (callback) untuk kembali ke menu utama

    /**
     * konstruktor untuk membuat objek GameViewModel baru
     */
    public GameViewModel(GameModel gameModel, Runnable quitCallback) {
        this.gameModel = gameModel;
        this.quitToMenuCallback = quitCallback;
        this.support = new PropertyChangeSupport(this);
        
        // menambahkan listener ke model
        // setiap kali ada perubahan properti di GameModel, kode di dalam ini akan berjalan
        gameModel.addPropertyChangeListener(evt -> {
            // meneruskan event perubahan dari Model ke View (GamePanel)
            support.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            
            // jika event yang terjadi adalah "gameOver" dan statusnya true (permainan baru saja berakhir)
            if ("gameOver".equals(evt.getPropertyName()) && (Boolean) evt.getNewValue()) {
                // panggil DatabaseManager untuk menyimpan skor dan mendapatkan status akhir permainan
                GameOverStatus status = new DatabaseManager().saveOrUpdateScore(
                    currentUsername, 
                    gameModel.getScore(), 
                    gameModel.getBallsCaught()
                );
                // mengatur status yang didapat dari database ke dalam model, agar bisa dibaca oleh View
                gameModel.setGameOverStatus(status);
            }
        });
    }
    
    /**
     * Metode untuk mengatur nama pengguna yang sedang bermain
     */
    public void setCurrentUsername(String username) { 
        this.currentUsername = username; 
    }
    
    /**
     * Meneruskan klik mouse dari View ke Model untuk diproses
     */
    public void handleMouseClick(int x, int y) {
        if (gameModel.isGameRunning()) {
            gameModel.handleClick(x, y);
        }
    }

    /**
     * Meneruskan tekanan tombol keyboard dari View ke Model untuk diproses
     */
    public void handleKeyPress(int keyCode) {
        // pergerakan pemain hanya bisa dilakukan jika tali sihir tidak sedang aktif
        if (gameModel.getMagicBeam().getState() == MagicBeam.BeamState.IDLE) {
            if (gameModel.isGameRunning()) {
                int dx = 0, dy = 0;
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:   
                        dy = -1; break;

                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:   
                        dy = 1;  break;

                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:   
                        dx = -1; break;

                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:   
                        dx = 1;  break;
                }
                gameModel.getPlayer().move(dx, dy, gameModel.getGameWidth(), gameModel.getGameHeight());
            }
        }
        
        // logika untuk menjeda (pause) dan melanjutkan (resume) permainan dengan tombol spasi
        if (keyCode == KeyEvent.VK_SPACE) {
            if (gameModel.isGameRunning()) {
                gameModel.pauseGame();
            } else {
                gameModel.resumeGame();
            }
        }
    }

    /**
     * Memulai ulang permainan dengan mereset dan memulai model
     */
    public void restartGame() {
        gameModel.resetGame();
        gameModel.startGame();
    }

    /**
     * Menjalankan fungsi callback untuk kembali ke menu utama
     */
    public void quitToMenu() {
        quitToMenuCallback.run();
    }

    /**
     * Meneruskan panggilan update dari View ke Model
     */
    public void updateGame(long currentTimeMillis) { 
        gameModel.update(currentTimeMillis); 
    }
    
    // fungsi getter untuk menyediakan data dari Model ke View
    public Player getPlayer() { return gameModel.getPlayer(); }
    public MagicBeam getMagicBeam() { return gameModel.getMagicBeam(); }
    public List<GlyphBall> getGlyphBalls() { return gameModel.getGlyphBalls(); }
    public Portal getPortal() { return gameModel.getPortal(); }
    public List<EvilWizard> getEvilWizards() { return gameModel.getEvilWizards(); }
    public GameModel getGameModel() { return gameModel; }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) { 
        support.addPropertyChangeListener(listener); 
    }
}
