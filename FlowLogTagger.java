import java.io.*;
import java.util.*;
/*
Plan:
1) Parse Flow Log - extract dstport and protocol
2) Parse Lookup table - map dstport and protocol combos
3) Assign Flow Log entries to tags
4) Iterate each occurance of combinations
5) Return output file
*/

//most of the methods are declared static as they serve as utility functions to process data
//They don't possess any state between calls
public class FlowLogTagger {
    public static void main(String[] args) {

    }


    // Use BufferedReader to read the file.
    // Split each line by commas.
    // String of dstport & protocol as the key in the HashMap.
    // Convert keys to lowercase for case-insensitive matching.
    public static Map<String, String> parseLookupFile(String filePath) throws IOException {
        Map<String, String> combinationMap = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        
        String line = bufferedReader.readLine(); // Skip the header line.

        while ((line = bufferedReader.readLine()) != null) {
            String[] parsed = line.trim().split(",");
            if (parsed.length >= 3) {
                String dstport = parsed[0].trim().toLowerCase();
                String protocol = parsed[1].trim().toLowerCase();
                String tag = parsed[2].trim();
                String key = dstport + "," + protocol;
                combinationMap.put(key, tag);
            }
        }
        bufferedReader.close();
        return combinationMap;
    }

    //Read Flow Log File
    // Split each line by whitespace
    // Converts the protocol number to a protocol name using getProtocolName.
    // Return a combo of dstport & protocolNumber in List
    public static List<String[]> parseFlowFile(String filePath) throws IOException {
        List<String[]> flowEntries = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] fields = line.trim().split("\\s+");
            if (fields.length >= 14) {
                String dstport = fields[5].trim().toLowerCase();
                String protocolNumber = fields[6].trim();
                String protocolName = getProtocolName(protocolNumber).toLowerCase();
                flowEntries.add(new String[]{dstport, protocolName});
            }
        }
        bufferedReader.close();
        return flowEntries;
    }


    // Map protocol numbers to protocol names
    public static String getProtocolName(String protocolNumber) {
        return switch (protocolNumber) {
            case "6" -> "tcp";
            case "17" -> "udp";
            case "1" -> "icmp";
            default -> protocolNumber;
        }; // Return the number if it's an unknown protocol
    }

    // //testing parseLookupTable
    // public static void testParseLookupFile() {
    //     String filePath = "lookup.csv";
        
    //     try {
    //         // Parse the lookup table
    //         Map<String, String> lookupMap = parseLookupFile(filePath);
            
    //         // Print the entire map
    //         System.out.println("Lookup Table Contents:");
    //         for (Map.Entry<String, String> entry : lookupMap.entrySet()) {
    //             System.out.println("Key: " + entry.getKey() + " + Tag: " + entry.getValue());
    //         }
            
    //     } catch (IOException e) {
    //         System.err.println("Error reading the lookup table: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }



}