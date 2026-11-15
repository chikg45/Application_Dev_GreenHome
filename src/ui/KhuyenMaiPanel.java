package ui;

import bus.KhuyenMaiBUS;
import entity.KhuyenMai;
import entity.LoaiGiam;
import java.awt.*;
import java.sql.SQLException;
import java.time.*; import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

/** Quản lý khuyến mãi; chỉ UI, nghiệp vụ ở BUS và truy cập SQL ở DAO. */
public class KhuyenMaiPanel extends JPanel {
    private static final Color BG = new Color(30,33,37), CARD = new Color(18,19,21);
    private static final Color BORDER = new Color(57,61,67), TEXT = new Color(225,232,242);
    private static final Color MUTED = new Color(143,155,173), BLUE = new Color(54,111,232);
    private static final Color GREEN = new Color(48,174,121), RED = new Color(183,88,91);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final KhuyenMaiBUS bus = new KhuyenMaiBUS();
    private final boolean laQuanLy;
    private List<KhuyenMai> danhSach = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã KM","Tên chương trình","Loại","Giá trị","Bắt đầu","Kết thúc","Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
            "Tất cả trạng thái","Đang diễn ra","Sắp diễn ra","Sắp kết thúc","Đã kết thúc"});
    private final JLabel[] counts = {new JLabel("0"),new JLabel("0"),new JLabel("0"),new JLabel("0")};

    public KhuyenMaiPanel(boolean laQuanLy) {
        this.laQuanLy = laQuanLy;
        setLayout(new BorderLayout()); setBackground(BG);
        initUI(); bindEvents(); loadData();
    }
    @Override public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::loadData);
    }
    private void initUI() {
        JPanel content = new JPanel(); content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24,28,25,28));
        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false);
        JPanel intro = vertical(); intro.add(label("Quản lý khuyến mãi",22,Font.BOLD,TEXT));
        intro.add(Box.createVerticalStrut(6));
        intro.add(label("Quản lý chương trình ưu đãi và thời gian áp dụng.",12,Font.PLAIN,MUTED));
        head.add(intro, BorderLayout.WEST);
        if (laQuanLy) {
            JButton add = button("+  Thêm khuyến mãi", BLUE);
            add.addActionListener(e -> openEditor(null)); head.add(add, BorderLayout.EAST);
        }
        fullWidth(head); content.add(head); content.add(Box.createVerticalStrut(20));
        JPanel stats = new JPanel(new GridLayout(1,4,12,0)); stats.setOpaque(false);
        String[] titles = {"Tổng chương trình","Đang diễn ra","Sắp kết thúc","Đã kết thúc"};
        Color[] colors = {BLUE,GREEN,new Color(204,157,65),RED};
        for (int i=0;i<4;i++) stats.add(counter(titles[i],counts[i],colors[i]));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE,104));
        content.add(stats); content.add(Box.createVerticalStrut(20));
        JPanel box = new JPanel(new BorderLayout(0,16)); box.setBackground(CARD);
        box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(18,18,18,18)));
        JPanel tools = new JPanel(new BorderLayout(12,0)); tools.setOpaque(false);
        JPanel title = vertical(); title.add(label("Danh sách khuyến mãi",15,Font.BOLD,TEXT));
        title.add(Box.createVerticalStrut(5));
        title.add(label("Tìm kiếm, lọc và xem chi tiết chương trình",11,Font.PLAIN,MUTED));
        tools.add(title,BorderLayout.WEST);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); filter.setOpaque(false);
        styleInput(txtSearch); txtSearch.setPreferredSize(new Dimension(210,34));
        txtSearch.setToolTipText("Tìm theo mã hoặc tên khuyến mãi");
        txtSearch.putClientProperty("JTextField.placeholderText","Tìm mã / tên...");
        cmbStatus.setPreferredSize(new Dimension(165,34)); styleCombo(cmbStatus);
        filter.add(txtSearch); filter.add(cmbStatus); tools.add(filter,BorderLayout.EAST);
        box.add(tools,BorderLayout.NORTH);
        table.setRowHeight(45); table.setFont(new Font("SansSerif",Font.PLAIN,12));
        table.setBackground(CARD); table.setForeground(TEXT); table.setGridColor(BORDER);
        table.setShowVerticalLines(false); table.setSelectionBackground(new Color(41,58,85));
        table.setSelectionForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0,40));
        table.getTableHeader().setDefaultRenderer(new CellRenderer(true));
        int[] widths = {100,220,100,115,150,150,125};
        for (int i=0;i<widths.length;i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(new CellRenderer(false));
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(CARD); scroll.setPreferredSize(new Dimension(750,370));
        box.add(scroll,BorderLayout.CENTER);
        JPanel actions = new JPanel(new BorderLayout()); actions.setOpaque(false);
        actions.add(label("Danh sách khuyến mãi",10,Font.PLAIN,MUTED),BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); right.setOpaque(false);
        JButton detail = button("Xem chi tiết",new Color(54,63,78));
        detail.addActionListener(e -> viewSelected()); right.add(detail);
        if (laQuanLy) {
            JButton edit = button("Sửa",BLUE), delete = button("Xóa",RED);
            edit.addActionListener(e -> { KhuyenMai km = selected(); if (km!=null) openEditor(km); });
            delete.addActionListener(e -> deleteSelected()); right.add(edit); right.add(delete);
        }
        actions.add(right,BorderLayout.EAST); box.add(actions,BorderLayout.SOUTH);
        fullWidth(box); content.add(box);
        JScrollPane page = new JScrollPane(content); page.setBorder(null);
        page.getViewport().setBackground(BG); page.getVerticalScrollBar().setUnitIncrement(18);
        add(page,BorderLayout.CENTER);
    }
    private void bindEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
        cmbStatus.addActionListener(e -> filterData());
    }
    private void loadData() {
        try { danhSach = bus.getAll(); filterData(); }
        catch (SQLException | RuntimeException e) { error(e); }
    }
    private void filterData() {
        model.setRowCount(0);
        String query = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String status = String.valueOf(cmbStatus.getSelectedItem());
        int[] total = new int[4];
        for (KhuyenMai km : danhSach) {
            String state = bus.trangThai(km); total[0]++;
            if ("Đang diễn ra".equals(state)) total[1]++;
            if ("Sắp kết thúc".equals(state)) total[2]++;
            if ("Đã kết thúc".equals(state)) total[3]++;
            if (!km.getMaKM().toLowerCase(Locale.ROOT).contains(query)
                    && !km.getTenKM().toLowerCase(Locale.ROOT).contains(query)) continue;
            if (!"Tất cả trạng thái".equals(status) && !state.equals(status)) continue;
            boolean percent = "PHAN_TRAM".equals(km.getLoaiGiam());
            String value = String.format(Locale.forLanguageTag("vi-VN"),"%,.2f",percent ? km.getPhanTramGiam() : km.getSoTienGiam());
            model.addRow(new Object[]{km.getMaKM(),km.getTenKM(),percent?"Phần trăm":"Số tiền",
                    value+(percent?"%":" đ"),km.getNgayBatDau().format(DATE),km.getNgayKetThuc().format(DATE),state});
        }
        for (int i=0;i<4;i++) counts[i].setText(String.valueOf(total[i]));
    }
    private KhuyenMai selected() {
        int r = table.getSelectedRow();
        if (r<0) { JOptionPane.showMessageDialog(this,"Vui lòng chọn một khuyến mãi."); return null; }
        String id = model.getValueAt(table.convertRowIndexToModel(r),0).toString();
        try { return bus.findById(id); }
        catch (SQLException | RuntimeException e) { error(e); return null; }
    }
    private void viewSelected() {
        KhuyenMai k = selected();
        if (k == null) return;
        try {
            Map<String, String> products = bus.laySanPhamApDung(k.getMaKM());
            StringBuilder detail = new StringBuilder()
                .append("Mã: ").append(k.getMaKM()).append("\n")
                .append("Tên: ").append(k.getTenKM()).append("\n")
                .append("Loại: ").append(k.getLoaiGiam()).append("\n")
                .append("Phần trăm: ").append(k.getPhanTramGiam()).append("%\n")
                .append("Số tiền: ").append(k.getSoTienGiam()).append("\n")
                .append("Bắt đầu: ").append(k.getNgayBatDau().format(DATE)).append("\n")
                .append("Kết thúc: ").append(k.getNgayKetThuc().format(DATE)).append("\n")
                .append("Trạng thái: ").append(bus.trangThai(k)).append("\n")
                .append("Mô tả: ").append(k.getMoTaKM() == null ? "" : k.getMoTaKM())
                .append("\n\nSẢN PHẨM ÁP DỤNG (").append(products.size()).append("):\n");
            if (products.isEmpty()) detail.append("Chưa gán sản phẩm nào.");
            products.forEach((id, name) -> detail.append("• ").append(id).append(" — ").append(name).append("\n"));
            JTextArea text = new JTextArea(detail.toString(), 18, 48);
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setCaretPosition(0);
            JScrollPane scroll = new JScrollPane(text);
            scroll.setPreferredSize(new Dimension(520, 390));
            JOptionPane.showMessageDialog(this, scroll, "Chi tiết khuyến mãi", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | RuntimeException e) { error(e); }
    }
    private void deleteSelected() {
        KhuyenMai k = selected(); if (k==null) return;
        if (JOptionPane.showConfirmDialog(this,"Xóa khuyến mãi "+k.getMaKM()+"?","Xác nhận",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try { if (bus.delete(k.getMaKM())) loadData(); else JOptionPane.showMessageDialog(this,"Không tìm thấy khuyến mãi."); }
        catch (SQLException | RuntimeException e) { error(e); }
    }
    private void openEditor(KhuyenMai old) {
        boolean editing = old!=null;
        String preview;
        try { preview = editing?old.getMaKM():bus.previewNextId(); }
        catch (SQLException e) { error(e); return; }
        JTextField ma = new JTextField(preview); ma.setEditable(false);
        JTextField ten = new JTextField(editing?old.getTenKM():"");
        JComboBox<LoaiGiam> loai = new JComboBox<>(LoaiGiam.values());
        if (editing) loai.setSelectedItem(old.getLoaiGiamEnum());
        JTextField giaTri = new JTextField(editing ? String.valueOf(old.getLoaiGiamEnum()==LoaiGiam.PHAN_TRAM
                ? old.getPhanTramGiam():old.getSoTienGiam()):"10");
        DateSelector batDau = new DateSelector(editing?old.getNgayBatDau():LocalDateTime.now());
        DateSelector ketThuc = new DateSelector(editing?old.getNgayKetThuc():LocalDateTime.now().plusDays(7));
        List<String> daChon; LinkedHashMap<String,String> danhMuc;
        try {
            daChon = editing ? bus.layMaSanPhamApDung(old.getMaKM()) : Collections.emptyList();
            danhMuc = bus.layDanhMucSanPham();
        } catch (SQLException e) { error(e); return; }
        SanPhamKhuyenMaiPicker productArea = new SanPhamKhuyenMaiPicker(bus, danhMuc, daChon);
        JTextArea moTa = new JTextArea(editing && old.getMoTaKM()!=null?old.getMoTaKM():"",3,20);
        moTa.setLineWrap(true); moTa.setWrapStyleWord(true);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10,10,10,10));
        addField(form,0,"Mã KM (tự động):",ma,36);
        addField(form,1,"Tên chương trình:",ten,36);
        addField(form,2,"Loại giảm:",loai,36);
        addField(form,3,"Giá trị giảm:",giaTri,36);
        addField(form,4,"Ngày giờ bắt đầu:",batDau,36);
        addField(form,5,"Ngày giờ kết thúc:",ketThuc,36);
        addField(form,6,"Mô tả:",new JScrollPane(moTa),75);
        addField(form,7,"Sản phẩm áp dụng:",productArea,270);
        JScrollPane scroll = new JScrollPane(form); scroll.setPreferredSize(new Dimension(750,560));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        while (JOptionPane.showConfirmDialog(this,scroll,editing?"Sửa khuyến mãi":"Thêm khuyến mãi",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            try {
                LoaiGiam type=(LoaiGiam)loai.getSelectedItem();
                double amount=Double.parseDouble(giaTri.getText().trim());
                List<String> chosen=new ArrayList<>();
                chosen.addAll(productArea.getSelectedIds());
                if (chosen.isEmpty()) throw new IllegalArgumentException("Hãy chọn ít nhất một sản phẩm áp dụng.");
                KhuyenMai k=new KhuyenMai(editing?old.getMaKM():preview,ten.getText().trim(),
                        type==LoaiGiam.PHAN_TRAM?amount:0, batDau.getDateTime(), ketThuc.getDateTime(),
                        moTa.getText(), type.name(), type==LoaiGiam.SO_TIEN?amount:0);
                String savedId;
                if (editing) { if (!bus.update(k,chosen)) throw new IllegalArgumentException("Không tìm thấy khuyến mãi."); savedId=k.getMaKM(); }
                else savedId=bus.themTuDong(k,chosen).getMaKM();
                loadData(); JOptionPane.showMessageDialog(this,"Lưu thành công. Mã: "+savedId); return;
            } catch (SQLException | IllegalArgumentException e) { error(e); }
        }
    }
    private static void addField(JPanel form,int row,String name,JComponent component,int height) {
        GridBagConstraints g=new GridBagConstraints(); g.gridy=row; g.insets=new Insets(5,7,5,7);
        g.anchor=GridBagConstraints.WEST; g.gridx=0; g.weightx=0; form.add(new JLabel(name),g);
        g.gridx=1; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL;
        component.setPreferredSize(new Dimension(330,height)); form.add(component,g);
    }
    private static class DateSelector extends JPanel {
        private final JSpinner date, hour, minute;
        DateSelector(LocalDateTime initial) {
            super(new FlowLayout(FlowLayout.LEFT,4,0));
            Date when=java.sql.Timestamp.valueOf(initial);
            date=new JSpinner(new SpinnerDateModel(when,null,null,Calendar.DAY_OF_MONTH));
            date.setEditor(new JSpinner.DateEditor(date,"dd/MM/yyyy"));
            hour=new JSpinner(new SpinnerNumberModel(initial.getHour(),0,23,1));
            minute=new JSpinner(new SpinnerNumberModel(initial.getMinute(),0,59,1));
            date.setPreferredSize(new Dimension(145,32));
            hour.setPreferredSize(new Dimension(55,32)); minute.setPreferredSize(new Dimension(55,32));
            add(date); add(hour); add(new JLabel(":")); add(minute);
        }
        LocalDateTime getDateTime() {
            LocalDate day=((Date)date.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return day.atTime((Integer)hour.getValue(),(Integer)minute.getValue());
        }
    }
    private static JPanel vertical() {
        JPanel p=new JPanel(); p.setOpaque(false); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); return p;
    }
    private static void fullWidth(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE,c.getPreferredSize().height));
    }
    private static JLabel label(String name,int size,int weight,Color color) {
        JLabel l=new JLabel(name); l.setForeground(color); l.setFont(new Font("SansSerif",weight,size)); return l;
    }
    private static JButton button(String name,Color color) {
        JButton b=new JButton(name); b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setBackground(color); b.setForeground(Color.WHITE); b.setFocusPainted(false);
        b.setOpaque(true); b.setBorder(new EmptyBorder(10,16,10,16)); return b;
    }
    private static void styleInput(JTextField f) {
        f.setBackground(new Color(35,38,43)); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(new Font("SansSerif",Font.PLAIN,12));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(8,10,8,10)));
    }
    private static void styleCombo(JComboBox<String> c) {
        c.setBackground(new Color(35,38,43)); c.setForeground(TEXT);
        c.setFont(new Font("SansSerif",Font.PLAIN,12));
    }
    private static JPanel counter(String name,JLabel count,Color color) {
        JPanel p=vertical(); p.setBackground(CARD); p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(13,15,12,15)));
        p.add(label(name,11,Font.PLAIN,MUTED)); p.add(Box.createVerticalStrut(10));
        count.setForeground(color); count.setFont(new Font("SansSerif",Font.BOLD,25)); p.add(count); return p;
    }
    private void error(Exception e) {
        JOptionPane.showMessageDialog(this,e.getMessage(),"Không thể thực hiện",JOptionPane.ERROR_MESSAGE);
    }
    private static class CellRenderer extends DefaultTableCellRenderer {
        private final boolean header;
        CellRenderer(boolean header) { this.header=header; }
        @Override public Component getTableCellRendererComponent(JTable t,Object value,boolean selected,
                boolean focus,int row,int col) {
            super.getTableCellRendererComponent(t,value,selected,focus,row,col);
            setBorder(new EmptyBorder(0,12,0,8));
            setFont(new Font("SansSerif",header?Font.BOLD:Font.PLAIN,header?11:12));
            setBackground(header?new Color(29,32,36):selected?t.getSelectionBackground():CARD);
            setForeground(header?MUTED:selected?Color.WHITE:TEXT);
            if (!header && col==6 && !selected) {
                if ("Đang diễn ra".equals(value)) setForeground(GREEN);
                else if ("Sắp diễn ra".equals(value)||"Sắp kết thúc".equals(value)) setForeground(new Color(220,174,85));
            }
            return this;
        }
    }
    /** Thành phần UI nội bộ: hiển thị kết quả tìm kiếm và sản phẩm đã chọn. */
    private static class SanPhamKhuyenMaiPicker extends JPanel {
        private final KhuyenMaiBUS bus;
        private final Map<String, String> productNames = new LinkedHashMap<>();
        private final Set<String> selectedIds = new LinkedHashSet<>();
        private final List<String> resultIds = new ArrayList<>();
        private final List<String> chosenIds = new ArrayList<>();
        private final List<String> categoryIds = new ArrayList<>();
        private final JTextField keyword = new JTextField();
        private final JComboBox<String> category = new JComboBox<>();
        private final DefaultListModel<String> resultModel = new DefaultListModel<>();
        private final DefaultListModel<String> chosenModel = new DefaultListModel<>();
        private final JList<String> results = new JList<>(resultModel);
        private final JList<String> chosen = new JList<>(chosenModel);
        private final JLabel selectedCount = new JLabel();
        private final javax.swing.Timer searchDelay;

        SanPhamKhuyenMaiPicker(KhuyenMaiBUS bus, Map<String, String> categories, List<String> initial) {
            super(new BorderLayout(0, 6));
            this.bus = bus;
            selectedIds.addAll(initial);
            category.addItem("Tất cả danh mục");
            categoryIds.add(null);
            categories.forEach((id, name) -> { categoryIds.add(id); category.addItem(name); });
            keyword.setToolTipText("Tìm theo mã hoặc tên sản phẩm");
            JPanel filters = new JPanel(new BorderLayout(6, 0));
            filters.add(keyword, BorderLayout.CENTER);
            filters.add(category, BorderLayout.EAST);
            add(filters, BorderLayout.NORTH);

            results.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            chosen.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            for (JList<String> list : java.util.Arrays.asList(results, chosen)) {
                list.setBackground(new Color(35, 38, 43));
                list.setForeground(new Color(225, 232, 242));
                list.setSelectionBackground(new Color(41, 58, 85));
                list.setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            JPanel columns = new JPanel(new GridLayout(1, 2, 8, 0));
            columns.add(listSection("Kết quả tìm kiếm", results));
            columns.add(listSection("Đã chọn", chosen));
            add(columns, BorderLayout.CENTER);

            JButton add = new JButton("Thêm →");
            JButton remove = new JButton("← Gỡ chọn");
            JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
            controls.add(selectedCount);
            controls.add(add);
            controls.add(remove);
            add(controls, BorderLayout.SOUTH);
            add.addActionListener(e -> addSelected());
            remove.addActionListener(e -> removeSelected());
            results.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) addSelected();
                }
            });
            chosen.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) removeSelected();
                }
            });
            searchDelay = new javax.swing.Timer(250, e -> refreshResults());
            searchDelay.setRepeats(false);
            keyword.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { searchDelay.restart(); }
                public void removeUpdate(DocumentEvent e) { searchDelay.restart(); }
                public void changedUpdate(DocumentEvent e) { searchDelay.restart(); }
            });
            category.addActionListener(e -> searchDelay.restart());
            try { productNames.putAll(bus.laySanPham()); }
            catch (SQLException ex) { showError(ex); }
            refreshChosen();
            refreshResults();
        }

        private JPanel listSection(String title, JList<String> list) {
            JPanel panel = new JPanel(new BorderLayout(0, 4));
            panel.add(new JLabel(title), BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(list);
            scroll.setPreferredSize(new Dimension(245, 145));
            panel.add(scroll, BorderLayout.CENTER);
            return panel;
        }

        private void refreshResults() {
            try {
                String maDM = categoryIds.get(category.getSelectedIndex());
                Map<String, String> found = bus.timSanPham(keyword.getText(), maDM);
                productNames.putAll(found);
                resultIds.clear();
                resultModel.clear();
                found.forEach((id, name) -> {
                    resultIds.add(id);
                    resultModel.addElement(id + " — " + name);
                });
            } catch (SQLException | RuntimeException ex) { showError(ex); }
        }

        private void addSelected() {
            for (int index : results.getSelectedIndices()) selectedIds.add(resultIds.get(index));
            refreshChosen();
        }

        private void removeSelected() {
            for (int index : chosen.getSelectedIndices()) selectedIds.remove(chosenIds.get(index));
            refreshChosen();
        }

        private void refreshChosen() {
            chosenIds.clear();
            chosenModel.clear();
            for (String id : selectedIds) {
                chosenIds.add(id);
                chosenModel.addElement(id + " — " + productNames.getOrDefault(id, "(chưa có tên)"));
            }
            selectedCount.setText("Đã chọn: " + selectedIds.size());
        }

        private void showError(Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi tải sản phẩm", JOptionPane.ERROR_MESSAGE);
        }

        List<String> getSelectedIds() { return new ArrayList<>(selectedIds); }
    }
}
