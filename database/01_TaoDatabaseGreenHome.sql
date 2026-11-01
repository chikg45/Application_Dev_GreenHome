-- GreenHome cho T.Tai: schema theo DOCX và Entity chung; không có dữ liệu mẫu.
-- soLuongTon = số lượng sẵn bán; soLuongLoi là lượng lỗi riêng, không trừ lần hai.
-- Đơn nháp giữ lượng qua CHI_TIET_DON_HANG. RCSI tránh khóa đọc các đơn khác.
-- Chạy toàn file bằng SSMS trên database riêng GreenHome_TTai.
-- Không dùng file này làm migration của schema cũ GreenHome/GreenHome_BanHang.
USE master;
GO
IF DB_ID(N'GreenHome_TTai') IS NULL
    EXEC(N'CREATE DATABASE GreenHome_TTai');
GO
USE GreenHome_TTai;
GO
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;
GO
-- Khi gặp schema khác, NOEXEC ngăn SSMS chạy tiếp các batch phía sau.
IF EXISTS(SELECT 1 FROM sys.tables) AND OBJECT_ID(N'dbo.GH_SCHEMA_VERSION',N'U') IS NULL
BEGIN
    RAISERROR(N'Database đã có schema khác. Hãy dùng database trống hoặc đối chiếu trước khi chạy.',16,1);
    SET NOEXEC ON;
END;
GO
IF OBJECT_ID(N'dbo.GH_SCHEMA_VERSION',N'U') IS NOT NULL
BEGIN
    IF NOT EXISTS(SELECT 1 FROM dbo.GH_SCHEMA_VERSION WHERE phienBan=2)
    BEGIN
        RAISERROR(N'Schema phiên bản cũ hoặc không tương thích. Cần migration riêng, không chạy tiếp.',16,1);
        SET NOEXEC ON;
    END;
END;
GO
IF NOT EXISTS(SELECT 1 FROM sys.databases WHERE name=N'GreenHome_TTai' AND is_read_committed_snapshot_on=1)
    ALTER DATABASE GreenHome_TTai SET READ_COMMITTED_SNAPSHOT ON;
GO
IF OBJECT_ID(N'dbo.NHAN_VIEN',N'U') IS NULL
BEGIN
CREATE TABLE dbo.NHAN_VIEN(
maNV varchar(20) NOT NULL PRIMARY KEY,
 hoTen nvarchar(100) NOT NULL,
 sdtNV varchar(15) NOT NULL,
 soCCCD varchar(15) NOT NULL UNIQUE,
 ngaySinh date NOT NULL,
 ngayVaoLam date NOT NULL,
 diaChi nvarchar(255) NULL,
 CONSTRAINT CK_NV_Ten CHECK(LEN(LTRIM(RTRIM(hoTen))) BETWEEN 2 AND 50),
 CONSTRAINT CK_NV_SDT CHECK(LEN(sdtNV)=10 AND sdtNV NOT LIKE '%[^0-9]%' AND LEFT(sdtNV,1)='0'),
 CONSTRAINT CK_NV_CCCD CHECK(LEN(soCCCD)=12 AND soCCCD NOT LIKE '%[^0-9]%'),
 CONSTRAINT CK_NV_Tuoi CHECK(ngaySinh<=DATEADD(year,-18,CONVERT(date,GETDATE()))),
 CONSTRAINT CK_NV_NgayVao CHECK(ngayVaoLam<=CONVERT(date,GETDATE()))
);
END;
GO

IF OBJECT_ID(N'dbo.TAI_KHOAN',N'U') IS NULL
BEGIN
CREATE TABLE dbo.TAI_KHOAN(
maTK varchar(20) NOT NULL PRIMARY KEY,
 maNV varchar(20) NOT NULL UNIQUE REFERENCES dbo.NHAN_VIEN(maNV) ON DELETE CASCADE,
 tenDangNhap varchar(50) NOT NULL UNIQUE,
 matKhau varchar(255) NOT NULL,
 vaiTro varchar(20) NOT NULL CHECK(vaiTro IN('QuanLy','NhanVienBanHang')),
 dangHoatDong bit NOT NULL CONSTRAINT DF_TK_Active DEFAULT 1
);
END;
GO

