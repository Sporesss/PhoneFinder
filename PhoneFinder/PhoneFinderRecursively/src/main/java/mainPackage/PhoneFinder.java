package mainPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.Files.readAllLines;


public class PhoneFinder {

    private static ArrayList<String> content = new ArrayList<String>();
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
            PhoneFinder phoneFinder = new PhoneFinder();
            try {
                content = phoneFinder.processAllDirectories(path);
                ArrayList<String> allPhoneNumbers = phoneFinder.getAllPhoneNumbers(content);
                phoneFinder.formatMyPhoneNumbers(allPhoneNumbers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please, provide directory path");
        }
    }

    /**
     * Recursive method, find all ".txt" files in specified directory and add all strings to ArrayList.
     */
    private ArrayList<String> processAllDirectories(String path) throws IOException {
        File[] allFiles = new File(path).listFiles();
        for (File eachFile : allFiles) {
            if (eachFile.isDirectory()) {
                processAllDirectories(eachFile.getAbsolutePath());
            } else {
                if (eachFile.getName().endsWith(".txt")) {
                    content.addAll(readAllLines(Paths.get(eachFile.getAbsolutePath())));
                }
            }
        }
        return content;
    }

    /**
     * Method get unsorted string array and return array where each elements contains only one phone number.
     */
    private ArrayList<String> getAllPhoneNumbers(ArrayList<String> contentList) {
        /**
         * Delete all symbols except digits, "+", "-" and " ".
         */
        ListIterator<String> listIterator = contentList.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().replaceAll("[^\\d\\+\\-\\s]", "").trim());
        }
        /**
         * Check if the string are phone number and how many numbers it contains. Each phone number add to ArrayList.
         */
        ArrayList<String> phoneNumbers = new ArrayList<String>();
        for (String eachString : contentList) {
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
        ListIterator<String> listIterator2 = phoneNumbers.listIterator();
        while (listIterator2.hasNext()) {
            listIterator2.set(listIterator2.next().replaceAll("[\\s\\-]", ""));
        }
        return phoneNumbers;
    }

    /**
     * Method formats phone numbers, add "+7" and "812" if it's necessary and add formatted records to
     * TreeSet to get ordered, unique collection of phone numbers.
     */
    private void formatMyPhoneNumbers(ArrayList<String> onlyPhoneNumbers) {
        formattedPhoneNumbers = new TreeSet<String>();
        for (int i = 0; i < onlyPhoneNumbers.size(); i++) {
            if (onlyPhoneNumbers.get(i) != null && onlyPhoneNumbers.get(i).length() == 12) {
                String formattedFullNumber = onlyPhoneNumbers.get(i).replaceFirst("(.\\d{1})(\\d{3})(\\d{3})(\\d{4})", "$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (onlyPhoneNumbers.get(i) != null && onlyPhoneNumbers.get(i).length() == 11) {
                String formattedFullNumber = onlyPhoneNumbers.get(i).replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d{4})", "+$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (onlyPhoneNumbers.get(i) != null && onlyPhoneNumbers.get(i).length() == 10) {
                String formattedFullNumber = onlyPhoneNumbers.get(i).replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "+7 ($1) $2-$3");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (onlyPhoneNumbers.get(i) != null && onlyPhoneNumbers.get(i).length() == 7) {
                String formattedFullNumber = onlyPhoneNumbers.get(i).replaceFirst("(\\d{3})(\\d{4})", "+7 (812) $1-$2");
                formattedPhoneNumbers.add(formattedFullNumber);
            }
        }
        for (String eachString : formattedPhoneNumbers) {
            System.out.println(eachString);
        }
    }
}

