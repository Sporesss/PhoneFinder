package mainPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PhoneFinderStreams {

    private static ArrayList<String> content = new ArrayList<>();
    private Set<String> formattedPhoneNumbers;

    /**
     * Getter for access to final TreeSet with all formatted phone numbers.
     */
    public Set<String> getFormattedPhoneNumbers() {
        return formattedPhoneNumbers;
    }

    public static void main(String[] args) {
        System.out.println("\nPlease, enter the path to the directory, where you want to find phone numbers: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File f = new File(path);
        if(f.exists() && f.isDirectory()) {
            PhoneFinderStreams phoneFinderStreams = new PhoneFinderStreams();
            try {
                content = phoneFinderStreams.processAllDirectories(path);
                ArrayList<String> allPhoneNumbers = phoneFinderStreams.getAllPhoneNumbers(content);
                phoneFinderStreams.formatMyPhoneNumbers(allPhoneNumbers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please, provide directory path");
        }
    }

    /**
     * Method find all ".txt" files in specified directory and add all strings from this files to ArrayList.
     */
    private ArrayList<String> processAllDirectories(String path) throws IOException {
        List<File> fileList = Files.walk(Paths.get(path)).filter(path1 -> path1.toString().endsWith(".txt")).map(Path::toFile)
                .collect(Collectors.toList());
        ArrayList<String> allPhoneNumbers = new ArrayList<>();
        fileList.forEach(eachFile -> {
            try {
                allPhoneNumbers.addAll(Files.readAllLines(Paths.get(eachFile.getAbsolutePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return allPhoneNumbers;
    }

    /**
     * Method get unsorted string array and return array where each elements contains only one phone number.
     */
    private ArrayList<String> getAllPhoneNumbers(ArrayList<String> contentArrayList) {
        /**
         * Delete all symbols except digits, "+", "-" and " ".
         */
        contentArrayList.replaceAll(e -> e.replaceAll("[^\\d\\+\\-\\s]", "").trim());
        /**
         * Check if the string are phone number and how many numbers it contains. Each phone number add to ArrayList.
         */
        ArrayList<String> phoneNumbers  = new ArrayList<>();
        for (String eachString : contentArrayList) {
            Pattern pattern = Pattern.compile("((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,15}");
            Matcher matcher = pattern.matcher(eachString);
            if (matcher.lookingAt()) {
                while (matcher.find()) {
                    MatchResult matchResult = matcher.toMatchResult();
                    String firstPhoneNumber = eachString.substring(0, matchResult.start());
                    String otherDetectedPhoneNumbers = eachString.substring(matchResult.start(), matchResult.end());
                    phoneNumbers.add(firstPhoneNumber);
                    phoneNumbers.add(otherDetectedPhoneNumbers);
                }
                phoneNumbers.add(eachString);
            }
        }
        /**
         * Delete "-" and " " for further handling.
         */
        phoneNumbers.replaceAll(e -> e.replaceAll("[\\s\\-]", ""));
        return phoneNumbers;
    }

    /**
     * Method formats phone numbers, add "+7" and "812" if it's necessary and add formatted records to
     * TreeSet to get ordered, unique collection of phone numbers.
     */
    private void formatMyPhoneNumbers(ArrayList<String> phoneNumbers) {
        formattedPhoneNumbers = new TreeSet<>();
        for (int i = 0; i < phoneNumbers.size(); i++) {
            if (phoneNumbers.get(i) != null && phoneNumbers.get(i).length() == 12) {
                String formattedFullNumber = phoneNumbers.get(i).replaceFirst("(.\\d{1})(\\d{3})(\\d{3})(\\d{4})", "$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumbers.get(i) != null && phoneNumbers.get(i).length() == 11) {
                String formattedFullNumber = phoneNumbers.get(i).replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d{4})", "+$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumbers.get(i) != null && phoneNumbers.get(i).length() == 10) {
                String formattedFullNumber = phoneNumbers.get(i).replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "+7 ($1) $2-$3");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumbers.get(i) != null && phoneNumbers.get(i).length() == 7) {
                String formattedFullNumber = phoneNumbers.get(i).replaceFirst("(\\d{3})(\\d{4})", "+7 (812) $1-$2");
                formattedPhoneNumbers.add(formattedFullNumber);
            }
        }
        formattedPhoneNumbers.forEach(System.out::println);
    }
}

