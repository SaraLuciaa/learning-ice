import java.util.concurrent.ConcurrentHashMap;

public class AckServiceI implements Demo.AckService{
    private final ConcurrentHashMap<String, Boolean> acks = new ConcurrentHashMap<>();

    @Override
    public void confirm(String messageId, com.zeroc.Ice.Current current) {
        System.out.println("ACK recibido: " + messageId);
        acks.put(messageId, true);
    }

    public boolean isAcked(String messageId) {
        return acks.getOrDefault(messageId, false);
    }
}