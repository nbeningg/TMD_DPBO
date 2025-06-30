// ini penanda jika file ini ada di dalam folder 'database'
package database;

// mengimpor atau panggil kelas-kelas lain yang dibutuhkan dari folder lain
import model.ScoreEntry;
import model.GameModel.GameOverStatus; // impor enum status game over dari gamemodel
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * kelas ini tugasnya mengurus semua hal yang berhubungan dengan database
 * untuk koneksi, bikin tabel, hingga simpan dan ambil data skor
 */
public class DatabaseManager {
    // ini adalah alamat, username, dan password buat konek ke database mysql
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_game";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String TABLE_NAME = "thasil"; // nama tabel untuk menyimpan skor.

    /**
     * konstuktor ini jalan pertama kali saat 'new DatabaseManager()' dibuat
     */
    public DatabaseManager() {
        try {
            // ini untuk memastikan driver JDBC MySQL sudah ada
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // jika driver-nya tidak ditemukan, tampilkan pesan error
            System.err.println("Error: MySQL JDBC Driver tidak ditemukan.");
            e.printStackTrace();
        }
    }

    /**
     * fungsi untuk membuat koneksi ke database
     * dibuat private karena hanya dipakai di dalam kelas ini aja
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * fungsi untuk menyiapkan database, terutama membuat tabel skor
     */
    public void initializeDatabase() {
        // perintah sql untuk membuat tabel 'thasil'
        // 'if not exists' ada untuk mencegah error jika tabelnya sudah ada
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                                  + "username VARCHAR(255) PRIMARY KEY," // kolom untuk nama pemain, harus unik (primary key)
                                  + "skor INT DEFAULT 0,"                // kolom untuk skor tertinggi, defaultnya 0
                                  + "count INT DEFAULT 0"                // kolom untuk jumlah bola, defaultnya 0
                                  + ");";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // jalankan perintah sql untuk buat tabel
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            // jika ada error saat membuat tabel, tampilkan pesan error
            System.err.println("Error inisialisasi database: " + e.getMessage());
        }
    }

    /**
     * fungsi untuk mengambil semua data skor dari tabel
     */
    public List<ScoreEntry> getAllScores() {
        // membuat list kosong untuk nampung skor
        List<ScoreEntry> scores = new ArrayList<>();
        // perintah sql untuk ambil semua data dari tabel, diurutkan dari skor paling tinggi
        String selectSQL = "SELECT username, skor, count FROM " + TABLE_NAME + " ORDER BY skor DESC, count DESC;";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {
            
            // looping untuk baca satu per satu baris data hasil query
            while (rs.next()) {
                // setiap baris data diubah jadi objek 'ScoreEntry', lalu dimasukin ke list
                scores.add(new ScoreEntry(rs.getString("username"), rs.getInt("skor"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil skor: " + e.getMessage());
        }
        // kembalikan list yang sudah berisi data skor
        return scores;
    }

    /**
     * fungsi untuk menyimpan atau update skor pemain
     * fungsi ini mengembalikan status (menang, kalah, rekor baru) agar bisa ditampilin di layar
     */
    public GameOverStatus saveOrUpdateScore(String username, int newScore, int newCount) {
        // jika username-nya kosong, jangan diproses
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Gagal menyimpan: username kosong.");
            return GameOverStatus.LOSE; 
        }

        // tentukan kondisi menang jika skornya 600 atau lebih
        boolean isWin = newScore >= 600;

        try (Connection conn = getConnection()) {
            // pertama, cek dulu apakah nama pemain ini sudah ada di database.
            String checkUserSQL = "SELECT skor FROM " + TABLE_NAME + " WHERE username = ?;";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSQL)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) { // jika ada, berarti ini pemain lama
                    int currentScore = rs.getInt("skor"); // ambil skor lama pemain tersebut
                    if (newScore > currentScore) {
                        // jika skor barunya lebih tinggi, update datanya di database
                        String updateSQL = "UPDATE " + TABLE_NAME + " SET skor = ?, count = ? WHERE username = ?;";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                            updateStmt.setInt(1, newScore);
                            updateStmt.setInt(2, newCount);
                            updateStmt.setString(3, username);
                            updateStmt.executeUpdate();
                        }
                        // karena skornya lebih tinggi, berarti ini rekor baru
                        return GameOverStatus.WIN_NEW_HIGHSCORE;
                    } else {
                        // jika skornya tidak lebih tinggi, tidak usah di-update
                        // kembalikan status menang atau kalah biasa.
                        return isWin ? GameOverStatus.WIN_NO_HIGHSCORE : GameOverStatus.LOSE;
                    }
                } else { // jika tidak ada, berarti ini pemain baru
                    // masukkan data pemain baru ini ke database
                    String insertSQL = "INSERT INTO " + TABLE_NAME + " (username, skor, count) VALUES (?, ?, ?);";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, username);
                        insertStmt.setInt(2, newScore);
                        insertStmt.setInt(3, newCount);
                        insertStmt.executeUpdate();
                    }
                    // kembalikan status sebagai pemain baru
                    return isWin ? GameOverStatus.WIN_NEW_PLAYER : GameOverStatus.LOSE;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menyimpan skor: " + e.getMessage());
        }
        
        // ini fallback jika ada error, misalnya koneksi database putus
        return isWin ? GameOverStatus.WIN_NO_HIGHSCORE : GameOverStatus.LOSE;
    }
}