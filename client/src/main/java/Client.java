import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

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

            Map<String, String> pendingMessages = storage.getAll();
            for (Map.Entry<String, String> entry : pendingMessages.entrySet()) {
                log.info("Retrying pending message: " + entry.getKey());
                Thread t = new MessageSender(entry.getKey(), entry.getValue(), printer, ackServicePrx, ackService, storage);
                t.start();
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Client is ready. Type messages to send. Type 'exit' to quit.");

            while (true) {
                System.out.print("Message: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                String newId = UUID.randomUUID().toString();
                Thread sender = new MessageSender(newId, input, printer, ackServicePrx, ackService, storage);
                sender.start();
            }

            scanner.close();
            System.out.println("Client terminated.");
        } catch (Exception e) {
            log.severe("Fatal error in client: " + e.getMessage());
        }
    }
}