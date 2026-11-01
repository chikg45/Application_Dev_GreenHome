package connectdb;

import exception.LoiDuLieu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

/** Kết nối CSDL GreenHome. Mỗi thao tác mở connection riêng. */
public final class ConnectDB implements NguonKetNoi {
    public static final String TEN_CSDL = "GreenHome";
    public static final String URL_MAC_DINH =
        "jdbc:sqlserver://localhost:1433;databaseName=" + TEN_CSDL
        + ";encrypt=true;trustServerCertificate=true;loginTimeout=5;socketTimeout=15000";
    private final Properties config=new Properties();
    public ConnectDB() {
        this(new Properties());
        Path file=Path.of(System.getProperty("greenhome.config","config/db.properties"));
        try(Reader reader=Files.newBufferedReader(file,StandardCharsets.UTF_8)){config.load(reader);}
        catch(IOException ex){throw new LoiDuLieu("Không đọc được "+file+". Hãy cấu hình theo db.properties.example.",ex);}
        for(String key:new String[]{"url","user","password"}) {
            String value=System.getenv("GREENHOME_DB_"+key.toUpperCase(java.util.Locale.ROOT));
            if(value!=null)config.setProperty(key,value);
        }
    }
    /** Cấu hình được truyền vào có thể ghi đè URL mặc định khi tích hợp/kiểm thử. */
    public ConnectDB(Properties p){
        config.setProperty("url", URL_MAC_DINH);
        config.putAll(p);
    }
    @Override public Connection mo() throws SQLException {
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch(ClassNotFoundException ex){throw new SQLException("Thiếu Microsoft JDBC. Kiểm tra JAR trong lib và Build Path.",ex);}
        String url=config.getProperty("url",URL_MAC_DINH).trim();
        if(!url.startsWith("jdbc:sqlserver:"))throw new SQLException("URL phải bắt đầu bằng jdbc:sqlserver:.");
        Properties login=new Properties();
        String user=config.getProperty("user","").trim();
        if(!user.isEmpty()){login.setProperty("user",user);login.setProperty("password",config.getProperty("password",""));}
        login.setProperty("applicationName","GreenHome");
        return DriverManager.getConnection(url,login);
    }
}
