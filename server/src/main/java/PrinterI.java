import java.util.HashSet;
import java.util.Set;

public class PrinterI implements Demo.Printer
{
    private final Demo.AckServicePrx ackProxy;
    private final Set<String> seen = new HashSet<>();

    public PrinterI(Demo.AckServicePrx ackProxy)
    {
        this.ackProxy = ackProxy;
    }

    public void printString(Demo.Message msg, com.zeroc.Ice.Current current)
    {
        if (seen.contains(msg.messageId)) return;

        System.out.println("Recibido: " + msg.text);
        seen.add(msg.messageId);

        ackProxy.confirm(msg.messageId);
    }
}