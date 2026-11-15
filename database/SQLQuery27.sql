-- GREEN HOME: SQLQuery27 - schema chung + trigger tu dong trang thai san pham.
-- Dung de TAO MOI CSDL QuanLyBanHang; cuoi file co du lieu khoi tao login va danh muc.
-- Da bo duong dan MDF co dinh va cac option sinh tu SQL Server 2019 khong hop SQL 2012.
-- Tinh tuong thich SQL 2012+ chua duoc chay thuc te tren may dung SQL Server 2012.
-- Voi CSDL da co, KHONG chay lai ca file; chi chon khoi BEGIN/END trigger o cuoi.

USE [master]
GO
/****** Object:  Database [QuanLyBanHang]    Script Date: 11/3/2026 4:31:43 AM ******/
-- Tao database voi duong dan data mac dinh cua SQL Server tren tung may.
-- CHI chay file day du tren database MOI (script khong co chuc nang migration toan bo bang).
CREATE DATABASE [QuanLyBanHang]
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [QuanLyBanHang].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [QuanLyBanHang] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET ARITHABORT OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [QuanLyBanHang] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [QuanLyBanHang] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [QuanLyBanHang] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET  ENABLE_BROKER 
GO
ALTER DATABASE [QuanLyBanHang] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [QuanLyBanHang] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [QuanLyBanHang] SET  MULTI_USER 
GO
ALTER DATABASE [QuanLyBanHang] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [QuanLyBanHang] SET DB_CHAINING OFF 
GO
ALTER DATABASE [QuanLyBanHang] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [QuanLyBanHang] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
USE [QuanLyBanHang]
GO
USE [QuanLyBanHang]
GO
/****** Object:  Sequence [dbo].[Seq_GH_DH]    Script Date: 11/3/2026 4:31:43 AM ******/
CREATE SEQUENCE [dbo].[Seq_GH_DH] 
 AS [bigint]
 START WITH 81
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE 
GO
USE [QuanLyBanHang]
GO
/****** Object:  Sequence [dbo].[Seq_GH_HD]    Script Date: 11/3/2026 4:31:43 AM ******/
CREATE SEQUENCE [dbo].[Seq_GH_HD] 
 AS [bigint]
 START WITH 69
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE 
GO
USE [QuanLyBanHang]
GO
/****** Object:  Sequence [dbo].[Seq_GH_KH]    Script Date: 11/3/2026 4:31:43 AM ******/
CREATE SEQUENCE [dbo].[Seq_GH_KH] 
 AS [bigint]
 START WITH 23
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE 
GO
/****** Object:  Table [dbo].[CaLamViec]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CaLamViec](
	[maCa] [varchar](20) NOT NULL,
	[tenCa] [nvarchar](100) NOT NULL,
	[gioBatDau] [time](7) NOT NULL,
	[gioKetThuc] [time](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maCa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenCa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChiTietDonHang]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietDonHang](
	[idChiTietDH] [int] IDENTITY(1,1) NOT NULL,
	[maDH] [varchar](20) NOT NULL,
	[maSP] [varchar](20) NOT NULL,
	[soLuong] [int] NOT NULL,
	[donGia] [decimal](18, 2) NOT NULL,
	[serialIMEI] [varchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[idChiTietDH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maDH] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChiTietHoaDon]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietHoaDon](
	[idChiTietHD] [int] IDENTITY(1,1) NOT NULL,
	[maHD] [varchar](20) NOT NULL,
	[maSP] [varchar](20) NOT NULL,
	[maKM] [varchar](20) NULL,
	[soLuong] [int] NOT NULL,
	[donGia] [decimal](18, 2) NOT NULL,
	[serialIMEI] [varchar](100) NULL,
	[soTienGiam] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[idChiTietHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_CTHD_Id_maHD_maSP] UNIQUE NONCLUSTERED 
(
	[idChiTietHD] ASC,
	[maHD] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_CTHD_maHD_maSP] UNIQUE NONCLUSTERED 
(
	[maHD] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChiTietPhieuDoiTra]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietPhieuDoiTra](
	[idChiTietDoiTra] [int] IDENTITY(1,1) NOT NULL,
	[maPhieuDoiTra] [varchar](20) NOT NULL,
	[maHD] [varchar](20) NOT NULL,
	[idChiTietHD] [int] NOT NULL,
	[maSP] [varchar](20) NOT NULL,
	[soLuong] [int] NOT NULL,
	[donGia] [decimal](18, 2) NOT NULL,
	[serialIMEI] [varchar](100) NULL,
	[lyDo] [nvarchar](500) NULL,
	[tinhTrangKhiDoiTra] [varchar](30) NOT NULL,
	[hinhThucXuLy] [varchar](30) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[idChiTietDoiTra] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maPhieuDoiTra] ASC,
	[idChiTietHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DanhMuc]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DanhMuc](
	[maDM] [varchar](20) NOT NULL,
	[tenDM] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maDM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenDM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DonHang]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DonHang](
	[maDH] [varchar](20) NOT NULL,
	[ngayDat] [datetime2](7) NOT NULL,
	[trangThaiDH] [varchar](30) NOT NULL,
	[ghiChu] [nvarchar](500) NULL,
	[maKH] [varchar](20) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maDH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_DonHang_maDH_maKH] UNIQUE NONCLUSTERED 
(
	[maDH] ASC,
	[maKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HoaDon]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HoaDon](
	[maHD] [varchar](20) NOT NULL,
	[ngayLap] [datetime2](7) NOT NULL,
	[phuongThucThanhToan] [varchar](30) NOT NULL,
	[maKH] [varchar](20) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[maDH] [varchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[maHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KhachHang]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhachHang](
	[maKH] [varchar](20) NOT NULL,
	[tenKH] [nvarchar](100) NOT NULL,
	[sdtKH] [varchar](15) NOT NULL,
	[diaChi] [nvarchar](255) NULL,
	[diemTichLuy] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[sdtKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KhuyenMai]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhuyenMai](
	[maKM] [varchar](20) NOT NULL,
	[tenKM] [nvarchar](150) NOT NULL,
	[phanTramGiam] [decimal](5, 2) NOT NULL,
	[ngayBatDau] [datetime2](7) NOT NULL,
	[ngayKetThuc] [datetime2](7) NOT NULL,
	[moTaKM] [nvarchar](500) NULL,
	[loaiGiam] [varchar](20) NOT NULL,
	[soTienGiam] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maKM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[LichSuSanPham]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LichSuSanPham](
	[maLichSu] [bigint] IDENTITY(1,1) NOT NULL,
	[maSP] [varchar](20) NOT NULL,
	[hanhDong] [varchar](10) NOT NULL,
	[maNV] [varchar](20) NULL,
	[thoiDiem] [datetime2](0) NOT NULL,
	[duLieuCu] [nvarchar](max) NULL,
	[duLieuMoi] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[maLichSu] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NhanVien]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NhanVien](
	[maNV] [varchar](20) NOT NULL,
	[hoTen] [nvarchar](100) NOT NULL,
	[sdtNV] [varchar](15) NOT NULL,
	[soCCCD] [varchar](20) NOT NULL,
	[ngaySinh] [date] NOT NULL,
	[ngayVaoLam] [date] NOT NULL,
	[diaChi] [nvarchar](255) NULL,
	[dangLamViec] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[soCCCD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PhanCongCa]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhanCongCa](
	[maPhanCong] [varchar](20) NOT NULL,
	[ngayLam] [date] NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[maCa] [varchar](20) NOT NULL,
	[trangThai] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maPhanCong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PhieuDoiTra]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhieuDoiTra](
	[maPhieuDoiTra] [varchar](20) NOT NULL,
	[ngayLap] [datetime2](7) NOT NULL,
	[trangThaiDuyet] [varchar](20) NOT NULL,
	[maHD] [varchar](20) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[tenLienHe] [nvarchar](100) NULL,
	[sdtLienHe] [varchar](15) NULL,
	[hienTrang] [nvarchar](500) NULL,
	[phuKien] [nvarchar](255) NULL,
	[maNVDuyet] [varchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[maPhieuDoiTra] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_PhieuDoiTra_Phieu_HD] UNIQUE NONCLUSTERED 
(
	[maPhieuDoiTra] ASC,
	[maHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SanPham]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SanPham](
	[maSP] [varchar](20) NOT NULL,
	[tenSP] [nvarchar](150) NOT NULL,
	[maDM] [varchar](20) NOT NULL,
	[maTH] [varchar](20) NOT NULL,
	[thongSoKyThuat] [nvarchar](max) NULL,
	[giaNiemYet] [decimal](18, 2) NOT NULL,
	[model] [nvarchar](100) NULL,
	[donViTinh] [nvarchar](30) NOT NULL,
	[moTaSP] [nvarchar](500) NULL,
	[tinhTrang] [varchar](30) NOT NULL,
	[soLuongTon] [int] NOT NULL,
	[soLuongLoi] [int] NOT NULL,
	[soLuongTonToiThieu] [int] NOT NULL,
	[hinhAnh] [nvarchar](500) NULL,
PRIMARY KEY CLUSTERED 
(
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SanPhamKhuyenMai]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SanPhamKhuyenMai](
	[maSP] [varchar](20) NOT NULL,
	[maKM] [varchar](20) NOT NULL,
 CONSTRAINT [PK_SanPhamKhuyenMai] PRIMARY KEY CLUSTERED 
(
	[maSP] ASC,
	[maKM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TaiKhoan]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TaiKhoan](
	[maTK] [varchar](20) NOT NULL,
	[tenDangNhap] [varchar](100) NOT NULL,
	[matKhau] [varchar](255) NOT NULL,
	[vaiTro] [varchar](30) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[dangHoatDong] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maTK] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenDangNhap] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ThuongHieu]    Script Date: 11/3/2026 4:31:43 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ThuongHieu](
	[maTH] [varchar](20) NOT NULL,
	[tenTH] [nvarchar](100) NOT NULL,
	[moTaThuongHieu] [nvarchar](500) NULL,
PRIMARY KEY CLUSTERED 
(
	[maTH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenTH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UX_HoaDon_maDH]    Script Date: 11/3/2026 4:31:43 AM ******/
