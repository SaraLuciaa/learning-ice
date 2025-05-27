import java.util.Scanner;
import java.util.logging.Logger;

import Demo.Message;

public class Client {
    public static void main(String[] args) {
        Logger log = AppLogger.get();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                    "AckAdapter", "default -p 10010");

            AckServiceI ackService = new AckServiceI();
            com.zeroc.Ice.ObjectPrx callback = adapter.add(ackService,
                    com.zeroc.Ice.Util.stringToIdentity("AckService"));
            adapter.activate();

            Demo.AckServicePrx ackServicePrx = Demo.AckServicePrx.uncheckedCast(callback);
            Demo.PrinterPrx printer = Demo.PrinterPrx.uncheckedCast(
                    communicator.stringToProxy("Printer:default -h localhost -p 10011"));

            MessageStorage storage = new MessageStorage();

            MessageSender t = new MessageSender(printer, ackServicePrx, ackService, storage);
            t.start();

            Scanner scanner = new Scanner(System.in);
            String input = "";
            System.out.println("Client is ready. Type messages to send. Type 'exit' to quit.");

            while(!input.equalsIgnoreCase("exit")) {
                System.out.print("Message: ");
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                String newId = java.util.UUID.randomUUID().toString();
                Demo.Message msg = new Demo.Message(newId, input);
                t.addMessage(msg);
            }

            scanner.close();
            System.out.println("Client terminated.");
        } catch (Exception e) {
            log.severe("Fatal error in client: " + e.getMessage());
        }
    }
}