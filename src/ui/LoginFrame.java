
package ui;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;

public class LoginFrame extends JFrame {

    // Màu sắc sử dụng trong giao diện
    private final Color LEFT_BG = new Color(32, 35, 43);
    private final Color RIGHT_BG = new Color(19, 19, 19);
    private final Color BLUE = new Color(39, 100, 235);
    private final Color TEXT = new Color(230, 235, 245);
    private final Color MUTED = new Color(143, 158, 180);

    // Các thành phần cần xử lý sự kiện
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Green Home - Đăng nhập");
        setSize(1150, 650);
        setMinimumSize(new Dimension(900, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        initEvents();
    }

    // Tạo giao diện chính
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());

        setContentPane(mainPanel);
    }

    // Phần giới thiệu bên trái
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LEFT_BG);
        panel.setBorder(new EmptyBorder(28, 35, 24, 35));

        // Logo
        JLabel lblLogo = new JLabel("G   GREEN HOME");
        lblLogo.setForeground(new Color(90, 211, 165));
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 17));
        panel.add(lblLogo, BorderLayout.NORTH);

        // Nội dung giới thiệu
        JPanel intro = new JPanel();
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.setOpaque(false);

        JLabel lblSmall = new JLabel("HỆ THỐNG BÁN HÀNG TẠI QUẦY");
        lblSmall.setForeground(MUTED);
        lblSmall.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblTitle = new JLabel(
            "<html>Một nơi cho mọi<br>nghiệp vụ cửa hàng.</html>"
        );
        lblTitle.setForeground(new Color(142, 180, 227));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 29));

        JLabel lblDescription = new JLabel(
            "<html>Bán hàng, chăm sóc khách hàng và xử lý đổi trả<br>"
            + "với vai trò rõ ràng.</html>"
        );
        lblDescription.setForeground(MUTED);
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));

        intro.add(lblSmall);
        intro.add(Box.createVerticalStrut(22));
        intro.add(lblTitle);
        intro.add(Box.createVerticalStrut(18));
        intro.add(lblDescription);
        intro.add(Box.createVerticalStrut(28));

        addFeature(intro, "✓  Bán hàng và thanh toán");
        addFeature(intro, "✓  Đổi / trả và phê duyệt");
        addFeature(intro, "✓  Thống kê và quản lý");

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(intro);

        panel.add(center, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel(
            "Green Home - Sàn bán hàng tốc độ và an toàn"
        );
        lblFooter.setForeground(MUTED);
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 11));

        panel.add(lblFooter, BorderLayout.SOUTH);

        return panel;
    }

    // Thêm một dòng mô tả tính năng
    private void addFeature(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    }

    // Phần đăng nhập bên phải
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(RIGHT_BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(350, 380));

        JLabel lblWelcome = new JLabel("CHÀO MỪNG TRỞ LẠI");
        lblWelcome.setForeground(MUTED);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblTitle = new JLabel("Đăng nhập hệ thống");
        lblTitle.setForeground(new Color(142, 180, 227));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel lblDescription = new JLabel(
            "Chọn tài khoản mẫu để trải nghiệm từng vai trò."
        );
        lblDescription.setForeground(MUTED);
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        styleInput(txtUsername);
        styleInput(txtPassword);

        btnLogin = new JButton("Đăng nhập  →");
        btnLogin.setBackground(BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 44)
        );

        form.add(lblWelcome);
        form.add(Box.createVerticalStrut(10));
        form.add(lblTitle);
        form.add(Box.createVerticalStrut(12));
        form.add(lblDescription);
        form.add(Box.createVerticalStrut(32));

        form.add(createLabel("Tên đăng nhập"));
        form.add(Box.createVerticalStrut(8));
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(18));

        form.add(createLabel("Mật khẩu"));
        form.add(Box.createVerticalStrut(8));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(24));

        form.add(btnLogin);

        panel.add(form);

        getRootPane().setDefaultButton(btnLogin);

        return panel;
    }

    // Tạo nhãn cho ô nhập liệu
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Định dạng ô nhập liệu
    private void styleInput(JTextField input) {
        input.setBackground(RIGHT_BG);
        input.setForeground(TEXT);
        input.setCaretColor(Color.WHITE);
        input.setFont(new Font("SansSerif", Font.PLAIN, 14));

        input.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 64, 72), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 44)
        );
        input.setPreferredSize(new Dimension(350, 44));
    }

    // Xử lý sự kiện đăng nhập
    private void initEvents() {
        btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập tên đăng nhập và mật khẩu!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Giao diện đăng nhập đã hoạt động!\n"
                + "Chức năng xác thực sẽ được tích hợp sau.",
                "Green Home",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

        Arrays.fill(password, '\0');
    }

    // Chạy thử giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