IF OBJECT_ID(N'dbo.CA_LAM_VIEC',N'U') IS NULL
BEGIN
CREATE TABLE dbo.CA_LAM_VIEC(
maCa varchar(20) NOT NULL PRIMARY KEY,
 tenCa nvarchar(50) NOT NULL,
 gioBatDau time NOT NULL,
 gioKetThuc time NOT NULL,
 CONSTRAINT CK_Ca_ThoiGian CHECK(gioKetThuc>gioBatDau)
);
END;
GO

IF OBJECT_ID(N'dbo.PHAN_CONG_CA',N'U') IS NULL
BEGIN
CREATE TABLE dbo.PHAN_CONG_CA(
maPhanCong varchar(20) NOT NULL PRIMARY KEY,
 maNV varchar(20) NOT NULL REFERENCES dbo.NHAN_VIEN(maNV),
 maCa varchar(20) NOT NULL REFERENCES dbo.CA_LAM_VIEC(maCa),
 ngayLam date NOT NULL,
 CONSTRAINT UQ_PhanCong UNIQUE(maNV,maCa,ngayLam)
);
END;
GO

IF OBJECT_ID(N'dbo.KHACH_HANG',N'U') IS NULL
BEGIN
CREATE TABLE dbo.KHACH_HANG(
maKH varchar(20) NOT NULL PRIMARY KEY,
 tenKH nvarchar(100) NOT NULL,
 sdtKH varchar(15) NULL,
 diaChi nvarchar(255) NULL,
 diemTichLuy int NOT NULL CONSTRAINT DF_KH_Diem DEFAULT 0 CHECK(diemTichLuy>=0),
 email nvarchar(150) NULL,
 CONSTRAINT CK_KH_Ten CHECK(LEN(LTRIM(RTRIM(tenKH))) BETWEEN 2 AND 50),
 CONSTRAINT CK_KH_SDT CHECK(sdtKH IS NULL OR (LEN(sdtKH)=10 AND sdtKH NOT LIKE '%[^0-9]%' AND LEFT(sdtKH,1)='0'))
);
END;
GO

IF OBJECT_ID(N'dbo.THUONG_HIEU',N'U') IS NULL
BEGIN
CREATE TABLE dbo.THUONG_HIEU(
maThuongHieu varchar(20) NOT NULL PRIMARY KEY,
 tenThuongHieu nvarchar(100) NOT NULL UNIQUE
);
END;
GO

IF OBJECT_ID(N'dbo.SAN_PHAM',N'U') IS NULL
BEGIN
CREATE TABLE dbo.SAN_PHAM(
maSP varchar(20) NOT NULL PRIMARY KEY,
 maThuongHieu varchar(20) NOT NULL REFERENCES dbo.THUONG_HIEU(maThuongHieu),
 tenSP nvarchar(150) NOT NULL,
 danhMuc nvarchar(50) NOT NULL,
 hinhAnh varchar(255) NULL,
 thongSoKyThuat nvarchar(max) NULL,
 giaNiemYet decimal(18,2) NOT NULL CHECK(giaNiemYet>0 AND giaNiemYet=ROUND(giaNiemYet,0)),
 model varchar(50) NULL,
 donViTinh nvarchar(20) NOT NULL CONSTRAINT DF_SP_DVT DEFAULT N'Cái',
 moTaSP nvarchar(max) NULL,
 tinhTrang nvarchar(50) NOT NULL CHECK(tinhTrang IN(N'DangBan',N'HetHang',N'NgungBan')),
 soLuongTon int NOT NULL CONSTRAINT DF_SP_Ton DEFAULT 0 CHECK(soLuongTon>=0),
 soLuongTonToiThieu int NOT NULL CONSTRAINT DF_SP_ToiThieu DEFAULT 0 CHECK(soLuongTonToiThieu>=0),
 soLuongLoi int NOT NULL CONSTRAINT DF_SP_Loi DEFAULT 0 CHECK(soLuongLoi>=0),
 quanLySerial bit NOT NULL CONSTRAINT DF_SP_Serial DEFAULT 0,
 CONSTRAINT CK_SP_Ten CHECK(LEN(LTRIM(RTRIM(tenSP))) BETWEEN 2 AND 100),
 CONSTRAINT CK_SP_DanhMuc CHECK(danhMuc IN(N'Điện thoại',N'Laptop',N'Tivi',N'Tủ lạnh',N'Máy giặt',N'Máy lạnh',N'Phụ kiện')),
 CONSTRAINT CK_SP_TrangThai CHECK(tinhTrang=N'NgungBan' OR (tinhTrang=N'HetHang' AND soLuongTon=0) OR (tinhTrang=N'DangBan' AND soLuongTon>0))
);
END;
GO

