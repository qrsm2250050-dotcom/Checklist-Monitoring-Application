import java.io.File;
import java.util.Scanner;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.w3c.dom.Document;
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
            System.out.println("<" + (i + 1) + "> " + yearArray[i]); 
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
            System.out.println("<" + (i + 1) + "> " + semesterArray[i]); 
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

        System.out.println("Welcome, " + name + "!" + " (" + currentYear + ", " + currentTerm + ")"); 
    }

    // 3. MAIN MENU
    public static void mainMenu(Document doc){
        String resetDataInput = "";
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("<0> Reset data");
            System.out.println("<1> Show subjects for each school term");
            System.out.println("<2> Show subjects with grades for current term");
            System.out.println("<3> Enter grades for subjects recently finished");
            System.out.println("<4> Edit a course grade");
            System.out.println("<5> Edit personal information");
            System.out.println("<6> Save and exit");
            System.out.print("Select an option: ");
            String choice = kbd.nextLine();

            switch (choice) {
                case "0" -> {
                    System.out.println("\nAre you sure you want to reset your data?: ");
                    System.out.println("<1> YES, I want to reset my data and start over.");
                    System.out.println("<2> NO, go back to the Main Menu.");
                    System.out.println();
                    System.out.print("Select an option: ");
                    resetDataInput = kbd.nextLine();

                    switch (resetDataInput){
                        case "1" -> {
                            System.out.println("Resetting data...");
                            try (PrintWriter pw = new PrintWriter(new FileWriter("UserInfo.txt"))) {  
                                pw.print(""); 
                            } catch (IOException e) { System.out.println("Error clearing user info."); }
                            
                            try {
                                Files.copy(Paths.get("src/Data_copy.xml"), Paths.get("src/Data.xml"), StandardCopyOption.REPLACE_EXISTING); 
                            } catch (IOException e) { System.out.println("Error saving user info."); }

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
                case "1" -> showAllSubjects(doc);
                case "2" -> displayCoursesTable(doc, yearInput, getXmlTerm());
                case "3" -> enterGradesCurrentTerm(doc);
                case "4" -> {
                    GradeEditor editor = new GradeEditor(doc, filePath, kbd);
                    editor.showGradeMenu();
                }
                case "5" -> {
                    userInput();
                    displayDashboard();
                }
                case "6" -> {
                    System.out.println("Saving document...");
                    saveXMLFile(doc);
                    System.out.println("Application closed. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Maps the user's term choice (1, 2, 3) to the actual term names in the XML
    private static String getXmlTerm() {
        return switch (termInput) {
            case "1" -> "1st Semester";
            case "2" -> "2nd Semester";
            case "3" -> "Short Term";
            default -> "1st Semester";
        };
    }

    public static void enterGradesCurrentTerm(Document doc) {
        String xmlYear = yearInput;
        String xmlTerm = getXmlTerm();

        System.out.println("\n--- Entering Grades for " + currentYear + " | " + currentTerm + " ---");
        NodeList yearNodes = doc.getElementsByTagName("Year");
        boolean termFound = false;

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);
            if (yearElement.getAttribute("level").equals(xmlYear)) {
                NodeList termNodes = yearElement.getElementsByTagName("Term");
                
                for (int j = 0; j < termNodes.getLength(); j++) {
                    Element termElement = (Element) termNodes.item(j);
                    
                    if (termElement.getAttribute("name").equalsIgnoreCase(xmlTerm)) {
                        termFound = true;
                        NodeList courseNodes = termElement.getElementsByTagName("Course");
                        
                        for (int k = 0; k < courseNodes.getLength(); k++) {
                            Element course = (Element) courseNodes.item(k);
                            String cNumber = getTextValue(course, "CourseNumber");
                            String cTitle = getTextValue(course, "DescriptiveTitle");
                            String currentGrade = getTextValue(course, "Grade");

                            System.out.print("Enter grade for " + cNumber + " - " + cTitle + " (Current: " + currentGrade + ") [Press Enter to skip]: ");
                            String newGrade = kbd.nextLine();

                            if (!newGrade.trim().isEmpty()) {
                                NodeList gradeList = course.getElementsByTagName("Grade");
                                if (gradeList.getLength() > 0) {
                                    gradeList.item(0).setTextContent(newGrade);
                                } else {
                                    Element newGradeElem = doc.createElement("Grade");
                                    newGradeElem.setTextContent(newGrade);
                                    course.appendChild(newGradeElem);
                                }
                                System.out.println(">>> Grade updated.");

                            }

                        }
                    }
                }
            }
        }
        
        if (!termFound) {
            System.out.println("Could not find entries for Year: " + xmlYear + " and Term: " + xmlTerm);
        }
        displayCoursesTable(doc, yearInput, getXmlTerm());
    }

    public static void showAllSubjects(Document doc) {
        System.out.println("\n===== VIEW SUBJECTS =====");
        System.out.println("<1> View subjects (without grade column)");
        System.out.println("<2> View subjects with grades");
        System.out.print("Select an option: ");

        String viewChoice = kbd.nextLine().trim();

        switch (viewChoice) {
            case "1" -> displayAllTerms(doc, false);
            case "2" -> displayAllTerms(doc, true);
            default  -> System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    //Display Method
    public static void displayAllTerms(Document doc, boolean showGrades) {
        NodeList yearNodes = doc.getElementsByTagName("Year");

        if (yearNodes.getLength() == 0) {
            System.out.println("No data found in " + filePath + ".");
            return;
        }

        String[] yearLabels = {"", "FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH"};
        String[] termKeys   = {"1st Semester", "2nd Semester", "Short Term"};
        String[] termLabels = {"FIRST SEMESTER", "SECOND SEMESTER", "SHORT TERM"};

        int tableWidth = showGrades ? 100 : 85;

        // Print overall table header once
        System.out.println("\n" + "=".repeat(tableWidth));
        if (showGrades) {
            System.out.printf("  %-15s | %-60s | %-10s%n", "Course Number", "Descriptive Title", "Grade");
        } else {
            System.out.printf("  %-15s | %-60s%n", "Course Number", "Descriptive Title");
        }
        System.out.println("=".repeat(tableWidth));

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);
            String yearLevel = yearElement.getAttribute("level");

            //number to word
            int yearIndex = 0;
            try { yearIndex = Integer.parseInt(yearLevel); } catch (NumberFormatException e) { yearIndex = 0; }
            String yearLabel = (yearIndex > 0 && yearIndex < yearLabels.length)
                    ? yearLabels[yearIndex] + " YEAR"
                    : "YEAR " + yearLevel;

            NodeList termNodes = yearElement.getElementsByTagName("Term");

            for (int j = 0; j < termNodes.getLength(); j++) {
                Element termElement = (Element) termNodes.item(j);
                String termName = termElement.getAttribute("name");

                NodeList courseNodes = termElement.getElementsByTagName("Course");
                int courseCount = courseNodes.getLength();

                //Skips no terms w/no subject
                if (courseCount == 0) continue;

                //Labels
                String termLabel = termName.toUpperCase();
                for (int t = 0; t < termKeys.length; t++) {
                    if (termKeys[t].equalsIgnoreCase(termName)) {
                        termLabel = termLabels[t];
                        break;
                    }
                }

                //Divider
                System.out.printf("  %s%n", yearLabel + " \u2014 " + termLabel);
                System.out.println("-".repeat(tableWidth));

                //Store course for array
                String[] courseNumbers = new String[courseCount];
                String[] titles        = new String[courseCount];
                String[] grades        = new String[courseCount];

                for (int k = 0; k < courseCount; k++) {
                    Element course = (Element) courseNodes.item(k);

                    courseNumbers[k] = getTextValue(course, "CourseNumber");
                    titles[k]        = getTextValue(course, "DescriptiveTitle");
                    grades[k]        = getTextValue(course, "Grade");

                    // Truncate long titles to keep table clean
                    if (titles[k].length() > 60) titles[k] = titles[k].substring(0, 57) + "...";
                }

                //Print rows
                for (int k = 0; k < courseCount; k++) {
                    if (showGrades) {
                        System.out.printf("  %-15s | %-60s | %-10s%n",
                                courseNumbers[k], titles[k], grades[k]);
                    } else {
                        System.out.printf("  %-15s | %-60s%n",
                                courseNumbers[k], titles[k]);
                    }
                }

                System.out.println("-".repeat(tableWidth));
            }
        }

        System.out.println("=".repeat(tableWidth));
    }


    /**
     * Searches the XML document for a specific Year and Term, then prints all courses
     * in a neatly formatted console table.
     */
    public static void displayCoursesTable(Document doc, String yearLevel, String termName) {
        NodeList yearNodes = doc.getElementsByTagName("Year");
        boolean termFound = false;

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);

            if (yearElement.getAttribute("level").equals(yearLevel)) {
                NodeList termNodes = yearElement.getElementsByTagName("Term");

                for (int j = 0; j < termNodes.getLength(); j++) {
                    Element termElement = (Element) termNodes.item(j);

                    if (termElement.getAttribute("name").equalsIgnoreCase(termName)) {
                        termFound = true;
                        System.out.println("\n--- Courses for Year " + yearLevel + ", " + termElement.getAttribute("name") + " ---");

                        // Print Table Header
                        System.out.printf("%-15s | %-65s | %-5s | %-20s | %-5s%n",
                                "Course Number", "Descriptive Title", "Units", "Prerequisites", "Grade");
                        System.out.println("-".repeat(125)); // Java 11+ feature

                        // Extract and print course details
                        NodeList courseNodes = termElement.getElementsByTagName("Course");
                        for (int k = 0; k < courseNodes.getLength(); k++) {
                            Element course = (Element) courseNodes.item(k);

                            String cNumber = getTextValue(course, "CourseNumber");
                            String title = getTextValue(course, "DescriptiveTitle");
                            String units = getTextValue(course, "Units");
                            String prereq = getTextValue(course, "Prerequisites");
                            String grade = getTextValue(course, "Grade");

                            // Truncate long titles/prerequisites slightly to keep the table clean if necessary
                            if (title.length() > 65) title = title.substring(0, 62) + "...";
                            if (prereq.length() > 20) prereq = prereq.substring(0, 17) + "...";

                            System.out.printf("%-15s | %-65s | %-5s | %-20s | %-5s%n",
                                    cNumber, title, units, prereq, grade);
                        }
                        System.out.println("-".repeat(125));
                        break;
                    }
                }
            }
        }

        if (!termFound) {
            System.out.println("Could not find entries for Year: " + yearLevel + " and Term: " + termName);
        }
    }

    private static String getTextValue(Element parentElement, String tagName) {
        NodeList list = parentElement.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    public static void saveXMLFile(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            System.out.println(">>> Changes saved to file.");
        } catch (Exception e) {
            System.out.println(">>> Error saving XML: " + e.getMessage());
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
