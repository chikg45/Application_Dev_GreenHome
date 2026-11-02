package dao;

import connectDB.Jdbc;
import connectDB.NguonKetNoi;
import database.DatabaseConnection;
import entity.CaLamViec;
import java.sql.*;
import java.util.*;

/** Ca sang / chieu / toi lay tu CSDL, khong hard-code thoi gian trong DAO. */
public class CaLamViecDAO {
    private final NguonKetNoi nguon;
    public CaLamViecDAO() { this(DatabaseConnection::getConnection); }
    public CaLamViecDAO(NguonKetNoi nguon) { this.nguon = Objects.requireNonNull(nguon); }
    public List<CaLamViec> layTatCa() {
        return Jdbc.doc(nguon, c -> {
            List<CaLamViec> result = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT maCa,tenCa,gioBatDau,gioKetThuc FROM dbo.CaLamViec ORDER BY gioBatDau");
                 ResultSet r = ps.executeQuery()) {
                while (r.next()) result.add(new CaLamViec(r.getString(1),r.getString(2),
                        r.getTime(3).toLocalTime(),r.getTime(4).toLocalTime()));
            }
            return result;
        });
    }
}
