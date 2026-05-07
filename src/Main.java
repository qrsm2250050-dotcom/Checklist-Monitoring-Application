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
            System.out.println("1. View Courses Table (by Year and Term)");
            System.out.println("2. Update Course Grade");
            System.out.println("3. Save and Exit");
            System.out.print("Select an option: ");

            String choice = kbd.nextLine();

            switch (choice) {
                case "1" -> {
                    showAllSubjects(doc);
                }
                case "2" -> {
                    System.out.print("Enter Course Number to update (e.g., 'CS 111'): ");
                    String courseNum = kbd.nextLine();
                    System.out.print("Enter new Grade: ");
                    String newGrade = kbd.nextLine();
                    updateCourseGrade(doc, courseNum, newGrade);
                }
                case "3" -> {
                    System.out.println("Saving document...");
                    saveXMLDocument(doc, filePath);
                    System.out.println("Application closed. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==========================================================
    // XML Methods
    // ==========================================================

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
}