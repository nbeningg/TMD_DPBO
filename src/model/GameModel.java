// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas-kelas yang diperlukan
import utils.SoundManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import model.GameModel.GameOverStatus;

import java.awt.geom.Point2D;

/**
 * Kelas ini adalah inti dari semua logika permainan
 * Bertanggung jawab untuk mengelola semua objek, status, skor, dan aturan main
 */
public class GameModel {
    /**
     * Enum ini mendefinisikan semua kemungkinan status akhir permainan
     * Digunakan untuk menentukan pesan apa yang akan ditampilkan di layar Game Over
     */
    public enum GameOverStatus {
        INITIAL,             // status awal sebelum permainan berakhir
        WIN_NEW_PLAYER,      // status untuk pemain baru yang berhasil menang
        WIN_NEW_HIGHSCORE,   // status untuk pemain lama yang menang dan memecahkan rekor skor
        WIN_NO_HIGHSCORE,    // status untuk pemain lama yang menang tapi tidak memecahkan rekor
        LOSE                 // status jika pemain kalah (waktu habis dan skor di bawah target).
    }

    // deklarasi semua objek yang ada di dalam game
    private Player player;
    private MagicBeam magicBeam;
    private List<GlyphBall> glyphBalls;
    private Portal portal;
    private List<EvilWizard> evilWizards;

    // deklarasi variabel untuk menyimpan status dan data permainan
    private int score;
    private int ballsCaught;
    private int timeLeftSeconds;
    private boolean gameOver;
    private boolean gameRunning;
    private long gameStartTimeMillis;
    private long lastBallSpawnTime;
    private long lastEvilWizardSpawnTime;
    
    // variabel untuk menyimpan status akhir permainan
    private GameOverStatus gameOverStatus = GameOverStatus.INITIAL;

    // konstanta untuk mengatur aturan dan kesulitan permainan
    private static final long EVIL_WIZARD_SPAWN_INTERVAL_MS = 4000; // wizard baru muncul setiap 4 detik
    private boolean spawnNextInTop = true; // flag untuk menentukan bola muncul di atas atau bawah
    private int gameWidth, gameHeight;
    private static final int INITIAL_TIME_SECONDS = 60; // waktu permainan adalah 60 detik
    private static final long BALL_SPAWN_INTERVAL_MS = 600; // bola baru muncul setiap 0.6 detik
    private static final int MAX_BALLS = 22; // jumlah maksimal bola yang ada di layar
    private static final Random random = new Random();
    
    // objek ini digunakan untuk memberitahu GameViewModel jika ada perubahan data
    private PropertyChangeSupport support;

    /**
     * konstruktor untuk membuat objek GameModel
     */
    public GameModel(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth; this.gameHeight = gameHeight;
        // membuat semua objek yang dibutuhkan saat permainan dimulai
        this.player = new Player(gameWidth / 2 - 40, gameHeight / 2 - 40);
        this.magicBeam = new MagicBeam();
        this.glyphBalls = new ArrayList<>();
        this.portal = new Portal(gameWidth - 140, gameHeight / 2 - 55);
        this.evilWizards = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
        resetGame(); // menyiapkan semua variabel ke kondisi awal
    }
    
    // getter dan setter untuk properti gameOverStatus
    public GameOverStatus getGameOverStatus() {
        return this.gameOverStatus;
    }

    public void setGameOverStatus(GameOverStatus status) {
        this.gameOverStatus = status;
    }

