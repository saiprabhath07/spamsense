package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    /**
     * Simple immutable record that stores one email/SMS row.
     *
     * @param label   Class label: "spam" or "ham"
     * @param message Raw message text
     */
    public record EmailRecord(String label, String message) {
    }

    /**
     * Reads a CSV file containing two columns: label and message.
     * Supports quoted messages and commas inside quoted fields.
     * Expects a header row with column names: label,message
     *
     * @param filePath Path to the CSV file
     * @return List of loaded EmailRecord objects
     * @throws IOException If the file cannot be read
     */
    public List<EmailRecord> loadCSV(String filePath) throws IOException {
        List<EmailRecord> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String line = reader.readLine();

            if (line == null) {
                return records;
            }

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                List<String> columns = parseCSVLine(line);

                if (columns.size() < 2) {
                    continue;
                }

                String label = columns.get(0).trim().toLowerCase();
                String message = columns.get(1).trim();

                if (!label.equals("spam") && !label.equals("ham")) {
                    continue;
                }

                records.add(new EmailRecord(label, message));
            }
        }

        return records;
    }

    /**
     * Parses one CSV line while handling quoted values and escaped quotes.
     *
     * @param line A single line from the CSV file
     * @return Parsed column values
     */
    private List<String> parseCSVLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (ch == ',' && !insideQuotes) {
                columns.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        columns.add(current.toString());
        return columns;
    }
}