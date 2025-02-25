import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

//The class is not yet finished nor tested. It is a bit more than an evolution of the pseudocode I had previously written
//Regex were ported 1-1 from bas to Java, changing the single '\' into '\\'


public class Entrypoint {
    // ANSI escape codes for colors
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String NC = "\u001B[0m";

    // Regex patterns TODO test Regex patterns and get a deeper understanding
    private static final String PATTERN_TAG = "^([0-9]+\\.[0-9]+\\.[0-9]+)(-)([a-z]+)$";
    private static final String PATTERN_RELEASE = "[0-9]+\\.[0-9]+\\.[0-9]+";
    
    //Finer error checking 
    private static final String PATTERN_STARTS_WITH_LETTER = "^[a-z][0-9]+\\.[0-9]+\\.[0-9]+$";
    private static final String PATTERN_ENDS_WITH_LETTER = "^[0-9]+\\.[0-9]+\\.[0-9]+[a-z]$";

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
}













