-- Chạy sau 01_TaoDatabaseGreenHome.sql bằng sa. Không thêm dữ liệu mẫu.
USE GreenHome;
GO
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF DB_NAME()<>N'GreenHome' OR OBJECT_ID(N'dbo.PHIEU_DOI_TRA',N'U') IS NULL
    THROW 50200,N'Cần database GreenHome đúng schema của nhóm trước khi nâng cấp.',1;
IF COL_LENGTH('dbo.PHIEU_DOI_TRA','ngayHoanTat') IS NULL
BEGIN
    ALTER TABLE dbo.PHIEU_DOI_TRA ADD
        ngayDuyet datetime2(3) NULL, ngayHoanTat datetime2(3) NULL,
        maNVDuyet varchar(20) NULL REFERENCES dbo.NHAN_VIEN(maNV),
        maNVHoanTat varchar(20) NULL REFERENCES dbo.NHAN_VIEN(maNV),
        lyDoTuChoi nvarchar(255) NULL,
        nguoiLienHe nvarchar(100) NULL, sdtLienHe varchar(15) NULL,
        phuKien nvarchar(255) NULL,
        loiKyThuat bit NOT NULL CONSTRAINT DF_DT_LoiKT DEFAULT 0,
        nguyenVen bit NOT NULL CONSTRAINT DF_DT_Nguyen DEFAULT 0,
        duPhuKien bit NOT NULL CONSTRAINT DF_DT_PK DEFAULT 0,
        banTiep bit NULL;
END;
IF COL_LENGTH('dbo.CHI_TIET_PHIEU_DOI_TRA','maSPDoi') IS NULL
    ALTER TABLE dbo.CHI_TIET_PHIEU_DOI_TRA ADD
        maSPDoi varchar(20) NULL REFERENCES dbo.SAN_PHAM(maSP),
        tenSPDoi nvarchar(150) NULL;
-- Một hóa đơn chỉ có một yêu cầu đang xử lý/hoàn tất, cho phép gửi lại sau từ chối.
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID('dbo.PHIEU_DOI_TRA') AND name='UX_DT_HoaDonDangXuLy')
    CREATE UNIQUE INDEX UX_DT_HoaDonDangXuLy ON dbo.PHIEU_DOI_TRA(maHD)
        WHERE trangThaiDuyet<>'TuChoi';
IF OBJECT_ID(N'dbo.SERIAL_DOI_TRA',N'U') IS NULL
    CREATE TABLE dbo.SERIAL_DOI_TRA(
        maPhieuDoiTra varchar(20) NOT NULL REFERENCES dbo.PHIEU_DOI_TRA(maPhieuDoiTra),
        serialIMEI varchar(50) NOT NULL REFERENCES dbo.THIET_BI_SERIAL(serialIMEI),
        huong varchar(10) NOT NULL CHECK(huong IN('NhanLai','GiaoDoi')),
        PRIMARY KEY(maPhieuDoiTra,serialIMEI,huong)
    );
IF COL_LENGTH('dbo.THIET_BI_SERIAL','maPhieuDoiTra') IS NULL
BEGIN
    ALTER TABLE dbo.THIET_BI_SERIAL ADD maPhieuDoiTra varchar(20) NULL
        REFERENCES dbo.PHIEU_DOI_TRA(maPhieuDoiTra);
    ALTER TABLE dbo.THIET_BI_SERIAL DROP CONSTRAINT CK_Serial_HD;
    EXEC(N'ALTER TABLE dbo.THIET_BI_SERIAL ADD CONSTRAINT CK_Serial_HD CHECK(
        (tinhTrang=''DaBan'' AND
            ((maHD IS NOT NULL AND maPhieuDoiTra IS NULL)
             OR (maHD IS NULL AND maPhieuDoiTra IS NOT NULL)))
        OR (tinhTrang<>''DaBan'' AND maHD IS NULL AND maPhieuDoiTra IS NULL)
    );');
END;
COMMIT;
GO
