import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Image bgImage;
    private JLabel timerLabel;
    private LeaderboardView miniLeaderboard;
    private JPanel leaderboardCard;
    private JLabel diffAndIndex;
    private JLabel targetWord;
    private JTextField inputField;
    private String latestScoreData = "";

    private MainFrame mainFrame;
    private String[] words;
    private int currentIndex = 0;
    private int timeLeft = 15;
    private Timer gameTimer;

    // dark frosted cards, since this screen's background is a dark forest
    private static final Color CARD_DARK = new Color(3, 22, 20, 150);
    // both top side cards share this width, which keeps the timer exactly centred
    private static final int SIDE_WIDTH = 200;

    public GamePanel(MainFrame mainFrame, String[] words) {
        this.mainFrame = mainFrame;
        this.words = words;

        try {
            bgImage = new ImageIcon(getClass().getResource("/img/GamePanel_Background.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("cannot load Background.png, use basic");
        }

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(26, 40, 34, 40));

        TopPanel();
        CenterPanel();
        BottomPanel();

        if (words != null && words.length > 0) {
            updateWordDisplay();
        }

        startTimer();
        SwingUtilities.invokeLater(() -> {
            inputField.requestFocusInWindow();
        });

        // keep long Thai words inside the word card when the window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (targetWord.getText() != null && !targetWord.getText().isEmpty()) {
                    setTargetWordText(targetWord.getText());
                }
            }
        });

    }

    private static MainMenu.RoundedPanel darkCard(int radius) {
        MainMenu.RoundedPanel card = new MainMenu.RoundedPanel(radius, CARD_DARK);
        card.setLayout(new BorderLayout());
        return card;
    }

    // Painted by hand so long player names are ellipsized to the card width
    // (an HTML JLabel happily draws past its own bounds).
    static class LeaderboardView extends JComponent {
        private static final int PAD_X = 16;
        private static final int PAD_Y = 12;
        private static final int MAX_ROWS = 6;

        private final List<String[]> rows = new ArrayList<>();

        LeaderboardView() {
            setFont(new Font("Tahoma", Font.PLAIN, 15));
        }

        void setRows(List<String[]> newRows) {
            rows.clear();
            rows.addAll(newRows);
            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fmHeading = getFontMetrics(getFont().deriveFont(Font.BOLD));
            FontMetrics fm = getFontMetrics(getFont());
            int lines = Math.max(1, Math.min(rows.size(), MAX_ROWS));
            int extra = rows.size() > MAX_ROWS ? fm.getHeight() : 0;
            return new Dimension(160, PAD_Y * 2 + fmHeading.getHeight() + lines * fm.getHeight() + extra);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.clipRect(0, 0, getWidth(), getHeight());

            int x = PAD_X;
            int right = getWidth() - PAD_X;

            g2.setFont(getFont().deriveFont(Font.BOLD));
            FontMetrics fmHeading = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            int y = PAD_Y + fmHeading.getAscent();
            g2.drawString("อันดับคะแนน", x, y);
            y += fmHeading.getHeight();

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();

            if (rows.isEmpty()) {
                g2.setColor(new Color(255, 255, 255, 185));
                g2.drawString("กำลังรอ...", x, y + fm.getAscent());
                g2.dispose();
                return;
            }

            g2.setColor(new Color(255, 255, 255, 235));
            int limit = Math.min(rows.size(), MAX_ROWS);
            for (int i = 0; i < limit; i++) {
                String name = rows.get(i)[0];
                String score = rows.get(i)[1];
                int scoreWidth = fm.stringWidth(score);

                int baseline = y + fm.getAscent();
                g2.drawString(ellipsize(name, fm, right - x - scoreWidth - 12), x, baseline);
                g2.drawString(score, right - scoreWidth, baseline);
                y += fm.getHeight();
            }

            if (rows.size() > MAX_ROWS) {
                g2.setColor(new Color(255, 255, 255, 160));
                g2.drawString("…", x, y + fm.getAscent());
            }
            g2.dispose();
        }

        private static String ellipsize(String text, FontMetrics fm, int maxWidth) {
            if (maxWidth <= 0) {
                return "";
            }
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
    }

    private void TopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(18, 8));
        topPanel.setOpaque(false);

        // ---- left: mini leaderboard, fixed width so long names cannot push the timer ----
        miniLeaderboard = new LeaderboardView();

        leaderboardCard = darkCard(22);
        leaderboardCard.add(miniLeaderboard, BorderLayout.CENTER);
        leaderboardCard.setPreferredSize(
                new Dimension(SIDE_WIDTH, miniLeaderboard.getPreferredSize().height));

        // ---- centre: countdown ----
        timerLabel = new JLabel("15", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Tahoma", Font.BOLD, 54));
        timerLabel.setForeground(Color.WHITE);

        MainMenu.RoundedPanel timerCard = darkCard(58);
        timerCard.setPreferredSize(new Dimension(126, 116));
        timerCard.add(timerLabel, BorderLayout.CENTER);

        JPanel timerWrap = new JPanel(new GridBagLayout());
        timerWrap.setOpaque(false);
        GridBagConstraints timerGbc = new GridBagConstraints();
        timerGbc.anchor = GridBagConstraints.NORTH;
        timerGbc.weighty = 1.0;
        timerWrap.add(timerCard, timerGbc);

        // ---- right: word counter + difficulty ----
        diffAndIndex = new JLabel("<html><center><b>คำที่ 1 / 20</b><br>ง่าย</center></html>", SwingConstants.CENTER);
        diffAndIndex.setFont(new Font("Tahoma", Font.PLAIN, 16));
        diffAndIndex.setForeground(Color.WHITE);
        diffAndIndex.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        MainMenu.RoundedPanel diffCard = darkCard(22);
        diffCard.add(diffAndIndex, BorderLayout.CENTER);

        JPanel eastWrap = new JPanel(new BorderLayout());
        eastWrap.setOpaque(false);
        eastWrap.setPreferredSize(new Dimension(SIDE_WIDTH, 60));
        eastWrap.add(diffCard, BorderLayout.NORTH);

        topPanel.add(leaderboardCard, BorderLayout.WEST);
        topPanel.add(timerWrap, BorderLayout.CENTER);
        topPanel.add(eastWrap, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void CenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        targetWord = new JLabel("", SwingConstants.CENTER);
        targetWord.setFont(new Font("Tahoma", Font.BOLD, 56));
        targetWord.setForeground(Color.WHITE);
        targetWord.setVerticalAlignment(SwingConstants.CENTER);
        targetWord.setBorder(BorderFactory.createEmptyBorder(24, 72, 32, 72));

        MainMenu.RoundedPanel wordCard = darkCard(32);
        wordCard.add(targetWord, BorderLayout.CENTER);

        centerPanel.add(wordCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void BottomPanel() {
        inputField = new MainMenu.RoundedTextField(25, 26);
        inputField.setFont(new Font("Tahoma", Font.PLAIN, 28));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.addActionListener(e -> checkAndSendAnswer());

        // JTextField sizes itself from its column count, so wrap it to control the width
        JPanel fieldWrap = new JPanel(new BorderLayout());
        fieldWrap.setOpaque(false);
        fieldWrap.setPreferredSize(new Dimension(560, 68));
        fieldWrap.add(inputField, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(fieldWrap);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void checkAndSendAnswer() {
        if (words == null || currentIndex >= words.length) {
            return;
        }

        // NFC keeps Thai combining marks consistent, so typing matches the word exactly
        String userInput = Normalizer.normalize(inputField.getText().trim(), Normalizer.Form.NFC);
        String answer = Normalizer.normalize(words[currentIndex].trim(), Normalizer.Form.NFC);

        if (userInput.equalsIgnoreCase(answer)) {
            mainFrame.getNetworkManager().sendAnswer(timeLeft);
        } else {
            mainFrame.getNetworkManager().sendAnswer(0);
        }

        currentIndex++;
        if (currentIndex < words.length) {
            updateWordDisplay();
            resetTimer();
        } else {
            finishGame();
        }

        inputField.setText("");
    }

    // shrink the font for long words so the text never runs past the card / window edge
    private void setTargetWordText(String text) {
        targetWord.setText(text);

        int width = getWidth() > 0 ? getWidth() : 1000;
        int available = width - 260;    // window padding + card padding
        int size = 56;
        Font font = new Font("Tahoma", Font.BOLD, size);
        while (size > 28 && targetWord.getFontMetrics(font).stringWidth(text) > available) {
            size -= 2;
            font = new Font("Tahoma", Font.BOLD, size);
        }
        targetWord.setFont(font);
    }

    private void updateWordDisplay() {
        setTargetWordText(words[currentIndex]);

        String diff = "ง่าย";
        if (currentIndex >= 5)
            diff = "ปานกลาง";
        if (currentIndex >= 10)
            diff = "ยาก";
        if (currentIndex >= 15)
            diff = "ยากมาก";

        diffAndIndex.setText("<html><center><b>คำที่ " + (currentIndex + 1) + " / 20</b><br>" + diff + "</center></html>");
    }

    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                currentIndex++;
                if (currentIndex < words.length) {
                    updateWordDisplay();
                    resetTimer();
                } else {
                    finishGame();
                }
            }
        });
        gameTimer.start();
    }

    private void resetTimer() {
        timeLeft = 15;
        timerLabel.setText("15");
    }

    private void finishGame() {
        setTargetWordText("จบเกมแล้ว!");
        inputField.setEnabled(false);
        if (gameTimer != null)
            gameTimer.stop();

        Timer delayTimer = new Timer(3000, e -> {
            mainFrame.switchScreen(new Leaderboard(mainFrame, latestScoreData));
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void updateLeaderboard(String scoreData) {
        this.latestScoreData = scoreData;

        List<String[]> rows = new ArrayList<>();
        for (String part : scoreData.split(",")) {
            String[] info = part.split(":");
            if (info.length == 2 && !info[0].trim().isEmpty()) {
                rows.add(new String[] { info[0].trim(), info[1].trim() });
            }
        }
        miniLeaderboard.setRows(rows);

        leaderboardCard.setPreferredSize(
                new Dimension(SIDE_WIDTH, miniLeaderboard.getPreferredSize().height));
        leaderboardCard.revalidate();
        revalidate();
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
}
