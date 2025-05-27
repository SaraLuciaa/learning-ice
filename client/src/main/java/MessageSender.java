import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import Demo.AckServicePrx;
import Demo.Message;
import Demo.PrinterPrx;

public class MessageSender extends Thread {
    private final List<Message> messageQueue = new ArrayList<>();
    private final PrinterPrx printer;
    private final AckServicePrx ackServicePrx;
    private final AckServiceI ackService;
    private final MessageStorage storage;
    private final Logger log = AppLogger.get();

    public MessageSender(PrinterPrx printer, AckServicePrx ackServicePrx, AckServiceI ackService, MessageStorage storage) {
        this.printer = printer;
        this.ackServicePrx = ackServicePrx;
        this.ackService = ackService;
        this.storage = storage;
        synchronized (messageQueue) {
            messageQueue.addAll(storage.getAll());
        }
    }

    public void addMessage(Message msg) {
        synchronized (messageQueue) {
            messageQueue.add(msg);
            storage.add(msg);
            messageQueue.notifyAll(); 
        }
        log.info("Message added to queue: \"" + msg.text + "\" [ID: " + msg.id + "]");
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Message msg = null;
            synchronized (messageQueue) {
                while (messageQueue.isEmpty()) {
                    try {
                        messageQueue.wait(); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                msg = messageQueue.get(0);
            }

            if (msg != null) {
                sendMessage(msg);
            }
        }
    }

    private void sendMessage(Message msg) {
        int attempts = 0;
        while (attempts < 10 && !Thread.currentThread().isInterrupted()) {
            try {
                printer.printString(msg, ackServicePrx);
                log.info("Message sended: \"" + msg.text + "\" [ID: " + msg.id + "]");
                Thread.sleep(1000);

                if (ackService.isAcked(msg.id)) {
                    log.info("ACK received for message ID: " + msg.id);
                    synchronized (messageQueue) {
                        messageQueue.remove(msg);
                    }
                    storage.remove(msg.id);
                    break;
                } else {
                    log.fine("Waiting ACK: " + msg.id);
                }

            } catch (com.zeroc.Ice.ConnectionRefusedException e) {
                log.warning("Retrying after send failure");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            } catch (Exception e) {
                log.severe("Error sending message: " + e.getMessage());
                return;
            }
            attempts++;
        }
    }
}