-- GreenHome | Cài mới hoặc cập nhật GreenHome trên SQL Server 2019 trở lên. Mở cả file trong SSMS, nhấn F5.
USE [master];
GO
SET NOCOUNT ON;
SET IMPLICIT_TRANSACTIONS OFF;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET QUOTED_IDENTIFIER ON;
SET NUMERIC_ROUNDABORT OFF;
IF DB_ID(N'QuanLyBanHang') IS NULL EXEC(N'CREATE DATABASE [QuanLyBanHang]');
GO
BEGIN TRY
 BEGIN TRANSACTION;
 DECLARE @empty bit;
 EXEC [QuanLyBanHang].sys.sp_executesql N'
 DECLARE @lock int;
 EXEC @lock=sys.sp_getapplock @Resource=N''GreenHome_CaiDat'', @LockMode=''Exclusive'', @LockOwner=''Transaction'';
 IF @lock<0 THROW 51000,N''Đang có phiên cài đặt khác.'',1;
 SELECT @empty=CASE WHEN EXISTS (SELECT 1 FROM sys.objects WHERE is_ms_shipped=0 AND type IN (''U'',''SO'')) THEN 0 ELSE 1 END;',
 N'@empty bit OUTPUT',@empty OUTPUT;
 IF @empty=1 BEGIN
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE SEQUENCE [dbo].[Seq_GH_DH] 
 AS [bigint]
 START WITH 81
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE SEQUENCE [dbo].[Seq_GH_HD] 
 AS [bigint]
 START WITH 69
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE SEQUENCE [dbo].[Seq_GH_KH] 
 AS [bigint]
 START WITH 23
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 99999999
 CACHE';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[CaLamViec](
	[maCa] [varchar](20) NOT NULL,
	[tenCa] [nvarchar](100) NOT NULL,
	[gioBatDau] [time](7) NOT NULL,
	[gioKetThuc] [time](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maCa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenCa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[ChiTietDonHang](
	[idChiTietDH] [int] IDENTITY(1,1) NOT NULL,
	[maDH] [varchar](20) NOT NULL,
	[maSP] [varchar](20) NOT NULL,
	[soLuong] [int] NOT NULL,
	[donGia] [decimal](18, 2) NOT NULL,
	[serialIMEI] [varchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[idChiTietDH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maDH] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[ChiTietHoaDon](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_CTHD_Id_maHD_maSP] UNIQUE NONCLUSTERED 
(
	[idChiTietHD] ASC,
	[maHD] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_CTHD_maHD_maSP] UNIQUE NONCLUSTERED 
(
	[maHD] ASC,
	[maSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[ChiTietPhieuDoiTra](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maPhieuDoiTra] ASC,
	[idChiTietHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[DanhMuc](
	[maDM] [varchar](20) NOT NULL,
	[tenDM] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maDM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenDM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[DonHang](
	[maDH] [varchar](20) NOT NULL,
	[ngayDat] [datetime2](7) NOT NULL,
	[trangThaiDH] [varchar](30) NOT NULL,
	[ghiChu] [nvarchar](500) NULL,
	[maKH] [varchar](20) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maDH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_DonHang_maDH_maKH] UNIQUE NONCLUSTERED 
(
	[maDH] ASC,
	[maKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[HoaDon](
	[maHD] [varchar](20) NOT NULL,
	[ngayLap] [datetime2](7) NOT NULL,
	[phuongThucThanhToan] [varchar](30) NOT NULL,
	[maKH] [varchar](20) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[maDH] [varchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[maHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[KhachHang](
	[maKH] [varchar](20) NOT NULL,
	[tenKH] [nvarchar](100) NOT NULL,
	[sdtKH] [varchar](15) NOT NULL,
	[diaChi] [nvarchar](255) NULL,
	[diemTichLuy] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[sdtKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[KhuyenMai](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[LichSuSanPham](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[NhanVien](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[soCCCD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[PhanCongCa](
	[maPhanCong] [varchar](20) NOT NULL,
	[ngayLam] [date] NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[maCa] [varchar](20) NOT NULL,
	[trangThai] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maPhanCong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[PhieuDoiTra](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_PhieuDoiTra_Phieu_HD] UNIQUE NONCLUSTERED 
(
	[maPhieuDoiTra] ASC,
	[maHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[SanPham](
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[SanPhamKhuyenMai](
	[maSP] [varchar](20) NOT NULL,
	[maKM] [varchar](20) NOT NULL,
 CONSTRAINT [PK_SanPhamKhuyenMai] PRIMARY KEY CLUSTERED 
(
	[maSP] ASC,
	[maKM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[TaiKhoan](
	[maTK] [varchar](20) NOT NULL,
	[tenDangNhap] [varchar](100) NOT NULL,
	[matKhau] [varchar](255) NOT NULL,
	[vaiTro] [varchar](30) NOT NULL,
	[maNV] [varchar](20) NOT NULL,
	[dangHoatDong] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[maTK] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenDangNhap] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[maNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE TABLE [dbo].[ThuongHieu](
	[maTH] [varchar](20) NOT NULL,
	[tenTH] [nvarchar](100) NOT NULL,
	[moTaThuongHieu] [nvarchar](500) NULL,
PRIMARY KEY CLUSTERED 
(
	[maTH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[tenTH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE UNIQUE NONCLUSTERED INDEX [UX_HoaDon_maDH] ON [dbo].[HoaDon]
(
	[maDH] ASC
)
WHERE ([maDH] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'CREATE UNIQUE NONCLUSTERED INDEX [UX_PC_CaHieuLuc] ON [dbo].[PhanCongCa]
(
	[ngayLam] ASC,
	[maNV] ASC,
	[maCa] ASC
)
WHERE ([trangThai]=''DA_PHAN_CONG'')
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon] ADD  DEFAULT ((0)) FOR [soTienGiam]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhachHang] ADD  DEFAULT ((0)) FOR [diemTichLuy]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai] ADD  DEFAULT ((0)) FOR [phanTramGiam]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai] ADD  DEFAULT ((0)) FOR [soTienGiam]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[LichSuSanPham] ADD  DEFAULT (sysdatetime()) FOR [thoiDiem]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien] ADD  DEFAULT ((1)) FOR [dangLamViec]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhanCongCa] ADD  DEFAULT (''DA_PHAN_CONG'') FOR [trangThai]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongTon]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongLoi]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] ADD  DEFAULT ((0)) FOR [soLuongTonToiThieu]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan] ADD  DEFAULT ((1)) FOR [dangHoatDong]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD FOREIGN KEY([maDH])
REFERENCES [dbo].[DonHang] ([maDH])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD FOREIGN KEY([maHD])
REFERENCES [dbo].[HoaDon] ([maHD])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [FK_CTHD_SanPhamKhuyenMai] FOREIGN KEY([maSP], [maKM])
REFERENCES [dbo].[SanPhamKhuyenMai] ([maSP], [maKM])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [FK_CTHD_SanPhamKhuyenMai]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_CTPDT_ChiTietHoaDon] FOREIGN KEY([idChiTietHD], [maHD], [maSP])
REFERENCES [dbo].[ChiTietHoaDon] ([idChiTietHD], [maHD], [maSP])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra] CHECK CONSTRAINT [FK_CTPDT_ChiTietHoaDon]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_CTPDT_Phieu_HD] FOREIGN KEY([maPhieuDoiTra], [maHD])
REFERENCES [dbo].[PhieuDoiTra] ([maPhieuDoiTra], [maHD])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra] CHECK CONSTRAINT [FK_CTPDT_Phieu_HD]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD FOREIGN KEY([maKH])
REFERENCES [dbo].[KhachHang] ([maKH])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([maKH])
REFERENCES [dbo].[KhachHang] ([maKH])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [FK_HoaDon_DonHangKhachHang] FOREIGN KEY([maDH], [maKH])
REFERENCES [dbo].[DonHang] ([maDH], [maKH])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [FK_HoaDon_DonHangKhachHang]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD FOREIGN KEY([maCa])
REFERENCES [dbo].[CaLamViec] ([maCa])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD FOREIGN KEY([maHD])
REFERENCES [dbo].[HoaDon] ([maHD])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [FK_PhieuDoiTra_NguoiDuyet] FOREIGN KEY([maNVDuyet])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra] CHECK CONSTRAINT [FK_PhieuDoiTra_NguoiDuyet]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD FOREIGN KEY([maDM])
REFERENCES [dbo].[DanhMuc] ([maDM])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD FOREIGN KEY([maTH])
REFERENCES [dbo].[ThuongHieu] ([maTH])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPhamKhuyenMai]  WITH CHECK ADD FOREIGN KEY([maKM])
REFERENCES [dbo].[KhuyenMai] ([maKM])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPhamKhuyenMai]  WITH CHECK ADD FOREIGN KEY([maSP])
REFERENCES [dbo].[SanPham] ([maSP])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD FOREIGN KEY([maNV])
REFERENCES [dbo].[NhanVien] ([maNV])';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[CaLamViec]  WITH CHECK ADD  CONSTRAINT [CK_Ca_ThoiGian] CHECK  (([maCa]=''CA001'' AND [gioBatDau]=''08:00:00'' AND [gioKetThuc]=''14:00:00'' OR [maCa]=''CA002'' AND [gioBatDau]=''14:00:00'' AND [gioKetThuc]=''18:00:00'' OR [maCa]=''CA003'' AND [gioBatDau]=''18:00:00'' AND [gioKetThuc]=''22:00:00''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[CaLamViec] CHECK CONSTRAINT [CK_Ca_ThoiGian]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD CHECK  (([donGia]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietDonHang]  WITH CHECK ADD CHECK  (([soLuong]>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([donGia]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([soLuong]>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD CHECK  (([soTienGiam]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [CK_CTHD_GiamToiDa] CHECK  (([soTienGiam]<=[soLuong]*[donGia]))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [CK_CTHD_GiamToiDa]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([donGia]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([hinhThucXuLy]=''BAO_HANH'' OR [hinhThucXuLy]=''DOI_SAN_PHAM'' OR [hinhThucXuLy]=''HOAN_TIEN''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([soLuong]>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ChiTietPhieuDoiTra]  WITH CHECK ADD CHECK  (([tinhTrangKhiDoiTra]=''HU_HONG'' OR [tinhTrangKhiDoiTra]=''LOI_KY_THUAT'' OR [tinhTrangKhiDoiTra]=''CON_NGUYEN_VEN''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DanhMuc]  WITH CHECK ADD  CONSTRAINT [CK_DM_Dung7Nhom] CHECK  (([maDM]=''DM001'' AND [tenDM]=N''Điện thoại'' OR [maDM]=''DM002'' AND [tenDM]=N''Laptop'' OR [maDM]=''DM003'' AND [tenDM]=N''Tivi'' OR [maDM]=''DM004'' AND [tenDM]=N''Tủ lạnh'' OR [maDM]=''DM005'' AND [tenDM]=N''Máy giặt'' OR [maDM]=''DM006'' AND [tenDM]=N''Máy lạnh'' OR [maDM]=''DM007'' AND [tenDM]=N''Phụ kiện điện tử''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DanhMuc] CHECK CONSTRAINT [CK_DM_Dung7Nhom]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD CHECK  (([trangThaiDH]=''DA_HUY'' OR [trangThaiDH]=''HOAN_THANH'' OR [trangThaiDH]=''DANG_GIAO'' OR [trangThaiDH]=''DANG_XU_LY'' OR [trangThaiDH]=''DA_XAC_NHAN'' OR [trangThaiDH]=''CHO_XAC_NHAN''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DonHang]  WITH CHECK ADD  CONSTRAINT [CK_GH_DH_MaChuan] CHECK  ((datalength([maDH])=(10) AND [maDH] like ''DH[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[DonHang] CHECK CONSTRAINT [CK_GH_DH_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [CK_GH_HD_MaChuan] CHECK  ((datalength([maHD])=(10) AND [maHD] like ''HD[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [CK_GH_HD_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [CK_HoaDon_PhuongThucThanhToan] CHECK  (([phuongThucThanhToan]=''CHUYEN_KHOAN'' OR [phuongThucThanhToan]=''TIEN_MAT''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [CK_HoaDon_PhuongThucThanhToan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhachHang]  WITH CHECK ADD CHECK  (([diemTichLuy]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhachHang]  WITH CHECK ADD  CONSTRAINT [CK_GH_KH_MaChuan] CHECK  ((datalength([maKH])=(10) AND [maKH] like ''KH[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhachHang] CHECK CONSTRAINT [CK_GH_KH_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([ngayBatDau]<=[ngayKetThuc]))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([loaiGiam]=''SO_TIEN'' OR [loaiGiam]=''PHAN_TRAM''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([phanTramGiam]>=(0) AND [phanTramGiam]<=(100)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD CHECK  (([soTienGiam]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai]  WITH CHECK ADD  CONSTRAINT [CK_GH_KM_MaChuan] CHECK  ((datalength([maKM])=(10) AND [maKM] like ''KM[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[KhuyenMai] CHECK CONSTRAINT [CK_GH_KM_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[LichSuSanPham]  WITH CHECK ADD CHECK  (([hanhDong]=''XOA'' OR [hanhDong]=''SUA'' OR [hanhDong]=''THEM''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_GH_NV_MaChuan] CHECK  ((datalength([maNV])=(6) AND [maNV] like ''NV[0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_GH_NV_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_CCCD] CHECK  ((len([soCCCD])=(12) AND NOT [soCCCD] like ''%[^0-9]%''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_CCCD]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_HoTen] CHECK  ((len(ltrim(rtrim([hoTen])))>=(2) AND len(ltrim(rtrim([hoTen])))<=(50)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_HoTen]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [CK_NV_SDT] CHECK  ((len([sdtNV])=(10) AND [sdtNV] like ''0[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [CK_NV_SDT]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhanCongCa]  WITH CHECK ADD  CONSTRAINT [CK_PC_TrangThai] CHECK  (([trangThai]=''DA_HUY'' OR [trangThai]=''DA_PHAN_CONG''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhanCongCa] CHECK CONSTRAINT [CK_PC_TrangThai]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra]  WITH CHECK ADD  CONSTRAINT [CK_GH_DT_MaChuan] CHECK  ((datalength([maPhieuDoiTra])=(10) AND [maPhieuDoiTra] like ''DT[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[PhieuDoiTra] CHECK CONSTRAINT [CK_GH_DT_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([giaNiemYet]>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongTon]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongLoi]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([soLuongTonToiThieu]>=(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD CHECK  (([tinhTrang]=''HET_HANG'' OR [tinhTrang]=''NGUNG_KINH_DOANH'' OR [tinhTrang]=''DANG_KINH_DOANH''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_DonVi] CHECK  ((len(ltrim(rtrim([donViTinh])))>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_DonVi]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_HangLoi] CHECK  (([soLuongLoi]<=[soLuongTon]))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_HangLoi]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD  CONSTRAINT [CK_SP_Ten] CHECK  ((len(ltrim(rtrim([tenSP])))>=(2) AND len(ltrim(rtrim([tenSP])))<=(100)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[SanPham] CHECK CONSTRAINT [CK_SP_Ten]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_GH_TK_MaChuan] CHECK  ((datalength([maTK])=(6) AND [maTK] like ''TK[0-9][0-9][0-9][0-9]''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_GH_TK_MaChuan]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_TK_TenDangNhap] CHECK  ((len(ltrim(rtrim([tenDangNhap])))>(0)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_TK_TenDangNhap]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [CK_TK_VaiTro] CHECK  (([vaiTro]=''NHAN_VIEN_BAN_HANG'' OR [vaiTro]=''QUAN_LY''))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [CK_TK_VaiTro]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ThuongHieu]  WITH CHECK ADD  CONSTRAINT [CK_TH_Ma] CHECK  (([maTH] like ''TH[0-9][0-9][0-9]'' AND len([maTH])=(5)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ThuongHieu] CHECK CONSTRAINT [CK_TH_Ma]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ThuongHieu]  WITH CHECK ADD  CONSTRAINT [CK_TH_Ten] CHECK  ((len(ltrim(rtrim([tenTH])))>=(2) AND len(ltrim(rtrim([tenTH])))<=(50)))';
 EXEC [QuanLyBanHang].sys.sp_executesql N'ALTER TABLE [dbo].[ThuongHieu] CHECK CONSTRAINT [CK_TH_Ten]';
 EXEC [QuanLyBanHang].sys.sp_executesql N'-- Dữ liệu tối thiểu cho bản chạy thử.
INSERT INTO dbo.DanhMuc (maDM, tenDM) VALUES
(''DM001'', N''Điện thoại''), (''DM002'', N''Laptop''), (''DM003'', N''Tivi''),
(''DM004'', N''Tủ lạnh''), (''DM005'', N''Máy giặt''), (''DM006'', N''Máy lạnh''),
(''DM007'', N''Phụ kiện điện tử'');

INSERT INTO dbo.NhanVien (maNV, hoTen, sdtNV, soCCCD, ngaySinh, ngayVaoLam, diaChi, dangLamViec)
VALUES (''NV0001'', N''Quản lý GreenHome'', ''0900000000'', ''000000000001'', ''19950101'', CAST(GETDATE() AS date), NULL, 1);

INSERT INTO dbo.TaiKhoan (maTK, tenDangNhap, matKhau, vaiTro, maNV, dangHoatDong)
VALUES (''TK0001'', ''admin'', ''PBKDF2$120000$LJ0meXXWxI4ovf/608BuCQ==$CzDxilJB6RwLe3bu+LnC3zKD1GY7uJd1JcKxw6s8OHg='', ''QUAN_LY'', ''NV0001'', 1);
';
 END;
 EXEC [QuanLyBanHang].sys.sp_executesql N'IF EXISTS (SELECT 1 FROM (VALUES (N''maCa''),(N''tenCa''),(N''gioBatDau''),(N''gioKetThuc'')) c(n) WHERE COL_LENGTH(''dbo.CaLamViec'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.CaLamViec của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''idChiTietDH''),(N''maDH''),(N''maSP''),(N''soLuong''),(N''donGia''),(N''serialIMEI'')) c(n) WHERE COL_LENGTH(''dbo.ChiTietDonHang'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.ChiTietDonHang của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''idChiTietHD''),(N''maHD''),(N''maSP''),(N''maKM''),(N''soLuong''),(N''donGia''),(N''serialIMEI''),(N''soTienGiam'')) c(n) WHERE COL_LENGTH(''dbo.ChiTietHoaDon'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.ChiTietHoaDon của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''idChiTietDoiTra''),(N''maPhieuDoiTra''),(N''maHD''),(N''idChiTietHD''),(N''maSP''),(N''soLuong''),(N''donGia''),(N''serialIMEI''),(N''lyDo''),(N''tinhTrangKhiDoiTra''),(N''hinhThucXuLy'')) c(n) WHERE COL_LENGTH(''dbo.ChiTietPhieuDoiTra'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.ChiTietPhieuDoiTra của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maDM''),(N''tenDM'')) c(n) WHERE COL_LENGTH(''dbo.DanhMuc'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.DanhMuc của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maDH''),(N''ngayDat''),(N''trangThaiDH''),(N''ghiChu''),(N''maKH''),(N''maNV'')) c(n) WHERE COL_LENGTH(''dbo.DonHang'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.DonHang của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maHD''),(N''ngayLap''),(N''phuongThucThanhToan''),(N''maKH''),(N''maNV''),(N''maDH'')) c(n) WHERE COL_LENGTH(''dbo.HoaDon'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.HoaDon của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maKH''),(N''tenKH''),(N''sdtKH''),(N''diaChi''),(N''diemTichLuy'')) c(n) WHERE COL_LENGTH(''dbo.KhachHang'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.KhachHang của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maKM''),(N''tenKM''),(N''phanTramGiam''),(N''ngayBatDau''),(N''ngayKetThuc''),(N''moTaKM''),(N''loaiGiam''),(N''soTienGiam'')) c(n) WHERE COL_LENGTH(''dbo.KhuyenMai'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.KhuyenMai của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maLichSu''),(N''maSP''),(N''hanhDong''),(N''maNV''),(N''thoiDiem''),(N''duLieuCu''),(N''duLieuMoi'')) c(n) WHERE COL_LENGTH(''dbo.LichSuSanPham'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.LichSuSanPham của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maNV''),(N''hoTen''),(N''sdtNV''),(N''soCCCD''),(N''ngaySinh''),(N''ngayVaoLam''),(N''diaChi''),(N''dangLamViec'')) c(n) WHERE COL_LENGTH(''dbo.NhanVien'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.NhanVien của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maPhanCong''),(N''ngayLam''),(N''maNV''),(N''maCa''),(N''trangThai'')) c(n) WHERE COL_LENGTH(''dbo.PhanCongCa'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.PhanCongCa của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maPhieuDoiTra''),(N''ngayLap''),(N''trangThaiDuyet''),(N''maHD''),(N''maNV''),(N''tenLienHe''),(N''sdtLienHe''),(N''hienTrang''),(N''phuKien''),(N''maNVDuyet'')) c(n) WHERE COL_LENGTH(''dbo.PhieuDoiTra'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.PhieuDoiTra của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maSP''),(N''tenSP''),(N''maDM''),(N''maTH''),(N''thongSoKyThuat''),(N''giaNiemYet''),(N''model''),(N''donViTinh''),(N''moTaSP''),(N''tinhTrang''),(N''soLuongTon''),(N''soLuongLoi''),(N''soLuongTonToiThieu''),(N''hinhAnh'')) c(n) WHERE COL_LENGTH(''dbo.SanPham'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.SanPham của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maSP''),(N''maKM'')) c(n) WHERE COL_LENGTH(''dbo.SanPhamKhuyenMai'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.SanPhamKhuyenMai của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maTK''),(N''tenDangNhap''),(N''matKhau''),(N''vaiTro''),(N''maNV''),(N''dangHoatDong'')) c(n) WHERE COL_LENGTH(''dbo.TaiKhoan'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.TaiKhoan của GreenHome.'',1;
IF EXISTS (SELECT 1 FROM (VALUES (N''maTH''),(N''tenTH''),(N''moTaThuongHieu'')) c(n) WHERE COL_LENGTH(''dbo.ThuongHieu'',c.n) IS NULL) THROW 51002,N''Thiếu bảng hoặc cột dbo.ThuongHieu của GreenHome.'',1;
IF OBJECT_ID(''dbo.Seq_GH_DH'',''SO'') IS NULL THROW 51003,N''Thiếu sequence Seq_GH_DH.'',1;
IF OBJECT_ID(''dbo.Seq_GH_KH'',''SO'') IS NULL THROW 51003,N''Thiếu sequence Seq_GH_KH.'',1;
IF OBJECT_ID(''dbo.Seq_GH_HD'',''SO'') IS NULL THROW 51003,N''Thiếu sequence Seq_GH_HD.'',1;';
 EXEC [QuanLyBanHang].sys.sp_executesql N'-- Ràng buộc CHECK gốc của bản xuất SSMS có tên do SQL Server sinh.
DECLARE @old sysname;
SELECT @old=name FROM sys.check_constraints
WHERE parent_object_id=OBJECT_ID(N''dbo.PhieuDoiTra'')
  AND REPLACE(REPLACE(REPLACE(REPLACE(definition, ''('', ''''), '')'', ''''), ''['', ''''), '']'', '''') =
      ''trangThaiDuyet=''''TU_CHOI'''' OR trangThaiDuyet=''''DA_DUYET'''' OR trangThaiDuyet=''''CHO_DUYET'''''';
IF @old IS NOT NULL
BEGIN
    DECLARE @drop nvarchar(400) = N''ALTER TABLE dbo.PhieuDoiTra DROP CONSTRAINT '' + QUOTENAME(@old);
    EXEC sp_executesql @drop;
END;
IF OBJECT_ID(N''dbo.CK_GH_Phieu_TrangThai'', N''C'') IS NULL
    ALTER TABLE dbo.PhieuDoiTra WITH CHECK ADD CONSTRAINT CK_GH_Phieu_TrangThai
        CHECK (trangThaiDuyet IN (''CHO_DUYET'',''DA_DUYET'',''TU_CHOI'',''HOAN_TAT''));

IF OBJECT_ID(N''dbo.LichSuDoiTra'', N''U'') IS NULL
BEGIN
    CREATE TABLE dbo.LichSuDoiTra (
        id bigint IDENTITY PRIMARY KEY,
        maPhieu varchar(20) NOT NULL REFERENCES dbo.PhieuDoiTra(maPhieuDoiTra),
        suKien varchar(20) NOT NULL,
        maNV varchar(20) NOT NULL REFERENCES dbo.NhanVien(maNV),
        thoiDiem datetime2 NULL DEFAULT SYSDATETIME(),
        tienHoanDuKien decimal(18,2) NULL,
        tienHoanThucTe decimal(18,2) NULL,
        chenhLechDoi decimal(18,2) NULL,
        phuongThuc varchar(30) NULL
    );
    CREATE UNIQUE INDEX UX_GH_HoanTat ON dbo.LichSuDoiTra(maPhieu) WHERE suKien=''HOAN_TAT'';
    INSERT dbo.LichSuDoiTra(maPhieu,suKien,maNV,thoiDiem)
       SELECT maPhieuDoiTra,''TIEP_NHAN'',maNV,ngayLap FROM dbo.PhieuDoiTra;
    INSERT dbo.LichSuDoiTra(maPhieu,suKien,maNV,thoiDiem)
       SELECT maPhieuDoiTra,trangThaiDuyet,maNVDuyet,NULL FROM dbo.PhieuDoiTra
       WHERE trangThaiDuyet IN (''DA_DUYET'',''TU_CHOI'') AND maNVDuyet IS NOT NULL;
END;
IF COL_LENGTH(''dbo.ChiTietPhieuDoiTra'',''giaTriGoc'') IS NULL
    ALTER TABLE dbo.ChiTietPhieuDoiTra ADD giaTriGoc decimal(18,2) NULL;
IF COL_LENGTH(''dbo.ChiTietPhieuDoiTra'',''maSPThayThe'') IS NULL
    ALTER TABLE dbo.ChiTietPhieuDoiTra ADD maSPThayThe varchar(20) NULL REFERENCES dbo.SanPham(maSP);
IF COL_LENGTH(''dbo.ChiTietPhieuDoiTra'',''giaThayThe'') IS NULL
    ALTER TABLE dbo.ChiTietPhieuDoiTra ADD giaThayThe decimal(18,2) NULL;
IF OBJECT_ID(N''dbo.MaQuetSanPham'', N''U'') IS NULL
    CREATE TABLE dbo.MaQuetSanPham (
        maQuet nvarchar(128) PRIMARY KEY,
        maSP varchar(20) NOT NULL REFERENCES dbo.SanPham(maSP) ON DELETE CASCADE
    );';
 EXEC [QuanLyBanHang].sys.sp_executesql N'-- Sản phẩm dùng đúng mã được in trên tem.
DECLARE @sp TABLE(maSP varchar(20) PRIMARY KEY,tenSP nvarchar(100),gia int,ton int);
INSERT @sp VALUES (''SP9801'',N''Chuột không dây M1'',180000,20),(''SP9802'',N''Bàn phím USB K2'',320000,15),
 (''SP9803'',N''Tai nghe có dây H3'',250000,18),(''SP9804'',N''Cáp USB-C C4'',90000,30);
IF NOT EXISTS (SELECT 1 FROM dbo.DanhMuc WHERE maDM=''DM007'')
 INSERT dbo.DanhMuc(maDM,tenDM) VALUES (''DM007'',N''Phụ kiện điện tử'');
IF NOT EXISTS (SELECT 1 FROM dbo.ThuongHieu WHERE maTH=''TH981'')
 INSERT dbo.ThuongHieu(maTH,tenTH) VALUES (''TH981'',N''GreenHome Demo'');
IF EXISTS (SELECT 1 FROM dbo.ThuongHieu WHERE maTH=''TH981'' AND tenTH<>N''GreenHome Demo'')
 THROW 51005,N''Trùng mã thương hiệu TH981 với dữ liệu hiện có.'',1;
IF EXISTS (SELECT 1 FROM @sp d JOIN dbo.SanPham s ON s.maSP=d.maSP WHERE s.tenSP<>d.tenSP OR s.maTH<>''TH981'')
 THROW 51006,N''Trùng mã sản phẩm mẫu với một sản phẩm khác.'',1;
IF EXISTS (SELECT 1 FROM @sp d JOIN dbo.MaQuetSanPham m ON d.maSP=m.maQuet WHERE m.maSP<>d.maSP)
 THROW 51007,N''Một tem mẫu đã gắn với sản phẩm khác.'',1;
INSERT dbo.SanPham(maSP,tenSP,maDM,maTH,giaNiemYet,donViTinh,tinhTrang,soLuongTon,soLuongLoi,soLuongTonToiThieu)
 SELECT maSP,tenSP,''DM007'',''TH981'',gia,N''Cái'',''DANG_KINH_DOANH'',ton,0,2 FROM @sp d
 WHERE NOT EXISTS (SELECT 1 FROM dbo.SanPham s WHERE s.maSP=d.maSP);
INSERT dbo.MaQuetSanPham(maQuet,maSP)
 SELECT maSP,maSP FROM @sp d WHERE NOT EXISTS (SELECT 1 FROM dbo.MaQuetSanPham m WHERE m.maQuet=d.maSP);
DECLARE @nv varchar(20),@tk varchar(20),@kh varchar(20),@hd varchar(20),@dh varchar(20);
SELECT @nv=t.maNV FROM dbo.TaiKhoan t JOIN dbo.NhanVien n ON n.maNV=t.maNV
 WHERE t.tenDangNhap=''banhang'' AND t.vaiTro=''NHAN_VIEN_BAN_HANG'' AND t.dangHoatDong=1 AND n.dangLamViec=1;
IF @nv IS NULL BEGIN
 IF EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE tenDangNhap=''banhang'')
  THROW 51008,N''Tên banhang đã thuộc tài khoản khác vai trò hoặc đã dừng hoạt động.'',1;
 DECLARE @n int=(SELECT ISNULL(MAX(TRY_CONVERT(int,RIGHT(maNV,4))),0)+1 FROM dbo.NhanVien);
 DECLARE @t int=(SELECT ISNULL(MAX(TRY_CONVERT(int,RIGHT(maTK,4))),0)+1 FROM dbo.TaiKhoan);
 IF @n>9999 OR @t>9999 THROW 51009,N''Hết mã nhân viên hoặc tài khoản.'',1;
 SET @nv=''NV''+RIGHT(''0000''+CAST(@n AS varchar(5)),4);
 SET @tk=''TK''+RIGHT(''0000''+CAST(@t AS varchar(5)),4);
 INSERT dbo.NhanVien(maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,dangLamViec)
 VALUES (@nv,N''Nhân viên Demo'',''0900980002'',''000000980002'',''20000101'',CAST(GETDATE() AS date),1);
 INSERT dbo.TaiKhoan(maTK,tenDangNhap,matKhau,vaiTro,maNV,dangHoatDong)
 VALUES (@tk,''banhang'',''PBKDF2$120000$Cbf3hFcMrM1K8dwru+ZOKQ==$8cTRhW36WKZ63eb0JYSO/1SmVpjqeBDrWo4ALy/fahA='',''NHAN_VIEN_BAN_HANG'',@nv,1);
END;
DECLARE @tag nvarchar(100)=N''GreenHome • hóa đơn mẫu quét mã SP9801 và SP9802'';
SELECT @hd=h.maHD FROM dbo.HoaDon h JOIN dbo.DonHang d ON d.maDH=h.maDH WHERE d.ghiChu=@tag;
IF @hd IS NULL BEGIN
 DECLARE @now datetime2=SYSDATETIME();
 SET @kh=''KH''+RIGHT(''00000000''+CAST(NEXT VALUE FOR dbo.Seq_GH_KH AS varchar(8)),8);
 SET @dh=''DH''+RIGHT(''00000000''+CAST(NEXT VALUE FOR dbo.Seq_GH_DH AS varchar(8)),8);
 SET @hd=''HD''+RIGHT(''00000000''+CAST(NEXT VALUE FOR dbo.Seq_GH_HD AS varchar(8)),8);
 INSERT dbo.KhachHang(maKH,tenKH,sdtKH,diemTichLuy) VALUES (@kh,N''Khách hàng Demo'',''0900980003'',5);
 INSERT dbo.DonHang(maDH,ngayDat,trangThaiDH,maKH,maNV,ghiChu) VALUES (@dh,@now,''HOAN_THANH'',@kh,@nv,@tag);
 INSERT dbo.ChiTietDonHang(maDH,maSP,soLuong,donGia) VALUES (@dh,''SP9801'',1,180000),(@dh,''SP9802'',1,320000);
 INSERT dbo.HoaDon(maHD,ngayLap,phuongThucThanhToan,maKH,maNV,maDH) VALUES (@hd,@now,''TIEN_MAT'',@kh,@nv,@dh);
 INSERT dbo.ChiTietHoaDon(maHD,maSP,soLuong,donGia,soTienGiam) VALUES (@hd,''SP9801'',1,180000,0),(@hd,''SP9802'',1,320000,0);
END;
SELECT DB_NAME() AS CoSoDuLieu,@hd AS MaHoaDonThuDoiTra;
SELECT s.maSP,s.tenSP,s.giaNiemYet,s.soLuongTon FROM dbo.SanPham s JOIN @sp d ON s.maSP=d.maSP ORDER BY s.maSP;';
 COMMIT;
 PRINT N'Đã hoàn tất cài đặt QuanLyBanHang.';
END TRY
BEGIN CATCH
 IF XACT_STATE()<>0 ROLLBACK;
 THROW;
END CATCH;