IF OBJECT_ID(N'dbo.KHUYEN_MAI',N'U') IS NULL
BEGIN
CREATE TABLE dbo.KHUYEN_MAI(
maKM varchar(20) NOT NULL PRIMARY KEY,
 tenKM nvarchar(100) NOT NULL,
 loaiGiam nvarchar(50) NOT NULL CHECK(loaiGiam IN(N'PhanTram',N'SoTien')),
 phanTramGiam decimal(5,2) NOT NULL CONSTRAINT DF_KM_PT DEFAULT 0 CHECK(phanTramGiam BETWEEN 0 AND 100),
 soTienGiam decimal(18,2) NOT NULL CONSTRAINT DF_KM_Tien DEFAULT 0 CHECK(soTienGiam>=0 AND soTienGiam=ROUND(soTienGiam,0)),
 ngayBatDau datetime2(3) NOT NULL,
 ngayKetThuc datetime2(3) NOT NULL,
 moTaKM nvarchar(255) NULL,
 dangApDung bit NOT NULL CONSTRAINT DF_KM_Active DEFAULT 1,
 CONSTRAINT CK_KM_ThoiGian CHECK(ngayKetThuc>=ngayBatDau),
 CONSTRAINT CK_KM_GiaTri CHECK((loaiGiam=N'PhanTram' AND phanTramGiam>0) OR (loaiGiam=N'SoTien' AND soTienGiam>0))
);
END;
GO

IF OBJECT_ID(N'dbo.KHUYEN_MAI_SAN_PHAM',N'U') IS NULL
BEGIN
CREATE TABLE dbo.KHUYEN_MAI_SAN_PHAM(
maKM varchar(20) NOT NULL REFERENCES dbo.KHUYEN_MAI(maKM) ON DELETE CASCADE,
 maSP varchar(20) NOT NULL REFERENCES dbo.SAN_PHAM(maSP) ON DELETE CASCADE,
 CONSTRAINT PK_KM_SP PRIMARY KEY(maKM,maSP)
);
END;
GO

IF OBJECT_ID(N'dbo.DON_HANG',N'U') IS NULL
BEGIN
CREATE TABLE dbo.DON_HANG(
maDH varchar(20) NOT NULL PRIMARY KEY,
 maNV varchar(20) NOT NULL REFERENCES dbo.NHAN_VIEN(maNV),
 maKH varchar(20) NULL REFERENCES dbo.KHACH_HANG(maKH),
 ngayDat datetime2(3) NOT NULL CONSTRAINT DF_DH_Ngay DEFAULT SYSDATETIME(),
 trangThaiDH nvarchar(50) NOT NULL CONSTRAINT DF_DH_TT DEFAULT N'ChuaThanhToan' CHECK(trangThaiDH IN(N'ChuaThanhToan',N'DaThanhToan',N'DaHuy')),
 ghiChu nvarchar(255) NULL,
 phienBan int NOT NULL CONSTRAINT DF_DH_Version DEFAULT 0 CHECK(phienBan>=0)
);
END;
GO

IF OBJECT_ID(N'dbo.CHI_TIET_DON_HANG',N'U') IS NULL
BEGIN
CREATE TABLE dbo.CHI_TIET_DON_HANG(
maDH varchar(20) NOT NULL REFERENCES dbo.DON_HANG(maDH) ON DELETE CASCADE,
 maSP varchar(20) NOT NULL REFERENCES dbo.SAN_PHAM(maSP),
 soLuong int NOT NULL CHECK(soLuong>0),
 donGia decimal(18,2) NOT NULL CHECK(donGia>0 AND donGia=ROUND(donGia,0)),
 serialIMEI varchar(50) NULL,
 CONSTRAINT PK_CT_DH PRIMARY KEY(maDH,maSP)
);
END;
GO

