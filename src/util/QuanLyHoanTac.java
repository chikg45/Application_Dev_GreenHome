package util;

import java.awt.Component;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.JOptionPane;

public final class QuanLyHoanTac {

    @FunctionalInterface
    public interface ThaoTacHoanTac {
        void thucHien() throws Exception;
    }

    private static final class HanhDong {
        final String moTa;
        final ThaoTacHoanTac lenhKhoiPhuc;
        final Runnable capNhatGiaoDien;

        HanhDong(String moTa, ThaoTacHoanTac lenhKhoiPhuc, Runnable capNhatGiaoDien) {
            this.moTa = moTa;
            this.lenhKhoiPhuc = lenhKhoiPhuc;
            this.capNhatGiaoDien = capNhatGiaoDien;
        }
    }

    private static final int GIOI_HAN = 20;
    private static final Deque<HanhDong> nganXep = new ArrayDeque<>();

    private QuanLyHoanTac() {}

    public static void ghiNhan(String moTa, ThaoTacHoanTac lenhKhoiPhuc, Runnable capNhatGiaoDien) {
        if (nganXep.size() >= GIOI_HAN) {
            nganXep.removeLast();
        }
        nganXep.push(new HanhDong(moTa, lenhKhoiPhuc, capNhatGiaoDien));
    }

    public static void hoanTacGanNhat(Component parent) {
        if (nganXep.isEmpty()) {
            JOptionPane.showMessageDialog(
                parent,
                "Không có thao tác nào gần đây để hoàn tác.",
                "Hoàn tác (Ctrl + Z)",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        HanhDong hd = nganXep.pop();
        try {
            hd.lenhKhoiPhuc.thucHien();
            if (hd.capNhatGiaoDien != null) {
                hd.capNhatGiaoDien.run();
            }
            JOptionPane.showMessageDialog(
                parent,
                "Đã hoàn tác: " + hd.moTa,
                "Hoàn tác thành công (Ctrl + Z)",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parent,
                "Không thể hoàn tác (" + hd.moTa + "):\n" + ex.getMessage(),
                "Lỗi hoàn tác",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void xoaLichSu() {
        nganXep.clear();
    }
}