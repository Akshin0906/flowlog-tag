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

//The methods are declared static as they serve as utility functions to process data
//They don't possess any state between calls
public class FlowLogTagger {
    public static void main(String[] args) {
        String flowLogFilePath;
        String lookupFilePath;
        String outputFilePath;
        //Create a scanner object to get user input
        try (Scanner scanner = new Scanner(System.in)) {
            //Prompt user to enter path to the flow log file
            System.out.print("Enter the path to the flow log file: ");
            flowLogFilePath = scanner.nextLine().trim();
            //Prompt user to enter path to the lookup file
            System.out.print("Enter the path to the lookup file: ");
            lookupFilePath = scanner.nextLine().trim();
            //Set the output file path
            outputFilePath = "output.txt";
        }

        try {
            //Parse the lookup table
            Map<String, String> lookupMap = parseLookupFile(lookupFilePath);

            //Parse the flow log file
            List<String[]> flowEntries = parseFlowFile(flowLogFilePath);

            //Initialize a map to hold port/protocol combination counts
            Map<String, Integer> portProtocolCounts = new HashMap<>();

            //Assign tags and get tag counts
            Map<String, Integer> tagCounts = assignTags(flowEntries, lookupMap, portProtocolCounts);

            //Write the results to the output file
            writeOutput(outputFilePath, tagCounts, portProtocolCounts);

            int totalEntries = flowEntries.size();
            int taggedEntries = totalEntries - tagCounts.getOrDefault("Untagged", 0);
            int untaggedEntries = tagCounts.getOrDefault("Untagged", 0);

            System.out.println("Processing completed. Output written to " + outputFilePath);
            System.out.println("Total flow log entries processed: " + totalEntries);
            System.out.println("Total tagged entries: " + taggedEntries);
            System.out.println("Total untagged entries: " + untaggedEntries);
            System.out.println("Processing completed. Output written to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
            e.printStackTrace();
        }
    }





    // Use BufferedReader to read the file.
    // Split each line by commas.
    // String of dstport & protocol as the key in the HashMap.
    // Convert keys to lowercase for case-insensitive matching.
    public static Map<String, String> parseLookupFile(String filePath) throws IOException {
        Map<String, String> combinationMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line = bufferedReader.readLine(); // Skip the header line.
            
            while ((line = bufferedReader.readLine()) != null) {
                String[] parsed = line.trim().split(",");
                if (parsed.length >= 3) {
                    String dstport = parsed[0].trim().toLowerCase();
                    String protocol = parsed[1].trim().toLowerCase();
                    String tag = parsed[2].trim().toLowerCase();
                    String key = dstport + "," + protocol;
                    combinationMap.put(key, tag);
                }
            }
        } // Skip the header line.
        return combinationMap;
    }

    //Read Flow Log File
    // Split each line by whitespace
    // Convert the protocol number to a protocol name using getProtocolName.
    // Return a combo of dstport & protocolNumber in List
    public static List<String[]> parseFlowFile(String filePath) throws IOException {
        List<String[]> flowEntries = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.trim().split("\\s+");
                if (fields.length >= 14) {
                    String dstport = fields[6].trim().toLowerCase();
                    String protocolNumber = fields[7].trim();
                    String protocolName = getProtocolName(protocolNumber).toLowerCase();
                    flowEntries.add(new String[]{dstport, protocolName});
                }
            }
        }
        return flowEntries;
    }


    // Map protocol numbers to protocol names
    public static String getProtocolName(String protocolNumber) {
        return switch (protocolNumber) {
            case "6" -> "tcp";
            case "17" -> "udp";
            case "1" -> "icmp";
            default -> "unknown";
        }; // Return unknown if it's an unknown protocol
    }

    //Traverse each flow entry.
    //Make key from dstport and protocol for lookup.
    //Get corresponding tag from map.
    //Add to count for each tag and port/protocol combo.
    public static Map<String, Integer> assignTags(List<String[]> flowEntries, Map<String, String> lookupMap, Map<String, Integer> portProtocolCount) {
    
        Map<String, Integer> tagCounts = new HashMap<>();

        for (String[] entry : flowEntries) {
            String dstport = entry[0];
            String protocol = entry[1];
            String key = dstport + "," + protocol;

            // Look up tag; default if not found
            String tag = lookupMap.getOrDefault(key, "Untagged");

            // Update tag count
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

            // Update port & protocol counts
            portProtocolCount.put(key, portProtocolCount.getOrDefault(key, 0) + 1);
        }

        return tagCounts;
    }

    //Write the tag count
    //Write the port/protocol combination count
    //Format the output to match the sample output given
    public static void writeOutput(String outputFilePath, Map<String, Integer> tagCounts, Map<String, Integer> portProtocolCounts) throws IOException {
        // Write Tag Counts
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write Tag Counts
            bufferedWriter.write("Tag Counts:\n");
            bufferedWriter.write("Tag,Count\n");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                bufferedWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
            
            bufferedWriter.write("\nPort/Protocol Combination Counts:\n");
            bufferedWriter.write("Port,Protocol,Count\n");
            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                String[] portProtocol = entry.getKey().split(",");
                String port = portProtocol[0];
                String protocol = portProtocol[1];
                bufferedWriter.write(port + "," + protocol + "," + entry.getValue() + "\n");
            }
        }
    }
}