    /**
     * Metode utama yang berjalan di setiap frame untuk memperbarui semua logika game
     */
    public void update(long currentTimeMillis) {
        // jika game dijeda atau sudah berakhir, hentikan semua proses update
        if (!this.gameRunning || this.gameOver) return;

        updateTimer(currentTimeMillis);
        this.player.move(0, 0, this.gameWidth, this.gameHeight); // dipanggil agar posisi bola yang dipegang ikut terupdate
        this.magicBeam.update();
        checkMagicBeamAnimationState();
        
        // memperbarui posisi setiap bola sihir menggunakan iterator untuk menghindari error
        Iterator<GlyphBall> ballIterator = this.glyphBalls.iterator();
        while (ballIterator.hasNext()) {
            GlyphBall ball = ballIterator.next();
            ball.update();
            // hapus bola dari permainan jika sudah keluar dari layar
            if (ball.getX() < -ball.getRadius() * 2 || ball.getX() > this.gameWidth + ball.getRadius() * 2) {
                ballIterator.remove();
            }
        }
        
        // memunculkan bola baru jika interval waktu sudah tercapai dan jumlah bola belum maksimal
        if (currentTimeMillis - this.lastBallSpawnTime > BALL_SPAWN_INTERVAL_MS && this.glyphBalls.size() < MAX_BALLS) {
            spawnBall();
            this.lastBallSpawnTime = currentTimeMillis;
        }

        // memperbarui posisi setiap evil wizard dan memeriksa tabrakan dengan pemain
        Iterator<EvilWizard> evilWizardIterator = this.evilWizards.iterator();
        while (evilWizardIterator.hasNext()) {
            EvilWizard ew = evilWizardIterator.next();
            ew.update();
            // periksa tabrakan antara hitbox pemain dan wizard
            if (this.player.getBounds().intersects(ew.getBounds())) {
                handlePlayerCollision();
                evilWizardIterator.remove(); // hapus wizard setelah menabrak
            } else if (ew.isOffScreen(this.gameWidth)) {
                evilWizardIterator.remove(); // hapus wizard jika keluar dari layar
            }
        }
        
        // memunculkan wizard baru jika interval waktu sudah tercapai
        if (currentTimeMillis - this.lastEvilWizardSpawnTime > EVIL_WIZARD_SPAWN_INTERVAL_MS) {
            spawnEvilWizard();
            this.lastEvilWizardSpawnTime = currentTimeMillis;
        }
    }
    
    /**
     * Membuat objek wizard baru dan menambahkannya ke dalam list permainan
     */
    private void spawnEvilWizard() {
        boolean moveLeft = random.nextBoolean(); // mengacak arah gerakan wizard
        this.evilWizards.add(new EvilWizard(this.gameWidth, this.gameHeight, moveLeft));
    }
    
    /**
     * Menangani logika ketika pemain bertabrakan dengan wizard
     */
    private void handlePlayerCollision() {
        int oldScore = this.score;
        this.score -= 30; // mengurangi skor pemain
        if (this.score < 0) this.score = 0; // memastikan skor tidak menjadi negatif
        SoundManager.playSound("crash.wav"); // memainkan suara tabrakan
        /* Asset Sound Crash oleh Creator Assets (https://youtu.be/SoZhpnTuQBo?si=1gKid3v0l-H2AL8u) */
        this.support.firePropertyChange("score", oldScore, this.score); // memberitahu view bahwa skor berubah
    }
    
    /**
     * Memeriksa status tali sihir. Jika sudah selesai menarik bola, bola akan diberikan ke pemain
     */
    private void checkMagicBeamAnimationState() {
        if (this.magicBeam.getState() != MagicBeam.BeamState.IDLE) return; // hanya proses jika tali sedang tidak aktif
        GlyphBall justAttachedBall = this.magicBeam.getAttachedBall();
        if (justAttachedBall != null) {
            if (this.player.getHeldBall() == null) {
                this.player.setHeldBall(justAttachedBall); // pemain sekarang memegang bola
                this.magicBeam.setAttachedBall(null); // tali sihir tidak lagi terikat pada bola
            }
        }
    }
    
