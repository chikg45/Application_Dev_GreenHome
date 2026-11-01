package ui;

import bus.BanHangBUS;
import bus.DoiTraBUS;
import entity.NhanVien;
import java.util.Objects;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Khung giao dien Green Home - Java Swing thuan.
 * Du lieu Dashboard hien tai chi la du lieu mau, chua ket noi CSDL.
 */
public class MainFrame extends JFrame {

    private static final Color BG = new Color(30, 33, 37);
    private static final Color SIDE = new Color(17, 18, 20);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(48, 174, 121);
    private static final Color GOLD = new Color(193, 143, 55);
    private static final Color RED = new Color(183, 88, 91);

    private final BanHangBUS banHangBUS;
    private final NhanVien nhanVienDangNhap;
    private final String tenNguoiDung;
    private final boolean laQuanLy;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
    private final JLabel lblPageTitle = label("Tổng quan", 17, Font.BOLD, TEXT);

    /** Constructor khung UI cũ, chưa gắn phiên đăng nhập SQL. */
    public MainFrame(String tenNguoiDung, boolean laQuanLy) {
        this(tenNguoiDung, laQuanLy, null, null);
    }

    /** Module đăng nhập gọi overload này sau khi đã xác thực nhân viên và vai trò. */
    public MainFrame(NhanVien nhanVien, boolean laQuanLy) {
        this(Objects.requireNonNull(nhanVien).getHoTen(), laQuanLy,
             new BanHangBUS(nhanVien), nhanVien);
    }

    private MainFrame(String tenNguoiDung, boolean laQuanLy, BanHangBUS bus, NhanVien nhanVien) {
        this.banHangBUS = bus;
        this.nhanVienDangNhap = nhanVien;
        this.tenNguoiDung = tenNguoiDung;
        this.laQuanLy = laQuanLy;
        setTitle("Green Home - Quản lý bán hàng");
        setSize(1360, 860);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        showPage(banHangBUS == null ? "Tổng quan" : "Bán hàng");
    }

