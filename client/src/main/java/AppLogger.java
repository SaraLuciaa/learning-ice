import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("ReliableClient");

    static {
        try {
            Handler fileHandler = new FileHandler("system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // do not print to console
        } catch (IOException e) {
            System.err.println("⚠ Failed to initialize logger: " + e.getMessage());
        }
    }

    public static Logger get() {
        return logger;
    }
}