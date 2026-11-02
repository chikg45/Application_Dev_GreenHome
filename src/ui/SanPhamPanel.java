package ui;

import bus.SanPhamBUS;
import bus.ThuongHieuBUS;
import entity.SanPham;
import entity.ThuongHieu;
import enums.DanhMucSP;
import enums.TinhTrangSP;
import util.PhienDangNhap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * UC002 - Quan ly san pham. Du lieu that qua BUS -> DAO -> SQL Server.
 * Giao dien khong truy van SQL truc tiep va khong tao du lieu mau.
 */
public class SanPhamPanel extends JPanel {
    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(58, 62, 69);
    private static final Color TEXT = new Color(232, 237, 246);
    private static final Color MUTED = new Color(158, 167, 182);
    private static final Color BLUE = new Color(45, 105, 230);

    private final boolean laQuanLy;
    private final SanPhamBUS sanPhamBUS;
    private final ThuongHieuBUS thuongHieuBUS;
    private final List<SanPham> tatCa = new ArrayList<>();
    private final List<SanPham> dangHienThi = new ArrayList<>();
    private final List<ThuongHieu> thuongHieus = new ArrayList<>();
    private final JTextField timKiem = new JTextField();
    private final JComboBox<Object> locDanhMuc = new JComboBox<>();
    private final JComboBox<Object> locTrangThai = new JComboBox<>();
    private final JComboBox<Object> locThuongHieu = new JComboBox<>();
    private final JLabel thongBao = new JLabel("Đang kết nối dữ liệu...");
    private final JLabel tong = new JLabel("0");
    private final JLabel coTheBan = new JLabel("0");
    private final JLabel ngungBan = new JLabel("0");
    private final JButton them = nut("+ Thêm sản phẩm", true);
    private final JButton sua = nut("Sửa", false);
    private final JButton xoa = nut("Xóa", false);
    private final JButton doiTrangThai = nut("Đang bán / Ngừng bán", false);
    private final JButton thuongHieu = nut("Thương hiệu", false);
    private final JButton lamMoi = nut("Làm mới", false);

    private final DefaultTableModel model = new DefaultTableModel(new String[]{
            "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu", "Giá niêm yết",
            "Tồn", "Lỗi", "Có thể bán", "Trạng thái"
    }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable bang = new JTable(model);

    private record DuLieu(List<SanPham> sanPhams, List<ThuongHieu> thuongHieus) { }

    public SanPhamPanel(boolean laQuanLy) {
        this.laQuanLy = laQuanLy;
        // maNV cua nhan vien da dang nhap, khong lay tu truong nhap tren UI.
        String maNV = null;
        try { maNV = PhienDangNhap.hienTai().getMaNV(); }
        catch (IllegalStateException ignored) { /* Che do xem thu UI: khong co quyen ghi. */ }
        this.sanPhamBUS = new SanPhamBUS(maNV);
        this.thuongHieuBUS = new ThuongHieuBUS(maNV);

        setBackground(BG);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(22, 24, 22, 24));
        taoGiaoDien();
        if (!laQuanLy) {
            them.setVisible(false); sua.setVisible(false); xoa.setVisible(false);
            doiTrangThai.setVisible(false); thuongHieu.setVisible(false);
        }
        SwingUtilities.invokeLater(this::taiDuLieu);
    }

