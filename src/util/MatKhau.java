package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Kiem tra hash trong file demo: PBKDF2$120000$base64(salt)$base64(hash). */
public final class MatKhau {
    private MatKhau() { }

    /** Bam mat khau moi de luu DB, moi tai khoan mot salt ngau nhien. */
    public static String taoHash(char[] matKhau) {
        if (matKhau == null || matKhau.length < 6)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(matKhau, salt, 120000, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            try {
                return "PBKDF2$120000$" + Base64.getEncoder().encodeToString(salt)
                        + "$" + Base64.getEncoder().encodeToString(hash);
            } finally { Arrays.fill(hash, (byte) 0); }
        } catch (Exception ex) { throw new IllegalStateException("Không thể băm mật khẩu.", ex); }
        finally { spec.clearPassword(); Arrays.fill(salt, (byte) 0); }
    }

    public static boolean khop(char[] matKhau, String giaTriLuu) {
        if (matKhau == null || giaTriLuu == null) return false;
        try {
            String[] parts = giaTriLuu.split("\\$", -1);
            if (parts.length != 4 || !"PBKDF2".equals(parts[0])) return false;
            int rounds = Integer.parseInt(parts[1]);
            if (rounds < 100_000 || rounds > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] hash = Base64.getDecoder().decode(parts[3]);
            if (salt.length < 16 || hash.length != 32) return false;
            PBEKeySpec key = new PBEKeySpec(matKhau, salt, rounds, hash.length * 8);
            try {
                byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(key).getEncoded();
                try { return MessageDigest.isEqual(hash, actual); }
                finally { Arrays.fill(actual, (byte) 0); }
            } finally { key.clearPassword(); }
        } catch (Exception e) { return false; }
    }
}