IF OBJECT_ID(N'dbo.HOA_DON',N'U') IS NULL
BEGIN
CREATE TABLE dbo.HOA_DON(
maHD varchar(20) NOT NULL PRIMARY KEY,
 maDH varchar(20) NOT NULL UNIQUE REFERENCES dbo.DON_HANG(maDH),
 maNV varchar(20) NOT NULL REFERENCES dbo.NHAN_VIEN(maNV),
 maKH varchar(20) NULL REFERENCES dbo.KHACH_HANG(maKH),
 ngayLap datetime2(3) NOT NULL CONSTRAINT DF_HD_Ngay DEFAULT SYSDATETIME(),
 phuongThucThanhToan nvarchar(50) NOT NULL CHECK(phuongThucThanhToan IN(N'TienMat',N'ChuyenKhoan')),
 tenKhachHangLucBan nvarchar(100) NOT NULL,
 sdtKhachHangLucBan varchar(15) NULL,
 tenNhanVienLucBan nvarchar(100) NOT NULL,
 diemDaDung int NOT NULL CHECK(diemDaDung>=0),
 diemDaCong int NOT NULL CHECK(diemDaCong>=0),
 soDuDiemSauGD int NOT NULL CHECK(soDuDiemSauGD>=0),
 tienKhachDua decimal(18,2) NOT NULL CHECK(tienKhachDua>=0),
 tienThua decimal(18,2) NOT NULL CHECK(tienThua>=0),
 CONSTRAINT CK_HD_VangLai CHECK(maKH IS NOT NULL OR (diemDaDung=0 AND diemDaCong=0 AND soDuDiemSauGD=0))
);
END;
GO

IF OBJECT_ID(N'dbo.CHI_TIET_HOA_DON',N'U') IS NULL
BEGIN
CREATE TABLE dbo.CHI_TIET_HOA_DON(
maHD varchar(20) NOT NULL REFERENCES dbo.HOA_DON(maHD) ON DELETE CASCADE,
 maSP varchar(20) NOT NULL REFERENCES dbo.SAN_PHAM(maSP),
 tenSP nvarchar(150) NOT NULL,
 soLuong int NOT NULL CHECK(soLuong>0),
 donGia decimal(18,2) NOT NULL CHECK(donGia>0 AND donGia=ROUND(donGia,0)),
 serialIMEI varchar(50) NULL,
 soTienGiam decimal(18,2) NOT NULL CONSTRAINT DF_CTHD_Giam DEFAULT 0 CHECK(soTienGiam>=0),
 maKM varchar(20) NULL REFERENCES dbo.KHUYEN_MAI(maKM),
 giamKhuyenMai decimal(18,2) NOT NULL CHECK(giamKhuyenMai>=0),
 giamDiem decimal(18,2) NOT NULL CHECK(giamDiem>=0),
 loaiGiamApDung nvarchar(50) NULL,
 giaTriGiamApDung decimal(18,2) NULL,
 giaChuaThue decimal(18,2) NOT NULL CHECK(giaChuaThue>=0),
 tienVAT decimal(18,2) NOT NULL CHECK(tienVAT>=0),
 CONSTRAINT PK_CT_HD PRIMARY KEY(maHD,maSP),
 CONSTRAINT CK_CTHD_Giam CHECK(soTienGiam=giamKhuyenMai+giamDiem AND soTienGiam<=soLuong*donGia),
 CONSTRAINT CK_CTHD_VAT CHECK(giaChuaThue+tienVAT=soLuong*donGia)
);
END;
GO

IF OBJECT_ID(N'dbo.PHIEU_DOI_TRA',N'U') IS NULL
BEGIN
CREATE TABLE dbo.PHIEU_DOI_TRA(
maPhieuDoiTra varchar(20) NOT NULL PRIMARY KEY,
 maHD varchar(20) NOT NULL REFERENCES dbo.HOA_DON(maHD),
 maNV varchar(20) NOT NULL REFERENCES dbo.NHAN_VIEN(maNV),
 ngayLap datetime2(3) NOT NULL CONSTRAINT DF_DT_Ngay DEFAULT SYSDATETIME(),
 lyDo nvarchar(255) NULL,
 tinhTrangKhiDoiTra nvarchar(255) NULL,
 hinhThucXuLy nvarchar(100) NULL,
 phuongThucHoanTien nvarchar(100) NULL,
 trangThaiDuyet varchar(20) NOT NULL CONSTRAINT DF_DT_TT DEFAULT 'ChoDuyet' CHECK(trangThaiDuyet IN('ChoDuyet','DaDuyet','TuChoi','HoanTat')),
 CONSTRAINT UQ_DT_HD UNIQUE(maPhieuDoiTra,maHD)
);
END;
GO

