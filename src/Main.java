import java.io.File;
import java.util.Scanner;

// for xml input and output

// for files
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// exceptions
import java.io.IOException;

public class Main {
    // Class variables
    public static Scanner kbd = new Scanner(System.in);
    public static String name = new String();
    public static String yearInput = "";
    public static String termInput = "";
    public static String currentYear = "1"; // Default to 1
    public static String currentTerm = "1"; // Default to 1
    public static String filePath = "src/Data.xml";


    public static void main(String[] args) {
        Document doc;

        try {
            File xmlFile = new File(filePath);

            if (!xmlFile.exists()) {
                System.out.println("Error: Could not find " + filePath);
                return;
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 1. Set up the user profile
        userInput();

        // 2. Display dashboard
        displayDashboard();

        // 3. Show Main Menu
        mainMenu(doc);

    }

    // 1. USER INPUT
    public static void userInput(){
        // Welcome/Input Screen

        System.out.println("--------------------------------------");
        System.out.println("Welcome to your Checklist Monitoring Application!");
        System.out.println("Please enter your information.");

        System.out.print("Enter name: ");
        name = kbd.nextLine();

        System.out.println();

        // Year level
        System.out.println("Year Level");
        String[] yearArray = {"First Year", "Second Year", "Third Year", "Fourth Year"};
        for (int i = 0; i < yearArray.length; i++) {
            System.out.println("<" + (i + 1) + "> " + yearArray[i]); // for loop to display year level menu
        }
        System.out.println();

        // User will select year level
        System.out.print("Choose Year Level: ");
        yearInput = kbd.nextLine();
        System.out.println();

        // Current term
        System.out.println("Current Term");
        String[] semesterArray = {"First Semester", "Second Semester", "Short Term"};
        for (int i = 0; i < semesterArray.length; i++) {
            System.out.println("<" + (i + 1) + "> " + semesterArray[i]); // for loop to display current term menu
        }
        System.out.println();

        // User will select current term
        System.out.print("Choose Current Term: ");
        termInput = kbd.nextLine();

        System.out.println();

        // Saves user info to a txt file
        try (PrintWriter pw = new PrintWriter(new FileWriter("UserInfo.txt"))) {
            pw.println(name);
            pw.println(yearInput);
            pw.println(termInput);
        } catch (IOException e) {
            System.out.println("Error saving user info.");
        }
    }

    // 2. DASHBOARD
    public static void displayDashboard(){
        System.out.println("Welcome to your Checklist Monitoring Application! \n" +
                "===============DASHBOARD===============");

        // Display name
        System.out.println("NAME: " + name);

        // Switch statements for current year
        switch (yearInput) {
            case "1" -> currentYear = "First Year";
            case "2" -> currentYear = "Second Year";
            case "3" -> currentYear = "Third Year";
            case "4" -> currentYear = "Fourth Year";
            case "5" -> currentYear = "Fifth Year";
            default -> System.out.println("Unrecognized year. Defaulting to Year 1.");
        }
        System.out.println("CURRENT YEAR: " + currentYear); // Display current year

        // Switch statements for current term
        switch (termInput) {
            case "1" -> currentTerm = "First Semester";
            case "2" -> currentTerm = "Second Semester";
            case "3" -> currentTerm = "Short Term";
            default -> System.out.println("Unrecognized term. Defaulting to Semester 1.");
        }
        System.out.println("CURRENT TERM: " + currentTerm); // Display current term

        System.out.println("Welcome, " + name + "!" + " (" + currentYear + ", " + currentTerm + ")"); // Welcome message
    }

    // 3. MAIN MENU
    public static void mainMenu(Document doc){
        String resetDataInput = "";

        boolean running = true;
        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("0. Reset Data");
            System.out.println("1. View Courses Table (by Year and Term)");
            System.out.println("2. Update Course Grade");
            System.out.println("3. Save and Exit");
            System.out.print("Select an option: ");
            String choice = kbd.nextLine();

            System.out.println();

            switch (choice) {
                case "0" -> {
                    System.out.println("Are you sure you want to reset your data?: ");
                    System.out.println("<1> YES, I want to reset my data and start over. \n<2> NO, go back to the Main Menu.");
                    System.out.println();
                    System.out.print("Select an option: ");
                    resetDataInput = kbd.nextLine();

                    switch (resetDataInput){
                        case "1" -> {
                            System.out.println("Resetting data...");

                            //reset data
                            try (PrintWriter pw = new PrintWriter(new FileWriter("UserInfo.txt"))) {  // Opens UserInfo.txt
                                pw.print(""); // clears all data inside UserInfo.txt
                            } catch (IOException e) { System.out.println("Error clearing user info."); }
                            try {
                                Files.copy(Paths.get("src/Data_copy.xml"), Paths.get("src/Data.xml"), StandardCopyOption.REPLACE_EXISTING); // Copies the clean file to the original copy
                            } catch (IOException e) { System.out.println("Error saving user info."); }

                            // Reset class variables
                            name = "";
                            currentYear = "";
                            currentTerm = "";

                            System.out.println("Reset successful! Please input your information again.");
                            userInput();
                            displayDashboard();
                        }

                        case "2" -> {
                            System.out.println("Going back to the Main Menu...");
                        }
                    }
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
