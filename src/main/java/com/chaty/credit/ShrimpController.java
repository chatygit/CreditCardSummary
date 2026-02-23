package com.chaty.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shrimp/api")
public class ShrimpController {

    private static final String SHRIMP_DIRECTORY = "src/main/resources/shrimp/";

    @PostMapping("/save")
    public List<Map<String, Object>> saveJsonToFile(@RequestBody List<Map<String, Object>> jsonData, @RequestParam String filename) {
        log.info("Received request to save JSON data to data: {} and {}", jsonData,filename);
        ObjectMapper objectMapper = new ObjectMapper();
        File directory = new File(SHRIMP_DIRECTORY);

        // Ensure the directory exists
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("Failed to create directory: {}", SHRIMP_DIRECTORY);
                throw new RuntimeException("Failed to create directory.");
            }
        }

        File file = new File(SHRIMP_DIRECTORY + filename);
        try {
            // Initialize a list to hold the JSON data
            List<Map<String, Object>> dataList = new ArrayList<>();

            log.info("Received JSON data to save: {}", jsonData);

            // If the file exists, read the existing data
            if (file.exists()) {
                dataList = objectMapper.readValue(file, List.class);
            }

            // Iterate through the incoming data and check for existing IDs
            for (Map<String, Object> newItem : jsonData) {
                String newId = (String) newItem.get("id");
                boolean idExists = false;

                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, Object> existingItem = dataList.get(i);
                    String existingId = (String) existingItem.get("id");

                    if (existingId != null && existingId.equals(newId)) {
                        // Overwrite the existing item with the new item
                        dataList.set(i, newItem);
                        idExists = true;
                        break;
                    }
                }

                // If the ID does not exist, add the new item to the list
                if (!idExists) {
                    dataList.add(newItem);
                }
            }

            // Write the updated list back to the file
            objectMapper.writeValue(file, dataList);
            log.info("JSON data saved to file: {}", file.getAbsolutePath());
            return dataList;
        } catch (IOException e) {
            log.error("Error saving JSON data to file", e);
            throw new RuntimeException("Error saving JSON data.", e);
        }
    }

    @GetMapping("/get")
    public Object getJsonFromFile(@RequestParam String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(SHRIMP_DIRECTORY + filename);

        try {
            // Check if the file exists
            if (!file.exists()) {
                log.error("File not found: {}", file.getAbsolutePath());
                return "File not found.";
            }

            // Read and return the JSON data
            return objectMapper.readValue(file, Object.class);
        } catch (IOException e) {
            log.error("Error reading JSON data from file", e);
            return "Error reading JSON data.";
        }
    }

    @DeleteMapping("/delete")
    public Object deleteById(@RequestParam String filename, @RequestParam String id) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(SHRIMP_DIRECTORY + filename);

        try {
            // Check if the file exists
            if (!file.exists()) {
                log.error("File not found: {}", file.getAbsolutePath());
                return "File not found.";
            }

            // Read the existing data
            List<Map<String, Object>> dataList = objectMapper.readValue(file, List.class);

            // Find and remove the entry with the specified ID
            boolean removed = dataList.removeIf(item -> id.equals(item.get("id")));

            if (!removed) {
                log.warn("ID not found: {}", id);
                return "ID not found.";
            }

            // Write the updated list back to the file
            objectMapper.writeValue(file, dataList);
            log.info("Entry with ID {} deleted successfully from file: {}", id, file.getAbsolutePath());
            return dataList;
        } catch (IOException e) {
            log.error("Error deleting entry from file", e);
            return "Error deleting entry.";
        }
    }
}