IF OBJECT_ID(N'dbo.CHI_TIET_PHIEU_DOI_TRA',N'U') IS NULL
BEGIN
CREATE TABLE dbo.CHI_TIET_PHIEU_DOI_TRA(
maPhieuDoiTra varchar(20) NOT NULL,
 maHD varchar(20) NOT NULL,
 maSP varchar(20) NOT NULL,
 soLuong int NOT NULL CHECK(soLuong>0),
 donGia decimal(18,2) NOT NULL CHECK(donGia>0),
 serialIMEI varchar(50) NULL,
 lyDo nvarchar(255) NULL,
 tinhTrangKhiDoiTra nvarchar(50) NULL,
 hinhThucXuLy nvarchar(50) NULL,
 thanhTienHoan decimal(18,2) NULL CHECK(thanhTienHoan>=0),
 CONSTRAINT PK_CT_DT PRIMARY KEY(maPhieuDoiTra,maHD,maSP),
 CONSTRAINT FK_CTDT_Phieu FOREIGN KEY(maPhieuDoiTra,maHD) REFERENCES dbo.PHIEU_DOI_TRA(maPhieuDoiTra,maHD) ON DELETE CASCADE,
 CONSTRAINT FK_CTDT_CT_HD FOREIGN KEY(maHD,maSP) REFERENCES dbo.CHI_TIET_HOA_DON(maHD,maSP)
);
END;
GO

IF OBJECT_ID(N'dbo.UU_DAI_CA_NHAN',N'U') IS NULL
BEGIN
CREATE TABLE dbo.UU_DAI_CA_NHAN(
maKM varchar(20) NOT NULL REFERENCES dbo.KHUYEN_MAI(maKM) ON DELETE CASCADE,
 maKH varchar(20) NOT NULL REFERENCES dbo.KHACH_HANG(maKH),
 CONSTRAINT PK_UuDai PRIMARY KEY(maKM,maKH)
);
END;
GO

IF OBJECT_ID(N'dbo.LICH_SU_DIEM',N'U') IS NULL
BEGIN
CREATE TABLE dbo.LICH_SU_DIEM(
maLichSu bigint IDENTITY(1,1) NOT NULL PRIMARY KEY,
 maKH varchar(20) NOT NULL REFERENCES dbo.KHACH_HANG(maKH),
 maHD varchar(20) NOT NULL REFERENCES dbo.HOA_DON(maHD),
 thoiDiem datetime2(3) NOT NULL CONSTRAINT DF_LSD_Ngay DEFAULT SYSDATETIME(),
 loaiGiaoDich varchar(20) NOT NULL CHECK(loaiGiaoDich IN('SuDung','TichLuy','DieuChinh')),
 bienDong int NOT NULL CHECK(bienDong<>0),
 soDuSau int NOT NULL CHECK(soDuSau>=0),
 CONSTRAINT UQ_Diem_HD UNIQUE(maHD,loaiGiaoDich)
);
END;
GO

IF OBJECT_ID(N'dbo.THIET_BI_SERIAL',N'U') IS NULL
BEGIN
CREATE TABLE dbo.THIET_BI_SERIAL(
serialIMEI varchar(50) NOT NULL PRIMARY KEY,
 maSP varchar(20) NOT NULL REFERENCES dbo.SAN_PHAM(maSP),
 tinhTrang varchar(20) NOT NULL CONSTRAINT DF_Serial_TT DEFAULT 'SanBan' CHECK(tinhTrang IN('SanBan','DaBan','Loi')),
 maHD varchar(20) NULL,
 CONSTRAINT UQ_Serial_SP UNIQUE(serialIMEI,maSP),
 CONSTRAINT FK_Serial_HD FOREIGN KEY(maHD,maSP) REFERENCES dbo.CHI_TIET_HOA_DON(maHD,maSP),
 CONSTRAINT CK_Serial_HD CHECK((tinhTrang='DaBan' AND maHD IS NOT NULL) OR (tinhTrang<>'DaBan' AND maHD IS NULL))
);
END;
GO

IF OBJECT_ID(N'dbo.SERIAL_HOA_DON',N'U') IS NULL
BEGIN
CREATE TABLE dbo.SERIAL_HOA_DON(
maHD varchar(20) NOT NULL,
 maSP varchar(20) NOT NULL,
 serialIMEI varchar(50) NOT NULL,
 CONSTRAINT PK_Serial_HD PRIMARY KEY(maHD,serialIMEI),
 CONSTRAINT FK_SHD_CT FOREIGN KEY(maHD,maSP) REFERENCES dbo.CHI_TIET_HOA_DON(maHD,maSP),
 CONSTRAINT FK_SHD_Serial FOREIGN KEY(serialIMEI,maSP) REFERENCES dbo.THIET_BI_SERIAL(serialIMEI,maSP)
);
END;
GO

