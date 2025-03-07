import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import java.util.List;


//The class is not yet finished nor tested. It is a bit more than an evolution of the pseudocode I had previously written
//Regex were ported 1-1 from bas to Java, changing the single '\' into '\\'


public class Entrypoint {
    // ANSI escape codes for colors
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String NC = "\u001B[0m";

    // Regex patterns TODO test Regex patterns and get a deeper understanding
    private static final String PATTERN_TAG ="^([0-9]+\\.[0-9]+\\.[0-9]+)(-)([a-z]+)$"; //"^([0-9]+\\.[0-9]+\\.[0-9]+)(-)([a-z]+)$";
    private static final String PATTERN_RELEASE = "[0-9]+\\.[0-9]+\\.[0-9]+"; //"[0-9]+\\.[0-9]+\\.[0-9]+";
    private static final String PATTERN_BRANCH = "[a-z]+";


    //Finer error checking 
    private static final String PATTERN_STARTS_WITH_LETTER = "^[a-z][0-9]+\\.[0-9]+\\.[0-9]+$";
    private static final String PATTERN_ENDS_WITH_LETTER = "^[0-9]+\\.[0-9]+\\.[0-9]+[a-z]$";

    //Strings
    private static String TAG = "None";
    private static String RELEASE = "None";
    private static String BRANCH = "None";



    public static void main(String[] args) {
        String releaseVersion = System.getenv("RELEASE_VERSION");
        if (releaseVersion == null) releaseVersion = "";
        //check if it is null. I am torn on what to do if this happens, I was thinking about throwing an exception here
        //Gotta check back with the bash file a bit better.

        System.out.println(BLUE + "Validating RELEASE_VERSION..." + NC); //honestly hope the colours work here
        System.out.println(BLUE + "Input: RELEASE_VERSION" + NC);
        System.out.println(releaseVersion + "\n");

        if (!validateReleaseVersion(releaseVersion)) {

            printErrorMessages(releaseVersion);
            
            createPipelineLock();
            
            System.exit(1);
        }

        System.out.println(BLUE + "Filter Ok, skip" + NC);

        processMatchLog(releaseVersion, PATTERN_TAG, PATTERN_RELEASE, PATTERN_BRANCH);

        // Read match.log results
        try {
            List<String> lines = Files.readAllLines(Paths.get("match.log"));
            if (lines.size() > 0) TAG = lines.get(0);
            if (lines.size() > 1) RELEASE = lines.get(1);
            if (lines.size() > 2) BRANCH = lines.get(2);
        } catch (IOException e) {
            System.err.println("Error reading match.log");
            createPipelineLock();
            System.exit(1);
        }

        // Fix branch if needed
        if (BRANCH.isEmpty() || BRANCH.equals("None")) {
            System.out.println(YELLOW + "Warning: Invalid branch matched!" + NC);
            System.out.println(BLUE + "Setting branch to master..." + NC);
            BRANCH = "master";
        }
        //Let the user know what is going on
        System.out.println(BLUE + "Results:" + NC);
        System.out.println(BLUE + "TAG: " + TAG + NC);
        System.out.println(BLUE + "RELEASE: " + RELEASE + NC);
        System.out.println(BLUE + "BRANCH: " + BRANCH + NC);

        // Generate artifacts
        writeFile("tag.txt", TAG);
        writeFile("release.txt", RELEASE);
        writeFile("branch.txt", BRANCH);

        // Copy to staging
        copyToStaging("tag.txt");
        copyToStaging("release.txt");
        copyToStaging("branch.txt");
        copyToStaging("match.log");

        System.out.println(BLUE + "Done." + NC);

    }

    //Using a boolean function to validate with the Regex PATTERN_TAG
    private static boolean validateReleaseVersion(String version) {
        if (version.matches(PATTERN_TAG)) {
            return validateTagBranch(version);
        }

        System.out.println(YELLOW + "RELEASE_VERSION not dev or test environment release" + NC + "\n");
        System.out.println(BLUE + "Checking if RELEASE_VERSION is a production environment release..." + NC);
//Checking directly if we're going straight to production with PATTERN_RELEASE if not simply return and print the error printErrorMessages
//
        boolean productionValid = Pattern.compile(PATTERN_RELEASE).matcher(version).find();
        if (!productionValid) return false;

        boolean invalidStart = version.matches(PATTERN_STARTS_WITH_LETTER);
        boolean invalidEnd = version.matches(PATTERN_ENDS_WITH_LETTER);
        
        return !invalidStart && !invalidEnd;
    }

    //Validate the branch tag, without this I believe any tag would actually pass without throwing error
    private static boolean validateTagBranch(String version) {
        Matcher m = Pattern.compile(PATTERN_TAG).matcher(version);
        if (m.find()) {
            String branch = m.group(3);
            return branch.equals("dev") || branch.equals("tst") || branch.equals("crt");
        }
        return false;
    }

//Doesnt need a comment imo
    private static void printErrorMessages(String version) {
        System.out.println(RED + "Fatal RELEASE_VERSION " + version + " provided not compliance" + NC);
        System.out.println(YELLOW + "Please provide valid RELEASE_VERSION" + NC + "\n");
        System.out.println(BLUE + "Accepted formats:" + NC);
        System.out.println(BLUE + "1.0.0-dev" + NC);
        System.out.println(BLUE + "1.0.0-tst" + NC);
        System.out.println(BLUE + "1.0.0-crt" + NC);
        System.out.println(BLUE + "1.0.0" + NC + "\n");
        System.out.println(RED + "Aborting entire pipeline..." + NC);
    }

    private static void createPipelineLock() {
        try {
            new File("/staging/pipeline.lock").createNewFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not create pipeline lock file");
        }
    }
    
    private static void writeFile(String filename, String content) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing " + filename);
            createPipelineLock();
            System.exit(1);
        }
    }
//This seems to be the issue, as every time I run the code, no matter the content I get an error on the copy of
//fo the first file, which can be traced back to here, logically
    private static void copyToStaging(String filename) {
        try {
            Files.copy(Paths.get(filename), 
                      Paths.get("/staging/" + filename), 
                      StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error copying " + filename);
            createPipelineLock();
            System.exit(1);
        }
    }

    private static String extractFirstMatch(String input, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        return matcher.find() ? matcher.group() : "None";
    }




    private static void processMatchLog(String version, String tagPattern, 
                                      String releasePattern, String branchPattern) {
        try {
            // Process TAG
            String tag = version.matches(tagPattern) ? version : "None";
            Files.write(Paths.get("match.log"), (tag + "\n").getBytes());

            // Process RELEASE
            String release = extractFirstMatch(version, releasePattern);
            Files.write(Paths.get("match.log"), (release + "\n").getBytes(), 
                       StandardOpenOption.APPEND);

            // Process BRANCH
            String branch = extractFirstMatch(version, branchPattern);
            Files.write(Paths.get("match.log"), (branch + "\n").getBytes(), 
                       StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error processing match.log");
            createPipelineLock();
            System.exit(1);
        }
    }


}













