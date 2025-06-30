// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

/**
 * Kelas ini Untuk data skor
 * Fungsinya hanya untuk menampung satu baris data skor (username, skor, count) yang diambil dari database
 */
public class ScoreEntry {
    // deklarasi variabel untuk menyimpan data
    private String username;
    private int score;
    private int count;

    /**
     * konstruktor untuk membuat objek ScoreEntry baru
     */
    public ScoreEntry(String username, int score, int count) {
        this.username = username;
        this.score = score;
        this.count = count;
    }

    // getter untuk mendapatkan nilai dari properti
    public String getUsername() { return this.username; }
    public int getScore() { return this.score; }
    public int getCount() { return this.count; }
}