    private void moLichSuHoaDon() {
        if (banHangBUS == null) HoaDonDialog.moDanhSach(this);
        else new HoaDonChucNangDialog(banHangBUS).moDanhSach(this);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ==================== SIDEBAR ====================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDE);
        sidebar.setPreferredSize(new Dimension(214, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(21, 15, 20, 14));
        brand.add(new Avatar("G", new Color(30, 156, 111), 34, 9), BorderLayout.WEST);
        JPanel brandText = vertical();
        brandText.add(label("GREEN HOME", 13, Font.BOLD, TEXT));
        brandText.add(Box.createVerticalStrut(3));
        brandText.add(label("ĐIỆN MÁY CHO MỌI NHÀ", 8, Font.PLAIN, MUTED));
        brand.add(brandText, BorderLayout.CENTER);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel navigation = vertical();
        navigation.setBorder(new EmptyBorder(12, 10, 10, 10));
        navigation.add(label("  NGHIỆP VỤ CỬA HÀNG", 10, Font.BOLD, MUTED));
        navigation.add(Box.createVerticalStrut(14));
        addMenu(navigation, "Tổng quan", "grid");
        addMenu(navigation, "Bán hàng", "cart");
        addMenu(navigation, "Khách hàng", "users");
        addMenu(navigation, "Sản phẩm", "box");
        addMenu(navigation, "Khuyến mãi", "tag");
        addMenu(navigation, "Đổi / trả", "return");
        addMenu(navigation, "Lịch làm việc", "calendar");
        if (laQuanLy) {
            addMenu(navigation, "Thống kê", "chart");
            addMenu(navigation, "Nhân viên & tài khoản", "users");
        }
        navigation.add(Box.createVerticalGlue());
        sidebar.add(navigation, BorderLayout.CENTER);

        JPanel footer = vertical();
        footer.setBorder(new EmptyBorder(6, 11, 15, 11));

        RoundedPanel today = rounded(new Color(29, 31, 35), BORDER, 12);
        today.setLayout(new BorderLayout(8, 0));
        today.setBorder(new EmptyBorder(10, 9, 10, 9));
        today.setMaximumSize(new Dimension(Integer.MAX_VALUE, 59));
        today.add(new JLabel(new UiIcon("calendar", MUTED, 17)), BorderLayout.WEST);
        JPanel todayText = vertical();
        todayText.add(label("Lịch hôm nay", 11, Font.BOLD, TEXT));
        todayText.add(Box.createVerticalStrut(3));
        todayText.add(label("Chưa có dữ liệu phân công", 9, Font.PLAIN, MUTED));
        today.add(todayText, BorderLayout.CENTER);
        today.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(today);
        footer.add(Box.createVerticalStrut(10));

        footer.add(footerButton("Quy trình nghiệp vụ", "help", () -> info("Quy trình nghiệp vụ")));
        footer.add(footerButton("Nhật ký thao tác", "history", () -> info("Nhật ký thao tác")));
        footer.add(Box.createVerticalStrut(10));
        JSeparator line = new JSeparator();
        line.setForeground(BORDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(line);
        footer.add(Box.createVerticalStrut(13));

        JPanel account = new JPanel(new BorderLayout(10, 0));
        account.setOpaque(false);
        account.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        account.add(new Avatar(initials(tenNguoiDung), new Color(43, 59, 89), 34, 12), BorderLayout.WEST);
        JPanel accountText = vertical();
        JLabel accountName = label(tenNguoiDung, 11, Font.BOLD, TEXT);
        accountName.setToolTipText(tenNguoiDung);
        accountText.add(accountName);
        accountText.add(Box.createVerticalStrut(3));
        accountText.add(label(laQuanLy ? "Quản lý" : "Nhân viên bán hàng", 9, Font.PLAIN, MUTED));
        account.add(accountText, BorderLayout.CENTER);
        JButton logout = new JButton(new UiIcon("logout", MUTED, 16));
        logout.setToolTipText("Đăng xuất");
        logout.setForeground(MUTED);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setFocusPainted(false);
        logout.addActionListener(e -> logout());
        account.add(logout, BorderLayout.EAST);
        account.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(account);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addMenu(JPanel navigation, String name, String icon) {
        JButton button = new JButton(name, new UiIcon(icon, MUTED, 17));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(12);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setForeground(MUTED);
        button.setBackground(SIDE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(0, 12, 0, 7));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 37));
        button.setPreferredSize(new Dimension(190, 37));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> showPage(name));
        navigation.add(button);
        navigation.add(Box.createVerticalStrut(3));
        menuButtons.put(name, button);
        if ("Tổng quan".equals(name)) {

            cardPanel.add(createDashboard(), name);

        } else if ("Bán hàng".equals(name)) {

            cardPanel.add(banHangBUS == null
                ? new BanHangPanel()
                : new BanHangChucNangPanel(banHangBUS, nhanVienDangNhap), name);

        } else if ("Khách hàng".equals(name)) {

            cardPanel.add(new KhachHangPanel(), name);

        } else if ("Sản phẩm".equals(name)) {

            cardPanel.add(new SanPhamPanel(laQuanLy), name);

        } else if ("Khuyến mãi".equals(name)) {

            cardPanel.add(new KhuyenMaiPanel(laQuanLy), name);

        } else if ("Đổi / trả".equals(name)) {

            cardPanel.add(nhanVienDangNhap == null ? new DoiTraPanel(laQuanLy)
                : new DoiTraPanel(new DoiTraBUS(nhanVienDangNhap)), name);

        } else if ("Lịch làm việc".equals(name)) {

            cardPanel.add(
                new LichLamViecPanel(laQuanLy, tenNguoiDung),
                name
            );

        } else if ("Thống kê".equals(name)) {

            cardPanel.add(new ThongKePanel(), name);

        } else if ("Nhân viên & tài khoản".equals(name)) {

            cardPanel.add(new NhanVienPanel(), name);

        } else {

            cardPanel.add(placeholder(name), name);
        }
    }

    private JPanel footerButton(String text, String icon, Runnable action) {
        JButton button = new JButton(text, new UiIcon(icon, MUTED, 15));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setForeground(MUTED);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(8, 4, 8, 0));

        button.addActionListener(e -> action.run());

        // Căn nút về bên trái Sidebar
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        row.add(button, BorderLayout.WEST);

        return row;
    }

    // ==================== KHUNG CHINH ====================

    private JPanel createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDE);
        header.setBorder(new EmptyBorder(10, 22, 10, 22));
        header.setPreferredSize(new Dimension(0, 65));
        JPanel headerLeft = vertical();
        headerLeft.add(label("Green Home  /  " + (laQuanLy ? "Quản lý" : "Nhân viên bán hàng"),
                10, Font.PLAIN, MUTED));
        headerLeft.add(Box.createVerticalStrut(4));
        headerLeft.add(lblPageTitle);
        header.add(headerLeft, BorderLayout.WEST);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        headerRight.setOpaque(false);
        headerRight.add(label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 11, Font.PLAIN, MUTED));
        JButton logout = new JButton(new UiIcon("logout", MUTED, 17));
        logout.setFocusPainted(false);
        logout.setContentAreaFilled(false);
        logout.setBorderPainted(false);
        logout.setBorder(new EmptyBorder(6, 7, 6, 7));
        logout.setToolTipText("Đăng xuất");
        logout.addActionListener(e -> logout());
        headerRight.add(logout);
        header.add(headerRight, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);
        cardPanel.setBackground(BG);
        main.add(cardPanel, BorderLayout.CENTER);
        return main;
    }

    // ==================== DASHBOARD ====================

    private JPanel createDashboard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        JPanel content = vertical();
        content.setBorder(new EmptyBorder(24, 28, 25, 28));

        JPanel welcome = new JPanel(new BorderLayout(10, 0));
        welcome.setOpaque(false);
        JPanel greeting = vertical();
        greeting.add(label("Xin chào, " + tenNguoiDung, 21, Font.BOLD, TEXT));
        greeting.add(Box.createVerticalStrut(6));
        greeting.add(label("Theo dõi hoạt động cửa hàng và các công việc cần xử lý.", 12, Font.PLAIN, MUTED));
        welcome.add(greeting, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        actions.setOpaque(false);
        actions.add(smallButton("Quy trình nghiệp vụ", "help", () -> info("Quy trình nghiệp vụ")));
        actions.add(primaryButton("Bán hàng", "cart", () -> {
            if (menuButtons.containsKey("Bán hàng")) showPage("Bán hàng");
            else info("Bán hàng - chỉ dành cho nhân viên bán hàng");
        }));
        welcome.add(actions, BorderLayout.EAST);
        content.add(welcome);
        content.add(Box.createVerticalStrut(17));

        RoundedPanel banner = rounded(new Color(31, 39, 53), new Color(50, 68, 101), 9);
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(11, 15, 11, 15));
        banner.add(label("ⓘ   Tổng quan chưa nối CSDL. Xem số liệu thật tại mục Thống kê.",
                11, Font.PLAIN, new Color(157, 181, 221)));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        content.add(banner);
        content.add(Box.createVerticalStrut(14));

        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 113));
        kpis.add(stat("Tổng doanh thu", "—", "Tiền bán đã thanh toán", "wallet", BLUE));
        kpis.add(stat("Tiền hoàn / trả", "—", "Hoàn tiền bán hàng", "return", RED));
        kpis.add(stat("Doanh thu thuần", "—", "Doanh thu sau hoàn trả", "chart", GREEN));
        kpis.add(stat("Hóa đơn đã bán", "—", "Đã thanh toán", "clipboard", GOLD));
        content.add(kpis);
        content.add(Box.createVerticalStrut(14));

        JPanel firstRow = dashboardRow(chartCard(), pendingCard(), 0.63);
        firstRow.setPreferredSize(new Dimension(850, 264));
        firstRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 264));
        content.add(firstRow);
        content.add(Box.createVerticalStrut(14));

        JPanel secondRow = dashboardRow(recentCard(), scheduleCard(), 0.63);
        secondRow.setPreferredSize(new Dimension(850, 230));
        secondRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        content.add(secondRow);
        content.add(Box.createVerticalStrut(14));

        JPanel end = new JPanel(new BorderLayout());
        end.setOpaque(false);
        end.add(label("Green Home · Tổng quan", 10, Font.PLAIN, MUTED), BorderLayout.WEST);
        end.add(label("Tổng quan chưa kết nối SQL Server", 10, Font.PLAIN, MUTED), BorderLayout.EAST);
        end.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        content.add(end);

        // GridBagLayout giu Dashboard o giua khi man hinh rong, khong lech ve Sidebar.
        JPanel centered = new JPanel(new GridBagLayout());
        centered.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        centered.add(content, gbc);

        JScrollPane scroll = new JScrollPane(centered);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel dashboardRow(JPanel left, JPanel right, double leftRatio) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        // Chia do rong theo weightx, tranh JPanel con ep rong cot phai.
        left.setPreferredSize(new Dimension(0, 210));
        right.setPreferredSize(new Dimension(0, 210));
        left.setMinimumSize(new Dimension(0, 0));
        right.setMinimumSize(new Dimension(0, 0));
        GridBagConstraints a = new GridBagConstraints();
        a.gridx = 0;
        a.gridy = 0;
        a.weightx = leftRatio;
        a.weighty = 1;
        a.fill = GridBagConstraints.BOTH;
        a.insets = new Insets(0, 0, 0, 7);
        row.add(left, a);
        GridBagConstraints b = new GridBagConstraints();
        b.gridx = 1;
        b.gridy = 0;
        b.weightx = 1 - leftRatio;
        b.weighty = 1;
        b.fill = GridBagConstraints.BOTH;
        b.insets = new Insets(0, 7, 0, 0);
        row.add(right, b);
        return row;
    }

    private JPanel stat(String title, String value, String caption, String icon, Color accent) {
        RoundedPanel card = rounded(CARD, BORDER, 12);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(15, 15, 13, 13));
        JPanel text = vertical();
        text.add(label(title, 11, Font.PLAIN, MUTED));
        text.add(Box.createVerticalStrut(9));
        text.add(label(value, 20, Font.BOLD, new Color(161, 192, 247)));
        text.add(Box.createVerticalStrut(7));
        text.add(label(caption, 10, Font.PLAIN, MUTED));
        card.add(text, BorderLayout.CENTER);
        JLabel badge = new JLabel(new UiIcon(icon, accent, 15));
        badge.setVerticalAlignment(SwingConstants.TOP);
        card.add(badge, BorderLayout.EAST);
        return card;
    }

    private JPanel chartCard() {
        RoundedPanel panel = section("Bán hàng & hoàn trả", "Tổng giá trị theo ngày", "Chi tiết", () -> showPage("Thống kê"));
        panel.add(new SalesChart(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel pendingCard() {
        RoundedPanel card = section("Cần xử lý", "Công việc đang chờ", "Xem tất cả", () -> showPage("Đổi / trả"));
        JPanel list = vertical();
        list.setBorder(new EmptyBorder(8, 0, 0, 0));
        list.add(pendingItem("Chờ phê duyệt đổi / trả", "Chưa có dữ liệu", "clipboard", GOLD, "—"));
        list.add(new JSeparator());
        list.add(pendingItem("Đơn đang xử lý", "Theo dõi tiến độ tại quầy", "cart", RED, "—"));
        list.add(new JSeparator());
        list.add(Box.createVerticalStrut(12));
        list.add(label("Chưa có dữ liệu công việc cần xử lý", 10, Font.PLAIN, MUTED));
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel pendingItem(String title, String subtitle, String icon, Color color, String count) {
        JPanel p = new JPanel(new BorderLayout(9, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(9, 3, 10, 3));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        p.add(new JLabel(new UiIcon(icon, color, 17)), BorderLayout.WEST);
        JPanel center = vertical();
        center.add(label(title, 12, Font.BOLD, TEXT));
        center.add(Box.createVerticalStrut(4));
        center.add(label(subtitle, 10, Font.PLAIN, MUTED));
        p.add(center, BorderLayout.CENTER);
        p.add(label(count, 13, Font.BOLD, new Color(160, 190, 242)), BorderLayout.EAST);
        return p;
    }

    private JPanel recentCard() {

    	RoundedPanel card = section(
    		    "Giao dịch gần đây",
    		    "Chưa có dữ liệu tổng quan",
    		    "Tra cứu",
    		    this::moLichSuHoaDon
    		);
        String[] columns = {
            "Hóa đơn",
            "Khách hàng",
            "Thực thanh toán",
            "Thời gian"
        };

        Object[][] rows = new Object[0][columns.length];

        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        table.setRowHeight(37);
        table.setShowGrid(false);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(37, 54, 82));
        table.setSelectionForeground(TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        // Căn lề tiêu đề các cột
        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value,
                    boolean selected, boolean focus,
                    int row, int column) {

                super.getTableCellRendererComponent(
                    t, value, selected, focus, row, column
                );

                setBorder(new EmptyBorder(5, 12, 5, 6));
                setBackground(new Color(28, 30, 33));
                setForeground(MUTED);
                setFont(new Font("SansSerif", Font.BOLD, 10));

                return this;
            }
        };

        table.getTableHeader().setDefaultRenderer(headerRenderer);

        // Căn lề nội dung của từng ô
        DefaultTableCellRenderer cellRenderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value,
                    boolean selected, boolean focus,
                    int row, int column) {

                super.getTableCellRendererComponent(
                    t, value, selected, focus, row, column
                );

                setBorder(new EmptyBorder(0, 12, 0, 6));

                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(cellRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        // Tạo khoảng cách giữa bảng và viền Card
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.setBorder(new EmptyBorder(0, 8, 0, 8));

        tableArea.add(scroll, BorderLayout.CENTER);
        card.add(tableArea, BorderLayout.CENTER);

        return card;
    }

    private JPanel scheduleCard() {
        RoundedPanel card = section("Lịch làm việc sắp tới", "Chưa có dữ liệu phân công",
                "Xem lịch", () -> showPage("Lịch làm việc"));
        JPanel list = vertical();
        list.setBorder(new EmptyBorder(3, 0, 0, 0));
        list.add(label("Chưa có dữ liệu lịch làm việc", 11, Font.PLAIN, MUTED));
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel scheduleRow(String day, String employee, String shift, Color accent) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JLabel date = label("<html><center>" + day.replace("/", "<br>") + "</center></html>", 11, Font.BOLD, TEXT);
        date.setHorizontalAlignment(SwingConstants.CENTER);
        date.setPreferredSize(new Dimension(37, 43));
        date.setOpaque(true);
        date.setBackground(new Color(32, 36, 45));
        row.add(date, BorderLayout.WEST);
        JPanel info = vertical();
        info.add(label(employee, 11, Font.BOLD, TEXT));
        info.add(Box.createVerticalStrut(4));
        info.add(label(shift, 10, Font.PLAIN, MUTED));
        row.add(info, BorderLayout.CENTER);
        JLabel tag = label("●", 13, Font.BOLD, accent);
        row.add(tag, BorderLayout.EAST);
        return row;
    }

    private RoundedPanel section(String title, String sub, String buttonText, Runnable action) {
        RoundedPanel card = rounded(CARD, BORDER, 12);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(14, 14, 12, 14));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel labels = vertical();
        labels.add(label(title, 13, Font.BOLD, TEXT));
        labels.add(Box.createVerticalStrut(4));
        labels.add(label(sub, 10, Font.PLAIN, MUTED));
        top.add(labels, BorderLayout.WEST);
        top.add(smallButton(buttonText, "arrow", action), BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);
        return card;
    }

    // ==================== CAC THANH PHAN DUNG CHUNG ====================

    private static JPanel vertical() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static RoundedPanel rounded(Color bg, Color border, int radius) {
        return new RoundedPanel(bg, border, radius);
    }

    private JButton smallButton(String title, String icon, Runnable action) {
        JButton button = new JButton(title, new UiIcon(icon, MUTED, 12));
        button.setBackground(new Color(26, 29, 34));
        button.setForeground(TEXT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 10));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(6, 8, 6, 8)));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton primaryButton(String title, String icon, Runnable action) {
        JButton button = new JButton(title, new UiIcon(icon, Color.WHITE, 13));
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 11, 9, 11));
        button.addActionListener(e -> action.run());
        return button;
    }

    private static String initials(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return "?";
        String[] parts = trimmed.split("\\s+");
        return parts[parts.length - 1].substring(0, 1).toUpperCase();
    }

    private JPanel placeholder(String name) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        JPanel inner = vertical();
        inner.add(label(name, 24, Font.BOLD, TEXT));
        inner.add(Box.createVerticalStrut(12));
        inner.add(label("Trang giao diện sẽ được code ở bước tiếp theo.", 13, Font.PLAIN, MUTED));
        panel.add(inner);
        return panel;
    }

    private void showPage(String name) {
        if (!menuButtons.containsKey(name)) {
            info("Trang " + name + " chưa có trong menu của vai trò này");
            return;
        }
        cardLayout.show(cardPanel, name);
        for (Component component : cardPanel.getComponents()) {
            if ("Đổi / trả".equals(name) && component instanceof DoiTraPanel panel) panel.lamMoi();
            if ("Thống kê".equals(name) && component instanceof ThongKePanel panel) panel.lamMoi();
            if ("Bán hàng".equals(name) && component instanceof BanHangChucNangPanel panel) panel.lamMoi();
        }
        lblPageTitle.setText(name);
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean selected = entry.getKey().equals(name);
            JButton button = entry.getValue();
            button.setBackground(selected ? new Color(36, 49, 71) : SIDE);
            button.setForeground(selected ? new Color(164, 195, 251) : MUTED);
            button.setFont(new Font("SansSerif", selected ? Font.BOLD : Font.PLAIN, 12));
            String icon = menuIcon(entry.getKey());
            button.setIcon(new UiIcon(icon, selected ? BLUE : MUTED, 17));
        }
    }

    private String menuIcon(String name) {
        switch (name) {
            case "Tổng quan": return "grid";
            case "Bán hàng": return "cart";
            case "Khách hàng": return "users";
            case "Sản phẩm": return "box";
            case "Khuyến mãi": return "tag";
            case "Đổi / trả": return "return";
            case "Lịch làm việc": return "calendar";
            case "Thống kê": return "chart";
            default: return "users";
        }
    }

    private void info(String feature) {
        JOptionPane.showMessageDialog(this,
                feature + " sẽ được triển khai trong giai đoạn tiếp theo.",
                "Green Home - Giao diện mẫu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có muốn đăng xuất không?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // ==================== VE THU CONG (KHONG THU VIEN NGOAI) ====================

    private static class RoundedPanel extends JPanel {
        private final Color background;
        private final Color outline;
        private final int radius;
        RoundedPanel(Color background, Color outline, int radius) {
            this.background = background;
            this.outline = outline;
            this.radius = radius;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(outline);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class Avatar extends JComponent {
        private final String value;
        private final Color fill;
        private final int size;
        private final int arc;
        Avatar(String value, Color fill, int size, int arc) {
            this.value = value;
            this.fill = fill;
            this.size = size;
            this.arc = arc;
            setPreferredSize(new Dimension(size, size));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, size, size, arc, arc);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(value, (size - fm.stringWidth(value)) / 2,
                    (size + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    private static class UiIcon implements Icon {
        private final String kind;
        private final Color color;
        private final int size;
        UiIcon(String kind, Color color, int size) {
            this.kind = kind;
            this.color = color;
            this.size = size;
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D p = (Graphics2D) g.create();
            p.translate(x, y);
            p.scale(size / 20.0, size / 20.0);
            p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            p.setStroke(new BasicStroke(1.65f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            p.setColor(color);
            switch (kind) {
                case "grid":
                    for (int a = 2; a <= 11; a += 9) for (int b = 2; b <= 11; b += 9)
                        p.drawRoundRect(a, b, 7, 7, 2, 2);
                    break;
                case "users":
                    p.drawOval(7, 2, 6, 6); p.drawArc(3, 10, 14, 10, 0, 180);
                    p.drawArc(0, 2, 5, 9, 90, 180); p.drawArc(15, 2, 5, 9, 270, 180);
                    break;
                case "cart":
                    p.drawLine(1, 2, 4, 2); p.drawLine(4, 2, 6, 13);
                    p.drawLine(6, 13, 17, 13); p.drawLine(5, 5, 19, 5);
                    p.drawLine(19, 5, 17, 11); p.drawOval(7, 16, 1, 1); p.drawOval(15, 16, 1, 1);
                    break;
                case "box":
                    p.draw(new Rectangle2D.Double(3, 6, 14, 11));
                    p.drawLine(3, 6, 10, 2); p.drawLine(10, 2, 17, 6);
                    p.drawLine(3, 6, 10, 10); p.drawLine(17, 6, 10, 10); p.drawLine(10, 10, 10, 17);
                    break;
                case "tag":
                    Path2D tag = new Path2D.Double(); tag.moveTo(3, 3); tag.lineTo(11, 3);
                    tag.lineTo(18, 10); tag.lineTo(10, 18); tag.lineTo(3, 11); tag.closePath();
                    p.draw(tag); p.drawOval(5, 5, 2, 2);
                    break;
                case "return":
                    p.drawArc(4, 3, 12, 11, 30, 240);
                    p.drawLine(2, 9, 4, 15); p.drawLine(4, 15, 9, 12);
                    break;
                case "calendar":
                    p.drawRoundRect(2, 4, 16, 14, 2, 2); p.drawLine(2, 9, 18, 9);
                    p.drawLine(6, 2, 6, 6); p.drawLine(14, 2, 14, 6); p.drawLine(6, 13, 9, 13);
                    break;
                case "chart":
                    p.drawLine(2, 18, 18, 18); p.drawLine(2, 18, 2, 3);
                    p.drawRect(5, 11, 2, 7); p.drawRect(10, 7, 2, 11); p.drawRect(15, 3, 2, 15);
                    break;
                case "wallet":
                    p.drawRoundRect(2, 5, 16, 12, 2, 2); p.drawLine(2, 8, 18, 8);
                    p.drawRoundRect(12, 10, 6, 4, 1, 1);
                    break;
                case "clipboard":
                    p.drawRoundRect(4, 4, 12, 14, 2, 2); p.drawRoundRect(7, 2, 6, 4, 1, 1);
                    p.drawLine(7, 10, 13, 10); p.drawLine(7, 14, 13, 14);
                    break;
                case "history":
                    p.drawArc(3, 3, 14, 14, 0, 285);
                    p.drawLine(3, 3, 3, 8); p.drawLine(3, 8, 8, 7);
                    p.drawLine(10, 6, 10, 10); p.drawLine(10, 10, 13, 12);
                    break;
                case "logout":
                    p.drawLine(11, 3, 3, 3); p.drawLine(3, 3, 3, 17);
                    p.drawLine(3, 17, 11, 17); p.drawLine(9, 10, 19, 10);
                    p.drawLine(15, 6, 19, 10); p.drawLine(19, 10, 15, 14);
                    break;
                case "help":
                    p.drawOval(2, 2, 16, 16); p.drawArc(7, 5, 6, 7, 0, 180);
                    p.drawLine(10, 11, 10, 13); p.fillOval(9, 15, 2, 2);
                    break;
                default:
                    p.drawLine(4, 10, 16, 10); p.drawLine(12, 6, 16, 10); p.drawLine(16, 10, 12, 14);
            }
            p.dispose();
        }
    }

    private static class SalesChart extends JPanel {
        SalesChart() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D p = (Graphics2D) g.create();
            p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 44, top = 16, right = getWidth() - 16, bottom = getHeight() - 32;
            if (right <= left || bottom <= top) { p.dispose(); return; }
            p.setFont(new Font("SansSerif", Font.PLAIN, 10));
            p.setColor(MUTED);
            p.drawString("Chưa có dữ liệu biểu đồ", left, top+25);
            p.dispose();
        }
    }

 // true: Quản lý | false: Nhân viên bán hàng
 //Giao dien nv:

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}