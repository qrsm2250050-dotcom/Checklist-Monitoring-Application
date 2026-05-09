import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
    public static Scanner kbd = new Scanner(System.in);
    public static String name = "";
    public static String currentYear = "1";
    public static String currentTerm = "1st Semester"; // Updated default to match standard XML
    public static String filePath = "src/Data.xml";
    public static Document doc;

    public static void main(String[] args) {
        loadXMLDocument();

        // Optional: Prompt for initial info if not yet set
        if (name.isEmpty()) {
            System.out.println("Welcome! Please set up your profile.");
            editPersonalInfo();
        }

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
                case "0" -> resetData();
                case "1" -> showSubjectsForEachTerm();
                case "2" -> showSubjectsForCurrentTerm();
                case "3" -> enterGradesForRecentSubjects();
                case "4" -> editCourseGrade();
                case "5" -> editPersonalInfo();
                case "6" -> {
                    saveXMLFile();
                    System.out.println("Saving document...");
                    System.out.println("Application closed. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void loadXMLDocument() {
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                System.out.println("Error: Could not find " + filePath);
                System.exit(0);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void resetData() {
        System.out.println("Are you sure you want to reset your data? (Y/N)");
        String confirm = kbd.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            System.out.println("Resetting data...");
            try (PrintWriter pw = new PrintWriter(new FileWriter("UserInfo.txt"))) {
                pw.print("");
            } catch (IOException e) {
                System.out.println("Error clearing user info.");
            }
            try {
                Files.copy(Paths.get("src/Data_copy.xml"), Paths.get("src/Data.xml"), StandardCopyOption.REPLACE_EXISTING);
                loadXMLDocument(); // Reload the reset XML
                name = "";
                currentYear = "1";
                currentTerm = "1st Semester";
                System.out.println("Reset successful! Please input your information again.");
                editPersonalInfo();
            } catch (IOException e) {
                System.out.println("Error saving user info.");
            }
        }
    }

    private static void showSubjectsForEachTerm() {
        System.out.print("Do you want to show grades alongside subjects? (Y/N): ");
        String showGradesChoice = kbd.nextLine();
        boolean showGrades = showGradesChoice.equalsIgnoreCase("Y");

        // Cycle through all years and terms available in the XML
        NodeList yearList = doc.getElementsByTagName("Year");
        for (int i = 0; i < yearList.getLength(); i++) {
            Element yearElement = (Element) yearList.item(i);
            String yearLevel = yearElement.getAttribute("level");

            NodeList termList = yearElement.getElementsByTagName("Term");
            for (int j = 0; j < termList.getLength(); j++) {
                Element termElement = (Element) termList.item(j);
                String termName = termElement.getAttribute("name");

                System.out.println("\n=== Year: " + yearLevel + " | Term: " + termName + " ===");
                displayCoursesTable(termElement, showGrades);
            }
        }
    }

    private static void showSubjectsForCurrentTerm() {
        Element currentTermElement = findTermElement(currentYear, currentTerm);
        if (currentTermElement != null) {
            System.out.println("\n=== Current Term (Year " + currentYear + ", " + currentTerm + ") ===");
            displayCoursesTable(currentTermElement, true);
        } else {
            System.out.println("Could not find the term for Year " + currentYear + " and " + currentTerm + " in the XML file.");
        }
    }

    private static void enterGradesForRecentSubjects() {
        Element currentTermElement = findTermElement(currentYear, currentTerm);
        if (currentTermElement != null) {
            System.out.println("\n--- Entering Grades for Year " + currentYear + ", " + currentTerm + " ---");
            NodeList courseList = currentTermElement.getElementsByTagName("Course");

            for (int i = 0; i < courseList.getLength(); i++) {
                Element courseElement = (Element) courseList.item(i);
                String cNumber = getTextValue(courseElement, "CourseNumber");
                String cTitle = getTextValue(courseElement, "DescriptiveTitle");
                String currentGrade = getTextValue(courseElement, "Grade");

                System.out.print("Enter grade for " + cNumber + " - " + cTitle + " (Current: " + currentGrade + ") [Press Enter to skip]: ");
                String newGrade = kbd.nextLine();
                if (!newGrade.trim().isEmpty()) {
                    setCourseGrade(courseElement, newGrade);
                    System.out.println(">>> Grade updated.");
                } else {
                    System.out.println(">>> Grade unchanged.");
                }
            }
            saveXMLFile();
        } else {
            System.out.println("Could not find the term for Year " + currentYear + " and " + currentTerm + ".");
        }
    }

    private static void editCourseGrade() {
        System.out.print("Enter Course Number to update (e.g., 'CS 111'): ");
        String targetCourse = kbd.nextLine().trim();
        System.out.print("Enter new Grade: ");
        String newGrade = kbd.nextLine().trim();

        boolean found = false;
        NodeList courseList = doc.getElementsByTagName("Course");
        for (int i = 0; i < courseList.getLength(); i++) {
            Element courseElement = (Element) courseList.item(i);
            String currentCourse = getTextValue(courseElement, "CourseNumber");

            if (currentCourse.equalsIgnoreCase(targetCourse)) {
                setCourseGrade(courseElement, newGrade);
                System.out.println(">>> Grade for " + targetCourse + " updated to " + newGrade + ".");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Course '" + targetCourse + "' not found.");
        } else {
            saveXMLFile();
        }
    }

    private static void editPersonalInfo() {
        System.out.println("\n--- Edit Personal Information ---");

        // 1. Name Selection
        System.out.print("Enter your Name (Press Enter to keep current): ");
        String newName = kbd.nextLine();
        if(!newName.trim().isEmpty()) name = newName;

        // 2. Year Selection
        System.out.println("\nSelect Current Year Level (Press Enter to keep current):");
        System.out.println("[1] 1st Year");
        System.out.println("[2] 2nd Year");
        System.out.println("[3] 3rd Year");
        System.out.println("[4] 4th Year");
        System.out.print("Choice: ");
        String yearChoice = kbd.nextLine().trim();

        switch (yearChoice) {
            case "1" -> currentYear = "1";
            case "2" -> currentYear = "2";
            case "3" -> currentYear = "3";
            case "4" -> currentYear = "4";
            case "" -> {} // Do nothing, keep current
            default -> System.out.println("Invalid choice. Keeping Year " + currentYear);
        }

        // 3. Term Selection
        System.out.println("\nSelect Current Term (Press Enter to keep current):");
        System.out.println("[1] 1st Semester");
        System.out.println("[2] 2nd Semester");
        System.out.println("[3] Short Term");
        System.out.print("Choice: ");
        String termChoice = kbd.nextLine().trim();

        switch (termChoice) {
            case "1" -> currentTerm = "1st Semester";
            case "2" -> currentTerm = "2nd Semester";
            case "3" -> currentTerm = "Short Term";
            case "" -> {} // Do nothing, keep current
            default -> System.out.println("Invalid choice. Keeping " + currentTerm);
        }

        System.out.println("\n>>> Profile Updated: " + (name.isEmpty() ? "Unknown User" : name) + " | Year " + currentYear + " | " + currentTerm);
    }

    // ---------- Helper Functions ----------

    private static Element findTermElement(String year, String term) {
        NodeList yearList = doc.getElementsByTagName("Year");
        for (int i = 0; i < yearList.getLength(); i++) {
            Element yearElement = (Element) yearList.item(i);
            if (yearElement.getAttribute("level").equalsIgnoreCase(year)) {
                NodeList termList = yearElement.getElementsByTagName("Term");
                for (int j = 0; j < termList.getLength(); j++) {
                    Element termElement = (Element) termList.item(j);
                    // Match by exact string ensuring it pulls properly
                    if (termElement.getAttribute("name").equalsIgnoreCase(term)) {
                        return termElement;
                    }
                }
            }
        }
        return null;
    }

    private static void displayCoursesTable(Element termElement, boolean showGrades) {
        NodeList courseList = termElement.getElementsByTagName("Course");

        if (showGrades) {
            System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n", "Course No.", "Descriptive Title", "Units", "Prerequisites", "Grade");
        } else {
            System.out.printf("%-15s | %-65s | %-5s | %-20s%n", "Course No.", "Descriptive Title", "Units", "Prerequisites");
        }
        System.out.println("-".repeat(130));

        for (int i = 0; i < courseList.getLength(); i++) {
            Element course = (Element) courseList.item(i);

            String cNumber = getTextValue(course, "CourseNumber");
            String title = getTextValue(course, "DescriptiveTitle");
            String units = getTextValue(course, "Units");
            String prereq = getTextValue(course, "Prerequisites");
            String grade = getTextValue(course, "Grade");

            if (title.length() > 65) title = title.substring(0, 62) + "...";
            if (prereq.length() > 20) prereq = prereq.substring(0, 17) + "...";

            if (showGrades) {
                System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n", cNumber, title, units, prereq, grade);
            } else {
                System.out.printf("%-15s | %-65s | %-5s | %-20s%n", cNumber, title, units, prereq);
            }
        }
        System.out.println("-".repeat(130));
    }

    private static String getTextValue(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }

    private static void setCourseGrade(Element courseElement, String newGrade) {
        NodeList list = courseElement.getElementsByTagName("Grade");
        if (list != null && list.getLength() > 0) {
            list.item(0).setTextContent(newGrade);
        } else {
            Element gradeElement = doc.createElement("Grade");
            gradeElement.setTextContent(newGrade);
            courseElement.appendChild(gradeElement);
        }
    }

    private static void saveXMLFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            System.out.println(">>> Error saving XML: " + e.getMessage());
        }
    }
}
