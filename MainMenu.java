import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

public class MainMenu extends JPanel {
    // ---- palette (sky / cartoon theme to match the background) ----
    private static final Color INK = new Color(0x22, 0x35, 0x48);
    private static final Color LABEL_INK = new Color(0x16, 0x3A, 0x5F);
    private static final Color FIELD_BORDER = new Color(0x2C, 0x4A, 0x6B);
    private static final Color FIELD_FOCUS = new Color(0xFF, 0xB3, 0x00);

    private MainFrame mainFrame;
    private Image bgImage;

    public MainMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        try {
            bgImage = new ImageIcon(getClass().getResource("/img/MainMenu_Background.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("cannot load Background.png, use basic");
        }

        setLayout(new BorderLayout());
        setOpaque(false);

        // ---------- text fields ----------
        RoundedTextField nameField = new RoundedTextField(20, 22);
        RoundedTextField ipField = new RoundedTextField(20, 22);
        ipField.setText("127.0.0.1");

        JLabel nameLabel = makeLabel("ชื่อผู้เล่น");
        JLabel ipLabel = makeLabel("IP เซิร์ฟเวอร์");

        JPanel form = new JPanel(new GridLayout(2, 2, 18, 18));
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(620, 140));
        form.setMinimumSize(new Dimension(620, 140));
        form.setMaximumSize(new Dimension(620, 140));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(nameLabel);
        form.add(nameField);
        form.add(ipLabel);
        form.add(ipField);

        // ---------- buttons ----------
        RoundedButton createButton = new RoundedButton("สร้างห้อง",
                24,
                new Color(0xFF, 0xE4, 0x7A),
                new Color(0xFF, 0x9E, 0x1E),
                new Color(0x2B, 0x3A, 0x4A),
                new Color(0x2B, 0x1E, 0x05),
                null);

        RoundedButton joinButton = new RoundedButton("เข้าร่วม",
                24,
                new Color(0x74, 0xD5, 0xFF),
                new Color(0x1B, 0x77, 0xD6),
                new Color(0x1B, 0x3A, 0x55),
                Color.WHITE,
                new Color(0x0B, 0x2A, 0x45, 235));

        RoundedBadge or = new RoundedBadge("หรือ");

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JComponent c : new JComponent[] { createButton, or, joinButton }) {
            c.setAlignmentY(Component.CENTER_ALIGNMENT);
        }

        buttons.add(createButton);
        buttons.add(Box.createHorizontalStrut(24));
        buttons.add(or);
        buttons.add(Box.createHorizontalStrut(24));
        buttons.add(joinButton);