    private void taoGiaoDien() {
        JPanel dau = new JPanel(new BorderLayout(12, 10));
        dau.setOpaque(false);
        JPanel tieuDe = new JPanel(new GridLayout(2, 1, 0, 4));
        tieuDe.setOpaque(false);
        JLabel title = nhan("Quản lý sản phẩm", 22, true);
        JLabel subtitle = nhan("Thông tin sản phẩm và số lượng có thể bán theo CSDL Green Home", 12, false);
        tieuDe.add(title); tieuDe.add(subtitle);
        dau.add(tieuDe, BorderLayout.WEST);
        JPanel nutTren = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        nutTren.setOpaque(false);
        nutTren.add(lamMoi); nutTren.add(thuongHieu); nutTren.add(them);
        dau.add(nutTren, BorderLayout.EAST);

        JPanel theThongKe = new JPanel(new GridLayout(1, 3, 13, 0));
        theThongKe.setOpaque(false);
        theThongKe.add(the("Số sản phẩm", tong));
        theThongKe.add(the("Tổng số lượng có thể bán", coTheBan));
        theThongKe.add(the("Mã sản phẩm ngừng bán", ngungBan));
        JPanel phanTren = new JPanel(new BorderLayout(0, 18));
        phanTren.setOpaque(false);
        phanTren.add(dau, BorderLayout.NORTH);
        phanTren.add(theThongKe, BorderLayout.CENTER);
        add(phanTren, BorderLayout.NORTH);

        JPanel thanhLoc = new JPanel(new GridLayout(1, 4, 8, 0));
        thanhLoc.setOpaque(false);
        dinhDangNhap(timKiem); timKiem.setToolTipText("Tìm mã, tên hoặc model");
        timKiem.putClientProperty("JTextField.placeholderText", "Tìm mã, tên hoặc model...");
        thanhLoc.add(timKiem);
        dinhDangChon(locDanhMuc); dinhDangChon(locThuongHieu); dinhDangChon(locTrangThai);
        thanhLoc.add(locDanhMuc); thanhLoc.add(locThuongHieu); thanhLoc.add(locTrangThai);
        locDanhMuc.addItem("Tất cả danh mục");
        for (DanhMucSP dm : DanhMucSP.values()) locDanhMuc.addItem(dm);
        locThuongHieu.addItem("Mọi thương hiệu");
        locTrangThai.addItem("Mọi trạng thái");
        for (TinhTrangSP tt : TinhTrangSP.values()) locTrangThai.addItem(tt);

        bang.setRowHeight(40);
        bang.setBackground(CARD); bang.setForeground(TEXT);
        bang.setGridColor(BORDER); bang.setSelectionBackground(new Color(48, 68, 112));
        bang.setSelectionForeground(Color.WHITE);
        bang.setShowVerticalLines(false);
        bang.getTableHeader().setBackground(new Color(36, 40, 47));
        bang.getTableHeader().setForeground(TEXT);
        bang.getTableHeader().setPreferredSize(new Dimension(0, 39));
        bang.setFillsViewportHeight(true);
        bang.setAutoCreateRowSorter(true);
        for (int col = 0; col < bang.getColumnCount(); col++) {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setBackground(CARD); renderer.setForeground(TEXT);
            renderer.setBorder(new EmptyBorder(0, 9, 0, 8));
            bang.getColumnModel().getColumn(col).setCellRenderer(renderer);
        }
        JScrollPane cuon = new JScrollPane(bang);
        cuon.getViewport().setBackground(CARD);
        cuon.setBorder(BorderFactory.createLineBorder(BORDER));
        cuon.getVerticalScrollBar().setUnitIncrement(18);

        JPanel giua = new JPanel(new BorderLayout(0, 12));
        giua.setOpaque(false);
        giua.add(thanhLoc, BorderLayout.NORTH);
        giua.add(cuon, BorderLayout.CENTER);
        add(giua, BorderLayout.CENTER);

        JPanel duoi = new JPanel(new BorderLayout(12, 0));
        duoi.setOpaque(false);
        thongBao.setForeground(MUTED);
        duoi.add(thongBao, BorderLayout.WEST);
        JPanel nutDuoi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nutDuoi.setOpaque(false);
        nutDuoi.add(doiTrangThai); nutDuoi.add(sua); nutDuoi.add(xoa);
        duoi.add(nutDuoi, BorderLayout.EAST);
        add(duoi, BorderLayout.SOUTH);

        timKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { apDungBoLoc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { apDungBoLoc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { apDungBoLoc(); }
        });
        locDanhMuc.addActionListener(e -> apDungBoLoc());
        locThuongHieu.addActionListener(e -> apDungBoLoc());
        locTrangThai.addActionListener(e -> apDungBoLoc());
        bang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) capNhatNut();
        });
        bang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SanPham p = dangChon();
                    if (p != null) moChiTiet(p);
                }
            }
        });
        lamMoi.addActionListener(e -> taiDuLieu());
        them.addActionListener(e -> moForm(null));
        sua.addActionListener(e -> { SanPham p = dangChon(); if (p != null) moForm(p); });
        xoa.addActionListener(e -> xoaSanPham());
        doiTrangThai.addActionListener(e -> doiTrangThai());
        thuongHieu.addActionListener(e -> moQuanLyThuongHieu());
        capNhatNut();
    }

    private JPanel the(String label, JLabel value) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(13, 15, 13, 15)));
        p.add(nhan(label, 11, false), BorderLayout.NORTH);
        value.setFont(new Font("SansSerif", Font.BOLD, 25));
        value.setForeground(TEXT);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void taiDuLieu() {
        thongBao.setText("Đang đọc sản phẩm từ SQL Server...");
        lamMoi.setEnabled(false);
        new SwingWorker<DuLieu, Void>() {
            @Override protected DuLieu doInBackground() {
                return new DuLieu(sanPhamBUS.layTatCa(), thuongHieuBUS.layTatCa());
            }
            @Override protected void done() {
                lamMoi.setEnabled(true);
                try {
                    DuLieu result = get();
                    tatCa.clear(); tatCa.addAll(result.sanPhams());
                    thuongHieus.clear(); thuongHieus.addAll(result.thuongHieus());
                    Object selected = locThuongHieu.getSelectedItem();
                    String maChon = selected instanceof ThuongHieu th ? th.getMaThuongHieu() : null;
                    locThuongHieu.removeAllItems();
                    locThuongHieu.addItem("Mọi thương hiệu");
                    for (ThuongHieu th : thuongHieus) {
                        locThuongHieu.addItem(th);
                        if (Objects.equals(th.getMaThuongHieu(), maChon)) locThuongHieu.setSelectedItem(th);
                    }
                    apDungBoLoc();
                    thongBao.setText("Đã tải " + tatCa.size() + " sản phẩm từ CSDL");
                } catch (Exception e) { baoLoi(e); thongBao.setText("Không tải được dữ liệu SQL Server"); }
            }
        }.execute();
    }

    private void apDungBoLoc() {
        if (model == null) return;
        String key = timKiem.getText().trim().toLowerCase(Locale.ROOT);
        Object danhMuc = locDanhMuc.getSelectedItem();
        Object tinhTrang = locTrangThai.getSelectedItem();
        Object th = locThuongHieu.getSelectedItem();
        dangHienThi.clear(); model.setRowCount(0);
        NumberFormat tien = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (SanPham p : tatCa) {
            if (!key.isEmpty() && !( (p.getMaSP() + " " + p.getTenSP() + " " + String.valueOf(p.getModel()))
                    .toLowerCase(Locale.ROOT).contains(key))) continue;
            if (danhMuc instanceof DanhMucSP dm && p.getDanhMuc() != dm) continue;
            if (tinhTrang instanceof TinhTrangSP tt && p.getTinhTrang() != tt) continue;
            if (th instanceof ThuongHieu t && !t.getMaThuongHieu().equals(p.getThuongHieu().getMaThuongHieu())) continue;
            dangHienThi.add(p);
            model.addRow(new Object[]{p.getMaSP(), p.getTenSP(), p.getDanhMuc(),
                    p.getThuongHieu().getTenThuongHieu(), tien.format(p.getGiaNiemYet()) + " đ",
                    p.getSoLuongTon(), p.getSoLuongLoi(), p.getSoLuongCoTheBan(), p.getTinhTrang()});
        }
        tong.setText(String.valueOf(tatCa.size()));
        coTheBan.setText(String.valueOf(tatCa.stream().filter(p -> p.getTinhTrang() == TinhTrangSP.DangBan)
                .mapToInt(SanPham::getSoLuongCoTheBan).sum()));
        ngungBan.setText(String.valueOf(tatCa.stream().filter(p -> p.getTinhTrang() == TinhTrangSP.NgungBan).count()));
        capNhatNut();
    }

    private SanPham dangChon() {
        int view = bang.getSelectedRow();
        if (view < 0) return null;
        int idx = bang.convertRowIndexToModel(view);
        if (idx < 0 || idx >= dangHienThi.size()) return null;
        return dangHienThi.get(idx);
    }

    private void capNhatNut() {
        boolean duocSua = laQuanLy && PhienDangNhapDaCo() && dangChon() != null;
        sua.setEnabled(duocSua); xoa.setEnabled(duocSua); doiTrangThai.setEnabled(duocSua);
    }

    private boolean PhienDangNhapDaCo() {
        try { return PhienDangNhap.hienTai() != null; }
        catch (IllegalStateException ex) { return false; }
    }

    private void moChiTiet(SanPham p) {
        JOptionPane.showMessageDialog(this,
                "Mã: " + p.getMaSP() + "\nTên: " + p.getTenSP()
                + "\nDanh mục: " + p.getDanhMuc() + "\nThương hiệu: " + p.getThuongHieu().getTenThuongHieu()
                + "\nModel: " + p.getModel() + "\nGiá đã gồm VAT: " + p.getGiaNiemYet()
                + "\nTồn: " + p.getSoLuongTon() + "\nHàng lỗi: " + p.getSoLuongLoi()
                + "\nCó thể bán: " + p.getSoLuongCoTheBan() + "\nTình trạng: " + p.getTinhTrang()
                + "\nThông số: " + p.getThongSoKyThuat() + "\nMô tả: " + p.getMoTaSP(),
                "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void moForm(SanPham cu) {
        if (!laQuanLy || !PhienDangNhapDaCo()) return;
        if (thuongHieus.isEmpty()) { thongDiep("Cần có thương hiệu trước khi thêm sản phẩm."); return; }
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                cu == null ? "Thêm sản phẩm" : "Cập nhật sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(690, 740); dialog.setLocationRelativeTo(this);
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(CARD); root.setBorder(new EmptyBorder(18, 20, 16, 20));
        JPanel fields = new JPanel(new GridLayout(0, 2, 12, 12));
        fields.setBackground(CARD);
        JTextField ma = nhap(cu == null ? "" : cu.getMaSP());
        ma.setEditable(cu == null);
        JTextField ten = nhap(cu == null ? "" : cu.getTenSP());
        JComboBox<DanhMucSP> dm = new JComboBox<>(DanhMucSP.values());
        JComboBox<ThuongHieu> th = new JComboBox<>(thuongHieus.toArray(new ThuongHieu[0]));
        JTextField modelField = nhap(cu == null ? "" : cu.getModel());
        JTextField dvt = nhap(cu == null ? "Cái" : cu.getDonViTinh());
        JTextField gia = nhap(cu == null ? "" : String.valueOf(cu.getGiaNiemYet()));
        JTextField ton = nhap(cu == null ? "0" : String.valueOf(cu.getSoLuongTon()));
        JTextField loi = nhap(cu == null ? "0" : String.valueOf(cu.getSoLuongLoi()));
        JTextField toiThieu = nhap(cu == null ? "0" : String.valueOf(cu.getSoLuongTonToiThieu()));
        JComboBox<TinhTrangSP> trangThai = new JComboBox<>(new TinhTrangSP[]{TinhTrangSP.DangBan, TinhTrangSP.NgungBan});
        JTextField hinh = nhap(cu == null ? "" : cu.getHinhAnh());
        JTextArea thongSo = area(cu == null ? "" : cu.getThongSoKyThuat());
        JTextArea moTa = area(cu == null ? "" : cu.getMoTaSP());
        if (cu != null) {
            dm.setSelectedItem(cu.getDanhMuc());
            for (int i = 0; i < th.getItemCount(); i++)
                if (th.getItemAt(i).getMaThuongHieu().equals(cu.getThuongHieu().getMaThuongHieu()))
                    th.setSelectedIndex(i);
            trangThai.setSelectedItem(cu.getTinhTrang() == TinhTrangSP.NgungBan ? TinhTrangSP.NgungBan : TinhTrangSP.DangBan);
        }
        fields.add(cot("Mã sản phẩm *", ma)); fields.add(cot("Tên sản phẩm *", ten));
        fields.add(cot("Danh mục *", dm)); fields.add(cot("Thương hiệu *", th));
        fields.add(cot("Model", modelField)); fields.add(cot("Đơn vị tính *", dvt));
        fields.add(cot("Giá niêm yết (đã VAT) *", gia)); fields.add(cot("Số lượng tồn", ton));
        fields.add(cot("Số lượng lỗi", loi)); fields.add(cot("Tồn tối thiểu", toiThieu));
        fields.add(cot("Trạng thái kinh doanh", trangThai)); fields.add(cot("Hình ảnh (đường dẫn)", hinh));
        fields.add(cot("Thông số kỹ thuật", new JScrollPane(thongSo)));
        fields.add(cot("Mô tả", new JScrollPane(moTa)));
        JScrollPane sc = new JScrollPane(fields);
        sc.setBorder(null); sc.getViewport().setBackground(CARD);
        sc.getVerticalScrollBar().setUnitIncrement(17);
        root.add(sc, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setOpaque(false);
        JButton huy = nut("Hủy", false), luu = nut("Lưu thông tin", true);
        huy.addActionListener(e -> dialog.dispose());
        luu.addActionListener(e -> {
            try {
                SanPham p = new SanPham(
                        ma.getText().trim(), ten.getText().trim(), (DanhMucSP) dm.getSelectedItem(),
                        (ThuongHieu) th.getSelectedItem(), thongSo.getText().trim(),
                        Double.parseDouble(gia.getText().trim()), modelField.getText().trim(),
                        dvt.getText().trim(), moTa.getText().trim(), (TinhTrangSP) trangThai.getSelectedItem(),
                        Integer.parseInt(ton.getText().trim()), Integer.parseInt(loi.getText().trim()),
                        Integer.parseInt(toiThieu.getText().trim()), hinh.getText().trim());
                // BUS cap nhat HetHang tu dong theo tinh trang va luong co the ban.
                thucHien(cu == null ? "Thêm sản phẩm" : "Cập nhật sản phẩm",
                        () -> cu == null ? sanPhamBUS.themSanPham(p) : sanPhamBUS.capNhatSanPham(p), dialog);
            } catch (RuntimeException ex) { baoLoi(ex); }
        });
        btns.add(huy); btns.add(luu);
        root.add(btns, BorderLayout.SOUTH);
        dialog.setContentPane(root); dialog.setVisible(true);
    }

    private void xoaSanPham() {
        SanPham p = dangChon(); if (p == null) return;
        int x = JOptionPane.showConfirmDialog(this,
                "Xóa sản phẩm " + p.getMaSP() + "?\nSản phẩm đã phát sinh giao dịch không được xóa vĩnh viễn.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (x == JOptionPane.YES_OPTION) thucHien("Xóa sản phẩm", () -> sanPhamBUS.xoaSanPham(p.getMaSP()), null);
    }

    private void doiTrangThai() {
        SanPham p = dangChon(); if (p == null) return;
        TinhTrangSP moi = p.getTinhTrang() == TinhTrangSP.NgungBan ? TinhTrangSP.DangBan : TinhTrangSP.NgungBan;
        int x = JOptionPane.showConfirmDialog(this,
                "Đổi trạng thái " + p.getMaSP() + " thành " + moi + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (x == JOptionPane.YES_OPTION)
            thucHien("Đổi trạng thái", () -> sanPhamBUS.doiTinhTrang(p.getMaSP(), moi), null);
    }

    private void moQuanLyThuongHieu() {
        if (!laQuanLy || !PhienDangNhapDaCo()) return;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Quản lý thương hiệu",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(540, 450); dialog.setLocationRelativeTo(this);
        DefaultTableModel m = new DefaultTableModel(new String[]{"Mã", "Tên thương hiệu"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable bangTH = new JTable(m);
        bangTH.setRowHeight(29); bangTH.setAutoCreateRowSorter(true);
        Runnable capNhatDanhSach = () -> {
            m.setRowCount(0);
            for (ThuongHieu b : thuongHieus) m.addRow(new Object[]{b.getMaThuongHieu(), b.getTenThuongHieu()});
        };
        capNhatDanhSach.run();
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton add = new JButton("Thêm"), edit = new JButton("Sửa"), delete = new JButton("Xóa");
        actions.add(add);actions.add(edit);actions.add(delete);
        add.addActionListener(e -> moFormThuongHieu(null, dialog));
        edit.addActionListener(e -> {
            int row = bangTH.getSelectedRow();if (row < 0) { thongDiep("Chọn thương hiệu cần sửa."); return; }
            String ma = String.valueOf(m.getValueAt(bangTH.convertRowIndexToModel(row), 0));
            for (ThuongHieu b : thuongHieus) if (b.getMaThuongHieu().equals(ma)) { moFormThuongHieu(b, dialog); break; }
        });
        delete.addActionListener(e -> {
            int row = bangTH.getSelectedRow();if (row < 0) { thongDiep("Chọn thương hiệu cần xóa."); return; }
            String ma = String.valueOf(m.getValueAt(bangTH.convertRowIndexToModel(row), 0));
            if (JOptionPane.showConfirmDialog(dialog, "Xóa thương hiệu " + ma + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                thucHien("Xóa thương hiệu", () -> thuongHieuBUS.xoaThuongHieu(ma), dialog);
        });
        dialog.add(new JScrollPane(bangTH), BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void moFormThuongHieu(ThuongHieu b, JDialog cha) {
        JTextField ma = new JTextField(b == null ? "" : b.getMaThuongHieu());
        JTextField ten = new JTextField(b == null ? "" : b.getTenThuongHieu());
        ma.setEditable(b == null);
        JPanel fields = new JPanel(new GridLayout(0, 1, 5, 5));
        fields.add(new JLabel("Mã thương hiệu (TH001)")); fields.add(ma);
        fields.add(new JLabel("Tên thương hiệu")); fields.add(ten);
        int result = JOptionPane.showConfirmDialog(cha, fields, b == null ? "Thêm thương hiệu" : "Sửa thương hiệu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            ThuongHieu moi = new ThuongHieu(ma.getText().trim(), ten.getText().trim());
            if (b != null) moi.setMoTaThuongHieu(b.getMoTaThuongHieu());
            thucHien(b == null ? "Thêm thương hiệu" : "Cập nhật thương hiệu",
                    () -> b == null ? thuongHieuBUS.themThuongHieu(moi) : thuongHieuBUS.capNhatThuongHieu(moi), cha);
        } catch (RuntimeException ex) { baoLoi(ex); }
    }

    @FunctionalInterface private interface GhiDuLieu { boolean ghi(); }
    private void thucHien(String ten, GhiDuLieu thaoTac, Window dialogCanDong) {
        thongBao.setText("Đang thực hiện: " + ten + "...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return thaoTac.ghi(); }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (!ok) { thongDiep(ten + " không thay đổi bản ghi nào."); return; }
                    if (dialogCanDong != null) dialogCanDong.dispose();
                    thongDiep(ten + " thành công.");
                    taiDuLieu();
                } catch (Exception ex) { baoLoi(ex); }
            }
        }.execute();
    }

    private void baoLoi(Throwable e) {
        Throwable cause = e;
        if (cause instanceof ExecutionException && cause.getCause() != null) cause = cause.getCause();
        thongBao.setText("Lỗi: " + cause.getMessage());
        JOptionPane.showMessageDialog(this, cause.getMessage(), "Green Home - Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    private void thongDiep(String text) {
        JOptionPane.showMessageDialog(this, text, "Green Home", JOptionPane.INFORMATION_MESSAGE);
    }

    private static JLabel nhan(String text, int font, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(bold ? TEXT : MUTED);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, font));
        return label;
    }
    private static JButton nut(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(primary ? BLUE : new Color(43, 47, 55));
        button.setForeground(TEXT);
        button.setBorder(new EmptyBorder(10, 13, 10, 13));
        return button;
    }
    private static JTextField nhap(String text) {
        JTextField f = new JTextField(text == null ? "" : text);
        dinhDangNhap(f);
        return f;
    }
    private static void dinhDangNhap(JTextField f) {
        f.setBackground(CARD); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 8, 7, 8)));
    }
    private static void dinhDangChon(JComboBox<?> c) {
        c.setBackground(CARD); c.setForeground(TEXT);
        c.setBorder(BorderFactory.createLineBorder(BORDER));
    }
    private static JTextArea area(String text) {
        JTextArea a = new JTextArea(text == null ? "" : text, 3, 16);
        a.setLineWrap(true); a.setWrapStyleWord(true);
        a.setBackground(CARD); a.setForeground(TEXT); a.setCaretColor(TEXT);
        return a;
    }
    private static JPanel cot(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));p.setOpaque(false);
        p.add(nhan(label, 12, false), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
}
