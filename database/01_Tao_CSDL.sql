-- SQL SERVER 2012+ (DUNG CHUNG VOI SQL SERVER 2022) | GIU NEN 01_Tao_CSDL.sql CUA NHOM TRUONG
-- CHINH SUA PHAM VI: SanPham, ThuongHieu, NhanVien, TaiKhoan, CaLamViec, PhanCongCa.
-- KHONG DROP DATABASE, KHONG DROP BANG, KHONG DI CHUYEN DU LIEU CU.
-- Chi chay tren database moi/chua co bang. Dung trong mot batch + TRANSACTION de tranh tao do.
IF DB_ID(N'QuanLyBanHang') IS NULL CREATE DATABASE QuanLyBanHang;
GO
USE QuanLyBanHang;
GO
SET XACT_ABORT ON;
IF EXISTS (SELECT 1 FROM sys.tables WHERE name IN
 (N'DanhMuc',N'ThuongHieu',N'NhanVien',N'TaiKhoan',N'CaLamViec',N'PhanCongCa',N'SanPham',N'DonHang',N'HoaDon'))
    THROW 51000, N'QuanLyBanHang da co bang. Khong chay de len CSDL cu; hay sao luu va su dung database trong.', 1;

BEGIN TRY
 BEGIN TRANSACTION;
