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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
                case "2" -> {
                    GradeEditor editor = new GradeEditor(doc, filePath, kbd);
                    editor.showGradeMenu();
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

/**
 * Updates the <Grade> tag of a specific course using its Course Number.
 */
class GradeEditor {
    private Document doc;
    private String filePath;
    private Scanner kbd;

    public GradeEditor(Document doc, String filePath, Scanner kbd) {
        this.doc = doc;
        this.filePath = filePath;
        this.kbd = kbd;
    }

    public void showGradeMenu() {
        boolean inGradeMenu = true;
        while (inGradeMenu) {
            System.out.println("\n--- GRADE MANAGEMENT ---");
            System.out.println("1. Edit Grade");
            System.out.println("2. Clear Grade");
            System.out.println("3. Save and Back to Main");
            System.out.print("Select an option: ");

            String choice = kbd.nextLine();
            switch (choice) {
                case "1" -> {
                    String courseNum;
                    while (true) {
                        System.out.print("Enter Course Number to edit (e.g., 'CS 111') or 'exit' to cancel: ");
                        courseNum = kbd.nextLine();
                        if (courseNum.equalsIgnoreCase("exit")) break;

                        if (courseExists(courseNum)) {
                            String newGrade;
                            while (true) {
                                System.out.print("Enter new Grade (0-99): ");
                                newGrade = kbd.nextLine();

                                if (isValidGradeValue(newGrade)) {
                                    updateGrade(courseNum, newGrade);
                                    break;
                                } else {
                                    System.out.println("\n>>> Invalid Input: Grade must be a number between 0 and 99.");
                                }
                            }
                            break;
                        } else {
                            System.out.println("\n>>> Error: Course '" + courseNum + "' not found. (Check your spacing)");
                        }
                    }
                }
                case "2" -> {
                    while (true) {
                        System.out.print("Enter Course Number to clear or 'exit' to cancel: ");
                        String courseNum = kbd.nextLine();
                        if (courseNum.equalsIgnoreCase("exit")) break;

                        if (courseExists(courseNum)) {
                            boolean validResponse = false;
                            while (!validResponse) {
                                System.out.print("Clear the grade for " + courseNum + "? (Y/N): ");
                                String confirm = kbd.nextLine().trim();

                                if (confirm.equalsIgnoreCase("Y")) {
                                    updateGrade(courseNum, "");
                                    System.out.println(">>> Grade cleared.\n");
                                    validResponse = true;
                                }
                                else if (confirm.equalsIgnoreCase("N")) {
                                    System.out.println(">>> Clear operation cancelled.");
                                    validResponse = true;
                                }
                                else {
                                    System.out.println("\n>>> Invalid input! Please enter only 'Y' for Yes or 'N' for No.");
                                }
                            }
                            break;
                        } else {
                            System.out.println("\n>>> Error: Course '" + courseNum + "' not found.");
                        }
                    }
                }
                case "3" -> {
                    // Note: Ensure saveXMLDocument is implemented in your Main class
                    // or change this to call a local saving method.
                    saveXMLFile();
                    inGradeMenu = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private boolean isValidGradeValue(String input) {
        try {
            int grade = Integer.parseInt(input);
            return grade >= 0 && grade <= 99;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean courseExists(String targetCourse) {
        NodeList courseList = doc.getElementsByTagName("Course");
        for (int i = 0; i < courseList.getLength(); i++) {
            Element element = (Element) courseList.item(i);
            String currentCourse = element.getElementsByTagName("CourseNumber").item(0).getTextContent();
            if (currentCourse.equalsIgnoreCase(targetCourse)) return true;
        }
        return false;
    }

    private void updateGrade(String targetCourse, String newGrade) {
        NodeList courseList = doc.getElementsByTagName("Course");
        for (int i = 0; i < courseList.getLength(); i++) {
            Element element = (Element) courseList.item(i);
            String currentCourse = element.getElementsByTagName("CourseNumber").item(0).getTextContent();
            if (currentCourse.equalsIgnoreCase(targetCourse)) {
                Node gradeNode = element.getElementsByTagName("Grade").item(0);
                gradeNode.setTextContent(newGrade);
                if (!newGrade.isEmpty()) {
                    System.out.println(">>> Grade updated to " + newGrade + ".");
                }
                break;
            }
        }
    }

    // Added a local helper to handle saving if Main.saveXMLDocument is missing
    private void saveXMLFile() {
        try {
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(new java.io.File(filePath));
            transformer.transform(source, result);
            System.out.println(">>> Changes saved to file.");
        } catch (Exception e) {
            System.out.println(">>> Error saving XML: " + e.getMessage());
        }
    }
}