    /**
     * Menangani event klik mouse dari pemain
     */
    public void handleClick(int x, int y) {
        if (this.magicBeam.getState() != MagicBeam.BeamState.IDLE) return; // jangan proses jika tali sihir sedang bekerja

        double playerCenterX = this.player.getX() + (double)this.player.getWidth() / 2;
        double playerCenterY = this.player.getY() + (double)this.player.getHeight() / 2;
        
        if (this.player.getHeldBall() != null) {
            // jika pemain sedang memegang bola, periksa apakah klik mengenai portal
            if (this.portal.getBounds().contains(x, y)) {
                this.magicBeam.startDepositing(playerCenterX, playerCenterY, this.portal, this.player.getHeldBall());
                scorePoints(this.player.getHeldBall()); // tambahkan skor
                this.player.setHeldBall(null); // pemain tidak lagi memegang bola
            }
        } else {
            // jika pemain tidak memegang bola, cari bola yang bisa ditarik
            GlyphBall ballToGrab = findFirstBallOnPath(playerCenterX, playerCenterY, x, y);
            if (ballToGrab != null) {
                this.magicBeam.startGrabbing(playerCenterX, playerCenterY, ballToGrab);
                SoundManager.playSound("grab_sound.wav");
                /* Asset Sound Grab oleh Creator Assets (https://youtu.be/SoZhpnTuQBo?si=1gKid3v0l-H2AL8u) */
                this.glyphBalls.remove(ballToGrab); // hapus bola dari list utama agar tidak bisa ditarik lagi
            }
        }
    }
    
    /**
     * Mencari bola pertama yang berada di dekat garis lurus antara pemain dan titik klik
     * Jika ada beberapa bola yang memenuhi syarat, bola yang paling dekat dengan pemain yang akan dipilih
     */
    private GlyphBall findFirstBallOnPath(double pX, double pY, double clickX, double clickY) {
        GlyphBall closestBall = null;
        double minDistance = Double.MAX_VALUE;
        for (GlyphBall ball : this.glyphBalls) {
            // menghitung jarak tegak lurus dari pusat bola ke garis imajiner dari klik
            double distanceToLine = getDistancePointToLineSegment(ball.getX(), ball.getY(), pX, pY, clickX, clickY);
            if (distanceToLine <= ball.getRadius()) { // jika jarak kurang dari radius, bola dianggap kena
                double distanceToPlayer = Point2D.distance(pX, pY, ball.getX(), ball.getY());
                if (distanceToPlayer < minDistance) { // di antara yang kena, pilih yang paling dekat dengan pemain
                    minDistance = distanceToPlayer;
                    closestBall = ball;
                }
            }
        }
        return closestBall;
    }
    
    /**
     * Menambahkan skor pemain dan jumlah bola yang ditangkap
     */
    private void scorePoints(GlyphBall ball) {
        int oldScore = this.score;
        int oldBallsCaught = this.ballsCaught;
        this.score += ball.getScoreValue();
        this.ballsCaught++;
        SoundManager.playSound("score_sound.wav");
        /* Asset Sound Score oleh Creator Assets (https://youtu.be/SoZhpnTuQBo?si=1gKid3v0l-H2AL8u) */
        this.support.firePropertyChange("score", oldScore, this.score);
        this.support.firePropertyChange("ballsCaught", oldBallsCaught, this.ballsCaught);
    }
    
    /**
     * Memperbarui sisa waktu permainan
     */
    private void updateTimer(long currentTimeMillis) {
        long elapsedTimeMillis = currentTimeMillis - this.gameStartTimeMillis;
        int newTimeLeftSeconds = INITIAL_TIME_SECONDS - (int) (elapsedTimeMillis / 1000);
        this.timeLeftSeconds = Math.max(0, newTimeLeftSeconds);
        this.support.firePropertyChange("timeLeftSeconds", -1, this.timeLeftSeconds);
        if (this.timeLeftSeconds == 0 && !this.gameOver) {
            setGameOver(true); // akhiri permainan jika waktu habis
        }
    }
    
    /**
     * Membuat objek bola baru dan menambahkannya ke list permainan
     */
    private void spawnBall() {
        this.glyphBalls.add(new GlyphBall(this.gameWidth, this.gameHeight, this.spawnNextInTop));
        this.spawnNextInTop = !this.spawnNextInTop; // ganti posisi spawn untuk bola berikutnya (atas/bawah)
    }

