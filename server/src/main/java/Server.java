
public class Server
{
    public static void main(String[] args)
    {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
        {
            Demo.AckServicePrx ack = Demo.AckServicePrx.checkedCast( 
                communicator.stringToProxy("AckService:default -h localhost -p 10010"));;
            // int retries = 5;

            // while (ack == null && retries-- > 0) {
            //     try {
            //         ack = Demo.AckServicePrx.checkedCast( 
            //             communicator.stringToProxy("AckService:default -h localhost -p 10010"));
            //     } catch (com.zeroc.Ice.ObjectNotExistException e) {
            //         System.out.println("❌ AckService no disponible aún. Reintentando...");
            //         try {
            //             Thread.sleep(10000);
            //         } catch (InterruptedException e2) {
            //             System.err.println("Error al esperar: " + e2.getMessage());
            //             return;
            //         }
            //     }
            // }
            // if (ack == null) {
            //     System.out.println("🚨 No se pudo conectar a AckService. Abortando.");
            //     return;
            // }

            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("PrinterAdapter", "default -p 10011");
            com.zeroc.Ice.Object object = new PrinterI(ack);
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("Printer"));
            adapter.activate();
            System.out.println("Servidor iniciado. Esperando mensajes...");
            communicator.waitForShutdown();
        }
    }
}