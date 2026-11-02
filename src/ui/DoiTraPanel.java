package ui;

import java.awt.*;
import bus.DoiTraBUS;
import dao.DoiTraDAO;
import java.sql.SQLException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện đổi/trả dùng chung cho nhân viên và quản lý.
 * Dữ liệu đọc/ghi qua DoiTraBUS / DoiTraDAO / SQL Server.
 */
public class DoiTraPanel extends JPanel {

    // ==================== MÀU SẮC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(59, 173, 121);
    private static final Color GOLD = new Color(208, 160, 65);
    private static final Color RED = new Color(205, 99, 102);

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== DỮ LIỆU ====================

    private final List<HoaDonView> HOA_DON =
            new ArrayList<>();

    private final List<PhieuView> PHIEU =
            new ArrayList<>();

    private final String maNV;

    private final boolean laQuanLy;

    private final List<PhieuView> dangHienThi =
            new ArrayList<>();

    private final JTextField txtTim = new JTextField();

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[] {
                "Tất cả trạng thái",
                "Chờ duyệt",
                "Đã duyệt",
                "Từ chối"
            });

    private final JLabel[] lblDem = new JLabel[3];

    private final DefaultTableModel model =
            new DefaultTableModel(
                new String[] {
                    "Phiếu / hóa đơn gốc",
                    "Khách hàng",
                    "Hướng xử lý",
                    "Tiền hoàn",
                    "Trạng thái",
                    "Thao tác"
                }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    // ==================== CONSTRUCTOR ====================

    public DoiTraPanel(boolean laQuanLy) {

        this.laQuanLy = laQuanLy;
        this.maNV = util.PhienDangNhap.hienTai().getMaNV();

        setLayout(new BorderLayout());
        setBackground(BG);

        taoGiaoDien();
        lamMoiTuCSDL();
    }

    /** Refresh cả hóa đơn và phiếu; dữ liệu không còn nằm trong danh sách mẫu. */
    public void lamMoiTuCSDL() {
        try {
            HOA_DON.clear();PHIEU.clear();
            Map<String,HoaDonView> hdMap=new LinkedHashMap<>();
            for(DoiTraDAO.HoaDonInfo hd:DoiTraBUS.danhSachHoaDon()) {
                HoaDonView view=new HoaDonView(hd.ma(),hd.khach(),hd.sdt(),hd.ngay());
                for(DoiTraDAO.DongHoaDonInfo d:hd.dongs()) {
                    view.dongs.add(new DongHoaDonView(String.valueOf(d.id()),d.tenSP(),
                        d.soLuong(),d.giaThucTra().setScale(0,java.math.RoundingMode.DOWN).longValue()));
                }
                HOA_DON.add(view);hdMap.put(view.ma,view);
            }
            for(DoiTraDAO.PhieuInfo ph:DoiTraBUS.danhSachPhieu()) {
                HoaDonView hd=hdMap.get(ph.maHD());
                if(hd==null) continue;
                PhieuView p=new PhieuView(ph.ma(),hd);
                p.maNV=ph.maNV();p.nguoiLap=ph.tenNguoiLap();
                p.nguoiDuyet=ph.tenNguoiDuyet();
                p.nguoiLienHe=ph.tenLienHe();p.sdt=ph.sdtLienHe();
                p.lyDo=ph.lyDo();p.hienTrang=ph.hienTrang();p.phuKien=ph.phuKien();
                p.huongXuLy=switch(ph.huongXuLy()) {
                    case "HOAN_TIEN" -> "Trả hàng & hoàn tiền";
                    case "DOI_SAN_PHAM" -> "Đổi sản phẩm tương đương";
                    default -> "Bảo hành (dữ liệu cũ - không triển khai)";
                };
                p.tinhTrangSP=ph.tinhTrangSP();
                p.trangThai=switch(ph.trangThai()) {
                    case "DA_DUYET" -> "Đã duyệt";
                    case "TU_CHOI" -> "Từ chối";
                    default -> "Chờ duyệt";
                };
                for(DoiTraDAO.DongTraInfo dong:ph.chiTiet()) {
                    for(DongHoaDonView origin:hd.dongs) {
                        if(origin.ma.equals(String.valueOf(dong.idChiTietHD()))) {
                            p.chiTiet.add(new DongTraView(origin,dong.soLuong()));break;
                        }
                    }
                }
                PHIEU.add(p);
            }
            taiDanhSach();
        } catch(SQLException ex) {
            throw new IllegalStateException("Không tải được hóa đơn/phiếu đổi trả từ SQL Server: "
                + ex.getMessage() + ". Nếu thiếu cột, hãy chạy database/04_BoSungThongTinDoiTra.sql.",ex);
        }
    }

    // ==================== DANH SÁCH PHIẾU ====================

    private void taoGiaoDien() {

        JPanel page = new JPanel(new BorderLayout(0, 13));

        page.setBackground(BG);
        page.setBorder(new EmptyBorder(22, 25, 20, 25));

        JPanel top = vertical();

        // Tiêu đề
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel title = vertical();

        title.add(label(
            "Xử lý đổi / trả hàng",
            21,
            TEXT,
            Font.BOLD
        ));

        title.add(Box.createVerticalStrut(6));

        title.add(label(
            laQuanLy
                ? "Kiểm tra và phê duyệt phiếu trước khi nhân viên thực hiện."
                : "Tiếp nhận yêu cầu đổi/trả; mọi phiếu đều cần quản lý duyệt.",
            11,
            MUTED,
            Font.PLAIN
        ));

        heading.add(title, BorderLayout.WEST);

        if (!laQuanLy) {

            JButton btn = button(
                "+  Tiếp nhận đổi / trả", true
            );

            btn.addActionListener(e -> moTraCuu());

            heading.add(btn, BorderLayout.EAST);
        }

        addRow(top, heading, 52);

        // Thanh quy trình
        JPanel progress = card(
            new GridLayout(1, 4, 5, 0)
        );

        String[] steps = {
            "① Nhân viên tiếp nhận",
            "② Quản lý phê duyệt",
            "③ Đổi hàng / hoàn tiền",
            "④ Đánh giá hàng nhận lại"
        };

        for (String step : steps) {

            JLabel l = label(
                step, 11, MUTED, Font.PLAIN
            );

            l.setHorizontalAlignment(
                SwingConstants.CENTER
            );

            progress.add(l);
        }

        progress.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 7, 12, 7)
        ));

        addRow(top, progress, 48);

        // Bốn thẻ thống kê
        JPanel stats = new JPanel(
            new GridLayout(1, 3, 12, 0)
        );

        stats.setOpaque(false);

        String[] captions = {
            "Chờ duyệt",
            "Đã duyệt",
            "Từ chối"
        };

        Color[] colors = {
            GOLD, BLUE, RED, GREEN
        };

        for (int i = 0; i < 3; i++) {

            lblDem[i] = label(
                "0",
                23,
                new Color(164, 193, 247),
                Font.BOLD
            );

            stats.add(stat(
                captions[i],
                lblDem[i],
                colors[i]
            ));
        }

        addRow(top, stats, 105);

        page.add(top, BorderLayout.NORTH);

        // Khung danh sách
        JPanel list = card(new BorderLayout());

        list.setBorder(new LineBorder(BORDER));

        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 12)
        );

        filters.setOpaque(false);

        txtTim.setPreferredSize(new Dimension(300, 35));
        inputStyle(txtTim);

        txtTim.setToolTipText(
            "Tìm mã phiếu, hóa đơn, tên hoặc SĐT khách hàng"
        );

        cboTrangThai.setBackground(CARD);
        cboTrangThai.setForeground(TEXT);

        cboTrangThai.setPreferredSize(
            new Dimension(165, 35)
        );

        filters.add(txtTim);
        filters.add(cboTrangThai);

        list.add(filters, BorderLayout.NORTH);

        // JTable
        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(38, 51, 72)
        );

        table.setSelectionForeground(TEXT);
        table.setRowHeight(53);

        table.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        table.setShowGrid(false);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 34)
        );

        // Renderer tiêu đề
        table.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v,
                        boolean selected, boolean focused,
                        int row, int col) {

                    super.getTableCellRendererComponent(
                        t, v, selected, focused, row, col
                    );

                    setBackground(new Color(28, 30, 33));
                    setForeground(MUTED);

                    setFont(new Font(
                        "SansSerif", Font.BOLD, 10
                    ));

                    setBorder(
                        new EmptyBorder(0, 11, 0, 5)
                    );

                    return this;
                }
            }
        );

        // Renderer nội dung
        DefaultTableCellRenderer cell =
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v,
                        boolean selected, boolean focused,
                        int row, int col) {

                    super.getTableCellRendererComponent(
                        t, v, selected, focused, row, col
                    );

                    setBorder(
                        new EmptyBorder(0, 11, 0, 5)
                    );

                    if (col == 4) {

                        setForeground(
                            mauTrangThai(String.valueOf(v))
                        );

                    } else if (col == 5) {

                        setForeground(
                            new Color(160, 192, 250)
                        );
                    }

                    setHorizontalAlignment(
                        col == 5
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT
                    );

                    return this;
                }
            };

        for (int i = 0; i < 6; i++) {

            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(cell);
        }

        // Độ rộng cột
        table.getColumnModel().getColumn(0)
             .setPreferredWidth(175);

        table.getColumnModel().getColumn(1)
             .setPreferredWidth(150);

        table.getColumnModel().getColumn(2)
             .setPreferredWidth(170);

        table.getColumnModel().getColumn(3)
             .setPreferredWidth(130);

        table.getColumnModel().getColumn(4)
             .setPreferredWidth(90);

        table.getColumnModel().getColumn(5)
             .setMaxWidth(135);

        // Click vào cột Thao tác hoặc double-click dòng
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0
                        && (col == 5 || e.getClickCount() == 2)) {

                    moChiTiet(
                        dangHienThi.get(
                            table.convertRowIndexToModel(row)
                        )
                    );
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        list.add(scroll, BorderLayout.CENTER);

        page.add(list, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);

        // Sự kiện tìm kiếm
        txtTim.getDocument().addDocumentListener(
            new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                public void removeUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                public void changedUpdate(DocumentEvent e) {
                    taiDanhSach();
                }
            }
        );

        cboTrangThai.addActionListener(e -> taiDanhSach());
    }

    // ==================== TẢI DANH SÁCH ====================

    private void taiDanhSach() {

        String q = txtTim.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String state = String.valueOf(
            cboTrangThai.getSelectedItem()
        );

        model.setRowCount(0);
        dangHienThi.clear();

        int[] dem = new int[4];

        for (PhieuView p : PHIEU) {

            // Nhân viên chỉ xem phiếu do chính tài khoản mình lập.
            if (!laQuanLy && !maNV.equals(p.maNV)) {
                continue;
            }

            int idx = Arrays.asList(
                "Chờ duyệt",
                "Đã duyệt",
                "Từ chối"
            ).indexOf(p.trangThai);

            if (idx >= 0) {
                dem[idx]++;
            }

            String search = (
                p.ma + " "
                + p.hoaDon.ma + " "
                + p.nguoiLienHe + " "
                + p.sdt
            ).toLowerCase(Locale.ROOT);

            if (!search.contains(q)
                    || (!state.startsWith("Tất cả")
                        && !state.equals(p.trangThai))) {

                continue;
            }

            dangHienThi.add(p);

            model.addRow(new Object[] {

                "<html><b>" + p.ma
                    + "</b><br><font color='#8F9BAD'>"
                    + p.hoaDon.ma
                    + "</font></html>",

                p.nguoiLienHe + " · " + p.sdt,

                p.huongXuLy + " · "
                    + p.soLuong() + " sản phẩm",

                tien(p.tienHoan()),

                p.trangThai,

                laQuanLy ? "Xem & duyệt" : "Chi tiết"
            });
        }

        for (int i = 0; i < 3; i++) {
            lblDem[i].setText(String.valueOf(dem[i]));
        }
    }

    // ==================== TRA CỨU HÓA ĐƠN ====================

    private void moTraCuu() {

        JDialog dialog = taoDialog(
            "Tra cứu giao dịch mua hàng", 850, 615
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            "Tra cứu giao dịch mua hàng",
            "Chọn hóa đơn gốc còn trong thời hạn đổi/trả 7 ngày.",
            dialog
        ), BorderLayout.NORTH);

        JPanel center = card(new BorderLayout(0, 13));

        center.setBorder(
            new EmptyBorder(20, 22, 15, 22)
        );

        JTextField tim = new JTextField();

        inputStyle(tim);

        tim.setPreferredSize(new Dimension(0, 40));

        tim.setToolTipText(
            "Mã hóa đơn, tên hoặc SĐT khách hàng"
        );

        center.add(tim, BorderLayout.NORTH);

        JPanel lines = vertical();

        JScrollPane scroll = new JScrollPane(lines);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(18);

        center.add(scroll, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        // Footer
        JButton close = button("Đóng", false);

        close.addActionListener(e -> dialog.dispose());

        JPanel footer = dialogFooter();

        footer.add(close);

        root.add(footer, BorderLayout.SOUTH);

        // Hiển thị kết quả tra cứu
        Runnable render = () -> {

            lines.removeAll();

            String q = tim.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            for (HoaDonView hd : HOA_DON) {

                String hay = (
                    hd.ma + " "
                    + hd.khach + " "
                    + hd.sdt
                ).toLowerCase(Locale.ROOT);

                if (!hay.contains(q)) {
                    continue;
                }

                boolean hopLe = trong7Ngay(hd.ngay);

                JPanel item = card(
                    new BorderLayout(10, 0)
                );

                item.setBorder(new CompoundBorder(
                    new LineBorder(BORDER),
                    new EmptyBorder(14, 14, 14, 14)
                ));

                JPanel text = vertical();

                text.add(label(
                    hd.ma + (hopLe
                        ? " · Trong 7 ngày"
                        : " · Quá 7 ngày"),
                    13,
                    hopLe ? GREEN : MUTED,
                    Font.BOLD
                ));

                text.add(Box.createVerticalStrut(7));

                text.add(label(
                    hd.khach + " · "
                        + (hd.sdt.isBlank()
                            ? "Không có SĐT"
                            : hd.sdt),
                    11,
                    TEXT,
                    Font.PLAIN
                ));

                text.add(Box.createVerticalStrut(5));

                text.add(label(
                    NGAY.format(hd.ngay)
                        + " · " + tien(hd.tongTien()),
                    11,
                    MUTED,
                    Font.PLAIN
                ));

                item.add(text, BorderLayout.CENTER);

                JButton choose = button(
                    hopLe ? "→ Chọn" : "Hết hạn",
                    false
                );

                choose.setEnabled(hopLe);

                choose.addActionListener(e -> {
                    dialog.dispose();
                    moTiepNhan(hd, null);
                });

                item.add(choose, BorderLayout.EAST);

                addRow(lines, item, 102);
            }

            if (lines.getComponentCount() == 0) {

                addRow(lines, label(
                    "Không tìm thấy hóa đơn.",
                    12,
                    MUTED,
                    Font.PLAIN
                ), 40);
            }

            lines.revalidate();
            lines.repaint();
        };

        tim.getDocument().addDocumentListener(
            new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    render.run();
                }

                public void removeUpdate(DocumentEvent e) {
                    render.run();
                }

                public void changedUpdate(DocumentEvent e) {
                    render.run();
                }
            }
        );

        render.run();

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== TIẾP NHẬN / SỬA ====================

    private void moTiepNhan(
            HoaDonView hd,
            PhieuView phieuCu) {

        if (laQuanLy) {
            return;
        }

        // Chỉ kiểm tra hạn khi tạo yêu cầu mới
        if (phieuCu == null && !trong7Ngay(hd.ngay)) {

            thongBao(
                "Hóa đơn đã quá hạn 7 ngày, "
                + "không thể lập phiếu mới."
            );

            return;
        }

        JDialog dialog = taoDialog(
            phieuCu == null
                ? "Tiếp nhận đổi / trả"
                : "Sửa phiếu đổi / trả",
            830,
            770
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            phieuCu == null
                ? "Tiếp nhận đổi / trả"
                : "Sửa & gửi lại",
            hd.ma + " · Đã thanh toán "
                + NGAY.format(hd.ngay),
            dialog
        ), BorderLayout.NORTH);

        JPanel fields = vertical();

        fields.setBorder(
            new EmptyBorder(18, 23, 18, 23)
        );

        addRow(fields, notice(
            "Hóa đơn gốc được giữ nguyên. "
            + "Chọn đơn vị hàng cần đổi/trả."
        ), 53);

        // Danh sách sản phẩm có thể đổi/trả
        List<JCheckBox> checks = new ArrayList<>();
        List<JSpinner> spinners = new ArrayList<>();
        List<DongHoaDonView> options = new ArrayList<>();

        for (DongHoaDonView d : hd.dongs) {

            int available =
                d.soLuong - daYeuCau(d, phieuCu);

            if (available <= 0) {
                continue;
            }

            int oldQty = phieuCu == null
                ? 0
                : phieuCu.soLuongCua(d);

            JPanel line = card(
                new BorderLayout(10, 0)
            );

            line.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(11, 12, 11, 12)
            ));

            JCheckBox cb = new JCheckBox(
                d.ten + " · " + tien(d.giaThucTra)
            );

            cb.setOpaque(false);
            cb.setForeground(TEXT);
            cb.setSelected(oldQty > 0);

            line.add(cb, BorderLayout.CENTER);

            JSpinner qty = new JSpinner(
                new SpinnerNumberModel(
                    oldQty > 0 ? oldQty : 1,
                    1,
                    available,
                    1
                )
            );

            qty.setPreferredSize(
                new Dimension(63, 28)
            );

            qty.setEnabled(cb.isSelected());

            cb.addActionListener(e ->
                qty.setEnabled(cb.isSelected())
            );

            JPanel side = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 7, 0)
            );

            side.setOpaque(false);

            side.add(label(
                "Số lượng",
                11,
                MUTED,
                Font.PLAIN
            ));

            side.add(qty);

            line.add(side, BorderLayout.EAST);

            addRow(fields, line, 65);

            checks.add(cb);
            spinners.add(qty);
            options.add(d);
        }

        if (options.isEmpty()) {

            addRow(fields, label(
                "Không còn số lượng được phép tiếp nhận.",
                12,
                RED,
                Font.PLAIN
            ), 40);
        }

        // Hướng xử lý
        JComboBox<String> huong =
            new JComboBox<>(new String[] {
                "Trả hàng & hoàn tiền",
                "Đổi sản phẩm tương đương"
            });

        styleCombo(huong);

        if (phieuCu != null) {
            huong.setSelectedItem(phieuCu.huongXuLy);
        }

        addRow(fields, field(
            "Hướng xử lý *", huong
        ), 66);

        JLabel tienHoan = label(
            "Tiền hoàn dự kiến: 0 đ",
            16,
            new Color(154, 187, 249),
            Font.BOLD
        );

        addRow(fields, notice(
            "Tiền hoàn lấy theo đơn giá thực trả "
            + "của từng dòng hóa đơn "
            + "(đã gồm VAT và giảm giá)."
        ), 57);

        addRow(fields, tienHoan, 36);

        // Thông tin liên hệ
        JTextField lienHe = input(
            phieuCu == null
                ? hd.khach
                : phieuCu.nguoiLienHe
        );

        JTextField sdt = input(
            phieuCu == null
                ? hd.sdt
                : phieuCu.sdt
        );

        addRow(fields, pair(
            field("Người liên hệ *", lienHe),
            field("Số điện thoại *", sdt)
        ), 70);

        // Lý do và hiện trạng
        JTextArea lyDo = area(
            phieuCu == null ? "" : phieuCu.lyDo
        );

        JTextArea hienTrang = area(
            phieuCu == null ? "" : phieuCu.hienTrang
        );

        JTextField phuKien = input(
            phieuCu == null ? "" : phieuCu.phuKien
        );

        addRow(fields, field(
            "Lỗi / lý do yêu cầu *",
            new JScrollPane(lyDo)
        ), 102);

        addRow(fields, field(
            "Hiện trạng sản phẩm *",
            new JScrollPane(hienTrang)
        ), 102);

        addRow(fields, field(
            "Phụ kiện đi kèm * (ghi 'Không có' nếu không có)",
            phuKien
        ), 69);

        JComboBox<String> tinhTrangSP = new JComboBox<>(new String[]{
            "Còn nguyên vẹn", "Lỗi kỹ thuật", "Hư hỏng"});
        styleCombo(tinhTrangSP);
        if(phieuCu!=null) tinhTrangSP.setSelectedIndex(switch(phieuCu.tinhTrangSP) {
            case "LOI_KY_THUAT" -> 1;
            case "HU_HONG" -> 2;
            default -> 0;
        });
        addRow(fields,field("Phân loại tình trạng *",tinhTrangSP),65);


        addRow(fields, notice(
            "Phiếu gửi lên sẽ ở trạng thái Chờ duyệt. "
            + "Chưa hoàn tiền, cập nhật hàng hay điểm."
        ), 57);

        // Cập nhật tiền hoàn khi thay đổi sản phẩm
        Runnable capNhatTien = () -> {

            long value = 0;

            if (huong.getSelectedIndex() == 0) {

                for (int i = 0; i < options.size(); i++) {

                    if (checks.get(i).isSelected()) {

                        value += options.get(i).giaThucTra
                            * (Integer) spinners.get(i).getValue();
                    }
                }
            }

            tienHoan.setText(
                "Tiền hoàn dự kiến: " + tien(value)
            );
        };

        huong.addActionListener(e -> capNhatTien.run());

        for (JCheckBox c : checks) {

            c.addActionListener(e -> capNhatTien.run());
        }

        for (JSpinner s : spinners) {

            s.addChangeListener(e -> capNhatTien.run());
        }

        capNhatTien.run();

        // Vùng cuộn
        JScrollPane content = new JScrollPane(fields);

        content.setBorder(null);
        content.getViewport().setBackground(CARD);

        content.getVerticalScrollBar()
               .setUnitIncrement(18);

        root.add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = dialogFooter();

        JButton cancel = button("Quay lại", false);
        JButton send = button("Gửi quản lý duyệt", true);

        cancel.addActionListener(e -> dialog.dispose());

        // ==================== LƯU PHIẾU VÀO SQL SERVER ====================
        send.addActionListener(e -> {
            List<DoiTraDAO.DongTraInfo> dongs=new ArrayList<>();
            for(int i=0;i<options.size();i++) {
                if(checks.get(i).isSelected())
                    dongs.add(new DoiTraDAO.DongTraInfo(
                        Integer.parseInt(options.get(i).ma),(Integer)spinners.get(i).getValue()));
            }
            String huongXuLy=huong.getSelectedIndex()==0?"HOAN_TIEN":"DOI_SAN_PHAM";
            String tinhTrang=switch(tinhTrangSP.getSelectedIndex()) {
                case 1 -> "LOI_KY_THUAT";
                case 2 -> "HU_HONG";
                default -> "CON_NGUYEN_VEN";
            };
            try {
                String ma=DoiTraBUS.guiPhieu(phieuCu==null?null:phieuCu.ma,
                    hd.ma,maNV,lienHe.getText(),sdt.getText(),lyDo.getText(),
                    hienTrang.getText(),phuKien.getText(),huongXuLy,tinhTrang,dongs);
                lamMoiTuCSDL();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "Đã lưu phiếu "+ma+" vào SQL Server ở trạng thái CHỜ DUYỆT.\n"+
                    "Chưa hoàn tiền, trừ điểm hoặc cập nhật tồn kho.",
                    "Gửi phiếu thành công",JOptionPane.INFORMATION_MESSAGE);
            } catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,ex.getMessage(),
                    "Thông tin đổi/trả chưa hợp lệ",JOptionPane.WARNING_MESSAGE);
            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(dialog,loiSQL(ex),
                    "Không thể lưu phiếu đổi/trả",JOptionPane.ERROR_MESSAGE);
            } catch(IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog,ex.getMessage(),
                    "Lỗi khi tải phiếu",JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancel);
        footer.add(send);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== CHI TIẾT PHIẾU ====================

    private void moChiTiet(PhieuView p) {

        JDialog dialog = taoDialog(
            "Phiếu " + p.ma,
            860,
            750
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            "Phiếu " + p.ma,
            p.trangThai + " · " + p.huongXuLy,
            dialog
        ), BorderLayout.NORTH);

        JPanel details = vertical();

        details.setBorder(
            new EmptyBorder(21, 24, 25, 24)
        );

        // Logo phiếu
        JPanel brandArea = card(
            new GridLayout(2, 1)
        );

        brandArea.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        JLabel logo = label(
            "GREEN HOME",
            18,
            TEXT,
            Font.BOLD
        );

        logo.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        JLabel sub = label(
            "PHIẾU ĐỔI / TRẢ · "
                + p.ma + " · " + p.trangThai,
            11,
            MUTED,
            Font.PLAIN
        );

        sub.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        brandArea.add(logo);
        brandArea.add(sub);

        addRow(details, brandArea, 65);

        // Trạng thái quy trình
        JPanel stages = card(
            new GridLayout(1, 3)
        );

        stages.add(label(
            "① Tiếp nhận",
            12,
            GREEN,
            Font.PLAIN
        ));

        stages.add(label(
            "② " + (
                p.trangThai.equals("Chờ duyệt")
                    ? "Chờ duyệt"
                    : "Đã xét duyệt"
            ),
            12,
            MUTED,
            Font.PLAIN
        ));

        stages.add(label(
            "③ Hoàn tất",
            12,
            MUTED,
            Font.PLAIN
        ));

        addRow(details, stages, 36);

        addDivider(details);

        // Thông tin phiếu
        addRow(details, pair(
            info("Hóa đơn gốc", p.hoaDon.ma),
            info(
                "Người liên hệ",
                p.nguoiLienHe + " · " + p.sdt
            )
        ), 65);

        addRow(details, pair(
            info("Người lập / tiếp nhận", p.nguoiLap),
            info(
                "Người duyệt",
                p.nguoiDuyet.isBlank()
                    ? "Chưa được duyệt"
                    : p.nguoiDuyet
            )
        ), 65);

        addDivider(details);

        // Sản phẩm đổi/trả
        for (DongTraView ct : p.chiTiet) {

            addRow(details, info(
                ct.dong.ten,
                "Số lượng " + ct.soLuong
                    + " · Đơn giá thực trả "
                    + tien(ct.dong.giaThucTra)
            ), 63);
        }

        addDivider(details);

        // Lý do
        addRow(details, info(
            "Lỗi / lý do",
            p.lyDo
        ), 58);

        addRow(details, pair(
            info("Hiện trạng", p.hienTrang),
            info("Phụ kiện", p.phuKien)
        ), 64);

        addDivider(details);

        // Tiền hoàn
        addRow(details, info(
            "Tiền hoàn dự kiến",
            tien(p.tienHoan())
        ), 63);

        String diemDuKien = "Chưa có dữ liệu phân bổ điểm ở CSDL";

        addRow(details, info(
            "Điểm điều chỉnh (chưa triển khai)",
            diemDuKien
        ), 63);

        addRow(details, notice(
            "Phiếu và quyết định duyệt đã được lưu SQL. "
            + "Chưa có chức năng thực chi tiền, nhập hàng hay thu hồi điểm."
        ), 57);

        // Scroll
        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);

        // ==================== FOOTER THEO VAI TRÒ ====================

        JPanel footer = dialogFooter();

        JButton close = button("Đóng", false);

        close.addActionListener(e -> dialog.dispose());

        footer.add(close);

        // Quản lý xét duyệt
        if (laQuanLy
                && p.trangThai.equals("Chờ duyệt")) {

            JButton reject = button("Từ chối", false);
            JButton approve = button("Phê duyệt", true);

            reject.addActionListener(e ->
                thayDoiTrangThai(dialog, p, "Từ chối")
            );

            approve.addActionListener(e ->
                thayDoiTrangThai(dialog, p, "Đã duyệt")
            );

            footer.add(reject);
            footer.add(approve);

        } else if (!laQuanLy
                && (p.trangThai.equals("Chờ duyệt")
                    || p.trangThai.equals("Từ chối"))
                && !p.huongXuLy.startsWith("Bảo hành")) {

            // Nhân viên sửa và gửi lại
            JButton edit = button(
                "Sửa & gửi lại",
                true
            );

            edit.addActionListener(e -> {
                dialog.dispose();
                moTiepNhan(p.hoaDon, p);
            });

            footer.add(edit);

        } else if (!laQuanLy && p.trangThai.equals("Đã duyệt")) {
            JButton status = button("Đã duyệt · Chưa có chức năng hoàn tiền",false);
            status.setEnabled(false);
            footer.add(status);
        }

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== PHÊ DUYỆT / TỪ CHỐI ====================

    private void thayDoiTrangThai(JDialog dialog,PhieuView p,String status) {
        if(JOptionPane.showConfirmDialog(dialog,
            "Xác nhận chuyển phiếu "+p.ma+" sang '"+status+"'?\n"+
            "Chỉ ghi trạng thái duyệt; KHÔNG tự hoàn tiền hay thay đổi hàng tồn.",
            "Xét duyệt phiếu",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            DoiTraBUS.xetDuyet(p.ma,maNV,"Đã duyệt".equals(status));
            lamMoiTuCSDL();dialog.dispose();
            JOptionPane.showMessageDialog(this,"Đã cập nhật "+p.ma+" sang "+status+
                " trong SQL Server. Chưa thực hiện hoàn tiền/đổi hàng.",
                "Đã cập nhật",JOptionPane.INFORMATION_MESSAGE);
        } catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialog,ex.getMessage(),
                "Không thể duyệt phiếu",JOptionPane.WARNING_MESSAGE);
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(dialog,loiSQL(ex),
                "Lỗi SQL Server",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== KIỂM TRA SỐ LƯỢNG ====================

    private int daYeuCau(
            DongHoaDonView line,
            PhieuView dangSua) {

        int reserved = 0;

        for (PhieuView p : PHIEU) {

            if (p == dangSua
                    || p.trangThai.equals("Từ chối")) {

                continue;
            }

            reserved += p.soLuongCua(line);
        }

        return reserved;
    }

    // ==================== KIỂM TRA 7 NGÀY ====================

    private static boolean trong7Ngay(LocalDate date) {

        long soNgay = ChronoUnit.DAYS.between(
            date,
            LocalDate.now()
        );

        return soNgay >= 0 && soNgay <= 7;
    }

    // ==================== THẺ THỐNG KÊ ====================

    private JPanel stat(
            String title,
            JLabel count,
            Color color) {

        JPanel panel = card(new BorderLayout());

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(13, 13, 13, 13)
        ));

        JPanel text = vertical();

        text.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        text.add(Box.createVerticalStrut(8));
        text.add(count);
        text.add(Box.createVerticalStrut(7));

        text.add(label(
            "Phiếu đổi / trả",
            10,
            MUTED,
            Font.PLAIN
        ));

        panel.add(text, BorderLayout.CENTER);

        panel.add(label(
            "●", 15, color, Font.BOLD
        ), BorderLayout.EAST);

        return panel;
    }

    // ==================== HỘP THOẠI ====================

    private JDialog taoDialog(
            String name,
            int width,
            int height) {

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog d = new JDialog(
            owner,
            name,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        d.setSize(width, height);

        d.setMinimumSize(
            new Dimension(630, 490)
        );

        d.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        d.setLocationRelativeTo(owner);

        return d;
    }

    private static JPanel dialogHeader(
            String title,
            String sub,
            JDialog dialog) {

        JPanel head = card(new BorderLayout());

        head.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(17, 23, 17, 23)
        ));

        JPanel labels = vertical();

        labels.add(label(
            title, 21, TEXT, Font.BOLD
        ));

        labels.add(Box.createVerticalStrut(7));

        labels.add(label(
            sub, 12, MUTED, Font.PLAIN
        ));

        head.add(labels, BorderLayout.WEST);

        JButton x = button("×", false);

        x.addActionListener(e -> dialog.dispose());

        head.add(x, BorderLayout.EAST);

        return head;
    }

    private static JPanel dialogFooter() {

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 12)
        );

        footer.setBackground(
            new Color(29, 31, 35)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        return footer;
    }

    // ==================== COMPONENT DÙNG CHUNG ====================

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
            int weight) {

        JLabel l = new JLabel(value);

        l.setForeground(color);

        l.setFont(
            new Font("SansSerif", weight, size)
        );

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
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
            primary ? BLUE : new Color(29, 34, 43)
        );

        b.setFocusPainted(false);

        b.setBorder(new CompoundBorder(
            new LineBorder(primary ? BLUE : BORDER),
            new EmptyBorder(10, 12, 10, 12)
        ));

        b.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return b;
    }

    private static JTextField input(String value) {

        JTextField f = new JTextField(value);

        inputStyle(f);

        return f;
    }

    private static void inputStyle(JTextField f) {

        f.setBackground(
            new Color(23, 25, 28)
        );

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 11, 8, 11)
        ));
    }

    private static void styleCombo(
            JComboBox<String> combo) {

        combo.setBackground(
            new Color(23, 25, 28)
        );

        combo.setForeground(TEXT);

        combo.setPreferredSize(
            new Dimension(200, 36)
        );
    }

    private static JTextArea area(String initial) {

        JTextArea a = new JTextArea(
            initial, 3, 10
        );

        a.setBackground(
            new Color(23, 25, 28)
        );

        a.setForeground(TEXT);
        a.setCaretColor(TEXT);

        a.setLineWrap(true);
        a.setWrapStyleWord(true);

        a.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        a.setBorder(
            new EmptyBorder(8, 10, 8, 10)
        );

        return a;
    }

    private static JPanel field(
            String title,
            JComponent value) {

        JPanel p = new JPanel(
            new BorderLayout(0, 7)
        );

        p.setOpaque(false);

        p.add(label(
            title, 12, MUTED, Font.BOLD
        ), BorderLayout.NORTH);

        p.add(value, BorderLayout.CENTER);

        return p;
    }

    private static JPanel pair(
            JPanel left,
            JPanel right) {

        JPanel p = new JPanel(
            new GridLayout(1, 2, 14, 0)
        );

        p.setOpaque(false);

        p.add(left);
        p.add(right);

        return p;
    }

    private static JPanel info(
            String title,
            String value) {

        JPanel p = vertical();

        p.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        p.add(Box.createVerticalStrut(7));

        p.add(label(
            value,
            12,
            new Color(159, 185, 226),
            Font.BOLD
        ));

        return p;
    }

    private static JPanel notice(String message) {

        JPanel p = card(new BorderLayout());

        p.setBackground(
            new Color(31, 37, 46)
        );

        p.setBorder(
            new EmptyBorder(12, 12, 12, 12)
        );

        p.add(label(
            "ⓘ  " + message,
            11,
            MUTED,
            Font.PLAIN
        ), BorderLayout.WEST);

        return p;
    }

    private static void addDivider(JPanel parent) {

        JSeparator line = new JSeparator();

        line.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        line.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 1)
        );

        parent.add(line);
    }

    private static void addRow(
            JPanel parent,
            JComponent row,
            int height) {

        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, height)
        );

        row.setPreferredSize(
            new Dimension(640, height)
        );

        parent.add(row);
        parent.add(Box.createVerticalStrut(12));
    }

    private static Color mauTrangThai(String status) {

        switch (status) {

            case "Chờ duyệt":
                return GOLD;

            case "Đã duyệt":
                return BLUE;

            case "Từ chối":
                return RED;

            default:
                return GREEN;
        }
    }

    private static String tien(long amount) {

        return NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(amount) + " đ";
    }

    private static String loiSQL(SQLException ex) {
        if(ex.getErrorCode()==2627 || ex.getErrorCode()==2601)
            return "Mã phiếu hoặc chi tiết phiếu bị trùng. Hãy tải lại danh sách rồi thử lại.";
        if(ex.getErrorCode()==547)
            return "Hóa đơn/chi tiết không hợp lệ hoặc không đáp ứng khóa ngoại, ràng buộc SQL. Chi tiết: "+ex.getMessage();
        if(ex.getMessage()!=null && (ex.getMessage().contains("tenLienHe")
                || ex.getMessage().contains("maNVDuyet")))
            return "Database thiếu cột Đổi/Trả. Hãy chạy file database/04_BoSungThongTinDoiTra.sql.";
        return "Không thao tác được trên SQL Server: "+ex.getMessage();
    }

    private void thongBao(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Green Home - Đổi/trả",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== VIEW MODEL CỦA DỮ LIỆU SQL ====================

    private static class HoaDonView {

        final String ma;
        final String khach;
        final String sdt;
        final LocalDate ngay;

        final List<DongHoaDonView> dongs =
                new ArrayList<>();

        HoaDonView(
                String ma,
                String khach,
                String sdt,
                LocalDate ngay) {

            this.ma = ma;
            this.khach = khach;
            this.sdt = sdt;
            this.ngay = ngay;
        }

        long tongTien() {

            long sum = 0;

            for (DongHoaDonView d : dongs) {
                sum += d.giaThucTra * d.soLuong;
            }

            return sum;
        }
    }

    private static class DongHoaDonView {

        final String ma;
        final String ten;
        final int soLuong;
        final long giaThucTra;

        DongHoaDonView(
                String ma,
                String ten,
                int soLuong,
                long giaThucTra) {

            this.ma = ma;
            this.ten = ten;
            this.soLuong = soLuong;
            this.giaThucTra = giaThucTra;
        }
    }

    private static class DongTraView {

        final DongHoaDonView dong;
        final int soLuong;

        DongTraView(DongHoaDonView dong, int soLuong) {

            this.dong = dong;
            this.soLuong = soLuong;
        }
    }

    private static class PhieuView {

        final String ma;
        final HoaDonView hoaDon;

        final LocalDateTime ngayLap = LocalDateTime.now();
        String maNV = "";
        String tinhTrangSP = "CON_NGUYEN_VEN";

        String nguoiLap = "";
        String nguoiDuyet = "";

        String nguoiLienHe = "";
        String sdt = "";

        String huongXuLy = "Trả hàng & hoàn tiền";

        String lyDo = "";
        String hienTrang = "";
        String phuKien = "";

        String trangThai = "Chờ duyệt";

        final List<DongTraView> chiTiet =
                new ArrayList<>();

        PhieuView(
                String ma,
                HoaDonView hoaDon) {

            this.ma = ma;
            this.hoaDon = hoaDon;
        }

        int soLuong() {

            int sum = 0;

            for (DongTraView d : chiTiet) {
                sum += d.soLuong;
            }

            return sum;
        }

        int soLuongCua(DongHoaDonView dong) {

            int n = 0;

            for (DongTraView d : chiTiet) {

                if (d.dong == dong) {
                    n += d.soLuong;
                }
            }

            return n;
        }

        long tienHoan() {

            if (!huongXuLy.startsWith("Trả")) {
                return 0;
            }

            long sum = 0;

            for (DongTraView d : chiTiet) {

                sum += d.dong.giaThucTra * d.soLuong;
            }

            return sum;
        }

        long diemThuHoiDuKien() {

            // Công thức minh họa, không phải quy định thật
            return Math.round(tienHoan() / 100000.0);
        }
    }

    // ==================== CHẠY THỬ ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                "Green Home - Đổi / trả"
            );

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1150, 740);
            frame.setLocationRelativeTo(null);

            // false = Nhân viên; true = Quản lý
            frame.setContentPane(
                new DoiTraPanel(false)
            );

            frame.setVisible(true);
        });
    }
}