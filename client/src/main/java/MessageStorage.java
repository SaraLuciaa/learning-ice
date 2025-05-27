import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MessageStorage {
    private static final File FILE = new File("pending_messages.json");
    private final Map<String, String> pendingMessages = new HashMap<>();

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
                    .replaceAll("^\\{", "")   // quita {
                    .replaceAll("}$", "");    // quita }

            for (String entry : content.split(",")) {
                if (!entry.trim().isEmpty()) {
                    String[] parts = entry.split(":", 2);
                    String key = unquote(parts[0].trim());
                    String value = unquote(parts[1].trim());
                    pendingMessages.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pending messages: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            writer.write("{\n");
            int i = 0;
            for (Map.Entry<String, String> entry : pendingMessages.entrySet()) {
                writer.write("  \"" + escape(entry.getKey()) + "\": \"" + escape(entry.getValue()) + "\"");
                if (i++ < pendingMessages.size() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Error saving messages: " + e.getMessage());
        }
    }

    private String unquote(String s) {
        return s.replaceAll("^\"|\"$", "").replace("\\\"", "\"");
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    public synchronized void add(String id, String text) {
        pendingMessages.put(id, text);
        save();
    }

    public synchronized void remove(String id) {
        pendingMessages.remove(id);
        save();
    }

    public Map<String, String> getAll() {
        return new HashMap<>(pendingMessages);
    }
}