IF OBJECT_ID(N'dbo.GH_SCHEMA_VERSION',N'U') IS NULL
CREATE TABLE dbo.GH_SCHEMA_VERSION(phienBan int NOT NULL PRIMARY KEY,ngayTao datetime2(3) NOT NULL DEFAULT SYSDATETIME());
GO
IF NOT EXISTS(SELECT 1 FROM dbo.GH_SCHEMA_VERSION WHERE phienBan=2) INSERT dbo.GH_SCHEMA_VERSION(phienBan) VALUES(2);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.KHACH_HANG') AND name=N'UX_KH_SDT')
CREATE UNIQUE INDEX UX_KH_SDT ON dbo.KHACH_HANG(sdtKH) WHERE sdtKH IS NOT NULL;
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.THIET_BI_SERIAL') AND name=N'IX_Serial_Tim')
CREATE INDEX IX_Serial_Tim ON dbo.THIET_BI_SERIAL(maSP,tinhTrang,serialIMEI);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.HOA_DON') AND name=N'IX_HD_Ngay')
CREATE INDEX IX_HD_Ngay ON dbo.HOA_DON(ngayLap DESC,maHD DESC);
GO
IF NOT EXISTS(SELECT 1 FROM sys.sequences WHERE name=N'seq_KH' AND schema_id=SCHEMA_ID(N'dbo'))
EXEC(N'CREATE SEQUENCE dbo.seq_KH AS int START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 99999999 NO CYCLE');
GO
IF NOT EXISTS(SELECT 1 FROM sys.sequences WHERE name=N'seq_DH' AND schema_id=SCHEMA_ID(N'dbo'))
EXEC(N'CREATE SEQUENCE dbo.seq_DH AS int START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 99999999 NO CYCLE');
GO
IF NOT EXISTS(SELECT 1 FROM sys.sequences WHERE name=N'seq_HD' AND schema_id=SCHEMA_ID(N'dbo'))
EXEC(N'CREATE SEQUENCE dbo.seq_HD AS int START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 99999999 NO CYCLE');
GO
CREATE OR ALTER TRIGGER dbo.TR_HD_BatBien ON dbo.HOA_DON AFTER UPDATE,DELETE AS
BEGIN
 SET NOCOUNT ON;
 IF EXISTS(SELECT 1 FROM deleted) THROW 50002,N'Hóa đơn đã thanh toán phải được giữ nguyên.',1;
END;
GO
CREATE OR ALTER TRIGGER dbo.TR_CTHD_BatBien ON dbo.CHI_TIET_HOA_DON AFTER UPDATE,DELETE AS
BEGIN
 SET NOCOUNT ON;
 IF EXISTS(SELECT 1 FROM deleted) THROW 50003,N'Không sửa hoặc xóa chi tiết hóa đơn đã thanh toán.',1;
END;
GO
CREATE OR ALTER TRIGGER dbo.TR_DH_TrangThai ON dbo.DON_HANG AFTER UPDATE AS
BEGIN
 SET NOCOUNT ON;
 IF EXISTS(SELECT 1 FROM deleted d JOIN inserted i ON i.maDH=d.maDH
           WHERE d.trangThaiDH<>N'ChuaThanhToan')
    THROW 50004,N'Đơn hàng đã thanh toán hoặc đã hủy không được chỉnh sửa.',1;
END;
GO
CREATE OR ALTER TRIGGER dbo.TR_PhanCong_KhongTrung ON dbo.PHAN_CONG_CA AFTER INSERT,UPDATE AS
BEGIN
 SET NOCOUNT ON;
 IF EXISTS(SELECT 1 FROM inserted i JOIN dbo.CA_LAM_VIEC ci ON ci.maCa=i.maCa
   JOIN dbo.PHAN_CONG_CA p ON p.maNV=i.maNV AND p.ngayLam=i.ngayLam AND p.maPhanCong<>i.maPhanCong
   JOIN dbo.CA_LAM_VIEC cp ON cp.maCa=p.maCa
   WHERE ci.gioBatDau<cp.gioKetThuc AND cp.gioBatDau<ci.gioKetThuc)
    THROW 50005,N'Nhân viên bị trùng thời gian ca làm.',1;
END;
GO

SET NOEXEC OFF;
GO
