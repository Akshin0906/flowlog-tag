import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FlowLogTaggerTest {

    public static void main(String[] args) {
        // Run all test methods
        testParseLookupFile();
        testParseFlowFile();
        testAssignTags();
        testGetProtocolName();
    }

    public static void testParseLookupFile() {
        System.out.println("\nRunning testParseLookupFile...");

        // Prepare test data
        String testLookupData = "dstport,protocol,tag\n"
                + "22,tcp,SSH\n"
                + "80,tcp,Web\n"
                + "443,tcp,SecureWeb\n"
                + "53,udp,DNS\n";

        // Write test data to a temporary file
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("lookup", ".csv");
            Files.writeString(tempFile, testLookupData);

            // Call the method under test
            Map<String, String> lookupMap = FlowLogTagger.parseLookupFile(tempFile.toString());

            // Prepare expected result
            Map<String, String> expectedMap = new HashMap<>();
            expectedMap.put("22,tcp", "SSH");
            expectedMap.put("80,tcp", "Web");
            expectedMap.put("443,tcp", "SecureWeb");
            expectedMap.put("53,udp", "DNS");

            // Assert correct results
            if (lookupMap.equals(expectedMap)) {
                System.out.println("testParseLookupFile passed.\n");
            } else {
                System.out.println("testParseLookupFile failed.");
                System.out.println("Expected: " + expectedMap);
                System.out.println("Actual: " + lookupMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                if (tempFile != null) {
                    Files.deleteIfExists(tempFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testParseFlowFile() {
        System.out.println("Running testParseFlowFile...");

        // Prepare test data
        String testFlowLogData = "2 123456789 eni-12345 192.168.1.10 10.0.0.1 34567 22 6 10 5000 1632914310 1632914372 ACCEPT OK\n"
                + "2 123456789 eni-12345 192.168.1.11 10.0.0.2 34568 80 6 8 4000 1632914320 1632914382 ACCEPT OK\n"
                + "2 123456789 eni-12345 192.168.1.12 10.0.0.3 34569 53 17 5 2500 1632914360 1632914422 ACCEPT OK\n";

        // Write test data to a temporary file
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("flowlog", ".txt");
            Files.writeString(tempFile, testFlowLogData);

            // Call the method under test
            List<String[]> flowEntries = FlowLogTagger.parseFlowFile(tempFile.toString());

            // Prepare expected results
            List<String[]> expectedEntries = new ArrayList<>();
            expectedEntries.add(new String[]{"22", "tcp"});
            expectedEntries.add(new String[]{"80", "tcp"});
            expectedEntries.add(new String[]{"53", "udp"});

            // Assert expected results
            boolean passed = true;
            if (flowEntries.size() != expectedEntries.size()) {
                passed = false;
            } else {
                for (int i = 0; i < flowEntries.size(); i++) {
                    if (!Arrays.equals(flowEntries.get(i), expectedEntries.get(i))) {
                        passed = false;
                        break;
                    }
                }
            }

            if (passed) {
                System.out.println("testParseFlowFile passed.\n");
            } else {
                System.out.println("testParseFlowFile failed.\n");
                System.out.println("Expected: " + expectedEntries);
                System.out.println("Actual: " + flowEntries);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                if (tempFile != null) {
                    Files.deleteIfExists(tempFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testAssignTags() {
        System.out.println("Running testAssignTags...");

        // Prepare test data
        List<String[]> flowEntries = new ArrayList<>();
        flowEntries.add(new String[]{"22", "tcp"});
        flowEntries.add(new String[]{"80", "tcp"});
        flowEntries.add(new String[]{"53", "udp"});
        flowEntries.add(new String[]{"9999", "tcp"}); // Should be untagged

        Map<String, String> lookupMap = new HashMap<>();
        lookupMap.put("22,tcp", "SSH");
        lookupMap.put("80,tcp", "Web");
        lookupMap.put("443,tcp", "SecureWeb");
        lookupMap.put("53,udp", "DNS");

        Map<String, Integer> portProtocolCounts = new HashMap<>();

        // Call the method under test
        Map<String, Integer> tagCounts = FlowLogTagger.assignTags(flowEntries, lookupMap, portProtocolCounts);

        // Prepare expected results
        Map<String, Integer> expectedTagCounts = new HashMap<>();
        expectedTagCounts.put("SSH", 1);
        expectedTagCounts.put("Web", 1);
        expectedTagCounts.put("DNS", 1);
        expectedTagCounts.put("Untagged", 1);

        Map<String, Integer> expectedPortProtocolCounts = new HashMap<>();
        expectedPortProtocolCounts.put("22,tcp", 1);
        expectedPortProtocolCounts.put("80,tcp", 1);
        expectedPortProtocolCounts.put("53,udp", 1);
        expectedPortProtocolCounts.put("9999,tcp", 1);

        // Assert expected results
        if (tagCounts.equals(expectedTagCounts) && portProtocolCounts.equals(expectedPortProtocolCounts)) {
            System.out.println("testAssignTags passed.\n");
        } else {
            System.out.println("testAssignTags failed.");
            System.out.println("Expected tag counts: " + expectedTagCounts);
            System.out.println("Actual tag counts: " + tagCounts);
            System.out.println("Expected port/protocol counts: " + expectedPortProtocolCounts);
            System.out.println("Actual port/protocol counts: " + portProtocolCounts);
        }
    }

    public static void testGetProtocolName() {
        System.out.println("Running testGetProtocolName...");

        boolean passed = true;
        if (!FlowLogTagger.getProtocolName("6").equals("tcp")) passed = false;
        if (!FlowLogTagger.getProtocolName("17").equals("udp")) passed = false;
        if (!FlowLogTagger.getProtocolName("1").equals("icmp")) passed = false;
        if (!FlowLogTagger.getProtocolName("99").equals("unknown")) passed = false;

        if (passed) {
            System.out.println("testGetProtocolName passed.\n");
        } else {
            System.out.println("testGetProtocolName failed.");
        }
    }
}
