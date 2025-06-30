// ini adalah penanda bahwa file ini berada di dalam folder 'view'
package view;

// mengimpor semua kelas yang diperlukan dari folder lain dan library java
import model.*;
import viewmodel.GameViewModel;
import model.GameModel.GameOverStatus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Kelas ini adalah panel utama tempat semua elemen permainan digambar
 * Ini adalah bagian 'View' yang untuk menampilkan gameplay kepada pengguna
 */
public class GamePanel extends JPanel {
    // deklarasi variabel untuk properti panel
    private GameViewModel viewModel; // referensi ke ViewModel yang menjadi jembatan ke logika game
    private Timer gameTimer;         // timer yang berfungsi sebagai game loop untuk update
    private Image backgroundImage;   // gambar untuk latar belakang permainan
    
    // variabel untuk menyimpan area klik tombol di layar game over dan pause
    private Rectangle restartButtonArea, quitButtonArea;
    private Rectangle resumeButtonArea, pauseQuitButtonArea;

    /**
     * konstruktor untuk membuat objek GamePanel baru
     */
    public GamePanel(GameViewModel viewModel) {
        this.viewModel = viewModel;
        // mengatur ukuran panel sesuai dengan konstanta dari MainFrame
        setPreferredSize(new Dimension(MainFrame.GAME_WIDTH, MainFrame.GAME_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setFocusable(true); // membuat panel ini bisa menerima fokus untuk input keyboard
        setLayout(new BorderLayout());
        
        // memanggil metode-metode untuk setup awal
        loadAssets();
        setupListeners();
        setupTimer();
    }
    
    /**
     * Metode untuk memuat aset gambar yang dibutuhkan oleh panel ini
     */
    private void loadAssets(){
        try {
            URL bgUrl = getClass().getResource("/assets/images/background.png");
            if (bgUrl != null) this.backgroundImage = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Metode untuk mendaftarkan semua listener untuk input dari pengguna (keyboard dan mouse)
     */
    private void setupListeners() {
        // menambahkan listener untuk input keyboard
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                // meneruskan input keyboard ke ViewModel hanya jika permainan belum berakhir
                if (!viewModel.getGameModel().isGameOver()) {
                    viewModel.handleKeyPress(e.getKeyCode());
                }
            }
        });
        
        // menambahkan listener untuk input mouse
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (viewModel.getGameModel().isGameOver()) {
                    // jika game over, periksa apakah klik mengenai tombol restart atau quit
                    if (restartButtonArea != null && restartButtonArea.contains(e.getPoint())) viewModel.restartGame();
                    else if (quitButtonArea != null && quitButtonArea.contains(e.getPoint())) viewModel.quitToMenu();
                } else if (!viewModel.getGameModel().isGameRunning()) {
                    // jika game sedang dijeda, periksa apakah klik mengenai tombol resume atau quit
                    if (resumeButtonArea != null && resumeButtonArea.contains(e.getPoint())) viewModel.getGameModel().resumeGame();
                    else if (pauseQuitButtonArea != null && pauseQuitButtonArea.contains(e.getPoint())) viewModel.quitToMenu();
                } else {
                    // jika game sedang berjalan, teruskan event klik ke ViewModel untuk ditangani
                    viewModel.handleMouseClick(e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Metode untuk mengatur Timer yang berfungsi sebagai game loop
     */
    private void setupTimer() {
        // menambahkan listener untuk mendeteksi perubahan status dari ViewModel
        viewModel.addPropertyChangeListener(evt -> {
            String propName = evt.getPropertyName();
            // jika status game berubah (dijeda/dilanjutkan/berakhir), gambar ulang panel
            if ("gameRunning".equals(propName) || "gameOver".equals(propName)) {
                repaint();
            }
        });
        
        // membuat Timer yang akan berjalan 
        gameTimer = new Timer(1000 / 60, e -> {
            viewModel.updateGame(System.currentTimeMillis());
            repaint();
        });
    }

    /**
     * Metode inti dari JPanel yang bertanggung jawab untuk menggambar semua elemen grafis
     * Urutan penggambaran di sini sangat penting untuk menentukan lapisan visual (mana yang di depan)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // mengaktifkan antialiasing agar gambar terlihat lebih halus dan tidak bergerigi
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // urutan menggambar (dari lapisan paling belakang ke paling depan)
        // pertama, gambar latar belakang
        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // kemudian, gambar semua elemen permainan
        // gambar portal
        Portal portal = viewModel.getPortal();
        if (portal.getImage() != null) g2d.drawImage(portal.getImage(), portal.getX(), portal.getY(), 110, 110, this);

        // gambar semua bola yang ada di layar
        for (GlyphBall ball : viewModel.getGlyphBalls()) drawBall(g2d, ball, false);
        
        // gambar semua musuh (EvilWizard)
        for (EvilWizard ew : viewModel.getEvilWizards()) {
            g2d.drawImage(ew.getImage(), ew.getX(), ew.getY(), ew.getWidth(), ew.getHeight(), this);
        }
        
        // gambar pemain dan efek sihirnya
        Player player = viewModel.getPlayer();
        if (player.getHeldBall() != null) drawBall(g2d, player.getHeldBall(), true);
        
        drawPlayerAndEffects(g2d, player, viewModel.getMagicBeam());

        if (viewModel.getMagicBeam().getState() != MagicBeam.BeamState.IDLE && viewModel.getMagicBeam().getAttachedBall() != null) {
            drawBall(g2d, viewModel.getMagicBeam().getAttachedBall(), true);
        }
        
        // gambar skor dan time display
        drawInfoBox(g2d, portal);
        drawTimeDisplay(g2d);

        // layar overlay (game over atau pause) digambar paling akhir agar menutupi semua elemen lain
        if (viewModel.getGameModel().isGameOver()) {
            drawGameOverScreen(g2d);
        } else if (!viewModel.getGameModel().isGameRunning()) {
            drawPauseScreen(g2d);
        }

        // melepaskan sumber daya graphics untuk menghemat memori
        g2d.dispose();
    }
    
    /**
     * Metode bantu untuk menggambar kotak informasi skor dan jumlah bola
     */
    private void drawInfoBox(Graphics2D g2d, Portal portal) {
        int boxWidth = 180;
        int boxHeight = 80;
        int boxX = portal.getX() - (boxWidth / 2) + (portal.getBounds().width / 2) - 30;
        int boxY = portal.getY() - boxHeight - 10;
        g2d.setColor(new Color(40, 10, 60, 180));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);
        g2d.setColor(new Color(255, 220, 130, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);
        g2d.setColor(new Color(170, 120, 220, 80));
        g2d.setStroke(new BasicStroke(5f));
        g2d.drawRoundRect(boxX - 2, boxY - 2, boxWidth + 4, boxHeight + 4, 28, 28);
        g2d.setColor(new Color(255, 230, 180));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String scoreText = "Score: " + viewModel.getGameModel().getScore();
        String countText = "Count: " + viewModel.getGameModel().getBallsCaught();
        int yPos = boxY + 32;
        g2d.drawString(scoreText, boxX + (boxWidth - fm.stringWidth(scoreText)) / 2, yPos);
        yPos += 30;
        g2d.drawString(countText, boxX + (boxWidth - fm.stringWidth(countText)) / 2, yPos);
    }
    
    /**
     * Metode bantu untuk menggambar objek bola
     */
    private void drawBall(Graphics2D g2d, GlyphBall ball, boolean isHeld) {
        if (ball.getImage() == null) return;
        double radius = ball.getDisplayRadius();
        if (isHeld) radius -= 12; // jika dipegang, gambar sedikit lebih kecil
        if (radius < 1) return;
        g2d.drawImage(ball.getImage(), ball.getX() - (int)radius, ball.getY() - (int)radius, (int)radius * 2, (int)radius * 2, this);
        if (!isHeld) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String text = String.valueOf(ball.getScoreValue());
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(text, ball.getX() - fm.stringWidth(text) / 2, ball.getY() + fm.getAscent() / 2 - 2);
        }
    }

    /**
     * Metode bantu untuk menggambar pemain dan efek tali sihirnya
     */
    private void drawPlayerAndEffects(Graphics2D g2d, Player player, MagicBeam magicBeam) {
        if (magicBeam.getState() != MagicBeam.BeamState.IDLE) {
            drawAnimatedBeam(g2d, player, magicBeam);
        }
        if (player.getImage() != null) {
            g2d.drawImage(player.getImage(), player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
        }
    }

    /**
     * Metode bantu untuk menggambar animasi tali sihir dengan efek gradien dan glow
     */
    private void drawAnimatedBeam(Graphics2D g2d, Player player, MagicBeam magicBeam) {
        double startX = player.getX() + (double)player.getWidth() / 2;
        double startY = player.getY() + (double)player.getHeight() / 2;
        GradientPaint paint = new GradientPaint((float)startX, (float)startY, new Color(170, 120, 220, 220), (float)magicBeam.getCurrentX(), (float)magicBeam.getCurrentY(), new Color(255, 220, 130, 80));
        g2d.setPaint(paint);
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)startX, (int)startY, (int)magicBeam.getCurrentX(), (int)magicBeam.getCurrentY());
        g2d.setColor(new Color(170, 120, 220, 60));
        g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)startX, (int)startY, (int)magicBeam.getCurrentX(), (int)magicBeam.getCurrentY());
        g2d.setColor(new Color(255, 240, 180, 180));
        g2d.fillOval((int)magicBeam.getCurrentX() - 12, (int)magicBeam.getCurrentY() - 12, 24, 24);
        g2d.setColor(new Color(255, 220, 130, 100));
        g2d.fillOval((int)magicBeam.getCurrentX() - 18, (int)magicBeam.getCurrentY() - 18, 36, 36);
    }
    
