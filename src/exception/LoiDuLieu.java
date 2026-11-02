package exception;
public final class LoiDuLieu extends IllegalStateException {
    public LoiDuLieu(String message,Throwable cause){super(message,cause);}
    public LoiDuLieu(String message){super(message);}
}
