package entity;

import java.util.List;

/** Snapshot giá được so lại ngay trước lúc ghi nhận thanh toán. */
public record BaoGia(long phienBan,String maDon,String maKH,String maUuDai,boolean dungDiem,int diemHienTai,
        List<DongHoaDonBanHang> chiTiet,long tongHang,long giamKM,int diemDung,int diemCong,long canTra) {
    public BaoGia { chiTiet=List.copyOf(chiTiet); }
    public long giamDiem() { return diemDung*1000L; }
    public long tienDong(String maSP) {
        return chiTiet.stream().filter(c->c.getMaSP().equals(maSP)).mapToLong(DongHoaDonBanHang::getThucTra).sum();
    }
    public boolean cungGia(BaoGia b) {
        if(phienBan!=b.phienBan||!maDon.equals(b.maDon)||!maKH.equals(b.maKH)||!maUuDai.equals(b.maUuDai)
                ||dungDiem!=b.dungDiem||diemHienTai!=b.diemHienTai||tongHang!=b.tongHang
                ||diemCong!=b.diemCong||giamKM!=b.giamKM||diemDung!=b.diemDung||canTra!=b.canTra||chiTiet.size()!=b.chiTiet.size()) return false;
        for(int i=0;i<chiTiet.size();i++) {
            var a=chiTiet.get(i);var c=b.chiTiet.get(i);
            if(!a.getMaSP().equals(c.getMaSP())||a.getSoLuong()!=c.getSoLuong()||a.getDonGia()!=c.getDonGia()
                    ||a.getSoTienGiam()!=c.getSoTienGiam()||!a.getMaKM().equals(c.getMaKM())) return false;
        }
        return true;
    }
}
