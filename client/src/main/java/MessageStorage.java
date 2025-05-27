import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import Demo.Message;

public class MessageStorage {
    private static final File FILE = new File("pending_messages.json");
    private final ArrayList<Message> pendingMessages = new ArrayList<>();
    private final Logger log = AppLogger.get();

    public MessageStorage() {
        load();
    }

    private void load() {
        if (!FILE.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }

            String content = json.toString()
                    .replaceAll("^\\[", "")
                    .replaceAll("]$", "");    

            for (String entry : content.split("},")) {
                if (!entry.trim().isEmpty()) {
                    String cleanedEntry = entry.endsWith("}") ? entry : entry + "}";
                    String[] parts = cleanedEntry.replaceAll("[{}\"]", "").split(",");
                    String id = null, text = null;
                    for (String part : parts) {
                        String[] keyValue = part.split(":", 2);
                        if (keyValue[0].trim().equals("id")) {
                            id = keyValue[1].trim();
                        } else if (keyValue[0].trim().equals("text")) {
                            text = keyValue[1].trim();
                        }
                    }
                    if (id != null && text != null) {
                        pendingMessages.add(new Message(id, text));
                    }
                }
            }
        } catch (IOException e) {
            log.severe("Error loading pending messages: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            writer.write("[\n");
            int i = 0;
            for (Message msg : pendingMessages) {
                writer.write("  {\"id\": \"" + escape(msg.id) + "\", \"text\": \"" + escape(msg.text) + "\"}");
                if (i++ < pendingMessages.size() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("]\n");
        } catch (IOException e) {
            log.severe("Error saving messages: " + e.getMessage());
        }
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    public synchronized void add(Message msg) {
        pendingMessages.add(msg);
        save();
    }

    public synchronized void remove(String id) {
        pendingMessages.removeIf(msg -> msg.id.equals(id));
        save();
    }

    public ArrayList<Message> getAll() {
        return new ArrayList<>(pendingMessages);
    }
}