//========== add these for this function
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.Scanner;
import java.io.File;

// for xml input and output
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
    public static String name = new String();
    public static String filePath = "src/Data.xml";

    public static void main(String[] args) {
        Document doc = null;

        // xml initialization
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

        System.out.println("--------------------------------------");
        System.out.println("Welcome to your Checklist Monitoring Application");
        System.out.println("Please enter your information.");
        System.out.println();

        System.out.print("Enter name: ");
        name = kbd.nextLine();

        System.out.print("Year Level: ");
        String yrLevelInput = kbd.nextLine().toLowerCase();
        String currentYear = "1"; // Default to 1

        // Fixed switch statement syntax
        switch (yrLevelInput) {
            case "1", "first year", "first" -> currentYear = "1";
            case "2", "second year", "second" -> currentYear = "2";
            case "3", "third year", "third" -> currentYear = "3";
            case "4", "fourth year", "fourth" -> currentYear = "4";
            case "5", "fifth year", "fifth" -> currentYear = "5";
            default -> System.out.println("Unrecognized year. Defaulting to Year 1.");
        }

        System.out.println("\nWelcome, " + name + " (Year " + currentYear + ")");

        boolean running = true;
        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("4. Edit checklist");
            System.out.print("Select an option: ");

            String choice = kbd.nextLine();

            switch (choice) {
                case "4" -> {
                    System.out.println("\n--- Edit checklist ---");
                    System.out.println("1. Add course");
                    System.out.println("2. Remove a course");
                    System.out.print("Select an option: ");
                    String subChoice4 = kbd.nextLine();

                    if (subChoice4.equals("1")) {
                        printTermMenu();
                        System.out.print("\nSelect the term to add the course to (1-12): ");
                        String termChoice = kbd.nextLine();

                        String targetYear = "";
                        String targetTerm = "";

                        // Map the selection to the exact XML format
                        switch (termChoice) {
                            case "1" -> { targetYear = "1"; targetTerm = "1st Semester"; }
                            case "2" -> { targetYear = "1"; targetTerm = "2nd Semester"; }
                            case "3" -> { targetYear = "1"; targetTerm = "Short Term"; }
                            case "4" -> { targetYear = "2"; targetTerm = "1st Semester"; }
                            case "5" -> { targetYear = "2"; targetTerm = "2nd Semester"; }
                            case "6" -> { targetYear = "2"; targetTerm = "Short Term"; }
                            case "7" -> { targetYear = "3"; targetTerm = "1st Semester"; }
                            case "8" -> { targetYear = "3"; targetTerm = "2nd Semester"; }
                            case "9" -> { targetYear = "3"; targetTerm = "Short Term"; }
                            case "10" -> { targetYear = "4"; targetTerm = "1st Semester"; }
                            case "11" -> { targetYear = "4"; targetTerm = "2nd Semester"; }
                            case "12" -> { targetYear = "4"; targetTerm = "Short Term"; }
                            default -> System.out.println("Invalid selection.");
                        }

                        if (!targetYear.isEmpty()) {
                            System.out.print("Enter Course Number: ");
                            String cNum = kbd.nextLine();
                            System.out.print("Enter Descriptive Title: ");
                            String cTitle = kbd.nextLine();
                            System.out.print("Enter Units: ");
                            String cUnits = kbd.nextLine();
                            System.out.print("Enter Prerequisites: ");
                            String cPrereq = kbd.nextLine();

                            boolean success = addCourse(doc, targetYear, targetTerm, cNum, cTitle, cUnits, cPrereq);

                            if (success) {
                                saveXMLDocument(doc, filePath);
                                System.out.println(">>> Course successfully added!");

                                // === CALL THE NEW METHOD WITH YEAR AND TERM ===
                                displayTermCoursesAlphabetically(doc, targetYear, targetTerm);

                            } else {
                                System.out.println(">>> Error: Could not find the specified Year and Term in the XML.");
                            }
                        }
                    } else if (subChoice4.equals("2")) {
                        System.out.println(">>> Functionality not yet implemented.");
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==========================================================
    // Menu Helper Methods
    // ==========================================================

    private static void printTermMenu() {
        System.out.println("\n--- Select Term ---");
        System.out.println("1. First Year, First Semester");
        System.out.println("2. First Year, Second Semester");
        System.out.println("3. First Year, Short Term");
        System.out.println("4. Second Year, First Semester");
        System.out.println("5. Second Year, Second Semester");
        System.out.println("6. Second Year, Short Term");
        System.out.println("7. Third Year, First Semester");
        System.out.println("8. Third Year, Second Semester");
        System.out.println("9. Third Year, Short Term");
        System.out.println("10. Fourth Year, First Semester");
        System.out.println("11. Fourth Year, Second Semester");
        System.out.println("12. Fourth Year, Short Term");
    }

    // ==========================================================
    // XML Methods
    // ==========================================================

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

    /**
     * Updates the <Grade> tag of a specific course using its Course Number.
     */
    public static boolean updateCourseGrade(Document doc, String targetCourse, String newGrade) {
        NodeList courseList = doc.getElementsByTagName("Course");
        boolean updated = false;

        for (int i = 0; i < courseList.getLength(); i++) {
            Node courseNode = courseList.item(i);

            if (courseNode.getNodeType() == Node.ELEMENT_NODE) {
                Element courseElement = (Element) courseNode;
                String courseNumber = getTextValue(courseElement, "CourseNumber");

                if (courseNumber.equalsIgnoreCase(targetCourse)) {
                    Node gradeNode = courseElement.getElementsByTagName("Grade").item(0);
                    gradeNode.setTextContent(newGrade);
                    System.out.println(">>> Successfully updated grade for " + targetCourse + " to " + newGrade);
                    updated = true;
                    break;

                }
            }
        }

        if (!updated) {
            System.out.println(">>> Course " + targetCourse + " not found. Grade not updated.");
        }

        return updated;
    }

    /**
     * Saves the modified Document back to the XML file.
     */
    public static void saveXMLDocument(Document doc, String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);
            System.out.println(">>> Changes successfully saved to " + filePath);
        } catch (Exception e) {
            System.err.println("Error saving the XML file: " + e.getMessage());
        }
    }

    // Helper method to safely extract text content from an XML element
    private static String getTextValue(Element parentElement, String tagName) {
        NodeList list = parentElement.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    // THIS IS NECESSARY===============================================
    /**
     * Creates a new Course element and appends it to the specified Year and Term in the XML document.
     */
    public static boolean addCourse(Document doc, String yearLevel, String termName,
                                    String courseNum, String title, String units, String prereq) {
        NodeList yearNodes = doc.getElementsByTagName("Year");

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);

            if (yearElement.getAttribute("level").equals(yearLevel)) {
                NodeList termNodes = yearElement.getElementsByTagName("Term");

                for (int j = 0; j < termNodes.getLength(); j++) {
                    Element termElement = (Element) termNodes.item(j);

                    if (termElement.getAttribute("name").equalsIgnoreCase(termName)) {

                        // Create the new <Course> node
                        Element newCourse = doc.createElement("Course");

                        // Add <CourseNumber>
                        Element cNum = doc.createElement("CourseNumber");
                        cNum.appendChild(doc.createTextNode(courseNum));
                        newCourse.appendChild(cNum);

                        // Add <DescriptiveTitle>
                        Element cTitle = doc.createElement("DescriptiveTitle");
                        cTitle.appendChild(doc.createTextNode(title));
                        newCourse.appendChild(cTitle);

                        // Add <Units>
                        Element cUnits = doc.createElement("Units");
                        cUnits.appendChild(doc.createTextNode(units));
                        newCourse.appendChild(cUnits);

                        // Add <Prerequisites>
                        Element cPrereq = doc.createElement("Prerequisites");
                        cPrereq.appendChild(doc.createTextNode(prereq));
                        newCourse.appendChild(cPrereq);

                        // Add <Grade> with default text
                        Element cGrade = doc.createElement("Grade");
                        cGrade.appendChild(doc.createTextNode("NO GRADES YET"));
                        newCourse.appendChild(cGrade);

                        // Append the newly formed course to the Term element
                        termElement.appendChild(newCourse);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Extracts all courses from the XML document, sorts them alphabetically by Course Number,
     * and displays them in a formatted table.
     */
    public static void displayAllCoursesAlphabetically(Document doc) {
        NodeList courseNodes = doc.getElementsByTagName("Course");
        List<Element> courseList = new ArrayList<>();

        // 1. Extract all <Course> elements into a Java List
        for (int i = 0; i < courseNodes.getLength(); i++) {
            Node node = courseNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                courseList.add((Element) node);
            }
        }

        // 2. Sort the list alphabetically by the <CourseNumber> text content
        courseList.sort(Comparator.comparing(c -> getTextValue(c, "CourseNumber").toUpperCase()));

        // 3. Print the formatted table
        System.out.println("\n--- Full Course List (Alphabetical by Course Number) ---");
        System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n",
                "Course Number", "Descriptive Title", "Units", "Prerequisites", "Grade");
        System.out.println("-".repeat(130));

        for (Element course : courseList) {
            String cNumber = getTextValue(course, "CourseNumber");
            String title = getTextValue(course, "DescriptiveTitle");
            String units = getTextValue(course, "Units");
            String prereq = getTextValue(course, "Prerequisites");
            String grade = getTextValue(course, "Grade");

            // Truncate long strings to keep the table clean
            if (title.length() > 65) title = title.substring(0, 62) + "...";
            if (prereq.length() > 20) prereq = prereq.substring(0, 17) + "...";

            System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n",
                    cNumber, title, units, prereq, grade);
        }
        System.out.println("-".repeat(130));
    }
    /**
     * Extracts courses for a specific Year and Term, sorts them alphabetically by
     * Course Number, and displays them in a formatted table.
     */
    public static void displayTermCoursesAlphabetically(Document doc, String yearLevel, String termName) {
        NodeList yearNodes = doc.getElementsByTagName("Year");

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);

            if (yearElement.getAttribute("level").equals(yearLevel)) {
                NodeList termNodes = yearElement.getElementsByTagName("Term");

                for (int j = 0; j < termNodes.getLength(); j++) {
                    Element termElement = (Element) termNodes.item(j);

                    if (termElement.getAttribute("name").equalsIgnoreCase(termName)) {

                        // 1. Extract courses ONLY for this specific term
                        NodeList courseNodes = termElement.getElementsByTagName("Course");
                        List<Element> courseList = new ArrayList<>();

                        for (int k = 0; k < courseNodes.getLength(); k++) {
                            Node node = courseNodes.item(k);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                courseList.add((Element) node);
                            }
                        }

                        // 2. Sort the list alphabetically by CourseNumber
                        courseList.sort(Comparator.comparing(c -> getTextValue(c, "CourseNumber").toUpperCase()));

                        // 3. Print the formatted table
                        System.out.println("\n--- Updated Course List for Year " + yearLevel + ", " + termName + " ---");
                        System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n",
                                "Course Number", "Descriptive Title", "Units", "Prerequisites", "Grade");
                        System.out.println("-".repeat(130));

                        for (Element course : courseList) {
                            String cNumber = getTextValue(course, "CourseNumber");
                            String title = getTextValue(course, "DescriptiveTitle");
                            String units = getTextValue(course, "Units");
                            String prereq = getTextValue(course, "Prerequisites");
                            String grade = getTextValue(course, "Grade");

                            // Truncate long strings to keep the table clean
                            if (title.length() > 65) title = title.substring(0, 62) + "...";
                            if (prereq.length() > 20) prereq = prereq.substring(0, 17) + "...";

                            System.out.printf("%-15s | %-65s | %-5s | %-20s | %-15s%n",
                                    cNumber, title, units, prereq, grade);
                        }
                        System.out.println("-".repeat(130));
                        return; // Exit once we found and printed the term
                    }
                }
            }
        }
        System.out.println("Could not find entries for Year: " + yearLevel + " and Term: " + termName);
    }
}