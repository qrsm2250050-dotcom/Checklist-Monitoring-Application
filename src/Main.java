import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

// for xml input and output
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// for files
import java.io.FileWriter;
import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// exceptions
import java.io.IOException;

public class Main {
    public static Scanner kbd = new Scanner(System.in);
    public static String name = new String();
    public static String filePath = "Data.xml";
    public static String fileCopy = "Second_Data.xml";
    public static String yearInput = "";
    public static String termInput = "";


    public static void main(String[] args) {
        Document doc = null;

        // Welcome/Input Screen
        /* P.S Also medyo iba yung code below from the main file since this focuses more on the user choosing
        an option than typing their input. I changed it para tugma siya sa google docs file natin
         */

        System.out.println("--------------------------------------");
        System.out.println("Welcome to your Checklist Monitoring Application");
        System.out.println("Please enter your information.");

        // 1. Set up the user profile
        userInput();

        // 2. Display dashboard
        displayDashboard();

        // 3. Show Main Menu
        mainMenu();

    }

    // 1. USER INPUT
    public static void userInput(){
        // Input name
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
    }

    // 2. DASHBOARD
    public static void displayDashboard(){
        System.out.println("Welcome to your Checklist Monitoring Application! \n" +
                "===============DASHBOARD===============");

        // Default variables for switch statements
        String currentYear = "1"; // Default to 1
        String currentTerm = "1"; // Default to 1

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
    public static void mainMenu(){
        boolean running = true;
        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("0. Reset Data"); // Reset option has no function yet, will add soon
            System.out.println("1. View Courses Table (by Year and Term)");
            System.out.println("2. Update Course Grade");
            System.out.println("3. Save and Exit");
            System.out.print("Select an option: ");

            String choice = kbd.nextLine();

            switch (choice) {
                /*
                 case "0" -> {
                 System.out.println("Are you sure you want to reset your data?");
                 // clear data etc etc
                 */
                case "1" -> {
                    System.out.print("Enter Year Level (1, 2, 3, 4): ");
                    String year = kbd.nextLine();
                    System.out.print("Enter Term (e.g., '1st Semester', '2nd Semester', 'Short Term'): ");
                    String term = kbd.nextLine();
                    // displayCoursesTable(doc, year, term);
                }
                case "2" -> {
                    System.out.print("Enter Course Number to update (e.g., 'CS 111'): ");
                    String courseNum = kbd.nextLine();
                    System.out.print("Enter new Grade: ");
                    String newGrade = kbd.nextLine();
                    // updateCourseGrade(doc, courseNum, newGrade);
                }
                case "3" -> {
                    System.out.println("Saving document...");
                    // saveXMLDocument(doc, filePath);
                    System.out.println("Application closed. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
