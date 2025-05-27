public class MessageSender extends Thread {
    private final String msgId;
    private final String text;
    private final Demo.PrinterPrx printer;
    private final Demo.AckServicePrx ackServicePrx;
    private final AckServiceI ackService;
    private final MessageStorage storage;

    public MessageSender(String msgId, String text, Demo.PrinterPrx printer,
                         Demo.AckServicePrx ackServicePrx,
                         AckServiceI ackService,
                         MessageStorage storage) {
        this.msgId = msgId;
        this.text = text;
        this.printer = printer;
        this.ackServicePrx = ackServicePrx;
        this.ackService = ackService;
        this.storage = storage;
    }

    @Override
    public void run() {
        Demo.Message msg = new Demo.Message(msgId, text);

        storage.add(msgId, text);

        while (true) {
            try {
                printer.printString(msg, ackServicePrx);
                System.out.println("Sent: " + msg.text + " (id=" + msgId + ")");
                Thread.sleep(1000);

                if (ackService.isAcked(msgId)) {
                    System.out.println("✔ ACK recibido para mensaje: " + msgId);
                    storage.remove(msgId);
                    break;
                } else {
                    System.out.println("Esperando ACK para: " + msgId);
                }
            } catch (Exception e) {
                System.err.println("Error enviando mensaje " + msgId + ": " + e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}