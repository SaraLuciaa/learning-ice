import java.util.logging.Logger;

public class MessageSender extends Thread {
    private final String msgId;
    private final String text;
    private final Demo.PrinterPrx printer;
    private final Demo.AckServicePrx ackServicePrx;
    private final AckServiceI ackService;
    private final MessageStorage storage;
    private final Logger log = AppLogger.get();

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
                log.info("Message sent: \"" + msg.text + "\" [ID: " + msgId + "]");
                Thread.sleep(1000);

                if (ackService.isAcked(msgId)) {
                    log.info("ACK received for message ID: " + msgId);
                    storage.remove(msgId);
                    break;
                } else {
                    log.fine("Waiting for ACK for ID: " + msgId);
                }
            } catch (Exception e) {
                log.warning("Retrying after send failure (ID: " + msgId + "): " + e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.warning("Sender thread interrupted: " + ex.getMessage());
                    break;
                }
            }
        }
    }
}