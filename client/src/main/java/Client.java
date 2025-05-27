import java.util.Map;
import java.util.Scanner;
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
            Demo.PrinterPrx printer = Demo.PrinterPrx.uncheckedCast(communicator.stringToProxy("Printer:default -h localhost -p 10011"));;

            MessageStorage storage = new MessageStorage();

            Map<String, String> pendientes = storage.getAll();
            for (Map.Entry<String, String> entry : pendientes.entrySet()) {
                System.out.println("Reintentando mensaje pendiente: " + entry.getKey());
                Thread t = new MessageSender(entry.getKey(), entry.getValue(), printer, ackServicePrx, ackService, storage);
                t.start();
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Cliente listo. Escribe mensajes para enviarlos. Escribe 'salir' para terminar.");

            while (true) {
                System.out.print("Mensaje: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("salir")) {
                    break;
                }

                String newId = UUID.randomUUID().toString();
                Thread sender = new MessageSender(newId, input, printer, ackServicePrx, ackService, storage);
                sender.start(); 
            }

            scanner.close();
            System.out.println("Cliente finalizado.");
        }
    }
}