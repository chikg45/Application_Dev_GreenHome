package util;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;

/** Chạy JDBC trên luồng nền. Hộp chờ ngăn người dùng sửa giỏ khi đang lưu. */
public final class SwingTask {
    private SwingTask() {}
    public static <T> void chay(Component parent, String title, Callable<T> viec, Consumer<T> xong) {
        Window owner = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog wait = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        JProgressBar bar = new JProgressBar(); bar.setIndeterminate(true);
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        body.add(new JLabel(title + "…"), BorderLayout.NORTH); body.add(bar);
        wait.setContentPane(body); wait.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        wait.setSize(370, 115); wait.setLocationRelativeTo(parent);
        new SwingWorker<T, Void>() {
            protected T doInBackground() throws Exception { return viec.call(); }
            protected void done() {
                wait.dispose();
                try { xong.accept(get()); }
                catch (Exception e) {
                    Throwable cause = e;
                    if (cause instanceof java.util.concurrent.ExecutionException && cause.getCause() != null) cause = cause.getCause();
                    JOptionPane.showMessageDialog(parent, cause.getMessage(), "Green Home", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
        wait.setVisible(true);
    }
}
