import java.util.HashSet;
import java.util.Set;

import Demo.AckServicePrx;

public class PrinterI implements Demo.Printer
{
    private final Set<String> seen = new HashSet<>();

    public void printString(Demo.Message msg, AckServicePrx ackProxy, com.zeroc.Ice.Current current)
    {
        if (seen.contains(msg.id)) return;

        System.out.println("Recibido: " + msg.text);
        seen.add(msg.id);

        ackProxy.confirm(msg.id);
    }
}