CREATE UNIQUE NONCLUSTERED INDEX [UX_HoaDon_maDH] ON [dbo].[HoaDon]
(
	[maDH] ASC
)
WHERE ([maDH] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UX_PC_CaHieuLuc]    Script Date: 11/3/2026 4:31:43 AM ******/
CREATE UNIQUE NONCLUSTERED INDEX [UX_PC_CaHieuLuc] ON [dbo].[PhanCongCa]
(
	[ngayLam] ASC,
	[maNV] ASC,
	[maCa] ASC
)
WHERE ([trangThai]='DA_PHAN_CONG')
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[ChiTietHoaDon] ADD  DEFAULT ((0)) FOR [soTienGiam]
GO
ALTER TABLE [dbo].[KhachHang] ADD  DEFAULT ((0)) FOR [diemTichLuy]
GO
ALTER TABLE [dbo].[KhuyenMai] ADD  DEFAULT ((0)) FOR [phanTramGiam]
GO
ALTER TABLE [dbo].[KhuyenMai] ADD  DEFAULT ((0)) FOR [soTienGiam]
GO
ALTER TABLE [dbo].[LichSuSanPham] ADD  DEFAULT (sysdatetime()) FOR [thoiDiem]
GO
ALTER TABLE [dbo].[NhanVien] ADD  DEFAULT ((1)) FOR [dangLamViec]
GO
ALTER TABLE [dbo].[PhanCongCa] ADD  DEFAULT ('DA_PHAN_CONG') FOR [trangThai]
GO
ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongTon]
GO
ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongLoi]
GO
ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongTonToiThieu]
GO
ALTER TABLE [dbo].[TaiKhoan] ADD  DEFAULT ((1)) FOR [dangHoatDong]
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD FOREIGN KEY([maDH])
REFERENCES [dbo].[DonHang] ([maDH])
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD FOREIGN KEY([maHD])
REFERENCES [dbo].[HoaDon] ([maHD])
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [FK_CTHD_SanPhamKhuyenMai] FOREIGN KEY([maSP], [maKM])
REFERENCES [dbo].[SanPhamKhuyenMai] ([maSP], [maKM])
GO
ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [FK_CTHD_SanPhamKhuyenMai]
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_CTPDT_ChiTietHoaDon] FOREIGN KEY([idChiTietHD], [maHD], [maSP])
REFERENCES [dbo].[ChiTietHoaDon] ([idChiTietHD], [maHD], [maSP])
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra] CHECK CONSTRAINT [FK_CTPDT_ChiTietHoaDon]
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_CTPDT_Phieu_HD] FOREIGN KEY([maPhieuDoiTra], [maHD])
REFERENCES [dbo].[PhieuDoiTra] ([maPhieuDoiTra], [maHD])
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra] CHECK CONSTRAINT [FK_CTPDT_Phieu_HD]
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD FOREIGN KEY([maKH])
REFERENCES [dbo].[KhachHang] ([maKH])
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([maKH])
REFERENCES [dbo].[KhachHang] ([maKH])
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [FK_HoaDon_DonHangKhachHang] FOREIGN KEY([maDH], [maKH])
REFERENCES [dbo].[DonHang] ([maDH], [maKH])
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [FK_HoaDon_DonHangKhachHang]
GO
ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD FOREIGN KEY([maCa])
REFERENCES [dbo].[CaLamViec] ([maCa])
GO
ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD FOREIGN KEY([maHD])
REFERENCES [dbo].[HoaDon] ([maHD])
GO
ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_PhieuDoiTra_NguoiDuyet] FOREIGN KEY([maNVDuyet])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[PhieuDoiTra] CHECK CONSTRAINT [FK_PhieuDoiTra_NguoiDuyet]
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD FOREIGN KEY([maDM])
REFERENCES [dbo].[DanhMuc] ([maDM])
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD FOREIGN KEY([maTH])
REFERENCES [dbo].[ThuongHieu] ([maTH])
GO
ALTER TABLE [dbo].[SanPhamKhuyenMai]  WITH CHECK ADD FOREIGN KEY([maKM])
REFERENCES [dbo].[KhuyenMai] ([maKM])
GO
ALTER TABLE [dbo].[SanPhamKhuyenMai]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])
GO
ALTER TABLE [dbo].[CaLamViec]  WITH CHECK ADD  CONSTRAINT [CK_Ca_ThoiGian] CHECK  (([maCa]='CA001' AND [gioBatDau]='08:00:00' AND [gioKetThuc]='14:00:00' OR [maCa]='CA002' AND [gioBatDau]='14:00:00' AND [gioKetThuc]='18:00:00' OR [maCa]='CA003' AND [gioBatDau]='18:00:00' AND [gioKetThuc]='22:00:00'))
GO
ALTER TABLE [dbo].[CaLamViec] CHECK CONSTRAINT [CK_Ca_ThoiGian]
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD CHECK  (([donGia]>=(0)))
GO
ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD CHECK  (([soLuong]>(0)))
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([donGia]>=(0)))
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([soLuong]>(0)))
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([soTienGiam]>=(0)))
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [CK_CTHD_GiamToiDa] CHECK  (([soTienGiam]<=[soLuong]*[donGia]))
GO
ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [CK_CTHD_GiamToiDa]
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([donGia]>=(0)))
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([hinhThucXuLy]='BAO_HANH' OR [hinhThucXuLy]='DOI_SAN_PHAM' OR [hinhThucXuLy]='HOAN_TIEN'))
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([soLuong]>(0)))
GO
ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([tinhTrangKhiDoiTra]='HU_HONG' OR [tinhTrangKhiDoiTra]='LOI_KY_THUAT' OR [tinhTrangKhiDoiTra]='CON_NGUYEN_VEN'))
GO
ALTER TABLE [dbo].[DanhMuc]  WITH CHECK ADD  CONSTRAINT [CK_DM_Dung7Nhom] CHECK  (([maDM]='DM001' AND [tenDM]=N'Điện thoại' OR [maDM]='DM002' AND [tenDM]=N'Laptop' OR [maDM]='DM003' AND [tenDM]=N'Tivi' OR [maDM]='DM004' AND [tenDM]=N'Tủ lạnh' OR [maDM]='DM005' AND [tenDM]=N'Máy giặt' OR [maDM]='DM006' AND [tenDM]=N'Máy lạnh' OR [maDM]='DM007' AND [tenDM]=N'Phụ kiện điện tử'))
GO
ALTER TABLE [dbo].[DanhMuc] CHECK CONSTRAINT [CK_DM_Dung7Nhom]
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD CHECK  (([trangThaiDH]='DA_HUY' OR [trangThaiDH]='HOAN_THANH' OR [trangThaiDH]='DANG_GIAO' OR [trangThaiDH]='DANG_XU_LY' OR [trangThaiDH]='DA_XAC_NHAN' OR [trangThaiDH]='CHO_XAC_NHAN'))
GO
ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD  CONSTRAINT [CK_GH_DH_MaChuan] CHECK  ((datalength([maDH])=(10) AND [maDH] like 'DH[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [CK_GH_DH_MaChuan]
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [CK_GH_HD_MaChuan] CHECK  ((datalength([maHD])=(10) AND [maHD] like 'HD[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [CK_GH_HD_MaChuan]
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [CK_HoaDon_PhuongThucThanhToan] CHECK  (([phuongThucThanhToan]='CHUYEN_KHOAN' OR [phuongThucThanhToan]='TIEN_MAT'))
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [CK_HoaDon_PhuongThucThanhToan]
GO
ALTER TABLE [dbo].[KhachHang]  WITH CHECK ADD CHECK  (([diemTichLuy]>=(0)))
GO
ALTER TABLE [dbo].[KhachHang]  WITH CHECK ADD  CONSTRAINT [CK_GH_KH_MaChuan] CHECK  ((datalength([maKH])=(10) AND [maKH] like 'KH[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[KhachHang] CHECK CONSTRAINT [CK_GH_KH_MaChuan]
GO
ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([ngayBatDau]<=[ngayKetThuc]))
GO
ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([loaiGiam]='SO_TIEN' OR [loaiGiam]='PHAN_TRAM'))
GO
ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([phanTramGiam]>=(0) AND [phanTramGiam]<=(100)))
GO
ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([soTienGiam]>=(0)))
GO
ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD  CONSTRAINT [CK_GH_KM_MaChuan] CHECK  ((datalength([maKM])=(10) AND [maKM] like 'KM[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[KhuyenMai] CHECK CONSTRAINT [CK_GH_KM_MaChuan]
GO
ALTER TABLE [dbo].[LichSuSanPham]  WITH CHECK ADD CHECK  (([hanhDong]='XOA' OR [hanhDong]='SUA' OR [hanhDong]='THEM'))
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_GH_NV_MaChuan] CHECK  ((datalength([maNV])=(6) AND [maNV] like 'NV[0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_GH_NV_MaChuan]
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_CCCD] CHECK  ((len([soCCCD])=(12) AND NOT [soCCCD] like '%[^0-9]%'))
GO
ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_CCCD]
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_HoTen] CHECK  ((len(ltrim(rtrim([hoTen])))>=(2) AND len(ltrim(rtrim([hoTen])))<=(50)))
GO
ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_HoTen]
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_SDT] CHECK  ((len([sdtNV])=(10) AND [sdtNV] like '0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_SDT]
GO
ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD  CONSTRAINT [CK_PC_TrangThai] CHECK  (([trangThai]='DA_HUY' OR [trangThai]='DA_PHAN_CONG'))
GO
ALTER TABLE [dbo].[PhanCongCa] CHECK CONSTRAINT [CK_PC_TrangThai]
GO
ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD CHECK  (([trangThaiDuyet]='TU_CHOI' OR [trangThaiDuyet]='DA_DUYET' OR [trangThaiDuyet]='CHO_DUYET'))
GO
ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [CK_GH_DT_MaChuan] CHECK  ((datalength([maPhieuDoiTra])=(10) AND [maPhieuDoiTra] like 'DT[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[PhieuDoiTra] CHECK CONSTRAINT [CK_GH_DT_MaChuan]
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([giaNiemYet]>(0)))
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongTon]>=(0)))
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongLoi]>=(0)))
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongTonToiThieu]>=(0)))
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([tinhTrang]='HET_HANG' OR [tinhTrang]='NGUNG_KINH_DOANH' OR [tinhTrang]='DANG_KINH_DOANH'))
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_DonVi] CHECK  ((len(ltrim(rtrim([donViTinh])))>(0)))
GO
ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_DonVi]
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_HangLoi] CHECK  (([soLuongLoi]<=[soLuongTon]))
GO
ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_HangLoi]
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_Ten] CHECK  ((len(ltrim(rtrim([tenSP])))>=(2) AND len(ltrim(rtrim([tenSP])))<=(100)))
GO
ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_Ten]
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_GH_TK_MaChuan] CHECK  ((datalength([maTK])=(6) AND [maTK] like 'TK[0-9][0-9][0-9][0-9]'))
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_GH_TK_MaChuan]
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_TK_TenDangNhap] CHECK  ((len(ltrim(rtrim([tenDangNhap])))>(0)))
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_TK_TenDangNhap]
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_TK_VaiTro] CHECK  (([vaiTro]='NHAN_VIEN_BAN_HANG' OR [vaiTro]='QUAN_LY'))
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_TK_VaiTro]
GO
ALTER TABLE [dbo].[ThuongHieu]  WITH CHECK ADD  CONSTRAINT [CK_TH_Ma] CHECK  (([maTH] like 'TH[0-9][0-9][0-9]' AND len([maTH])=(5)))
GO
ALTER TABLE [dbo].[ThuongHieu] CHECK CONSTRAINT [CK_TH_Ma]
GO
ALTER TABLE [dbo].[ThuongHieu]  WITH CHECK ADD  CONSTRAINT [CK_TH_Ten] CHECK  ((len(ltrim(rtrim([tenTH])))>=(2) AND len(ltrim(rtrim([tenTH])))<=(50)))
GO
ALTER TABLE [dbo].[ThuongHieu] CHECK CONSTRAINT [CK_TH_Ten]
GO
-- ===== BEGIN GREEN HOME: TU DONG TRANG THAI SAN PHAM (CHAY RIENG DUOC CHO DB DA CO) =====
-- Neu database DA TON TAI: trong SSMS chi boi den tu dong BEGIN toi END va Execute.
-- Neu TAO MOI: chay toan bo SQLQuery27.sql (CHI tren server chua co database QuanLyBanHang).
USE QuanLyBanHang;
GO
IF OBJECT_ID(N'dbo.SanPham', N'U') IS NULL
    THROW 50001, N'Chua co bang dbo.SanPham. Hay tao CSDL Green Home truoc.', 1;