    /**
     * Menggambar layar Game Over dengan pesan yang dinamis berdasarkan status permainan
     */
    private void drawGameOverScreen(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 10, 60, 220), 0, getHeight(), new Color(10, 5, 20, 240));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // mengambil status akhir permainan dari Model melalui ViewModel
        GameOverStatus status = viewModel.getGameModel().getGameOverStatus();
        String titleText = "";
        String detailText = "";
        Color titleColor = Color.WHITE;

        // menentukan teks dan warna yang akan ditampilkan berdasarkan status
        switch (status) {
            case WIN_NEW_PLAYER:
                titleText = "YOU WIN!";
                titleColor = new Color(255, 240, 150);
                detailText = "Selamat datang, Pahlawan Baru!";
                break;
            case WIN_NEW_HIGHSCORE:
                titleText = "NEW HIGH SCORE!";
                titleColor = new Color(180, 255, 180);
                detailText = "Luar biasa! Kamu melampaui rekormu!";
                break;
            case WIN_NO_HIGHSCORE:
                titleText = "YOU WIN!";
                titleColor = new Color(255, 240, 150);
                detailText = "Kerja bagus! Coba lagi untuk rekor baru.";
                break;
            case LOSE:
            default:
                titleText = "YOU LOSE...";
                titleColor = new Color(255, 160, 160);
                detailText = "Jangan menyerah, coba lagi!";
                break;
        }

        // menggambar semua teks dan tombol ke layar
        Font largeFont = new Font("Arial", Font.BOLD, 68);
        g2d.setFont(largeFont);
        FontMetrics fmLarge = g2d.getFontMetrics();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(titleText, (getWidth() - fmLarge.stringWidth(titleText)) / 2 + 3, getHeight() / 2 - 120 + 3);
        g2d.setColor(titleColor);
        g2d.drawString(titleText, (getWidth() - fmLarge.stringWidth(titleText)) / 2, getHeight() / 2 - 120);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fmDetail = g2d.getFontMetrics();
        g2d.setColor(new Color(255, 230, 180));
        g2d.drawString(detailText, (getWidth() - fmDetail.stringWidth(detailText)) / 2, getHeight() / 2 - 70);

        String scoreText = "Your Score: " + viewModel.getGameModel().getScore();
        g2d.drawString(scoreText, (getWidth() - fmDetail.stringWidth(scoreText)) / 2, getHeight() / 2 - 30);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        fmLarge = g2d.getFontMetrics();
        restartButtonArea = new Rectangle(getWidth()/2 - 120, getHeight()/2 + 20, 240, 60);
        g2d.setColor(new Color(90, 30, 130));
        g2d.fillRoundRect(restartButtonArea.x, restartButtonArea.y, restartButtonArea.width, restartButtonArea.height, 20, 20);
        g2d.setColor(new Color(170, 120, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(restartButtonArea.x, restartButtonArea.y, restartButtonArea.width, restartButtonArea.height, 20, 20);
        String restartText = "Restart";
        g2d.setColor(new Color(255, 220, 130));
        g2d.drawString(restartText, restartButtonArea.x + (restartButtonArea.width - fmLarge.stringWidth(restartText)) / 2, restartButtonArea.y + 42);
        
        quitButtonArea = new Rectangle(getWidth()/2 - 120, getHeight()/2 + 100, 240, 60);
        g2d.setColor(new Color(80, 20, 100));
        g2d.fillRoundRect(quitButtonArea.x, quitButtonArea.y, quitButtonArea.width, quitButtonArea.height, 20, 20);
        g2d.setColor(new Color(170, 120, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(quitButtonArea.x, quitButtonArea.y, quitButtonArea.width, quitButtonArea.height, 20, 20);
        String quitText = "Quit";
        g2d.setColor(new Color(255, 220, 130));
        g2d.drawString(quitText, quitButtonArea.x + (quitButtonArea.width - fmLarge.stringWidth(quitText)) / 2, quitButtonArea.y + 42);
    }

    /**
     * Menggambar layar Jeda (Pause)
     */
    private void drawPauseScreen(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 10, 60, 180), 0, getHeight(), new Color(20, 5, 30, 200));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(170, 120, 220, 20));
        for (int i = 0; i < getHeight(); i += 20) {
            g2d.drawLine(0, i, getWidth(), i);
        }
        g2d.setFont(new Font("Arial", Font.BOLD, 68));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(new Color(0, 0, 0, 120));
        String pauseText = "PAUSED";
        g2d.drawString(pauseText, (getWidth() - fm.stringWidth(pauseText)) / 2 + 3, getHeight() / 2 - 100 + 3);
        g2d.setColor(new Color(255, 220, 130));
        g2d.drawString(pauseText, (getWidth() - fm.stringWidth(pauseText)) / 2, getHeight() / 2 - 100);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g2d.getFontMetrics();
        resumeButtonArea = new Rectangle(getWidth()/2 - 120, getHeight()/2, 240, 60);
        g2d.setColor(new Color(90, 30, 130));
        g2d.fillRoundRect(resumeButtonArea.x, resumeButtonArea.y, resumeButtonArea.width, resumeButtonArea.height, 20, 20);
        g2d.setColor(new Color(170, 120, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(resumeButtonArea.x, resumeButtonArea.y, resumeButtonArea.width, resumeButtonArea.height, 20, 20);
        g2d.setColor(new Color(255, 220, 130));
        String resumeText = "Resume";
        g2d.drawString(resumeText, resumeButtonArea.x + (resumeButtonArea.width - fm.stringWidth(resumeText)) / 2, resumeButtonArea.y + 42);
        pauseQuitButtonArea = new Rectangle(getWidth()/2 - 120, getHeight()/2 + 80, 240, 60);
        g2d.setColor(new Color(80, 20, 100));
        g2d.fillRoundRect(pauseQuitButtonArea.x, pauseQuitButtonArea.y, pauseQuitButtonArea.width, pauseQuitButtonArea.height, 20, 20);
        g2d.setColor(new Color(170, 120, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(pauseQuitButtonArea.x, pauseQuitButtonArea.y, pauseQuitButtonArea.width, pauseQuitButtonArea.height, 20, 20);
        g2d.setColor(new Color(255, 220, 130));
        String quitText = "Quit";
        g2d.drawString(quitText, pauseQuitButtonArea.x + (pauseQuitButtonArea.width - fm.stringWidth(quitText)) / 2, pauseQuitButtonArea.y + 42);
    }

    /**
     * Menggambar tampilan sisa waktu di bagian atas layar
     */
    private void drawTimeDisplay(Graphics2D g2d) {
        String timeText = "TIME: " + viewModel.getGameModel().getTimeLeftSeconds();
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int x = getWidth() / 2 - textWidth / 2;
        int y = 40;
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(timeText, x + 2, y + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, x, y);
    }
    
    public void startGame() { if (gameTimer != null && !gameTimer.isRunning()) gameTimer.start(); }
    public void stopGame() { if (gameTimer != null && gameTimer.isRunning()) gameTimer.stop(); }
}
