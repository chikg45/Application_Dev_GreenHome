package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Màn hình quản lý sản phẩm Green Home.
 * Hiện tại sử dụng dữ liệu mẫu, chưa kết nối cơ sở dữ liệu.
 */
public class SanPhamPanel extends JPanel {

    // ==================== MÀU SẮC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);

    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(60, 177, 124);

    // ==================== DỮ LIỆU ====================

    private final boolean laQuanLy;

    private final List<SanPhamMau> sanPhams = new ArrayList<>();
    private final List<SanPhamMau> dangHienThi = new ArrayList<>();

    private final JTextField txtTim = new JTextField();

    private final JComboBox<String> cboDanhMuc =
        new JComboBox<>(new String[] {
            "Tất cả danh mục",
            "Điện thoại",
            "Laptop",
            "Tivi",
            "Phụ kiện",
            "Tủ lạnh",
            "Máy giặt",
            "Máy lạnh"
        });

    private final JComboBox<String> cboTinhTrang =
        new JComboBox<>(new String[] {
            "Tất cả tình trạng",
            "Mới",
            "Trưng bày",
            "Đã qua sử dụng"
        });

    private final JComboBox<String> cboKinhDoanh =
        new JComboBox<>(new String[] {
            "Mọi trạng thái",
            "Đang kinh doanh",
            "Ngừng kinh doanh"
        });

    // Các số liệu thống kê
    private final JLabel lblTong =
        label("0", 22, BLUE, Font.BOLD);

    private final JLabel lblCoTheBan =
        label("0", 22, BLUE, Font.BOLD);

    private final JLabel lblNgungBan =
        label("0", 22, BLUE, Font.BOLD);

    private final JLabel lblSoDong =
        label("0 sản phẩm", 11, MUTED, Font.PLAIN);

    // ==================== TABLE ====================

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "Sản phẩm",
                "Danh mục / tình trạng",
                "Giá bán",
                "Có thể bán",
                "Kinh doanh",
                "Xem",
                "Sửa"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

    private final JTable table = new JTable(tableModel);

    private int maTiepTheo = 7;

    // ==================== CONSTRUCTOR ====================

    public SanPhamPanel(boolean laQuanLy) {

        this.laQuanLy = laQuanLy;

        setBackground(BG);
        setLayout(new BorderLayout());

        taoDuLieuMau();
        taoGiaoDien();
        taiDanhSach();
    }

    // ==================== DỮ LIỆU MẪU ====================

    private void taoDuLieuMau() {

        SanPhamMau iphone = new SanPhamMau(
            "SP0001",
            "iPhone 15",
            "Điện thoại",
            "Mới",
            "iPhone",
            "128GB · Xanh dương",
            "128GB · Xanh dương",
            18990000,
            "SP0001",
            "Cái",
            12,
            6,
            true,
            true
        );

        for (int i = 0; i < 6; i++) {
            iphone.serials.add("35278143010000" + i);
        }

        sanPhams.add(iphone);

        sanPhams.add(new SanPhamMau(
            "SP0002",
            "Dell Inspiron 15",
            "Laptop",
            "Mới",
            "Dell",
            "Core i5 · RAM 16GB · SSD 512GB",
            "Laptop văn phòng",
            22500000,
            "SP0002",
            "Cái",
            12,
            4,
            true,
            true
        ));

        sanPhams.add(new SanPhamMau(
            "SP0003",
            "Samsung Crystal UHD",
            "Tivi",
            "Mới",
            "Samsung",
            "55 inch · 4K",
            "Smart TV 55 inch",
            12900000,
            "SP0003",
            "Cái",
            24,
            4,
            true,
            true
        ));

        sanPhams.add(new SanPhamMau(
            "SP0004",
            "Tai nghe Bluetooth",
            "Phụ kiện",
            "Mới",
            "Khác",
            "Không dây",
            "Tai nghe không dây",
            1250000,
            "SP0004",
            "Cái",
            6,
            16,
            false,
            true
        ));

        sanPhams.add(new SanPhamMau(
            "SP0005",
            "Tủ lạnh Panasonic",
            "Tủ lạnh",
            "Mới",
            "Panasonic",
            "Inverter",
            "Tủ lạnh Inverter",
            9015500,
            "SP0005",
            "Cái",
            12,
            3,
            true,
            true
        ));

        sanPhams.add(new SanPhamMau(
            "SP0006",
            "Máy giặt LG Inverter",
            "Máy giặt",
            "Mới",
            "LG",
            "Inverter",
            "Máy giặt cửa trước",
            10570800,
            "SP0006",
            "Cái",
            24,
            2,
            true,
            true
        ));
    }