    /**
     * Mengembalikan semua status dan objek game ke kondisi awal
     * Dipanggil saat permainan baru dimulai atau di-restart
     */
    public void resetGame() {
        this.score = 0; 
        this.ballsCaught = 0; 
        this.timeLeftSeconds = INITIAL_TIME_SECONDS; 
        this.gameOver = false; 
        this.gameRunning = false;
        
        this.player.reset(this.gameWidth / 2 - this.player.getWidth() / 2, this.gameHeight / 2 - this.player.getHeight() / 2);
        
        // reset MagicBeam juga jangan lupa
        this.magicBeam.reset(); 

        this.glyphBalls.clear();
        this.evilWizards.clear();
        this.gameOverStatus = GameOverStatus.INITIAL; // kembalikan status game over ke awal
        this.support.firePropertyChange("reset", null, null);
    }

    /**
     * Fungsi untuk menghitung jarak terpendek dari sebuah titik ke sebuah segmen garis
     */
    private double getDistancePointToLineSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double l2 = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        if (l2 == 0.0) return Point2D.distance(px, py, x1, y1);
        double t = Math.max(0, Math.min(1, ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2));
        return Point2D.distance(px, py, x1 + t * (x2 - x1), y1 + t * (y2 - y1));
    }
    
    // fungsi getter untuk memberikan akses data ke GameViewModel
    public Player getPlayer() { return this.player; }
    public MagicBeam getMagicBeam() { return this.magicBeam; }
    public List<GlyphBall> getGlyphBalls() { return this.glyphBalls; }
    public Portal getPortal() { return this.portal; }
    public List<EvilWizard> getEvilWizards() { return this.evilWizards; }
    public int getScore() { return this.score; }
    public int getBallsCaught() { return this.ballsCaught; }
    public int getTimeLeftSeconds() { return this.timeLeftSeconds; }
    public boolean isGameOver() { return this.gameOver; }
    public boolean isGameRunning() { return this.gameRunning; }
    public int getGameWidth() { return this.gameWidth; }
    public int getGameHeight() { return this.gameHeight; }
    
    /**
     * Mengatur status game over dan memainkan suara yang sesuai
     */
    private void setGameOver(boolean newGameOver) {
        if (this.gameOver != newGameOver) {
            this.gameOver = newGameOver;
            SoundManager.stopMusic();
            if (this.score >= 600) {
                SoundManager.playSound("win.wav");
                /* Asset Sound Win oleh SemmiSound (https://youtu.be/KEVmZ4F9GFg?si=6tdrqqjSL8bqDzle) */
            } else {
                SoundManager.playSound("game_over.wav");
                /* Asset Sound Game Over oleh sound Effectx (https://youtu.be/MEpU87Rdscw?si=QiZM5-voCARIM_Zm) */
            }
            this.support.firePropertyChange("gameOver", !newGameOver, newGameOver);
        }
    }
    
    /**
     * Memulai permainan.
     */
    public void startGame() {
        this.gameRunning = true;
        this.gameStartTimeMillis = System.currentTimeMillis();
        this.lastBallSpawnTime = this.gameStartTimeMillis;
        this.lastEvilWizardSpawnTime = this.gameStartTimeMillis + 3000; // beri jeda sebelum wizard pertama muncul
        SoundManager.playMusic("background_music.wav");
        /* Asset Sound Background Music oleh MediaCharger - Music For YouTube Videos (https://youtu.be/-gGtnwQjC_4?si=Vjkw72Iu4DsaqeiK) */
    }
    
    /**
     * Menjeda permainan yang sedang berjalan.
     */
    public void pauseGame() {
        this.gameRunning = false;
        SoundManager.stopMusic();
        this.support.firePropertyChange("gameRunning", true, false);
    }

    /**
     * Melanjutkan permainan yang sedang dijeda
     */
    public void resumeGame() {
        if (!this.gameOver) {
            this.gameRunning = true;
            // menyesuaikan kembali waktu mulai agar timer tidak kacau setelah dijeda
            this.gameStartTimeMillis = System.currentTimeMillis() - (long)(INITIAL_TIME_SECONDS - this.timeLeftSeconds) * 1000;
            SoundManager.playMusic("background_music.wav");
            this.support.firePropertyChange("gameRunning", false, true);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
}
