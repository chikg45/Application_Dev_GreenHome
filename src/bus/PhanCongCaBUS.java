package bus;

import dao.CaLamViecDAO;
import dao.PhanCongCaDAO;
import entity.CaLamViec;
import entity.PhanCongCa;
import entity.NhanVien;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Quy trinh 2.7, CL-01..CL-07. */
public class PhanCongCaBUS {
    private final String maDangNhap;
    private final PhanCongCaDAO dao;
    private final CaLamViecDAO caDAO;
    public PhanCongCaBUS(String maDangNhap) {
        this(maDangNhap,new PhanCongCaDAO(),new CaLamViecDAO());
    }
    public PhanCongCaBUS(String maDangNhap,PhanCongCaDAO dao,CaLamViecDAO caDAO) {
        this.maDangNhap=Objects.requireNonNull(maDangNhap);
        this.dao=Objects.requireNonNull(dao);this.caDAO=Objects.requireNonNull(caDAO);
    }
    public List<CaLamViec> layDanhSachCa() {return caDAO.layTatCa();}

    /**
     * Dem so ca duoc phan cong cho nhan vien trong mot tuan.
     * ngayDauTuan la thu Hai.
     */
    public static int demSoCaTrongTuan(NhanVien nhanVien, LocalDate ngayDauTuan) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null.");
        }
        if (ngayDauTuan == null) {
            throw new IllegalArgumentException("Ngày đầu tuần không được null.");
        }

        LocalDate ngayCuoiTuan = ngayDauTuan.plusDays(6);
        int soCa = 0;

        for (PhanCongCa pc : nhanVien.getDanhSachPhanCong()) {
            LocalDate ngayLam = pc.getNgayLam();
            if (ngayLam != null
                    && !ngayLam.isBefore(ngayDauTuan)
                    && !ngayLam.isAfter(ngayCuoiTuan)) {
                soCa++;
            }
        }
        return soCa;
    }

    /** Dem tong so ca da duoc phan cong cho nhan vien. */
    public static int demTongSoCa(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null.");
        }
        return nhanVien.getDanhSachPhanCong().size();
    }
    public List<PhanCongCa> layLich(LocalDate dau,LocalDate cuoi,String nhanVienLoc) {
        return dao.layLich(maDangNhap,dau,cuoi,nhanVienLoc);
    }
    private void kiemTra(String maNV,LocalDate ngay,String maCa) {
        if (maNV==null||maNV.isBlank()||maCa==null||maCa.isBlank()||ngay==null)
            throw new IllegalArgumentException("Phải chọn nhân viên, ngày và ca hợp lệ.");
    }
    public String them(String maNV,LocalDate ngay,String maCa) {
        kiemTra(maNV,ngay,maCa);return dao.them(maDangNhap,maNV,ngay,maCa);
    }
    public boolean capNhat(String id,String maNV,LocalDate ngay,String maCa) {
        kiemTra(maNV,ngay,maCa);
        if(id==null||id.isBlank())throw new IllegalArgumentException("Thiếu mã phân công.");
        return dao.capNhat(maDangNhap,id,maNV,ngay,maCa);
    }
    public boolean huy(String id) {
        if(id==null||id.isBlank())throw new IllegalArgumentException("Thiếu mã phân công.");
        return dao.huy(maDangNhap,id);
    }
}
