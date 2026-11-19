import java.awt.*;
import javax.swing.*;

public class Lobby extends JPanel {
    private MainFrame mainFrame;
    private JLabel playerCountLabel;
    private JLabel ipLabel;
    private ScrollablePanel playerListPanel;
    private Image bgImage;

    // warm palette to match the hotel-lobby background (cream / teal / gold)
    private static final Color INK = new Color(0x12, 0x3B, 0x38);
    private static final Color PILL_FILL = new Color(255, 252, 244, 225);
    private static final Color ROW_FILL = new Color(255, 255, 255, 238);

    public Lobby(MainFrame mainFrame, String serverIp) {
        this.mainFrame = mainFrame;

        try {
            bgImage = new ImageIcon(getClass().getResource("/img/Lobby_Background.png")).getImage();
        } catch (Exception e) {
            System.out.println("cannot load Background.png, use basic");
        }

        setLayout(new BorderLayout(20, 16));
        setBorder(BorderFactory.createEmptyBorder(26, 70, 30, 70));
        setOpaque(false);

        // ---------- top: player count / title / ip ----------
        JPanel topPanel = new JPanel(new BorderLayout(18, 0));
        topPanel.setOpaque(false);

        playerCountLabel = new JLabel("ผู้เล่น : 1 คน");
        playerCountLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        playerCountLabel.setForeground(INK);
        topPanel.add(pill(playerCountLabel), BorderLayout.WEST);

        JLabel titleLabel = new JLabel("ห้องรอผู้เล่น", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        titleLabel.setForeground(INK);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        ipLabel = new JLabel("IP : " + serverIp);
        ipLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        ipLabel.setForeground(INK);
        topPanel.add(pill(ipLabel), BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ---------- centre: player list ----------
        playerListPanel = new ScrollablePanel();
        playerListPanel.setOpaque(false);
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scrollPane = new JScrollPane(playerListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        // keep the list a readable column instead of stretching across a 1920px window
        JPanel listColumn = new JPanel(new BorderLayout());
        listColumn.setOpaque(false);
        listColumn.setPreferredSize(new Dimension(820, 100));
        listColumn.setMaximumSize(new Dimension(820, Integer.MAX_VALUE));
        listColumn.add(scrollPane, BorderLayout.CENTER);

        JPanel listWrap = new JPanel();
        listWrap.setOpaque(false);
        listWrap.setLayout(new BoxLayout(listWrap, BoxLayout.X_AXIS));
        listWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        listWrap.add(Box.createHorizontalGlue());
        listWrap.add(listColumn);
        listWrap.add(Box.createHorizontalGlue());
        add(listWrap, BorderLayout.CENTER);

        // ---------- bottom: back / start buttons ----------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        bottomPanel.setOpaque(false);

        MainMenu.RoundedButton backButton = new MainMenu.RoundedButton("กลับหน้าหลัก",
                26,
                new Color(0x74, 0xD5, 0xFF),
                new Color(0x1B, 0x77, 0xD6),
                new Color(0x1B, 0x3A, 0x55),
                Color.WHITE,
                new Color(0x0B, 0x2A, 0x45, 235));
        backButton.setPreferredSize(new Dimension(215, 62));
        backButton.addActionListener(e -> {
            mainFrame.resetToMainMenu();
        });

        MainMenu.RoundedButton startBtn = new MainMenu.RoundedButton("เริ่มเกม",
                26,
                new Color(0xFF, 0xE4, 0x7A),
                new Color(0xFF, 0x9E, 0x1E),
                new Color(0x2B, 0x3A, 0x4A),
                new Color(0x2B, 0x1E, 0x05),
                null);
        startBtn.setPreferredSize(new Dimension(215, 62));
        startBtn.addActionListener(e -> {
            mainFrame.getNetworkManager().sendMessage("START_GAME");
        });

        bottomPanel.add(backButton);
        bottomPanel.add(startBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        showWaitingPlaceholder();
    }

    private static MainMenu.RoundedPanel pill(JLabel label) {
        MainMenu.RoundedPanel pill = new MainMenu.RoundedPanel(22, PILL_FILL);
        pill.setLayout(new BorderLayout());
        pill.setBorder(BorderFactory.createEmptyBorder(9, 20, 13, 20));
        pill.add(label, BorderLayout.CENTER);
        return pill;
    }

    private JComponent makePlayerRow(int index, String name) {
        MainMenu.RoundedPanel row = new MainMenu.RoundedPanel(20, ROW_FILL);
        row.setLayout(new BorderLayout());
        row.setBorder(BorderFactory.createEmptyBorder(10, 22, 14, 22));

        JLabel label = new JLabel(index + ".   " + name);
        label.setFont(new Font("Tahoma", Font.BOLD, 22));
        label.setForeground(INK);
        row.add(label, BorderLayout.WEST);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setPreferredSize(new Dimension(200, 60));
        return row;
    }

    private void showWaitingPlaceholder() {
        playerListPanel.removeAll();

        MainMenu.RoundedPanel card = new MainMenu.RoundedPanel(20, ROW_FILL);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(14, 22, 18, 22));

        JLabel waiting = new JLabel("กำลังรอผู้เล่น...", SwingConstants.CENTER);
        waiting.setFont(new Font("Tahoma", Font.PLAIN, 21));
        waiting.setForeground(INK);
        card.add(waiting, BorderLayout.CENTER);

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        playerListPanel.add(card);

        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    public void updateLobby(String[] players) {
        playerCountLabel.setText("ผู้เล่น : " + players.length + " คน");

        if (players.length == 0) {
            showWaitingPlaceholder();
            return;
        }

        playerListPanel.removeAll();
        for (int i = 0; i < players.length; i++) {
            playerListPanel.add(makePlayerRow(i + 1, players[i]));
            playerListPanel.add(Box.createVerticalStrut(10));
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }

    // rows must stretch to the viewport width, a plain JPanel keeps its preferred width
    static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
