import java.util.UUID;

public class Client {
    public static void main(String[] args) {
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("AckAdapter",
                    "default -p 10010");

            AckServiceI ackService = new AckServiceI();
            com.zeroc.Ice.ObjectPrx callback = adapter.add(ackService,
                    com.zeroc.Ice.Util.stringToIdentity("AckService"));
            adapter.activate();

            Demo.AckServicePrx ackServicePrx = Demo.AckServicePrx.uncheckedCast(callback);

            Demo.PrinterPrx printer = Demo.PrinterPrx
                    .uncheckedCast(communicator.stringToProxy("Printer:default -h localhost -p 10011"));

            String msgId = UUID.randomUUID().toString();
            Demo.Message msg = new Demo.Message(msgId, "Hello World!");
            while (true) {
                try {
                    printer.printString(msg, ackServicePrx);
                    System.out.println("Sent: " + msg.text);
                    Thread.sleep(1000);

                    if (ackService.isAcked(msgId)) {
                        System.out.println("Message " + msgId + " acknowledged.");
                        break;
                    } else {
                        System.out.println("Message " + msgId + " not acknowledged yet.");
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }
}