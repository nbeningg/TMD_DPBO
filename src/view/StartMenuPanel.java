// ini adalah penanda bahwa file ini berada di dalam folder 'view'
package view;

// mengimpor semua kelas yang diperlukan dari folder lain dan library java
import model.ScoreEntry;
import viewmodel.StartMenuViewModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Kelas ini adalah tampilan untuk menu utama (Start Menu)
 * Menampilkan leaderboard skor, input untuk username, dan tombol untuk memulai atau keluar dari permainan
 */
public class StartMenuPanel extends JPanel {
    // deklarasi variabel untuk properti dan komponen panel
    private MainFrame mainFrame;          // referensi ke jendela utama untuk berpindah panel
    private StartMenuViewModel viewModel; // referensi ke ViewModel yang menyediakan data
    private JTextField usernameField;     // field untuk input username
    private JTable scoreTable;            // tabel untuk menampilkan leaderboard
    private DefaultTableModel tableModel; // model untuk mengatur data di dalam JTable
    private Image backgroundImage;        // gambar untuk latar belakang menu

    /**
     * konstruktor untuk membuat objek StartMenuPanel
     */
    public StartMenuPanel(MainFrame mainFrame, StartMenuViewModel viewModel) {
        this.mainFrame = mainFrame;
        this.viewModel = viewModel;
        
        // menggunakan GridBagLayout untuk tata letak yang fleksibel dan responsif
        setLayout(new GridBagLayout());
        loadBackgroundImage();

        // membuat komponen-komponen utama panel
        JScrollPane scoreScrollPane = createScoreTable();
        JPanel controlPanel = createControlPanel();

        // mengatur tata letak komponen menggunakan GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();

        // bagian ini (gbc.gridy = 0) digunakan sebagai spacer atau pendorong dari atas
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // membentang selebar dua kolom
        gbc.weighty = 1.3; // memberikan bobot vertikal agar mendorong konten ke bawah
        add(new JLabel(), gbc); // menambahkan komponen kosong sebagai pendorong

        // membuat sebuah kontainer panel untuk menampung tabel dan panel kontrol
        JPanel bottomContainer = new JPanel(new GridBagLayout());
        bottomContainer.setOpaque(false);

        GridBagConstraints bottomGbc = new GridBagConstraints();
        
        // mengatur posisi tabel skor (leaderboard) di sisi kiri
        bottomGbc.gridx = 0;
        bottomGbc.gridy = 0;
        bottomGbc.weightx = 0.6; 
        bottomGbc.anchor = GridBagConstraints.EAST; 
        bottomGbc.insets = new Insets(0, 50, 50, 20); 
        bottomContainer.add(scoreScrollPane, bottomGbc);

        // mengatur posisi panel kontrol (username, tombol) di sisi kanan
        bottomGbc.gridx = 1;
        bottomGbc.weightx = 4; 
        bottomGbc.anchor = GridBagConstraints.WEST; 
        bottomGbc.insets = new Insets(50, 20, 10, 50); 
        bottomContainer.add(controlPanel, bottomGbc);
        
        // menambahkan container utama yang berisi tabel dan kontrol ke panel utama
        gbc.gridy = 1;
        gbc.weighty = 1.7; // memberikan bobot vertikal lebih banyak untuk area konten
        gbc.fill = GridBagConstraints.BOTH; // mengisi seluruh ruang yang tersedia
        gbc.insets = new Insets(0,0,0,0);
        add(bottomContainer, gbc);
    }
    
    /**
     * Metode untuk memuat gambar latar belakang dari folder assets
     */
    private void loadBackgroundImage() {
        try {
            URL bgUrl = getClass().getResource("/assets/images/background2.png"); 
            if (bgUrl != null) {
                this.backgroundImage = new ImageIcon(bgUrl).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode untuk membuat dan mengatur tampilan tabel leaderboard
     */
    private JScrollPane createScoreTable() {
        String[] columnNames = {"Username", "Score", "Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setRowHeight(32);
        scoreTable.setFont(new Font("Arial", Font.PLAIN, 18));
        // menambahkan listener agar saat baris di tabel diklik, username otomatis terisi
        scoreTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = scoreTable.getSelectedRow();
                if (row >= 0) usernameField.setText((String) tableModel.getValueAt(row, 0));
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        
        // mengatur tampilan tabel dan scrollpane agar semi-transparan dan sesuai tema
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scoreTable.setOpaque(false);
        scoreTable.setBackground(new Color(40, 10, 60, 160));
        scoreTable.setForeground(new Color(255, 230, 180));
        scoreTable.setGridColor(new Color(170, 120, 220, 80));
        
        // mengatur tampilan header tabel
        JTableHeader header = scoreTable.getTableHeader();
        header.setBackground(new Color(60, 20, 80, 200));
        header.setForeground(new Color(255, 220, 130));
        header.setFont(new Font("Arial", Font.BOLD, 20));
        
        return scrollPane;
    }

    /**
     * Metode untuk membuat dan mengatur panel kontrol di sisi kanan menu
     */
    private JPanel createControlPanel() {
        // membuat panel dengan GridBagLayout untuk tata letak yang fleksibel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // membuat label dan field untuk input username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(255, 220, 130));
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        // membuat field input untuk username
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(250, 45));
        usernameField.setBackground(new Color(50, 20, 70, 220));
        usernameField.setForeground(new Color(255, 230, 180));
        usernameField.setCaretColor(Color.WHITE);
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        // membuat tombol "Play" untuk memulai permainan
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.setPreferredSize(new Dimension(180, 55));
        playButton.setBackground(new Color(90, 30, 130));
        playButton.setForeground(new Color(255, 220, 130));
        playButton.setFocusPainted(false);
        playButton.addActionListener(e -> handlePlayButton());
        gbc.gridy = 2;
        gbc.insets = new Insets(25, 8, 12, 8); // beri jarak lebih di atas tombol play
        panel.add(playButton, gbc);

        // membuat tombol "Quit" untuk keluar dari aplikasi
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.setPreferredSize(new Dimension(180, 55));
        quitButton.setBackground(new Color(80, 20, 100));
        quitButton.setForeground(new Color(255, 220, 130));
        quitButton.setFocusPainted(false);
        quitButton.addActionListener(e -> System.exit(0)); // keluar dari aplikasi saat diklik
        gbc.gridy = 3;
        gbc.insets = new Insets(12, 8, 8, 8);
        panel.add(quitButton, gbc);

        return panel;
    }

    /**
     * Metode untuk mengambil data skor dari ViewModel dan menampilkannya di tabel
     */
    public void loadScores() {
        tableModel.setRowCount(0); // mengosongkan tabel terlebih dahulu untuk menghindari data ganda
        // mengambil semua data skor dari viewmodel lalu menambahkannya ke tabel satu per satu
        for (ScoreEntry entry : viewModel.getAllScores()) {
            tableModel.addRow(new Object[]{entry.getUsername(), entry.getScore(), entry.getCount()});
        }
    }

    /**
     * Metode yang dipanggil saat tombol "Play" ditekan
     */
    private void handlePlayButton() {
        String username = usernameField.getText().trim(); // mengambil teks dari field dan menghapus spasi di awal/akhir
        if (username.isEmpty()) {
            // jika username kosong, tampilkan pesan error
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        } else {
            // jika terisi, panggil metode di MainFrame untuk memulai permainan
            mainFrame.startGame(username);
        }
    }

    /**
     * Metode ini digunakan untuk menggambar komponen custom (gambar latar belakang)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