    // ==================== GIAO DIỆN CHÍNH ====================

    private void taoGiaoDien() {

        JPanel content = new JPanel(new BorderLayout(0, 13));

        content.setBackground(BG);
        content.setBorder(new EmptyBorder(22, 25, 20, 25));

        // -------- TIÊU ĐỀ --------

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel headingText = vertical();

        headingText.add(label(
            "Sản phẩm",
            21,
            TEXT,
            Font.BOLD
        ));

        headingText.add(Box.createVerticalStrut(6));

        headingText.add(label(
            "Thông tin hàng hóa, giá bán và số lượng phục vụ bán hàng.",
            11,
            MUTED,
            Font.PLAIN
        ));

        heading.add(headingText, BorderLayout.WEST);

        // Chỉ quản lý được thêm sản phẩm
        if (laQuanLy) {

            JButton btnThem = button("+  Thêm sản phẩm", true);

            btnThem.addActionListener(e -> moForm(null));

            heading.add(btnThem, BorderLayout.EAST);
        }

        // -------- THỐNG KÊ --------

        JPanel stats = new JPanel(
            new GridLayout(1, 3, 12, 0)
        );

        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 103));

        stats.add(statCard(
            "Mã / lô sản phẩm",
            lblTong,
            "Các sản phẩm đang quản lý",
            "▣"
        ));

        stats.add(statCard(
            "Sản phẩm có thể bán",
            lblCoTheBan,
            "Tổng số lượng đang bán",
            "▤"
        ));

        stats.add(statCard(
            "Ngừng kinh doanh",
            lblNgungBan,
            "Sản phẩm không còn bán",
            "↺"
        ));

        JPanel north = new JPanel(
            new BorderLayout(0, 15)
        );

        north.setOpaque(false);
        north.add(heading, BorderLayout.NORTH);
        north.add(stats, BorderLayout.CENTER);

        content.add(north, BorderLayout.NORTH);

        // -------- KHUNG DANH SÁCH --------

        JPanel listCard = new JPanel(new BorderLayout());

        listCard.setBackground(CARD);
        listCard.setBorder(new LineBorder(BORDER));

        // Hai tab theo mẫu thiết kế
        JPanel tabs = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 8)
        );

        tabs.setBackground(CARD);

        JButton tabSanPham = tabButton("Danh mục sản phẩm");
        JButton tabTra = tabButton("Sản phẩm trả");

        tabs.add(tabSanPham);
        tabs.add(tabTra);

        listCard.add(tabs, BorderLayout.NORTH);

        CardLayout tabLayout = new CardLayout();

        JPanel tabContent = new JPanel(tabLayout);
        tabContent.setBackground(CARD);

        JPanel danhSach = new JPanel(new BorderLayout());
        danhSach.setBackground(CARD);

        // -------- BỘ LỌC --------

        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 10)
        );

        filters.setBackground(CARD);

        txtTim.setPreferredSize(new Dimension(270, 33));

        txtTim.setToolTipText(
            "Tìm theo mã, tên hoặc model"
        );

        styleInput(txtTim);

        filters.add(txtTim);

        styleCombo(cboDanhMuc);
        styleCombo(cboTinhTrang);
        styleCombo(cboKinhDoanh);

        filters.add(cboDanhMuc);
        filters.add(cboTinhTrang);
        filters.add(cboKinhDoanh);

        danhSach.add(filters, BorderLayout.NORTH);

        // -------- CẤU HÌNH JTABLE --------

        table.setRowHeight(61);

        table.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(37, 52, 75)
        );

        table.setSelectionForeground(TEXT);

        table.setShowGrid(false);
        table.setFillsViewportHeight(true);

        table.setAutoCreateRowSorter(false);

        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 34)
        );

        // -------- TIÊU ĐỀ BẢNG --------

        DefaultTableCellRenderer headerRenderer =
            new DefaultTableCellRenderer();

        headerRenderer.setBackground(
            new Color(28, 30, 33)
        );

        headerRenderer.setForeground(MUTED);

        headerRenderer.setFont(
            new Font("SansSerif", Font.BOLD, 10)
        );

        headerRenderer.setBorder(
            new EmptyBorder(0, 10, 0, 4)
        );

        table.getTableHeader()
             .setDefaultRenderer(headerRenderer);

        // -------- NỘI DUNG BẢNG --------

        DefaultTableCellRenderer cell =
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t,
                        Object value,
                        boolean selected,
                        boolean focus,
                        int row,
                        int col) {

                    super.getTableCellRendererComponent(
                        t, value, selected, focus, row, col
                    );

                    setBorder(new EmptyBorder(0, 10, 0, 5));

                    if (col == 5 || col == 6) {
                        setHorizontalAlignment(
                            SwingConstants.CENTER
                        );
                    } else {
                        setHorizontalAlignment(
                            SwingConstants.LEFT
                        );
                    }

                    if (col == 5 || col == 6) {
                        setForeground(
                            new Color(160, 190, 248)
                        );
                    }

                    return this;
                }
            };

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(cell);
        }

        // Độ rộng các cột
        table.getColumnModel().getColumn(0)
             .setPreferredWidth(250);

        table.getColumnModel().getColumn(1)
             .setPreferredWidth(190);

        table.getColumnModel().getColumn(2)
             .setPreferredWidth(160);

        table.getColumnModel().getColumn(3)
             .setPreferredWidth(110);

        table.getColumnModel().getColumn(4)
             .setPreferredWidth(145);

        table.getColumnModel().getColumn(5)
             .setMaxWidth(65);

        table.getColumnModel().getColumn(6)
             .setMaxWidth(65);

        // -------- SỰ KIỆN XEM / SỬA --------

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0) {
                    return;
                }

                SanPhamMau p = dangHienThi.get(
                    table.convertRowIndexToModel(row)
                );

                if (col == 5 || e.getClickCount() == 2) {
                    moChiTiet(p);
                }

                if (col == 6 && laQuanLy) {
                    moForm(p);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar().setUnitIncrement(18);

        danhSach.add(scroll, BorderLayout.CENTER);

        // Số dòng
        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 14, 6)
        );

        bottom.setBackground(CARD);
        bottom.add(lblSoDong);

        danhSach.add(bottom, BorderLayout.SOUTH);

        // -------- TAB SẢN PHẨM TRẢ --------

        JPanel chuaLam = new JPanel(
            new GridBagLayout()
        );

        chuaLam.setBackground(CARD);

        chuaLam.add(label(
            "Danh sách sản phẩm trả sẽ liên kết với phiếu đổi/trả ở giai đoạn nghiệp vụ.",
            12,
            MUTED,
            Font.PLAIN
        ));

        tabContent.add(danhSach, "sanpham");
        tabContent.add(chuaLam, "tra");

        listCard.add(tabContent, BorderLayout.CENTER);

        tabSanPham.addActionListener(e ->
            tabLayout.show(tabContent, "sanpham")
        );

        tabTra.addActionListener(e ->
            tabLayout.show(tabContent, "tra")
        );

        content.add(listCard, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // -------- SỰ KIỆN TÌM KIẾM / LỌC --------

        txtTim.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    taiDanhSach();
                }
            }
        );

        cboDanhMuc.addActionListener(e -> taiDanhSach());
        cboTinhTrang.addActionListener(e -> taiDanhSach());
        cboKinhDoanh.addActionListener(e -> taiDanhSach());
    }

    // ==================== TẢI DANH SÁCH ====================

    private void taiDanhSach() {

        String query = txtTim.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String danhMuc =
            (String) cboDanhMuc.getSelectedItem();

        String tinhTrang =
            (String) cboTinhTrang.getSelectedItem();

        String kinhDoanh =
            (String) cboKinhDoanh.getSelectedItem();

        dangHienThi.clear();
        tableModel.setRowCount(0);

        for (SanPhamMau p : sanPhams) {

            boolean timDuoc = (
                p.ten + " " + p.ma + " " + p.model
            ).toLowerCase(Locale.ROOT).contains(query);

            boolean dungDanhMuc =
                danhMuc.startsWith("Tất cả")
                || p.danhMuc.equals(danhMuc);

            boolean dungTinhTrang =
                tinhTrang.startsWith("Tất cả")
                || p.tinhTrang.equals(tinhTrang);

            boolean dungKinhDoanh =
                kinhDoanh.startsWith("Mọi")
                || (kinhDoanh.equals("Đang kinh doanh")
                    == p.dangBan);

            if (!(timDuoc && dungDanhMuc
                    && dungTinhTrang && dungKinhDoanh)) {
                continue;
            }

            dangHienThi.add(p);

            tableModel.addRow(new Object[] {

                "<html><b>" + html(p.ten)
                    + "</b><br><font color='#8F9BAD'>"
                    + p.ma + " · " + html(p.model)
                    + "</font></html>",

                "<html>" + html(p.danhMuc)
                    + "<br><font color='#8F9BAD'>"
                    + html(p.tinhTrang)
                    + "</font></html>",

                tien(p.gia),

                p.ton,

                p.dangBan
                    ? "Đang kinh doanh"
                    : "Ngừng kinh doanh",

                "Xem",

                laQuanLy ? "Sửa" : ""
            });
        }

        // Cập nhật các thẻ thống kê
        int tongTon = 0;
        int ngung = 0;

        for (SanPhamMau p : sanPhams) {

            if (p.dangBan) {
                tongTon += p.ton;
            } else {
                ngung++;
            }
        }

        lblTong.setText(
            String.valueOf(sanPhams.size())
        );

        lblCoTheBan.setText(
            String.valueOf(tongTon)
        );

        lblNgungBan.setText(
            String.valueOf(ngung)
        );

        lblSoDong.setText(
            dangHienThi.size() + " sản phẩm"
        );
    }

    // ==================== THÊM / CẬP NHẬT ====================

    private void moForm(SanPhamMau p) {

        if (!laQuanLy) {
            return;
        }

        boolean sua = p != null;

        JDialog dialog = dialog(
            sua ? "Cập nhật sản phẩm" : "Thêm sản phẩm",
            750,
            760
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD);

        root.add(
            dialogHeader(
                sua ? "Cập nhật sản phẩm" : "Thêm sản phẩm",
                sua
                    ? "Mã sản phẩm: " + p.ma
                    : "Mã sản phẩm được tạo tự động.",
                dialog
            ),
            BorderLayout.NORTH
        );

        // -------- CÁC TRƯỜNG NHẬP --------

        JTextField ten = input(sua ? p.ten : "");

        JComboBox<String> danhMuc = combo(
            new String[] {
                "Điện thoại",
                "Laptop",
                "Tivi",
                "Tủ lạnh",
                "Máy giặt",
                "Máy lạnh",
                "Phụ kiện"
            }
        );

        JComboBox<String> tinhTrang = combo(
            new String[] {
                "Mới",
                "Trưng bày",
                "Đã qua sử dụng"
            }
        );

        JTextField thuongHieu = input(
            sua ? p.thuongHieu : ""
        );

        JTextField model = input(
            sua ? p.model : ""
        );

        JTextField moTa = input(
            sua ? p.moTa : ""
        );

        JTextField gia = input(
            sua ? String.valueOf(p.gia) : ""
        );

        JTextField maVach = input(
            sua ? p.maVach : ""
        );

        JTextField donVi = input(
            sua ? p.donVi : "Cái"
        );

        JTextField baoHanh = input(
            sua ? String.valueOf(p.baoHanh) : "12"
        );

        JCheckBox serial = check(
            "Quản lý từng thiết bị bằng Serial/IMEI",
            sua ? p.quanLySerial : false
        );

        JCheckBox dangBan = check(
            "Đang kinh doanh",
            !sua || p.dangBan
        );

        if (sua) {
            danhMuc.setSelectedItem(p.danhMuc);
            tinhTrang.setSelectedItem(p.tinhTrang);
        }

        // -------- BỐ CỤC FORM --------

        JPanel fields = vertical();

        fields.setBorder(
            new EmptyBorder(18, 24, 18, 24)
        );

        addFormRow(fields,
            field("Tên sản phẩm *", ten)
        );

        addFormRow(fields,
            pair(
                field("Danh mục *", danhMuc),
                field("Tình trạng chất lượng", tinhTrang)
            )
        );

        addFormRow(fields,
            pair(
                field("Thương hiệu *", thuongHieu),
                field("Model", model)
            )
        );

        addFormRow(fields,
            field("Thông số / mô tả ngắn", moTa)
        );

        addFormRow(fields,
            pair(
                field("Giá bán đã gồm VAT *", gia),
                field("Mã vạch", maVach)
            )
        );

        addFormRow(fields,
            pair(
                field("Đơn vị tính *", donVi),
                field("Thời hạn bảo hành (tháng)", baoHanh)
            )
        );

        JPanel checks = new JPanel(
            new GridLayout(1, 2, 12, 0)
        );

        checks.setOpaque(false);

        checks.add(serial);
        checks.add(dangBan);

        checks.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 43)
        );

        checks.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(checks);

        addFormRow(fields, notice(
            "Sản phẩm mới bắt đầu với số lượng 0. "
            + "Việc bổ sung số lượng sẽ xử lý riêng "
            + "ở nghiệp vụ sản phẩm."
        ));

        JScrollPane scroll = new JScrollPane(fields);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // -------- FOOTER --------

        JPanel footer = dialogFooter();

        JButton huy = button("Quay lại", false);
        JButton luu = button("Lưu thông tin", true);

        huy.addActionListener(e -> dialog.dispose());

        luu.addActionListener(e -> {

            String tenMoi = ten.getText().trim();
            String thuongHieuMoi =
                thuongHieu.getText().trim();

            String donViMoi = donVi.getText().trim();
            String maVachMoi = maVach.getText().trim();

            // Kiểm tra các trường bắt buộc
            if (tenMoi.isEmpty()
                    || thuongHieuMoi.isEmpty()
                    || donViMoi.isEmpty()) {

                thongBao(
                    dialog,
                    "Tên sản phẩm, thương hiệu và "
                    + "đơn vị tính không được trống."
                );

                return;
            }

            long giaMoi;
            int baoHanhMoi;

            try {

                giaMoi = Long.parseLong(
                    gia.getText().trim()
                );

                baoHanhMoi = Integer.parseInt(
                    baoHanh.getText().trim()
                );

                if (giaMoi <= 0 || baoHanhMoi < 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {

                thongBao(
                    dialog,
                    "Giá bán phải là số nguyên dương "
                    + "và thời hạn bảo hành phải "
                    + "từ 0 tháng trở lên."
                );

                return;
            }

            // Kiểm tra trùng mã vạch
            for (SanPhamMau item : sanPhams) {

                if (item != p
                        && !maVachMoi.isEmpty()
                        && maVachMoi.equalsIgnoreCase(
                            item.maVach
                        )) {

                    thongBao(
                        dialog,
                        "Mã vạch đã tồn tại ở sản phẩm khác."
                    );

                    return;
                }
            }

            SanPhamMau item = p;

            // Thêm sản phẩm mới
            if (!sua) {

                String maMoi = String.format(
                    "SP%04d", maTiepTheo++
                );

                item = new SanPhamMau(
                    maMoi, "", "", "", "",
                    "", "", 0, "", "",
                    0, 0, false, true
                );

                sanPhams.add(item);
            }

            // Cập nhật thông tin
            item.ten = tenMoi;
            item.danhMuc =
                (String) danhMuc.getSelectedItem();

            item.tinhTrang =
                (String) tinhTrang.getSelectedItem();

            item.thuongHieu = thuongHieuMoi;
            item.model = model.getText().trim();
            item.moTa = moTa.getText().trim();

            item.gia = giaMoi;
            item.maVach = maVachMoi;
            item.donVi = donViMoi;
            item.baoHanh = baoHanhMoi;

            item.quanLySerial = serial.isSelected();
            item.dangBan = dangBan.isSelected();

            taiDanhSach();
            dialog.dispose();
        });

        footer.add(huy);
        footer.add(luu);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== CHI TIẾT SẢN PHẨM ====================

    private void moChiTiet(SanPhamMau p) {

        JDialog dialog = dialog(
            p.ten,
            810,
            735
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD);

        root.add(
            dialogHeader(
                p.ten,
                p.ma + " · " + p.danhMuc,
                dialog
            ),
            BorderLayout.NORTH
        );

        JPanel details = vertical();

        details.setBorder(
            new EmptyBorder(24, 26, 24, 26)
        );

        // -------- HÌNH ẢNH VÀ THÔNG TIN CHÍNH --------

        JPanel hero = new JPanel(
            new BorderLayout(24, 0)
        );

        hero.setOpaque(false);

        hero.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 170)
        );

        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(
            new ProductArtwork(p.danhMuc),
            BorderLayout.WEST
        );

        JPanel heroInfo = vertical();

        heroInfo.add(label(
            p.model.isBlank() ? p.ten : p.model,
            14,
            MUTED,
            Font.BOLD
        ));

        heroInfo.add(Box.createVerticalStrut(14));

        heroInfo.add(label(
            tien(p.gia),
            28,
            new Color(153, 185, 252),
            Font.BOLD
        ));

        heroInfo.add(Box.createVerticalStrut(12));

        heroInfo.add(label(
            p.tinhTrang + "   ·   "
                + (p.dangBan
                    ? "Đang kinh doanh"
                    : "Ngừng kinh doanh"),
            12,
            p.dangBan ? GREEN : MUTED,
            Font.BOLD
        ));

        heroInfo.add(Box.createVerticalStrut(17));

        heroInfo.add(label(
            p.ton + " hàng ghi nhận · "
            + (p.dangBan ? p.ton : 0)
            + " có thể bán",
            12,
            MUTED,
            Font.PLAIN
        ));

        hero.add(heroInfo, BorderLayout.CENTER);

        details.add(hero);

        addFormRow(details, new JSeparator());

        // -------- THÔNG TIN CHI TIẾT --------

        JPanel info = new JPanel(
            new GridLayout(0, 2, 20, 16)
        );

        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(infoField(
            "Thương hiệu",
            p.thuongHieu
        ));

        info.add(infoField(
            "Model",
            p.model
        ));

        info.add(infoField(
            "Mã vạch",
            p.maVach.isBlank()
                ? "Chưa cung cấp"
                : p.maVach
        ));

        info.add(infoField(
            "Đơn vị tính",
            p.donVi
        ));

        info.add(infoField(
            "Thời hạn bảo hành",
            p.baoHanh + " tháng (thông tin tham khảo)"
        ));

        info.add(infoField(
            "Nguồn hàng",
            "Hàng phục vụ bán"
        ));

        info.add(infoField(
            "Thông số / mô tả",
            p.moTa
        ));

        info.add(infoField(
            "Tình trạng chất lượng",
            p.tinhTrang
        ));

        info.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 245)
        );

        details.add(info);

        // -------- SERIAL / IMEI --------

        addFormRow(details, label(
            "Serial / IMEI hiện có",
            14,
            new Color(160, 186, 229),
            Font.BOLD
        ));

        JPanel serials = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 7, 7)
        );

        serials.setOpaque(false);
        serials.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (p.serials.isEmpty()) {

            serials.add(label(
                p.quanLySerial
                    ? "Chưa có Serial/IMEI được ghi nhận"
                    : "Sản phẩm không theo dõi Serial/IMEI",
                12,
                MUTED,
                Font.PLAIN
            ));

        } else {

            for (String code : p.serials) {

                JLabel tag = label(
                    code,
                    11,
                    MUTED,
                    Font.PLAIN
                );

                tag.setOpaque(true);

                tag.setBackground(
                    new Color(37, 40, 45)
                );

                tag.setBorder(new CompoundBorder(
                    new LineBorder(BORDER),
                    new EmptyBorder(7, 9, 7, 9)
                ));

                serials.add(tag);
            }
        }

        serials.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 93)
        );

        details.add(serials);

        // -------- LỊCH SỬ BỔ SUNG --------

        addFormRow(details, label(
            "Lịch sử bổ sung số lượng",
            14,
            new Color(160, 186, 229),
            Font.BOLD
        ));

        addFormRow(details, label(
            "Chưa có dữ liệu bổ sung số lượng mới "
            + "trong bản UI mẫu.",
            12,
            MUTED,
            Font.PLAIN
        ));

        // -------- SCROLL --------

        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // -------- FOOTER --------

        JPanel footer = dialogFooter();

        JButton dong = button("Đóng", false);

        dong.addActionListener(e -> dialog.dispose());

        footer.add(dong);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== COMPONENT DÙNG CHUNG ====================

    private JPanel statCard(
            String title,
            JLabel value,
            String sub,
            String icon) {

        JPanel p = card(new BorderLayout());

        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(13, 14, 13, 14)
        ));

        JPanel text = vertical();

        text.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        text.add(Box.createVerticalStrut(7));
        text.add(value);
        text.add(Box.createVerticalStrut(6));

        text.add(label(
            sub, 10, MUTED, Font.PLAIN
        ));

        p.add(text, BorderLayout.CENTER);

        p.add(label(
            icon, 18, BLUE, Font.BOLD
        ), BorderLayout.EAST);

        return p;
    }

    private JDialog dialog(
            String title,
            int w,
            int h) {

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog d = new JDialog(
            owner,
            title,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        d.setSize(w, h);

        d.setMinimumSize(
            new Dimension(610, 520)
        );

        d.setLocationRelativeTo(owner);

        d.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        return d;
    }

    private JPanel dialogHeader(
            String title,
            String sub,
            JDialog dialog) {

        JPanel p = card(new BorderLayout());

        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(18, 25, 18, 25)
        ));

        JPanel titles = vertical();

        titles.add(label(
            title, 22, TEXT, Font.BOLD
        ));

        titles.add(Box.createVerticalStrut(7));

        titles.add(label(
            sub, 12, MUTED, Font.PLAIN
        ));

        p.add(titles, BorderLayout.WEST);

        JButton x = button("×", false);

        x.addActionListener(e -> dialog.dispose());

        p.add(x, BorderLayout.EAST);

        return p;
    }

    private JPanel dialogFooter() {

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 11, 12)
        );

        footer.setBackground(
            new Color(30, 31, 34)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        return footer;
    }

    private static JPanel field(
            String title,
            JComponent control) {

        JPanel p = new JPanel(
            new BorderLayout(0, 7)
        );

        p.setOpaque(false);

        p.add(
            label(title, 12, MUTED, Font.BOLD),
            BorderLayout.NORTH
        );

        p.add(control, BorderLayout.CENTER);

        return p;
    }

    private static JPanel pair(
            JPanel left,
            JPanel right) {

        JPanel p = new JPanel(
            new GridLayout(1, 2, 15, 0)
        );

        p.setOpaque(false);

        p.add(left);
        p.add(right);

        return p;
    }

    private static void addFormRow(
            JPanel parent,
            JComponent row) {

        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!(row instanceof JSeparator)) {

            row.setMaximumSize(
                new Dimension(
                    Integer.MAX_VALUE,
                    row.getPreferredSize().height + 6
                )
            );
        }

        parent.add(row);
        parent.add(Box.createVerticalStrut(13));
    }

    private static JPanel notice(String message) {

        JPanel p = card(new BorderLayout());

        p.setBackground(
            new Color(32, 37, 46)
        );

        p.setBorder(
            new EmptyBorder(13, 13, 13, 13)
        );

        p.add(label(
            "ⓘ  " + message,
            11,
            MUTED,
            Font.PLAIN
        ), BorderLayout.CENTER);

        return p;
    }

    private static JPanel infoField(
            String title,
            String value) {

        JPanel p = vertical();

        p.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        p.add(Box.createVerticalStrut(6));

        p.add(label(
            value.isBlank() ? "Chưa cung cấp" : value,
            12,
            new Color(158, 183, 220),
            Font.BOLD
        ));

        return p;
    }

    private static JPanel card(LayoutManager layout) {

        JPanel p = new JPanel(layout);

        p.setBackground(CARD);

        return p;
    }

    private static JPanel vertical() {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        return p;
    }

    private static JLabel label(
            String value,
            int size,
            Color color,
            int style) {

        JLabel l = new JLabel(value);

        l.setFont(
            new Font("SansSerif", style, size)
        );

        l.setForeground(color);

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private static JTextField input(String value) {

        JTextField f = new JTextField(value);

        styleInput(f);

        return f;
    }

    private static void styleInput(JTextField f) {

        f.setBackground(
            new Color(22, 24, 27)
        );

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 10, 8, 10)
        ));

        f.setPreferredSize(
            new Dimension(150, 36)
        );
    }

    private static JComboBox<String> combo(
            String[] choices) {

        JComboBox<String> c =
            new JComboBox<>(choices);

        styleCombo(c);

        return c;
    }

    private static void styleCombo(
            JComboBox<String> c) {

        c.setBackground(
            new Color(22, 24, 27)
        );

        c.setForeground(TEXT);

        c.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        c.setPreferredSize(
            new Dimension(145, 34)
        );
    }

    private static JCheckBox check(
            String title,
            boolean selected) {

        JCheckBox c = new JCheckBox(title, selected);

        c.setOpaque(false);
        c.setForeground(MUTED);

        c.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        return c;
    }

    private static JButton button(
            String title,
            boolean primary) {

        JButton b = new JButton(title);

        b.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        b.setForeground(Color.WHITE);

        b.setBackground(
            primary
                ? BLUE
                : new Color(31, 34, 40)
        );

        b.setFocusPainted(false);

        b.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        b.setBorder(new CompoundBorder(
            new LineBorder(
                primary ? BLUE : BORDER
            ),
            new EmptyBorder(9, 12, 9, 12)
        ));

        return b;
    }

    private static JButton tabButton(String title) {

        JButton b = button(title, false);

        b.setBorder(
            new EmptyBorder(8, 10, 8, 10)
        );

        return b;
    }

    private static String html(String value) {

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }

    private static String tien(long n) {

        return NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(n) + " đ";
    }

    private static void thongBao(
            Component parent,
            String message) {

        JOptionPane.showMessageDialog(
            parent,
            message,
            "Green Home",
            JOptionPane.WARNING_MESSAGE
        );
    }

    // ==================== MODEL TẠM ====================

    private static class SanPhamMau {

        final String ma;

        String ten;
        String danhMuc;
        String tinhTrang;
        String thuongHieu;
        String model;
        String moTa;
        String maVach;
        String donVi;

        long gia;

        int baoHanh;
        int ton;

        boolean quanLySerial;
        boolean dangBan;

        final List<String> serials = new ArrayList<>();

        SanPhamMau(
                String ma,
                String ten,
                String danhMuc,
                String tinhTrang,
                String thuongHieu,
                String model,
                String moTa,
                long gia,
                String maVach,
                String donVi,
                int baoHanh,
                int ton,
                boolean quanLySerial,
                boolean dangBan) {

            this.ma = ma;
            this.ten = ten;
            this.danhMuc = danhMuc;
            this.tinhTrang = tinhTrang;
            this.thuongHieu = thuongHieu;
            this.model = model;
            this.moTa = moTa;
            this.gia = gia;
            this.maVach = maVach;
            this.donVi = donVi;
            this.baoHanh = baoHanh;
            this.ton = ton;
            this.quanLySerial = quanLySerial;
            this.dangBan = dangBan;
        }
    }

    // ==================== HÌNH SẢN PHẨM ====================

    /**
     * Hình minh họa vẽ bằng Graphics2D.
     * Không sử dụng ảnh hoặc thư viện bên ngoài.
     */
    private static class ProductArtwork extends JPanel {

        private final String category;

        ProductArtwork(String category) {

            this.category = category;

            setPreferredSize(
                new Dimension(175, 155)
            );

            setBackground(
                new Color(34, 38, 45)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D p = (Graphics2D) g.create();

            p.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int x = getWidth() / 2;
            int y = getHeight() / 2;

            p.setColor(
                new Color(166, 198, 218)
            );

            if ("Điện thoại".equals(category)) {

                p.fillRoundRect(
                    x - 34,
                    y - 62,
                    68,
                    124,
                    13,
                    13
                );

                p.setColor(
                    new Color(67, 115, 146)
                );

                p.fillRoundRect(
                    x - 27,
                    y - 54,
                    54,
                    107,
                    8,
                    8
                );

                p.setColor(
                    new Color(210, 231, 244)
                );

                p.fillOval(
                    x - 3,
                    y - 48,
                    6,
                    6
                );

            } else if (
                "Laptop".equals(category)
                || "Tivi".equals(category)
            ) {

                p.fillRoundRect(
                    x - 69,
                    y - 50,
                    138,
                    86,
                    5,
                    5
                );

                p.setColor(
                    new Color(70, 121, 161)
                );

                p.fillRect(
                    x - 62,
                    y - 43,
                    124,
                    72
                );

                p.setColor(
                    new Color(180, 194, 208)
                );

                p.fillRoundRect(
                    x - 78,
                    y + 37,
                    156,
                    7,
                    3,
                    3
                );

            } else {

                p.fillRoundRect(
                    x - 45,
                    y - 59,
                    90,
                    118,
                    12,
                    12
                );

                p.setColor(
                    new Color(88, 137, 164)
                );

                p.fillOval(
                    x - 32,
                    y - 38,
                    64,
                    64
                );

                p.setColor(
                    new Color(184, 209, 225)
                );

                p.fillOval(
                    x - 23,
                    y - 29,
                    46,
                    46
                );
            }

            p.dispose();
        }
    }

    // ==================== CHẠY THỬ ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame(
                "Green Home - Sản phẩm (demo)"
            );

            f.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            f.setSize(1200, 760);
            f.setLocationRelativeTo(null);

            f.setContentPane(
                new SanPhamPanel(true)
            );

            f.setVisible(true);
        });
    }
}