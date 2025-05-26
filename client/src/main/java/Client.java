import java.util.UUID;

public class Client
{
    public static void main(String[] args)
    {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
        {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("AckAdapter", "default -p 10010");

            Demo.AckService ackService = new AckServiceI();
            adapter.add(ackService, com.zeroc.Ice.Util.stringToIdentity("AckService"));
            adapter.activate();

            Demo.PrinterPrx printer = Demo.PrinterPrx.checkedCast(communicator.stringToProxy("Printer:default -h localhost -p 10011"));

            String msgId = UUID.randomUUID().toString();
            Demo.Message msg = new Demo.Message(msgId, "Hello World!");
            try {
                while (true) {
                    printer.printString(msg);
                    System.out.println("Sent: " + msg.text);
                    Thread.sleep(1000); 

                    com.zeroc.Ice.Current dummy = new com.zeroc.Ice.Current();
                    if (ackService.isAcked(msgId, dummy))
                    {
                        System.out.println("Message " + msgId + " acknowledged.");
                        break;
                    }
                    else
                    {
                        System.out.println("Message " + msgId + " not acknowledged yet.");
                    }
                } 
            } catch (InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
            }             
        }
    }
}