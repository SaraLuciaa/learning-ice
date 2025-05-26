
public class Server
{
    public static void main(String[] args)
    {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
        {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("PrinterAdapter", "default -p 10011");
            com.zeroc.Ice.Object object = new PrinterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("Printer"));
            adapter.activate();
            System.out.println("Servidor iniciado. Esperando mensajes...");
            communicator.waitForShutdown();
        }
    }
}