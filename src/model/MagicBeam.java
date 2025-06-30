// ini adalah penanda bahwa file ini berada di dalam folder 'model'
package model;

// mengimpor kelas yang diperlukan
import java.awt.geom.Point2D;

/**
 * Kelas ini merepresentasikan objek tali sihir (Magic Beam) yang digunakan pemain untuk berinteraksi dengan bola
 */
public class MagicBeam {
    /**
     * Enum ini mendefinisikan semua kemungkinan status atau keadaan dari tali sihir
     */
    public enum BeamState {
        IDLE,                // status diam, tali sihir sedang tidak digunakan
        EXTENDING_GRAB,      // status saat tali memanjang untuk mengambil bola
        RETRACTING_BALL,     // status saat tali memendek sambil membawa bola kembali ke pemain
        EXTENDING_DEPOSIT,   // status saat tali memanjang untuk memasukkan bola ke portal
        RETRACTING_EMPTY     // status saat tali memendek setelah berhasil memasukkan bola
    }

    // deklarasi variabel untuk properti objek
    private BeamState state;    // variabel untuk menyimpan status tali sihir saat ini
    private double startX, startY, currentX, currentY, targetX, targetY; // variabel untuk menyimpan koordinat yang diperlukan untuk animasi pergerakan tali
    private GlyphBall attachedBall; // variabel untuk menyimpan referensi ke bola yang sedang ditarik atau dibawa
    private double animationSpeed = 30.0; // variabel untuk mengatur kecepatan pergerakan animasi tali
    
    /**
     * konstruktor untuk membuat objek MagicBeam
     */
    public MagicBeam() {
        // saat pertama kali dibuat, status tali sihir adalah diam (IDLE)
        this.state = BeamState.IDLE;
    }

    /**
     * Metode untuk mengembalikan semua properti tali sihir ke kondisi awal
     * Penting untuk dipanggil saat permainan di-reset
     */
    public void reset() {
        this.state = BeamState.IDLE; // mengembalikan status ke diam
        this.attachedBall = null;    // melepaskan referensi bola yang mungkin masih terikat
        this.startX = 0;             // mereset semua data koordinat
        this.startY = 0;
        this.currentX = 0;
        this.currentY = 0;
        this.targetX = 0;
        this.targetY = 0;
    }
    
    /**
     * Memulai animasi tali untuk mengambil bola
     */
    public void startGrabbing(double pX, double pY, GlyphBall ball) {
        // aksi hanya bisa dimulai jika tali sihir sedang dalam keadaan diam
        if (state == BeamState.IDLE) {
            this.state = BeamState.EXTENDING_GRAB; // mengubah status menjadi sedang memanjang untuk mengambil
            this.startX = pX; this.startY = pY;    // titik awal animasi adalah posisi pemain
            this.currentX = pX; this.currentY = pY;
            this.targetX = ball.getX(); this.targetY = ball.getY(); // titik tujuan animasi adalah posisi bola
            this.attachedBall = ball; // mengikat bola yang ditargetkan ke tali sihir
            ball.setBeingPulled(true); // memberitahu objek bola bahwa ia sedang ditarik agar berhenti bergerak
        }
    }
    
    /**
     * Memulai animasi tali untuk memasukkan bola ke portal
     */
    public void startDepositing(double pX, double pY, Portal portal, GlyphBall ballToDeposit) {
        if (state == BeamState.IDLE) {
            this.state = BeamState.EXTENDING_DEPOSIT;
            this.startX = pX; this.startY = pY;
            this.currentX = pX; this.currentY = pY;
            this.targetX = portal.getX() + 55; // titik tujuan animasi adalah bagian tengah dari portal
            this.targetY = portal.getY() + 55;
            this.attachedBall = ballToDeposit;
            
            if (this.attachedBall != null) {
                // memastikan bola kembali ke ukuran normal saat akan dimasukkan ke portal
                this.attachedBall.setDisplayRadius(this.attachedBall.getRadius());
            }
        }
    }
    
    /**
     * Metode ini dipanggil di setiap frame untuk menjalankan logika animasi tali sihir
     */
    public void update() {
        // jika tali sedang diam, tidak ada yang perlu diupdate
        if (state == BeamState.IDLE) return;

        // menghitung jarak dan arah dari posisi saat ini ke target
        double dx = targetX - currentX;
        double dy = targetY - currentY;
        double distance = Point2D.distance(currentX, currentY, targetX, targetY);

        if (distance < animationSpeed) {
            // jika jarak sudah sangat dekat dengan target, langsung pindahkan posisi ujung tali ke target
            currentX = targetX;
            currentY = targetY;
            // jika sedang memasukkan bola dan sudah tiba di portal
            if (state == BeamState.EXTENDING_DEPOSIT && attachedBall != null) {
                // buat bola menghilang dengan mengubah radiusnya menjadi nol
                attachedBall.setDisplayRadius(0);
            }
        } else {
            // jika masih jauh, gerakkan ujung tali selangkah demi selangkah menuju target
            currentX += (dx / distance) * animationSpeed;
            currentY += (dy / distance) * animationSpeed;
        }
        
        // jika ada bola yang terikat pada tali
        if (attachedBall != null) {
            // dan jika tali sedang membawa bola (menuju pemain atau menuju portal)
            if (state == BeamState.RETRACTING_BALL || state == BeamState.EXTENDING_DEPOSIT) {
                // pastikan posisi bola selalu sama dengan posisi ujung tali
                attachedBall.setPosition((int)currentX, (int)currentY);
            }
        }
        
        // jika ujung tali sudah sampai di tujuan (jarak sangat dekat)
        if (distance < animationSpeed) {
            // ganti status animasi ke tahap berikutnya
            switch (state) {
                case EXTENDING_GRAB:    // jika selesai memanjang untuk mengambil
                    state = BeamState.RETRACTING_BALL; // sekarang waktunya memendek sambil membawa bola
                    targetX = startX; targetY = startY; // target baru adalah posisi awal (pemain)
                    break;
                case RETRACTING_BALL:   // jika selesai membawa bola ke pemain
                    state = BeamState.IDLE; // kembali ke status diam
                    break;
                case EXTENDING_DEPOSIT: // jika selesai memasukkan bola ke portal
                    state = BeamState.RETRACTING_EMPTY; // sekarang waktunya memendek tanpa membawa bola
                    targetX = startX; targetY = startY;
                    break;
                case RETRACTING_EMPTY:  // jika selesai memendek dari portal
                    state = BeamState.IDLE; // kembali ke status diam
                    attachedBall = null;    // lepaskan referensi ke bola yang sudah dimasukkan
                    break;
            }
        }
    }

    // fungsi getter dan setter untuk mengakses dan mengubah properti dari kelas lain
    public GlyphBall getAttachedBall() { return attachedBall; }
    public void setAttachedBall(GlyphBall ball) { this.attachedBall = ball; }
    public BeamState getState() { return state; }
    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
    public double getCurrentX() { return currentX; }
    public double getCurrentY() { return currentY; }
}
