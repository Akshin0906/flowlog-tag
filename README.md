# FlowLogTagger

## Overview

`FlowLogTagger` is a Java program that processes AWS VPC flow logs and assigns tags to each entry based on a provided lookup table. It generates an output file containing counts of matches for each tag and counts of each port/protocol combination. The project includes a test class `FlowLogTaggerTest` to verify the functionality of the main program.

## Features

- **Flow Log Parsing**: Extracts destination ports and protocols from AWS VPC flow logs.
- **Lookup Table Processing**: Reads a lookup CSV file mapping destination ports and protocols to tags.
- **Tag Assignment**: Assigns tags to flow log entries based on the lookup table.
- **Counting and Reporting**:
  - Counts the number of occurrences for each tag.
  - Counts the number of occurrences for each port/protocol combination.
- **Output Generation**: Writes the results to an output file named `output.txt`.
- **Testing**: Includes a test class `FlowLogTaggerTest` with methods to test core functionalities.

## Assumptions

- **Flow Log Format**: Supports only the default flow log format (version 2) as per AWS documentation.
- **Protocol Mapping**: Recognizes protocol numbers 6 (TCP), 17 (UDP), and 1 (ICMP) up to 99. Unknown protocol numbers are labeled as "unknown".
- **File Format**: The lookup file must have at least three columns: `dstport`, `protocol`, and `tag`, with a header row. Files cannot have blank lines.
- **Case Sensitivity**:
  - Matching of ports and protocols is case-insensitive.
  - Tags retain their original casing.
- **Duplicate Entries**: If duplicate keys are found in the lookup file, the last entry overwrites previous ones.

## Dependencies

- **Java Version**: Requires Java Development Kit (JDK) 11 or higher.
- **Standard Libraries**: Uses only standard Java libraries; no external dependencies.

## How to Compile and Run the Program

* Make sure you have the CSV Filepath (or it is present in your directory). 
* Make sure you have the lookup file filepath (or it is present in your directory)

### Compile

Open a terminal or command prompt, navigate to the directory containing your `.java` files, and run:

```bash
javac FlowLogTagger.java FlowLogTaggerTest.java
```

### Run the Main Program
```bash
java FlowLogTagger
```
The program will prompt you to enter the paths to the flow log file and the lookup file.

### Run the Test Program
```bash
java FlowLogTaggerTest
```
## Program Output
- **Tag Counts**
```bash
Tag Counts:
Tag,Count
SSH,2
Email,2
Web,1
SecureWeb,1
DNS,1
RDP,1
Untagged,2
```
- **Port/Protocol Combination Counts**
```bash
Port/Protocol Combination Counts:
Port,Protocol,Count
22,tcp,2
25,tcp,1
80,tcp,1
110,tcp,1
443,tcp,1
53,udp,1
3389,tcp,1
8080,tcp,1
23,tcp,1
```

## Testing

The project includes a test class FlowLogTaggerTest with methods to test the core functionalities:
- testParseLookupFile: Tests the parsing of the lookup CSV file.
- testParseFlowFile: Tests the parsing of the flow log file.
- testAssignTags: Tests the tag assignment and counting logic.
- testGetProtocolName: Tests the protocol number to name conversion.
# Final Thoughts
* I used up my 2 hours with what I currently have pushed, if given more time I would implement sorting within the entries of the maps. This is to ensure uniformity within the data structure, as currently it is up to the internal ordering of the HashMap.
* I would also have used JUnit to implement my testing, but I chose a more rudimentary solution (Java Test Class) due to being short on time. 

### For any questions or Issues
Contact: akshingopal@gmail.com
