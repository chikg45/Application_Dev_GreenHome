-- Chạy bằng tài khoản quản trị sau 01_TaoDatabaseGreenHome.sql.
-- Tạo SQL Login greenhome_app bằng SSMS trước; đặt mật khẩu riêng trên máy.
-- File này chỉ tạo Database User và cấp quyền cần cho BUS/DAO Bán hàng.
USE GreenHome;
GO
IF DB_NAME()<>N'GreenHome'
    THROW 50100,N'Chưa chọn đúng database GreenHome.',1;
IF SUSER_ID(N'greenhome_app') IS NULL
    THROW 50101,N'Hãy tạo SQL Login greenhome_app trong SSMS trước, sau đó chạy lại file này.',1;
IF NOT EXISTS(SELECT 1 FROM sys.database_principals WHERE name=N'greenhome_app')
    CREATE USER greenhome_app FOR LOGIN greenhome_app;
IF NOT EXISTS(SELECT 1 FROM sys.database_principals WHERE name=N'greenhome_banhang')
    CREATE ROLE greenhome_banhang;
IF NOT EXISTS(SELECT 1 FROM sys.database_role_members
              WHERE role_principal_id=DATABASE_PRINCIPAL_ID(N'greenhome_banhang')
                AND member_principal_id=DATABASE_PRINCIPAL_ID(N'greenhome_app'))
    ALTER ROLE greenhome_banhang ADD MEMBER greenhome_app;

GRANT SELECT ON dbo.NHAN_VIEN TO greenhome_banhang;
GRANT SELECT ON dbo.TAI_KHOAN TO greenhome_banhang;
GRANT SELECT ON dbo.KHACH_HANG TO greenhome_banhang;
GRANT SELECT ON dbo.THUONG_HIEU TO greenhome_banhang;
GRANT SELECT ON dbo.SAN_PHAM TO greenhome_banhang;
GRANT SELECT ON dbo.KHUYEN_MAI TO greenhome_banhang;
GRANT SELECT ON dbo.KHUYEN_MAI_SAN_PHAM TO greenhome_banhang;
GRANT SELECT ON dbo.UU_DAI_CA_NHAN TO greenhome_banhang;
GRANT SELECT ON dbo.DON_HANG TO greenhome_banhang;
GRANT SELECT ON dbo.CHI_TIET_DON_HANG TO greenhome_banhang;
GRANT SELECT ON dbo.HOA_DON TO greenhome_banhang;
GRANT SELECT ON dbo.CHI_TIET_HOA_DON TO greenhome_banhang;
GRANT SELECT ON dbo.LICH_SU_DIEM TO greenhome_banhang;
GRANT SELECT ON dbo.THIET_BI_SERIAL TO greenhome_banhang;
GRANT SELECT ON dbo.SERIAL_HOA_DON TO greenhome_banhang;

GRANT INSERT,UPDATE ON dbo.DON_HANG TO greenhome_banhang;
GRANT INSERT,UPDATE,DELETE ON dbo.CHI_TIET_DON_HANG TO greenhome_banhang;
GRANT INSERT ON dbo.HOA_DON TO greenhome_banhang;
GRANT INSERT ON dbo.CHI_TIET_HOA_DON TO greenhome_banhang;
GRANT INSERT ON dbo.SERIAL_HOA_DON TO greenhome_banhang;
GRANT INSERT ON dbo.LICH_SU_DIEM TO greenhome_banhang;
GRANT UPDATE ON dbo.SAN_PHAM TO greenhome_banhang;
GRANT UPDATE ON dbo.KHACH_HANG TO greenhome_banhang;
GRANT UPDATE ON dbo.THIET_BI_SERIAL TO greenhome_banhang;
GRANT UPDATE ON OBJECT::dbo.seq_DH TO greenhome_banhang;
GRANT UPDATE ON OBJECT::dbo.seq_HD TO greenhome_banhang;
GO
