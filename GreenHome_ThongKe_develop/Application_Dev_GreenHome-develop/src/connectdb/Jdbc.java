package connectdb;

import exception.LoiDuLieu;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Locale;

public final class Jdbc {
    private Jdbc() { }
    @FunctionalInterface public interface CongViec<T>{T thucHien(Connection c)throws SQLException;}
    public static <T>T doc(NguonKetNoi nguon,CongViec<T> action) {
        try(Connection c=nguon.mo()){return action.thucHien(c);}
        catch(SQLException ex){throw loi(ex);}
    }
    public static <T>T giaoDich(NguonKetNoi nguon,CongViec<T> action) {
        try(Connection c=nguon.mo()) {
            c.setAutoCommit(false);
            try(Statement s=c.createStatement()){s.execute("SET XACT_ABORT ON; SET LOCK_TIMEOUT 15000;");}
            try {
                T value=action.thucHien(c);c.commit();return value;
            } catch(SQLException|RuntimeException ex) {
                try{c.rollback();}catch(SQLException rollback){ex.addSuppressed(rollback);}
                if(ex instanceof SQLException sql)throw loi(sql);
                throw ex;
            }
        } catch(SQLException ex){throw loi(ex);}
    }
    public static LoiDuLieu loi(SQLException ex) {
        if(ex.getErrorCode()==2601||ex.getErrorCode()==2627)
            return new LoiDuLieu("Mã, số điện thoại hoặc giao dịch đã tồn tại. Hãy tải lại dữ liệu.",ex);
        if(ex.getErrorCode()==1205||ex.getErrorCode()==1222)
            return new LoiDuLieu("Dữ liệu đang được quầy khác xử lý. Hãy kiểm tra đơn rồi thử lại.",ex);
        return new LoiDuLieu("Không thực hiện được thao tác SQL Server: "+ex.getMessage(),ex);
    }
    public static void bind(PreparedStatement s,Object... args)throws SQLException {
        for(int i=0;i<args.length;i++) {
            Object v=args[i];
            if(v instanceof LocalDateTime time)s.setTimestamp(i+1,Timestamp.valueOf(time));
            else s.setObject(i+1,v);
        }
    }
    public static int capNhat(Connection c,String sql,Object... args)throws SQLException {
        try(PreparedStatement s=c.prepareStatement(sql)){bind(s,args);return s.executeUpdate();}
    }
    public static String maMoi(Connection c,String loai)throws SQLException {
        String sequence=switch(loai){case "KH"->"dbo.seq_KH";case "DH"->"dbo.seq_DH";case "HD"->"dbo.seq_HD";default->throw new IllegalArgumentException("Loại mã không hợp lệ.");};
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT NEXT VALUE FOR "+sequence)) {
            r.next();return String.format(Locale.ROOT,"%s%08d",loai,r.getInt(1));
        }
    }
    public static LocalDateTime bayGio(Connection c)throws SQLException {
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT SYSDATETIME()")){r.next();return r.getTimestamp(1).toLocalDateTime();}
    }
    public static String chuoi(ResultSet r,String column)throws SQLException {String value=r.getString(column);return value==null?"":value;}
}