-- Bay danh muc co dinh theo Bang 4.3 (enum DanhMucSP) trong bao cao Final.
-- Ma DM001..DM007 duoc su dung nhat quan trong 02_Du_Lieu_Demo.sql.
CREATE TABLE DanhMuc (
    maDM VARCHAR(20) NOT NULL PRIMARY KEY,
    tenDM NVARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT CK_DM_Dung7Nhom CHECK (
        (maDM = 'DM001' AND tenDM = N'Điện thoại') OR
        (maDM = 'DM002' AND tenDM = N'Laptop') OR
        (maDM = 'DM003' AND tenDM = N'Tivi') OR
        (maDM = 'DM004' AND tenDM = N'Tủ lạnh') OR
        (maDM = 'DM005' AND tenDM = N'Máy giặt') OR
        (maDM = 'DM006' AND tenDM = N'Máy lạnh') OR
        (maDM = 'DM007' AND tenDM = N'Phụ kiện điện tử')
    )
);
CREATE TABLE ThuongHieu (
 maTH VARCHAR(20) PRIMARY KEY,
 tenTH NVARCHAR(100) NOT NULL UNIQUE,
 moTaThuongHieu NVARCHAR(500) NULL,
 CONSTRAINT CK_TH_Ma CHECK (maTH LIKE 'TH[0-9][0-9][0-9]' AND LEN(maTH)=5),
 CONSTRAINT CK_TH_Ten CHECK (LEN(LTRIM(RTRIM(tenTH))) BETWEEN 2 AND 50)
);
CREATE TABLE KhachHang (maKH VARCHAR(20) PRIMARY KEY, tenKH NVARCHAR(100) NOT NULL, sdtKH VARCHAR(15) NOT NULL UNIQUE, diaChi NVARCHAR(255), diemTichLuy INT NOT NULL DEFAULT 0 CHECK(diemTichLuy>=0));
CREATE TABLE NhanVien (
 maNV VARCHAR(20) PRIMARY KEY,
 hoTen NVARCHAR(100) NOT NULL,
 sdtNV VARCHAR(15) NOT NULL,
 soCCCD VARCHAR(20) NOT NULL UNIQUE,
 ngaySinh DATE NOT NULL,
 ngayVaoLam DATE NOT NULL,
 diaChi NVARCHAR(255) NULL,
 dangLamViec BIT NOT NULL DEFAULT 1,
 CONSTRAINT CK_NV_HoTen CHECK (LEN(LTRIM(RTRIM(hoTen))) BETWEEN 2 AND 50),
 CONSTRAINT CK_NV_SDT CHECK (LEN(sdtNV)=10 AND sdtNV LIKE '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
 CONSTRAINT CK_NV_CCCD CHECK (LEN(soCCCD)=12 AND soCCCD NOT LIKE '%[^0-9]%')
);
-- Quy dinh NV + 4 chu so se kiem tra khi sinh ma moi trong Java.
-- Ma NV001..NV005 cua demo cu duoc giu de khong phai sua FK cua cac bang ban hang.
CREATE TABLE TaiKhoan (
 maTK VARCHAR(20) PRIMARY KEY,
 tenDangNhap VARCHAR(100) NOT NULL UNIQUE,
 matKhau VARCHAR(255) NOT NULL,
 vaiTro VARCHAR(30) NOT NULL,
 maNV VARCHAR(20) NOT NULL UNIQUE REFERENCES NhanVien(maNV),
 dangHoatDong BIT NOT NULL DEFAULT 1,
 CONSTRAINT CK_TK_VaiTro CHECK (vaiTro IN ('QUAN_LY','NHAN_VIEN_BAN_HANG')),
 CONSTRAINT CK_TK_TenDangNhap CHECK (LEN(LTRIM(RTRIM(tenDangNhap))) > 0)
);
-- Mat khau demo la gia tri danh dau, KHONG phai tai khoan co the dang nhap. Ung dung phai bam mat khau (BCrypt/Argon2).
CREATE TABLE CaLamViec (
 maCa VARCHAR(20) PRIMARY KEY,
 tenCa NVARCHAR(100) NOT NULL UNIQUE,
 gioBatDau TIME NOT NULL,
 gioKetThuc TIME NOT NULL,
 CONSTRAINT CK_Ca_ThoiGian CHECK (
  (maCa='CA001' AND gioBatDau='08:00:00' AND gioKetThuc='14:00:00') OR
  (maCa='CA002' AND gioBatDau='14:00:00' AND gioKetThuc='18:00:00') OR
  (maCa='CA003' AND gioBatDau='18:00:00' AND gioKetThuc='22:00:00')
 )
);
CREATE TABLE PhanCongCa (
 maPhanCong VARCHAR(20) PRIMARY KEY,
 ngayLam DATE NOT NULL,
 maNV VARCHAR(20) NOT NULL REFERENCES NhanVien(maNV),
 maCa VARCHAR(20) NOT NULL REFERENCES CaLamViec(maCa),
 trangThai VARCHAR(20) NOT NULL DEFAULT 'DA_PHAN_CONG',
 CONSTRAINT CK_PC_TrangThai CHECK(trangThai IN ('DA_PHAN_CONG','DA_HUY'))
);
-- Cho phep huy ca va phan cong lai ca do; khong trung lich dang hieu luc.
CREATE UNIQUE INDEX UX_PC_CaHieuLuc ON PhanCongCa(ngayLam,maNV,maCa)
WHERE trangThai='DA_PHAN_CONG';
CREATE TABLE SanPham (
 maSP VARCHAR(20) PRIMARY KEY, tenSP NVARCHAR(150) NOT NULL,
 maDM VARCHAR(20) NOT NULL REFERENCES DanhMuc(maDM), maTH VARCHAR(20) NOT NULL REFERENCES ThuongHieu(maTH),
 thongSoKyThuat NVARCHAR(MAX), giaNiemYet DECIMAL(18,2) NOT NULL CHECK(giaNiemYet>0),
 model NVARCHAR(100), donViTinh NVARCHAR(30) NOT NULL, moTaSP NVARCHAR(500),
 tinhTrang VARCHAR(30) NOT NULL CHECK(tinhTrang IN ('DANG_KINH_DOANH','NGUNG_KINH_DOANH','HET_HANG')),
 soLuongTon INT NOT NULL DEFAULT 0 CHECK(soLuongTon>=0),
 soLuongLoi INT NOT NULL DEFAULT 0 CHECK(soLuongLoi>=0),
 soLuongTonToiThieu INT NOT NULL DEFAULT 0 CHECK(soLuongTonToiThieu>=0),
 hinhAnh NVARCHAR(500),
 CONSTRAINT CK_SP_Ten CHECK(LEN(LTRIM(RTRIM(tenSP))) BETWEEN 2 AND 100),
 CONSTRAINT CK_SP_DonVi CHECK(LEN(LTRIM(RTRIM(donViTinh)))>0),
 CONSTRAINT CK_SP_HangLoi CHECK(soLuongLoi<=soLuongTon)
);
-- soLuongCoTheBan = soLuongTon - soLuongLoi theo dac ta Class SanPham.
-- soLuongTonToiThieu giu lai de tuong thich demo cua nhom truong.

-- Audit UC002: luu cac thay doi san pham, ke ca san pham duoc xoa hop le.
-- Khong FK maSP de giu duoc lich su neu san pham chua co giao dich bi xoa.
CREATE TABLE dbo.LichSuSanPham (
 maLichSu BIGINT IDENTITY(1,1) PRIMARY KEY,
 maSP VARCHAR(20) NOT NULL,
 hanhDong VARCHAR(10) NOT NULL CHECK(hanhDong IN ('THEM','SUA','XOA')),
 maNV VARCHAR(20) NULL,
 thoiDiem DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
 duLieuCu NVARCHAR(MAX) NULL,
 duLieuMoi NVARCHAR(MAX) NULL
);
CREATE TABLE KhuyenMai (
 maKM VARCHAR(20) PRIMARY KEY, tenKM NVARCHAR(150) NOT NULL,
 phanTramGiam DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK(phanTramGiam BETWEEN 0 AND 100),
 ngayBatDau DATETIME2 NOT NULL, ngayKetThuc DATETIME2 NOT NULL,
 moTaKM NVARCHAR(500), loaiGiam VARCHAR(20) NOT NULL CHECK(loaiGiam IN ('PHAN_TRAM','SO_TIEN')),
 soTienGiam DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK(soTienGiam>=0),
 CHECK(ngayBatDau<=ngayKetThuc)
);
CREATE TABLE SanPhamKhuyenMai (
 maSP VARCHAR(20) NOT NULL REFERENCES SanPham(maSP),
 maKM VARCHAR(20) NOT NULL REFERENCES KhuyenMai(maKM),
 CONSTRAINT PK_SanPhamKhuyenMai PRIMARY KEY(maSP,maKM)
);
CREATE TABLE DonHang (
 maDH VARCHAR(20) PRIMARY KEY, ngayDat DATETIME2 NOT NULL,
 trangThaiDH VARCHAR(30) NOT NULL CHECK(trangThaiDH IN ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_XU_LY','DANG_GIAO','HOAN_THANH','DA_HUY')),
 ghiChu NVARCHAR(500), maKH VARCHAR(20) NOT NULL REFERENCES KhachHang(maKH),
 maNV VARCHAR(20) NOT NULL REFERENCES NhanVien(maNV),
 CONSTRAINT UQ_DonHang_maDH_maKH UNIQUE (maDH,maKH)
);
CREATE TABLE ChiTietDonHang (
 idChiTietDH INT IDENTITY(1,1) PRIMARY KEY, maDH VARCHAR(20) NOT NULL REFERENCES DonHang(maDH),
 maSP VARCHAR(20) NOT NULL REFERENCES SanPham(maSP), soLuong INT NOT NULL CHECK(soLuong>0),
 donGia DECIMAL(18,2) NOT NULL CHECK(donGia>=0), serialIMEI VARCHAR(100),
 UNIQUE(maDH,maSP)
);
CREATE TABLE HoaDon (
 maHD VARCHAR(20) PRIMARY KEY, ngayLap DATETIME2 NOT NULL,
 phuongThucThanhToan VARCHAR(30) NOT NULL CHECK(phuongThucThanhToan IN ('TIEN_MAT','CHUYEN_KHOAN','THE_NGAN_HANG','VI_DIEN_TU')),
 maKH VARCHAR(20) NOT NULL REFERENCES KhachHang(maKH), maNV VARCHAR(20) NOT NULL REFERENCES NhanVien(maNV),
 maDH VARCHAR(20) NULL,
 CONSTRAINT FK_HoaDon_DonHangKhachHang FOREIGN KEY(maDH,maKH) REFERENCES DonHang(maDH,maKH)
);
-- Cho phep hoa don ban tai quay (maDH NULL), mot don hang toi da mot hoa don.
CREATE UNIQUE INDEX UX_HoaDon_maDH ON HoaDon(maDH) WHERE maDH IS NOT NULL;
CREATE TABLE ChiTietHoaDon (
 idChiTietHD INT IDENTITY(1,1) PRIMARY KEY,
 maHD VARCHAR(20) NOT NULL REFERENCES HoaDon(maHD), maSP VARCHAR(20) NOT NULL REFERENCES SanPham(maSP),
 maKM VARCHAR(20) NULL, soLuong INT NOT NULL CHECK(soLuong>0),
 donGia DECIMAL(18,2) NOT NULL CHECK(donGia>=0), serialIMEI VARCHAR(100),
 soTienGiam DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK(soTienGiam>=0),
 CONSTRAINT CK_CTHD_GiamToiDa CHECK(soTienGiam<=soLuong*donGia),
 CONSTRAINT FK_CTHD_SanPhamKhuyenMai FOREIGN KEY(maSP,maKM) REFERENCES SanPhamKhuyenMai(maSP,maKM),
 CONSTRAINT UQ_CTHD_maHD_maSP UNIQUE(maHD,maSP),
 CONSTRAINT UQ_CTHD_Id_maHD_maSP UNIQUE(idChiTietHD,maHD,maSP)
);
-- Khi maKM NULL, FK ghep khong kiem tra (chi tiet khong giam gia la hop le).
CREATE TABLE PhieuDoiTra (
 maPhieuDoiTra VARCHAR(20) PRIMARY KEY, ngayLap DATETIME2 NOT NULL,
 trangThaiDuyet VARCHAR(20) NOT NULL CHECK(trangThaiDuyet IN ('CHO_DUYET','DA_DUYET','TU_CHOI')),
 maHD VARCHAR(20) NOT NULL REFERENCES HoaDon(maHD),
 maNV VARCHAR(20) NOT NULL REFERENCES NhanVien(maNV),
 CONSTRAINT UQ_PhieuDoiTra_Phieu_HD UNIQUE(maPhieuDoiTra,maHD)
);
CREATE TABLE ChiTietPhieuDoiTra (
 idChiTietDoiTra INT IDENTITY(1,1) PRIMARY KEY,
 maPhieuDoiTra VARCHAR(20) NOT NULL, maHD VARCHAR(20) NOT NULL,
 idChiTietHD INT NOT NULL, maSP VARCHAR(20) NOT NULL,
 soLuong INT NOT NULL CHECK(soLuong>0), donGia DECIMAL(18,2) NOT NULL CHECK(donGia>=0),
 serialIMEI VARCHAR(100), lyDo NVARCHAR(500),
 tinhTrangKhiDoiTra VARCHAR(30) NOT NULL CHECK(tinhTrangKhiDoiTra IN ('CON_NGUYEN_VEN','LOI_KY_THUAT','HU_HONG')),
 hinhThucXuLy VARCHAR(30) NOT NULL CHECK(hinhThucXuLy IN ('HOAN_TIEN','DOI_SAN_PHAM','BAO_HANH')),
 CONSTRAINT FK_CTPDT_Phieu_HD FOREIGN KEY(maPhieuDoiTra,maHD) REFERENCES PhieuDoiTra(maPhieuDoiTra,maHD),
 CONSTRAINT FK_CTPDT_ChiTietHoaDon FOREIGN KEY(idChiTietHD,maHD,maSP) REFERENCES ChiTietHoaDon(idChiTietHD,maHD,maSP),
 UNIQUE(maPhieuDoiTra,idChiTietHD)
);

-- Triggers chi lien quan SanPham / NhanVien / PhanCongCa.
-- SQL2012: audit duLieuCu/duLieuMoi la XML (chuoi NVARCHAR(MAX)), thay cho JSON SQL2016+.
-- maNV tu ung dung duoc truyen qua CONTEXT_INFO(), ho tro SQL Server 2012 va 2022.
-- Dung EXEC de giu toan bo bang va trigger trong cung giao dich DDL.
EXEC sys.sp_executesql N'CREATE TRIGGER dbo.TR_SanPham_LichSu ON dbo.SanPham
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
 SET NOCOUNT ON;
 -- Ma SP khong duoc doi sau khi tao.
 IF UPDATE(maSP)
    AND EXISTS (SELECT 1 FROM inserted)
    AND EXISTS (SELECT 1 FROM deleted)
    AND EXISTS (
   SELECT 1 FROM inserted i FULL JOIN deleted d ON i.maSP=d.maSP
   WHERE i.maSP IS NULL OR d.maSP IS NULL
 ) THROW 51011, N''Ma san pham khong duoc thay doi sau khi tao.'', 1;

 INSERT INTO dbo.LichSuSanPham(maSP,hanhDong,maNV,duLieuCu,duLieuMoi)
 SELECT COALESCE(i.maSP,d.maSP),
        CASE WHEN d.maSP IS NULL THEN ''THEM''
             WHEN i.maSP IS NULL THEN ''XOA'' ELSE ''SUA'' END,
        CASE WHEN CONTEXT_INFO() IS NULL OR SUBSTRING(CONTEXT_INFO(),1,1)=0x00
             THEN NULL ELSE RTRIM(CONVERT(VARCHAR(20), CONTEXT_INFO())) END,
        CASE WHEN d.maSP IS NULL THEN NULL ELSE
          CONVERT(NVARCHAR(MAX), (SELECT d.maSP,d.tenSP,d.maDM,d.maTH,d.thongSoKyThuat,d.giaNiemYet,
                  d.model,d.donViTinh,d.moTaSP,d.tinhTrang,d.soLuongTon,
                  d.soLuongLoi,d.soLuongTonToiThieu,d.hinhAnh
             FOR XML PATH(''SanPham''), TYPE)) END,
        CASE WHEN i.maSP IS NULL THEN NULL ELSE
          CONVERT(NVARCHAR(MAX), (SELECT i.maSP,i.tenSP,i.maDM,i.maTH,i.thongSoKyThuat,i.giaNiemYet,
                  i.model,i.donViTinh,i.moTaSP,i.tinhTrang,i.soLuongTon,
                  i.soLuongLoi,i.soLuongTonToiThieu,i.hinhAnh
             FOR XML PATH(''SanPham''), TYPE)) END
 FROM inserted i FULL JOIN deleted d ON i.maSP=d.maSP;
END';
EXEC sys.sp_executesql N'CREATE TRIGGER dbo.TR_NhanVien_HopLe ON dbo.NhanVien
AFTER INSERT, UPDATE
AS
BEGIN
 SET NOCOUNT ON;
 IF EXISTS (
   SELECT 1 FROM inserted
   WHERE ngaySinh > DATEADD(YEAR,-18,CONVERT(date,GETDATE()))
      OR ngayVaoLam > CONVERT(date,GETDATE())
 ) THROW 51020, N''Nhan vien phai du 18 tuoi va ngay vao lam khong duoc o tuong lai.'', 1;
END';
EXEC sys.sp_executesql N'CREATE TRIGGER dbo.TR_NhanVien_NghiViec ON dbo.NhanVien
AFTER UPDATE
AS
BEGIN
 SET NOCOUNT ON;
 IF NOT UPDATE(dangLamViec) RETURN;
 -- Khong xoa ho so, chi khoa tai khoan va huy ca tu hien tai.
 UPDATE tk SET dangHoatDong=0
 FROM dbo.TaiKhoan tk
 JOIN inserted i ON tk.maNV=i.maNV
 JOIN deleted d ON d.maNV=i.maNV
 WHERE d.dangLamViec=1 AND i.dangLamViec=0;

 UPDATE pc SET trangThai=''DA_HUY''
 FROM dbo.PhanCongCa pc
 JOIN inserted i ON pc.maNV=i.maNV
 JOIN deleted d ON d.maNV=i.maNV
 WHERE d.dangLamViec=1 AND i.dangLamViec=0
   AND pc.trangThai=''DA_PHAN_CONG''
   AND pc.ngayLam>=CONVERT(date,GETDATE());
END';
EXEC sys.sp_executesql N'CREATE TRIGGER dbo.TR_PhanCongCa_KiemTra ON dbo.PhanCongCa
AFTER INSERT, UPDATE
AS
BEGIN
 SET NOCOUNT ON;
 -- Khong phan cong cho nhan vien da nghi/khong duoc truy cap.
 IF EXISTS (
  SELECT 1 FROM inserted i
  JOIN dbo.NhanVien nv ON nv.maNV=i.maNV
  LEFT JOIN dbo.TaiKhoan tk ON tk.maNV=i.maNV
  WHERE i.trangThai=''DA_PHAN_CONG''
    AND (nv.dangLamViec=0 OR tk.maTK IS NULL OR tk.dangHoatDong=0)
 ) THROW 51030, N''Khong the phan cong ca cho nhan vien da nghi hoac tai khoan bi khoa.'', 1;

 -- Dieu kien giao thoa: bat dau ca A < ket thuc ca B va nguoc lai.
 IF EXISTS (
  SELECT 1 FROM inserted i
  JOIN dbo.CaLamViec ca ON ca.maCa=i.maCa
  JOIN dbo.PhanCongCa pc ON pc.maNV=i.maNV AND pc.ngayLam=i.ngayLam
                          AND pc.maPhanCong<>i.maPhanCong
                          AND pc.trangThai=''DA_PHAN_CONG''
  JOIN dbo.CaLamViec caKhac ON caKhac.maCa=pc.maCa
  WHERE i.trangThai=''DA_PHAN_CONG''
    AND ca.gioBatDau < caKhac.gioKetThuc
    AND caKhac.gioBatDau < ca.gioKetThuc
 ) THROW 51031, N''Nhan vien da co ca trung khoang thoi gian.'', 1;
END';

 COMMIT TRANSACTION;
 PRINT N'Tao schema QuanLyBanHang thanh cong. Tiep theo chay 02_Du_Lieu_Demo.sql.';
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK TRANSACTION;
 THROW;
END CATCH;
GO