GO
IF OBJECT_ID(N'dbo.trg_GH_TuDongTrangThaiSP', N'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_GH_TuDongTrangThaiSP;
GO
CREATE TRIGGER dbo.trg_GH_TuDongTrangThaiSP
ON dbo.SanPham
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    -- Goi sau moi lan ban hang tru ton, nhap them ton, cap nhat hang loi.
    -- NgungBan la quyet dinh kinh doanh, khong tu mo lai.
    IF NOT EXISTS (
        SELECT 1
        FROM dbo.SanPham sp
        JOIN inserted i ON i.maSP = sp.maSP
        WHERE sp.tinhTrang <> 'NGUNG_KINH_DOANH'
          AND sp.tinhTrang <> CASE WHEN sp.soLuongTon - sp.soLuongLoi <= 0
                               THEN 'HET_HANG' ELSE 'DANG_KINH_DOANH' END
    ) RETURN;

    UPDATE sp
    SET tinhTrang = CASE WHEN sp.soLuongTon - sp.soLuongLoi <= 0
                        THEN 'HET_HANG' ELSE 'DANG_KINH_DOANH' END
    FROM dbo.SanPham sp
    JOIN inserted i ON i.maSP = sp.maSP
    WHERE sp.tinhTrang <> 'NGUNG_KINH_DOANH'
      AND sp.tinhTrang <> CASE WHEN sp.soLuongTon - sp.soLuongLoi <= 0
                              THEN 'HET_HANG' ELSE 'DANG_KINH_DOANH' END;
END;
GO
-- Sua nhung gia tri da lech tu truoc (giu nguyen NgungBan).
UPDATE dbo.SanPham
SET tinhTrang = CASE WHEN soLuongTon - soLuongLoi <= 0
                    THEN 'HET_HANG' ELSE 'DANG_KINH_DOANH' END
WHERE tinhTrang <> 'NGUNG_KINH_DOANH'
  AND tinhTrang <> CASE WHEN soLuongTon - soLuongLoi <= 0
                        THEN 'HET_HANG' ELSE 'DANG_KINH_DOANH' END;
GO
SELECT tinhTrang, COUNT(*) AS soSanPham
FROM dbo.SanPham GROUP BY tinhTrang;
GO
-- ===== END GREEN HOME: TU DONG TRANG THAI SAN PHAM =====
GO

-- ===== BEGIN GREEN HOME: DU LIEU KHOI TAO (CHAY RIENG DUOC CHO DB DA CO) =====
-- Khi tao DB MOI: khoi nay da nam trong SQLQuery27.sql; KHONG can chay them.
-- Khi DB DA CO, dang trong: chi chay khoi BEGIN..END nay; KHONG chay lai CREATE DATABASE.
-- Tao 7 danh muc, 9 thuong hieu, 3 ca, 5 nhan vien + tai khoan demo.
-- Mat khau 123456 duoc BAM PBKDF2WithHmacSHA256 120000 lan voi salt rieng tung tai khoan.
-- Chi dung de demo; khong su dung mat khau mac dinh trong production.
USE [QuanLyBanHang];
GO
SET XACT_ABORT ON;
GO
BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.NhanVien',N'U') IS NULL OR OBJECT_ID(N'dbo.TaiKhoan',N'U') IS NULL
        THROW 51001, N'Chua co bang NhanVien/TaiKhoan. Hay chay SQLQuery27 tren DB moi truoc.', 1;

    -- Danh muc dung 7 nhom theo CK_DM_Dung7Nhom
    INSERT INTO dbo.DanhMuc (maDM,tenDM)
    SELECT v.maDM,v.tenDM
    FROM (VALUES
        ('DM001',N'Điện thoại'),('DM002',N'Laptop'),('DM003',N'Tivi'),
        ('DM004',N'Tủ lạnh'),('DM005',N'Máy giặt'),('DM006',N'Máy lạnh'),
        ('DM007',N'Phụ kiện điện tử')
    ) AS v(maDM,tenDM)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.DanhMuc d WHERE d.maDM=v.maDM);

    -- Danh muc va thuong hieu phai duoc tao truoc khi import Excel san pham
    INSERT INTO dbo.ThuongHieu (maTH,tenTH)
    SELECT v.maTH,v.tenTH
    FROM (VALUES
        ('TH001',N'Apple'),('TH002',N'Samsung'),('TH003',N'Xiaomi'),
        ('TH004',N'OPPO'),('TH005',N'ASUS'),('TH006',N'Lenovo'),
        ('TH007',N'Sony'),('TH008',N'Anker'),('TH009',N'JBL')
    ) AS v(maTH,tenTH)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.ThuongHieu d WHERE d.maTH=v.maTH);

    INSERT INTO dbo.CaLamViec (maCa,tenCa,gioBatDau,gioKetThuc)
    SELECT v.maCa,v.tenCa,v.batDau,v.ketThuc
    FROM (VALUES
        ('CA001',N'Ca sáng',CAST('08:00:00' AS time),CAST('14:00:00' AS time)),
        ('CA002',N'Ca chiều',CAST('14:00:00' AS time),CAST('18:00:00' AS time)),
        ('CA003',N'Ca tối',CAST('18:00:00' AS time),CAST('22:00:00' AS time))
    ) AS v(maCa,tenCa,batDau,ketThuc)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.CaLamViec d WHERE d.maCa=v.maCa);

    -- demo_nv1 / 123456 / QUAN_LY
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap='demo_nv1')
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV='NV0001')
            OR EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK='TK0001')
            THROW 51002, N'Ma NV0001 hoac TK0001 da duoc su dung. Khong ghi de du lieu cu.', 1;
        INSERT INTO dbo.NhanVien
            (maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec)
        VALUES
            ('NV0001',N'Trần Văn Huy','0800000001','079200000001',CONVERT(date,'19910515',112),CONVERT(date,'20230301',112),N'TP.HCM',1);
        INSERT INTO dbo.TaiKhoan
            (maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
        VALUES
            ('TK0001','demo_nv1','PBKDF2$120000$PoMUp4L2Nau7KJ0VED+uEg==$jBDZimtrJD5CNPwVcA8YLvAZiQszjwyeI5ZK2t7qBKE=','QUAN_LY','NV0001',1);
    END;

    -- demo_nv2 / 123456 / NHAN_VIEN_BAN_HANG
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap='demo_nv2')
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV='NV0002')
            OR EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK='TK0002')
            THROW 51003, N'Ma NV0002 hoac TK0002 da duoc su dung. Khong ghi de du lieu cu.', 1;
        INSERT INTO dbo.NhanVien
            (maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec)
        VALUES
            ('NV0002',N'Lê Thị Mai','0800000002','079200000002',CONVERT(date,'19920515',112),CONVERT(date,'20240301',112),N'TP.HCM',1);
        INSERT INTO dbo.TaiKhoan
            (maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
        VALUES
            ('TK0002','demo_nv2','PBKDF2$120000$uf0mMDSDfjLZA+giaeLwwg==$flsIEk8kDhi4zdGVl3fo/iEcOVOoWp0zwr8r2n5TD4M=','NHAN_VIEN_BAN_HANG','NV0002',1);
    END;

    -- demo_nv3 / 123456 / NHAN_VIEN_BAN_HANG
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap='demo_nv3')
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV='NV0003')
            OR EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK='TK0003')
            THROW 51004, N'Ma NV0003 hoac TK0003 da duoc su dung. Khong ghi de du lieu cu.', 1;
        INSERT INTO dbo.NhanVien
            (maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec)
        VALUES
            ('NV0003',N'Nguyễn Đức Long','0800000003','079200000003',CONVERT(date,'19930515',112),CONVERT(date,'20250301',112),N'TP.HCM',1);
        INSERT INTO dbo.TaiKhoan
            (maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
        VALUES
            ('TK0003','demo_nv3','PBKDF2$120000$ZRaw26lmB9/7GjjpnB0nRw==$DKn/phTlHSDyG3695vxdEjTDJVLJBc5B/vBQCicA5eE=','NHAN_VIEN_BAN_HANG','NV0003',1);
    END;

    -- demo_nv4 / 123456 / NHAN_VIEN_BAN_HANG
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap='demo_nv4')
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV='NV0004')
            OR EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK='TK0004')
            THROW 51005, N'Ma NV0004 hoac TK0004 da duoc su dung. Khong ghi de du lieu cu.', 1;
        INSERT INTO dbo.NhanVien
            (maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec)
        VALUES
            ('NV0004',N'Phạm Thu Trang','0800000004','079200000004',CONVERT(date,'19940515',112),CONVERT(date,'20220301',112),N'TP.HCM',1);
        INSERT INTO dbo.TaiKhoan
            (maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
        VALUES
            ('TK0004','demo_nv4','PBKDF2$120000$vc9Ko6hd4VUD8rjZ6kkhLA==$MKinpoqKaNT/P9v6y5aYD57sTwQfUV0IUCrW4FxYHdA=','NHAN_VIEN_BAN_HANG','NV0004',1);
    END;

    -- demo_nv5 / 123456 / NHAN_VIEN_BAN_HANG
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap='demo_nv5')
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV='NV0005')
            OR EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK='TK0005')
            THROW 51006, N'Ma NV0005 hoac TK0005 da duoc su dung. Khong ghi de du lieu cu.', 1;
        INSERT INTO dbo.NhanVien
            (maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec)
        VALUES
            ('NV0005',N'Võ Minh Tuấn','0800000005','079200000005',CONVERT(date,'19950515',112),CONVERT(date,'20230301',112),N'TP.HCM',1);
        INSERT INTO dbo.TaiKhoan
            (maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
        VALUES
            ('TK0005','demo_nv5','PBKDF2$120000$7Y8IJuhmxlWr1Q62gSOCMg==$Xl7TNa1MvjyIAJ18ifGrBVfuDgDv6p0Q/M7d1D4jwO8=','NHAN_VIEN_BAN_HANG','NV0005',1);
    END;

    COMMIT TRANSACTION;
    PRINT N'KHOI TAO DEMO THANH CONG. Dang nhap demo_nv1 / 123456 (QUAN_LY).';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    DECLARE @ThongBao nvarchar(4000) = ERROR_MESSAGE();
    RAISERROR(N'Loi khoi tao demo: %s',16,1,@ThongBao);
END CATCH;
GO
-- Kiem tra ket qua (khong hien hash)
SELECT tk.tenDangNhap,tk.vaiTro,tk.dangHoatDong,nv.maNV,nv.dangLamViec
FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv ON nv.maNV=tk.maNV
WHERE tk.tenDangNhap LIKE 'demo_nv%'
ORDER BY tk.tenDangNhap;
SELECT (SELECT COUNT(*) FROM dbo.DanhMuc) AS soDanhMuc,
       (SELECT COUNT(*) FROM dbo.ThuongHieu) AS soThuongHieu,
       (SELECT COUNT(*) FROM dbo.CaLamViec) AS soCa;
GO
-- ===== END GREEN HOME: DU LIEU KHOI TAO =====

USE [master]
GO
ALTER DATABASE [QuanLyBanHang] SET  READ_WRITE 
GO
