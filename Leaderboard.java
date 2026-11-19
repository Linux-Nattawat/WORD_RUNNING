import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Leaderboard extends JPanel {
    private MainFrame mainFrame;
    private ArrayList<Player> players;
    private Lobby.ScrollablePanel rankPanel;
    private Image bgImage;

    // green / cream palette to match the park background
    private static final Color INK = new Color(0x14, 0x3D, 0x2B);
    private static final Color MUTED = new Color(0x3F, 0x6B, 0x55);
    private static final Color GOLD_TEXT = new Color(0xB4, 0x7A, 0x00);
    private static final Color CARD_WHITE = new Color(255, 255, 250, 232);

    public Leaderboard(MainFrame mainFrame, String scoreData) {
        this.mainFrame = mainFrame;
        this.players = new ArrayList<>();

        try {
            bgImage = new ImageIcon(getClass().getResource("/img/Leaderboard_Background.png")).getImage();
        } catch (Exception e) {
            System.out.println("cannot load Background.png, use basic");
        }

        setLayout(new BorderLayout(20, 16));
        setBorder(BorderFactory.createEmptyBorder(26, 70, 30, 70));
        setOpaque(false);

        // ---------- title ----------
        JLabel titleLabel = new JLabel("อันดับคะแนน", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        titleLabel.setForeground(INK);

        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(pill(titleLabel));
        add(titleWrap, BorderLayout.NORTH);

        // ---------- ranking list ----------
        rankPanel = new Lobby.ScrollablePanel();
        rankPanel.setOpaque(false);
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS));
        rankPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scrollPane = new JScrollPane(rankPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        // capped column so the rows stay cards instead of stretching on a wide window
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

        // ---------- back button ----------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.setOpaque(false);

        MainMenu.RoundedButton mainMenuButton = new MainMenu.RoundedButton("กลับหน้าหลัก",
                26,
                new Color(0x74, 0xD5, 0xFF),
                new Color(0x1B, 0x77, 0xD6),
                new Color(0x1B, 0x3A, 0x55),
                Color.WHITE,
                new Color(0x0B, 0x2A, 0x45, 235));
        mainMenuButton.setPreferredSize(new Dimension(230, 62));
        mainMenuButton.addActionListener(e -> {
            mainFrame.resetToMainMenu();
        });

        bottomPanel.add(mainMenuButton);
        add(bottomPanel, BorderLayout.SOUTH);

        updateScores(scoreData);
    }

    private static MainMenu.RoundedPanel pill(JLabel label) {
        MainMenu.RoundedPanel pill = new MainMenu.RoundedPanel(26, new Color(255, 255, 250, 228));
        pill.setLayout(new BorderLayout());
        pill.setBorder(BorderFactory.createEmptyBorder(10, 30, 14, 30));
        pill.add(label, BorderLayout.CENTER);
        return pill;
    }

    private JComponent makeRankRow(int rank, Player player) {
        MainMenu.RoundedPanel row = new MainMenu.RoundedPanel(20, CARD_WHITE);
        row.setLayout(new BorderLayout(14, 0));
        row.setBorder(BorderFactory.createEmptyBorder(10, 16, 14, 22));

        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(new RankBadge(rank));
        row.add(badgeWrap, BorderLayout.WEST);

        Font nameFont = new Font("Tahoma", Font.BOLD, 22);
        Font scoreFont = new Font("Tahoma", Font.BOLD, 20);
        String scoreText = player.getScore() + " คะแนน";

        FontMetrics fmName = getFontMetrics(nameFont);
        FontMetrics fmScore = getFontMetrics(scoreFont);
        // row is 820 wide: minus insets, badge, gaps, score and scrollbar slack
        int nameSpace = 820 - 16 - 22 - 44 - 28 - 20 - fmScore.stringWidth(scoreText);

        JLabel nameLabel = new JLabel(ellipsize(player.getName(), fmName, nameSpace));
        nameLabel.setFont(nameFont);
        nameLabel.setForeground(INK);
        row.add(nameLabel, BorderLayout.CENTER);

        JLabel scoreLabel = new JLabel(scoreText);
        scoreLabel.setFont(scoreFont);
        scoreLabel.setForeground(rank == 1 ? GOLD_TEXT : MUTED);
        row.add(scoreLabel, BorderLayout.EAST);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setPreferredSize(new Dimension(200, 70));
        return row;
    }

    private JComponent makePlaceholderRow(String text) {
        MainMenu.RoundedPanel row = new MainMenu.RoundedPanel(20, CARD_WHITE);
        row.setLayout(new BorderLayout());
        row.setBorder(BorderFactory.createEmptyBorder(16, 22, 20, 22));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Tahoma", Font.PLAIN, 22));
        label.setForeground(MUTED);
        row.add(label, BorderLayout.CENTER);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return row;
    }

    private static String ellipsize(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "…";
        int end = text.length();
        while (end > 0 && fm.stringWidth(text.substring(0, end) + ellipsis) > maxWidth) {
            end--;
        }
        return text.substring(0, end) + ellipsis;
    }

    private void showRanking() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.size() - 1 - i; j++) {
                if (players.get(j).getScore() < players.get(j + 1).getScore()) {
                    Player temp = players.get(j);
                    players.set(j, players.get(j + 1));
                    players.set(j + 1, temp);
                }
            }
        }

        rankPanel.removeAll();
        if (players.isEmpty()) {
            rankPanel.add(makePlaceholderRow("ยังไม่มีคะแนน"));
        } else {
            for (int i = 0; i < players.size(); i++) {
                rankPanel.add(makeRankRow(i + 1, players.get(i)));
                rankPanel.add(Box.createVerticalStrut(10));
            }
        }
        rankPanel.revalidate();
        rankPanel.repaint();
    }

    public void updateScores(String scoreData) {
        players.clear();

        if (scoreData != null && !scoreData.isEmpty()) {
            for (String part : scoreData.split(",")) {
                String[] info = part.split(":");
                if (info.length != 2) {
                    continue;
                }
                try {
                    Player p = new Player(info[0].trim());
                    p.setScore(Integer.parseInt(info[1].trim()));
                    players.add(p);
                } catch (NumberFormatException ex) {
                    System.out.println("invalid score entry: " + part);
                }
            }
        }

        showRanking();
        revalidate();
        repaint();
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

    // gold / silver / bronze medal for the podium, plain for the rest
    static class RankBadge extends JComponent {
        private final int rank;

        RankBadge(int rank) {
            this.rank = rank;
            setPreferredSize(new Dimension(44, 44));
            setMinimumSize(new Dimension(44, 44));
            setMaximumSize(new Dimension(44, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight());
            Color top;
            Color bottom;
            Color textColor;

            if (rank == 1) {
                top = new Color(0xFF, 0xE4, 0x8A);
                bottom = new Color(0xE8, 0xA2, 0x00);
                textColor = new Color(0x4A, 0x33, 0x00);
            } else if (rank == 2) {
                top = new Color(0xF3, 0xF7, 0xFB);
                bottom = new Color(0xA9, 0xB8, 0xC7);
                textColor = new Color(0x2E, 0x3D, 0x4A);
            } else if (rank == 3) {
                top = new Color(0xF8, 0xCB, 0x9E);
                bottom = new Color(0xC0, 0x7A, 0x3A);
                textColor = new Color(0x43, 0x24, 0x0A);
            } else {
                top = new Color(0xEA, 0xF5, 0xEF);
                bottom = new Color(0xC3, 0xDD, 0xD0);
                textColor = INK;
            }

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(2, 3, d - 4, d - 4);

            g2.setPaint(new GradientPaint(0, 0, top, 0, d, bottom));
            g2.fillOval(1, 1, d - 3, d - 3);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(1, 1, d - 3, d - 3);

            g2.setFont(new Font("Tahoma", Font.BOLD, 19));
            FontMetrics fm = g2.getFontMetrics();
            String text = String.valueOf(rank);
            g2.setColor(textColor);
            g2.drawString(text,
                    (d - fm.stringWidth(text)) / 2,
                    (d - fm.getHeight()) / 2 + fm.getAscent() - 1);

            g2.dispose();
        }
    }
}

