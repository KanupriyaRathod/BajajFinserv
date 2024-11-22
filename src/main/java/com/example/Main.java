package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <RollNumber> <JSONFilePath>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replace(" ", "");
        String jsonFilePath = args[1];

        try {
            // Read and parse JSON
            String destinationValue = findDestinationValue(jsonFilePath);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Concatenate values and compute MD5 hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = computeMD5Hash(concatenatedString);

            // Output result
            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while processing.");
        }
    }

    // Function to find the first "destination" key in the JSON file
    private static String findDestinationValue(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        return findFirstDestination(rootNode);
    }

    private static String findFirstDestination(JsonNode node) {
        if (node.isObject()) {
            for (var field : node.fields()) {
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findFirstDestination(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                String result = findFirstDestination(item);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Function to generate a random 8-character alphanumeric string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Function to compute MD5 hash
    private static String computeMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