        // ---------- frosted card ----------
        RoundedPanel card = new RoundedPanel(34, new Color(255, 255, 255, 208));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 42, 36, 48));
        card.add(form);
        card.add(Box.createVerticalStrut(30));
        card.add(buttons);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(60, 0, 0, 0);
        center.add(card, gbc);
        add(center, BorderLayout.CENTER);

        // ---------- behaviour (unchanged) ----------
        joinButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String ip = ipField.getText().trim();

            if (name.isEmpty() || ip.isEmpty()) {
                UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.BOLD, 22));
                UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.BOLD, 22));
                JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อผู้เล่นและ IP เซิร์ฟเวอร์", "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean connected = mainFrame.getNetworkManager().connect(ip, 12345, name);

            if (connected) {
                mainFrame.setCurrentServerIp(ip);
                mainFrame.switchScreen(new Lobby(mainFrame, ip));
            } else {
                UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.BOLD, 22));
                UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.BOLD, 22));
                JOptionPane.showMessageDialog(this,
                        "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้",
                        "เชื่อมต่อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.BOLD, 22));
                UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.BOLD, 22));
                JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อผู้เล่น", "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            mainFrame.resetToMainMenu();

            GameServer gameServer = new GameServer();
            mainFrame.setGameServer(gameServer);

            String ip = "127.0.0.1";
            try {
                String address = InetAddress.getLocalHost().getHostAddress();
                ip = address;
            } catch (Exception ex) {
                System.out.println("cannot get local IP, use 127.0.0.1");
            }

            mainFrame.setCurrentServerIp(ip);
            boolean connected = mainFrame.getNetworkManager().connect(ip, 12345, name);
            if (connected) {
                mainFrame.switchScreen(new Lobby(mainFrame, ip));
            } else {
                JOptionPane.showMessageDialog(this, "เริ่มเซิร์ฟเวอร์ไม่สำเร็จ", "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static JLabel makeLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("Tahoma", Font.BOLD, 22));
        label.setForeground(LABEL_INK);
        return label;
    }

    static Color mix(Color a, Color b, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * ratio);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * ratio);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * ratio);
        return new Color(r, g, bl, a.getAlpha());
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

    // ------------------------------------------------------------------
    // Pill-shaped button with gradient fill, thick cartoon outline and
    // outlined text. Hover / press states are painted by hand.
    // ------------------------------------------------------------------
    static class RoundedButton extends JButton {
        private final int radius;
        private final Color topColor;
        private final Color bottomColor;
        private final Color borderColor;
        private final Color textColor;
        private final Color textOutline;
        private boolean hover;

        RoundedButton(String text, int radius, Color topColor, Color bottomColor,
                Color borderColor, Color textColor, Color textOutline) {
            super(text);
            this.radius = radius;
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.borderColor = borderColor;
            this.textColor = textColor;
            this.textOutline = textOutline;

            setFont(new Font("Tahoma", Font.BOLD, 24));
            setPreferredSize(new Dimension(172, 58));
            setMinimumSize(new Dimension(172, 58));
            setMaximumSize(new Dimension(172, 58));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder());

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    repaint();
                }
            };
            addMouseListener(mouse);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            boolean pressed = getModel().isPressed() && getModel().isArmed();
            int lift = pressed ? 3 : 0;

            Color top = topColor;
            Color bottom = bottomColor;
            if (!isEnabled()) {
                top = new Color(0xC9, 0xD2, 0xDA);
                bottom = new Color(0xA9, 0xB6, 0xC2);
            } else if (hover) {
                top = mix(topColor, Color.WHITE, 0.16f);
                bottom = mix(bottomColor, Color.WHITE, 0.10f);
            }

            // drop shadow
            g2.setColor(new Color(0, 30, 60, pressed ? 40 : 75));
            g2.fillRoundRect(4, 8 - lift, w - 8, h - 12, radius, radius);

            // body gradient
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
            g2.fillRoundRect(2, 2 - lift, w - 6, h - 10, radius, radius);

            // glossy top half
            g2.setColor(new Color(255, 255, 255, 85));
            g2.fillRoundRect(7, 5 - lift, w - 16, (h - 14) / 2, radius - 6, radius - 6);

            // thick outline
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(isEnabled() ? borderColor : new Color(0x8A, 0x99, 0xA6));
            g2.drawRoundRect(2, 2 - lift, w - 6, h - 10, radius, radius);

            // keyboard focus ring
            if (isFocusOwner()) {
                g2.setColor(new Color(0x1E, 0x88, 0xE5, 130));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, -lift, w - 2, h - 4, radius + 4, radius + 4);
            }

            // outlined label text
            String text = getText();
            if (text != null && !text.isEmpty()) {
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2 + (pressed ? 2 : 0);

                if (textOutline != null) {
                    g2.setColor(textOutline);
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx != 0 || dy != 0) {
                                g2.drawString(text, tx + dx, ty + dy);
                            }
                        }
                    }
                } else {
                    // soft drop shadow keeps Thai tone marks readable
                    g2.setColor(new Color(0, 0, 0, 45));
                    g2.drawString(text, tx, ty + 2);
                }
                g2.setColor(textColor);
                g2.drawString(text, tx, ty);
            }
            g2.dispose();
        }
    }

    // ------------------------------------------------------------------
    // Rounded text field with soft white fill and a golden focus ring.
    // ------------------------------------------------------------------
    static class RoundedTextField extends JTextField {
        private final int radius;

        RoundedTextField(int columns, int radius) {
            super(columns);
            this.radius = radius;

            setOpaque(false);
            setFont(new Font("Tahoma", Font.PLAIN, 20));
            setForeground(INK);
            setCaretColor(new Color(0x1B, 0x5E, 0x9B));
            setSelectionColor(new Color(0xFF, 0xD9, 0x3D));
            setSelectedTextColor(INK);
            setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            boolean focused = isFocusOwner();

            // soft shadow
            g2.setColor(new Color(0, 30, 60, 22));
            g2.fillRoundRect(2, 4, w - 5, h - 8, radius, radius);

            // fill
            g2.setColor(focused ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 248));
            g2.fillRoundRect(1, 1, w - 4, h - 7, radius, radius);

            // border (gold while focused)
            g2.setStroke(new BasicStroke(focused ? 3f : 2f));
            g2.setColor(focused ? FIELD_FOCUS : FIELD_BORDER);
            g2.drawRoundRect(1, 1, w - 4, h - 7, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------------
    // Frosted rounded card that groups the form and the buttons.
    // ------------------------------------------------------------------
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // layered shadow
            g2.setColor(new Color(0, 30, 60, 28));
            g2.fillRoundRect(6, 10, w - 12, h - 15, radius, radius);
            g2.setColor(new Color(0, 30, 60, 45));
            g2.fillRoundRect(3, 6, w - 8, h - 12, radius, radius);

            // body: soft vertical gradient so there is no visible seam
            Color top = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(),
                    Math.min(255, fill.getAlpha() + 26));
            g2.setPaint(new GradientPaint(0, 0, top, 0, h - 9, fill));
            g2.fillRoundRect(0, 0, w - 6, h - 9, radius, radius);

            // light border
            g2.setColor(new Color(255, 255, 255, 190));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, w - 6, h - 9, radius, radius);

            g2.dispose();
        }
    }

    // Small "OR" pill between the two buttons.
    static class RoundedBadge extends JComponent {
        private final String text;

        RoundedBadge(String text) {
            this.text = text;
            setFont(new Font("Tahoma", Font.BOLD, 18));

            // Thai labels are wider than "OR", so size the pill from the font metrics
            FontMetrics fm = getFontMetrics(getFont());
            int width = Math.max(54, fm.stringWidth(text) + 28);
            setPreferredSize(new Dimension(width, 40));
            setMinimumSize(new Dimension(width, 40));
            setMaximumSize(new Dimension(width, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int r = h - 5;

            g2.setColor(new Color(0, 30, 60, 40));
            g2.fillRoundRect(2, 4, w - 4, h - 7, r, r);

            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(1, 1, w - 3, h - 6, r, r);

            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 6, r, r);

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(LABEL_INK);
            g2.drawString(text,
                    (w - 3 - fm.stringWidth(text)) / 2,
                    (h - 6 - fm.getHeight()) / 2 + fm.getAscent());

            g2.dispose();
        }
    }
}
