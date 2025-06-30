// ini adalah penanda bahwa file ini berada di dalam folder 'viewmodel'
package viewmodel;

// mengimpor kelas-kelas yang diperlukan dari folder lain
import database.DatabaseManager;
import model.ScoreEntry;
import java.util.List;

/**
 * Kelas ini adalah ViewModel untuk StartMenuPanel
 * Tugasnya hanya menjadi perantara untuk mengambil data leaderboard dari DatabaseManager dan menyediakannya untuk View
 */
public class StartMenuViewModel {
    // deklarasi variabel untuk properti ViewModel
    private DatabaseManager dbManager; // referensi ke DatabaseManager untuk mengakses data

    /**
     * Konstruktor untuk membuat objek StartMenuViewModel baru
     */
    public StartMenuViewModel() {
        // membuat instance baru dari DatabaseManager
        this.dbManager = new DatabaseManager();
        // memanggil metode untuk memastikan tabel di database sudah siap digunakan
        this.dbManager.initializeDatabase();
    }

    /**
     * Metode untuk menyediakan daftar semua skor yang ada di database
     */
    public List<ScoreEntry> getAllScores() {
        // meneruskan permintaan dari View ke DatabaseManager
        return this.dbManager.getAllScores();
    }
}
