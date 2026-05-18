import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Main {
    public static Scanner kbd = new Scanner(System.in);
    public static String name = "";
    public static String yearInput = "";
    public static String termInput = "";
    public static String currentYear = "1";
    public static String currentTerm = "1";
    public static String filePath = "src/Data.xml";
    public static String userInfo = "src/userinfo.txt";
    public static String courseCode = "";
    public static String descriptiveTitle = "";
    public static double units = 0;

    public static String getFormattedInput(Scanner scanner) {
        String input = scanner.nextLine();
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.trim().toUpperCase().replaceAll("\\s+", " ");
    }

    static void main(String[] args) {
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

        try {
            File userinfoTxt = new File(userInfo);

            if (!userinfoTxt.exists() || userinfoTxt.length() == 0) {
                userInput();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(userInfo));

                String line = br.readLine();
                String line2 = br.readLine();
                String line3 = br.readLine();

                if (line.isEmpty() || !line.contains("Name:")) {
                    userInput();
                }
                if (line2.isEmpty() || !line2.contains("Year:")) {
                    userInput();
                }
                if (line3.isEmpty() || !line3.contains("Term:")) {
                    userInput();
                } else {
                    name = line.substring(line.indexOf(":") + 1).trim();
                    yearInput = line2.substring(line2.indexOf(":") + 1).trim();
                    termInput = line3.substring(line3.indexOf(":") + 1).trim();
                }

                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        displayDashboard();
        System.out.println("Welcome, " + name + "!");

        mainMenu(doc);
    }

    public static void userInput() {
        System.out.println("======================================");
        System.out.println("Welcome to your Checklist Monitoring Application!");
        System.out.println("Please enter your information.");

        System.out.print("Enter name: ");
        while (true) {
            name = getFormattedInput(kbd);
            if (!name.isEmpty()) break;
            System.out.print("Name cannot be empty. Enter name: ");
        }

        System.out.println();

        System.out.println("Year Level");
        String[] yearArray = {"First Year", "Second Year", "Third Year", "Fourth Year"};
        for (int i = 0; i < yearArray.length; i++) {
            System.out.println("<" + (i + 1) + "> " + yearArray[i]);
        }
        System.out.println();

        System.out.print("Choose Year Level: ");
        while (true) {
            yearInput = getFormattedInput(kbd);
            if (yearInput.matches("[1-4]")) break;
            System.out.print("Invalid input. Choose Year Level (1-4): ");
        }
        System.out.println();

        System.out.println("Current Term");
        String[] semesterArray = {"First Semester", "Second Semester", "Short Term"};
        for (int i = 0; i < semesterArray.length; i++) {
            System.out.println("<" + (i + 1) + "> " + semesterArray[i]);
        }
        System.out.println();

        System.out.print("Choose Current Term: ");
        while (true) {
            termInput = getFormattedInput(kbd);
            if (termInput.matches("[1-3]")) break;
            System.out.print("Invalid input. Choose Current Term (1-3): ");
        }

        System.out.println();

        try (PrintWriter pw = new PrintWriter(new FileWriter(userInfo))) {
            pw.println("Name: " + name);
            pw.println("Year: " + yearInput);
            pw.println("Term: " + termInput);
        } catch (IOException e) {
            System.out.println("Error saving user info.");
        }
    }

    public static void displayDashboard() {
        switch (yearInput) {
            case "1" -> currentYear = "First Year";
            case "2" -> currentYear = "Second Year";
            case "3" -> currentYear = "Third Year";
            case "4" -> currentYear = "Fourth Year";
            default -> {
                currentYear = "First Year";
                System.out.println("Unrecognized year. Defaulting to Year 1.");
            }
        }

        switch (termInput) {
            case "1" -> currentTerm = "First Semester";
            case "2" -> currentTerm = "Second Semester";
            case "3" -> currentTerm = "Short Term";
            default -> {
                currentTerm = "First Semester";
                System.out.println("Unrecognized term. Defaulting to Semester 1.");
            }
        }
    }

    public static void mainMenu(Document doc) {
        String resetDataInput = "";
        boolean running = true;

        while (running) {
            System.out.println("\n===== DASHBOARD =====");
            System.out.println("NAME: " + name);
            System.out.println("YEAR: " + currentYear);
            System.out.println("TERM: " + currentTerm);

            System.out.println("\n===== MAIN MENU =====");
            System.out.println("<0> Reset data");
            System.out.println("<1> Show subjects for each school term");
            System.out.println("<2> Show subjects with grades for current term");
            System.out.println("<3> Enter grades for subjects recently finished");
            System.out.println("<4> Edit a course grade");
            System.out.println("<5> Add additional course");
            System.out.println("<6> Edit personal information");
            System.out.println("<7> Save and exit");
            System.out.print("Select an option: ");

            String choice;
            while (true) {
                choice = getFormattedInput(kbd);
                if (choice.matches("[0-7]")) break;
                System.out.print("Invalid choice. Select an option (0-7): ");
            }

            switch (choice) {
                case "0" -> {
                    System.out.println("\nAre you sure you want to reset your data?: ");
                    System.out.println("<1> YES, I want to reset my data and start over.");
                    System.out.println("<2> NO, go back to the Main Menu.");
                    System.out.println();
                    System.out.print("Select an option: ");

                    while (true) {
                        resetDataInput = getFormattedInput(kbd);
                        if (resetDataInput.matches("[1-2]")) break;
                        System.out.print("Invalid input. Select an option (1-2): ");
                    }

                    switch (resetDataInput) {
                        case "1" -> {
                            System.out.println("Resetting data...");
                            try (PrintWriter pw = new PrintWriter(new FileWriter(userInfo))) {
                                pw.print("");
                            } catch (IOException e) {
                                System.out.println("Error clearing user info.");
                            }

                            try {
                                Files.copy(Paths.get("src/Data_copy.xml"), Paths.get("src/Data.xml"), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.out.println("Error saving user info.");
                            }

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
                case "2" -> displayCoursesTable(doc, getXmlYear(), getXmlTerm());
                case "3" -> enterGradesCurrentTerm(doc);
                case "4" -> {
                    GradeEditor editor = new GradeEditor(doc, filePath, kbd);
                    editor.showGradeMenu();
                }

                case "5" -> {
                    addCourse(doc);
                }

                case "6" -> {
                    userInput();
                    displayDashboard();
                }
                case "7" -> {
                    System.out.println("Saving document...");
                    saveXMLFile(doc);
                    System.out.println("Application closed. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static String getXmlTerm() {
        return switch (termInput) {
            case "1" -> "1st Semester";
            case "2" -> "2nd Semester";
            case "3" -> "Short Term";
            default -> "1st Semester";
        };
    }

    private static String getXmlYear() {
        return switch (yearInput) {
            case "First Year" -> "1";
            case "Second Year" -> "2";
            case "Third Year" -> "3";
            case "Fourth Year" -> "4";
            default -> "1";
        };
    }

    public static void enterGradesCurrentTerm(Document doc) {
        String xmlYear = yearInput;
        String xmlTerm = getXmlTerm();

        System.out.println("\n===== Entering Grades for " + currentYear + " | " + currentTerm + " =====");
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

                            String newGrade;
                            while (true) {
                                System.out.print("Enter grade for " + cNumber + " - " + cTitle + " (Current: " + currentGrade + ") [Press Enter to skip]: ");
                                newGrade = getFormattedInput(kbd);
                                if (newGrade.isEmpty()) break;
                                try {
                                    int g = Integer.parseInt(newGrade);
                                    if (g >= 65 && g <= 99) break;
                                    System.out.println("Invalid Grade. Must be between 65 and 99.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid Input. Enter a number between 65 and 99.");
                                }
                            }

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
        displayCoursesTable(doc, getXmlYear(), getXmlTerm());
    }

    public static void addCourse(Document doc) {
        System.out.println("\n=========== Add Course ===========");
        System.out.println("Please enter the following information:");
        do {
            System.out.print("Course Code: ");
            courseCode = kbd.nextLine().trim();
            if (courseCode.isEmpty()) {
                System.out.println("Course code cannot be empty. Please try again.");
            }
        } while (courseCode.isEmpty());

        do {
            System.out.print("Descriptive Title: ");
            descriptiveTitle = kbd.nextLine().trim();
            if (descriptiveTitle.isEmpty()) {
                System.out.println("Descriptive title cannot be empty. Please try again.");
            }
        } while (descriptiveTitle.isEmpty());

        System.out.print("Prerequisites (separate by comma): ");
        String preReq = kbd.nextLine();

        String[] preq = preReq.split(",");
        for (String p : preq) {
            p.trim();
        }
        String preqFinal = String.join(", ", preq);

        do {
            try {
                System.out.print("Units: ");
                units = Double.parseDouble(kbd.nextLine());

                if (units <= 0) {
                    System.out.println("Units must be greater than zero.");
                }

            } catch (NumberFormatException e) {
                units = -1;
                System.out.println("Invalid number.");
            }
        } while (units <= 0);

        try {
            String xmlYear = "";
            switch (currentYear.trim()) {

                case "First Year":
                    xmlYear = "1";
                    break;

                case "Second Year":
                    xmlYear = "2";
                    break;

                case "Third Year":
                    xmlYear = "3";
                    break;

                case "Fourth Year":
                    xmlYear = "4";
                    break;

            }

            String xmlTerm = "";

            switch (currentTerm.trim()) {

                case "First Semester":
                    xmlTerm = "1st Semester";
                    break;

                case "Second Semester":
                    xmlTerm = "2nd Semester";
                    break;

                case "Short Term":
                    xmlTerm = "Short Term";
                    break;
            }

            NodeList yearNodes = doc.getElementsByTagName("Year");

            Element targetTerm = null;

            for (int i = 0; i < yearNodes.getLength(); i++) {
                Element yearElement = (Element) yearNodes.item(i);
                String level = yearElement.getAttribute("level");
                if (level.equals(xmlYear)) {
                    NodeList termList = yearElement.getElementsByTagName("Term");
                    for (int j = 0; j < termList.getLength(); j++) {
                        Element termElement = (Element) termList.item(j);
                        String termName = termElement.getAttribute("name");
                        if (termName.equals(xmlTerm)) {
                            targetTerm = termElement;
                            break;
                        }
                    }
                }
            }

            if (targetTerm == null) {
                System.out.println("Current year/term not found.");
                return;
            }

            Element course = doc.createElement("Course");

            Element courseNumber = doc.createElement("CourseNumber");
            courseNumber.appendChild(doc.createTextNode(courseCode));
            course.appendChild(courseNumber);

            Element title = doc.createElement("DescriptiveTitle");
            title.appendChild(doc.createTextNode(descriptiveTitle));
            course.appendChild(title);

            Element unitsElement = doc.createElement("Units");
            unitsElement.appendChild(doc.createTextNode(String.valueOf(units)));
            course.appendChild(unitsElement);

            Element prereq = doc.createElement("Prerequisites");
            prereq.appendChild(doc.createTextNode(preqFinal));
            course.appendChild(prereq);

            Element gradeElement = doc.createElement("Grade");
            gradeElement.appendChild(doc.createTextNode("NO GRADES YET"));
            course.appendChild(gradeElement);

            targetTerm.appendChild(course);

            doc.getDocumentElement().normalize();
            removeWhitespaceNodes(doc.getDocumentElement());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(filePath);
            transformer.transform(source, result);

            System.out.println("\nCourse added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeWhitespaceNodes(Node node) {

        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE &&
                    child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.hasChildNodes()) {
                removeWhitespaceNodes(child);
            }
        }
    }

    public static void showAllSubjects(Document doc) {
        System.out.println("\n===== VIEW SUBJECTS =====");
        System.out.println("<1> View subjects (without grade column)");
        System.out.println("<2> View subjects with grades");
        System.out.print("Select an option: ");

        String viewChoice;
        while (true) {
            viewChoice = getFormattedInput(kbd);
            if (viewChoice.matches("[1-2]")) break;
            System.out.print("Invalid choice. Select an option (1-2): ");
        }

        switch (viewChoice) {
            case "1" -> displayAllTerms(doc, false);
            case "2" -> displayAllTerms(doc, true);
            default -> System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    public static void displayAllTerms(Document doc, boolean showGrades) {
        NodeList yearNodes = doc.getElementsByTagName("Year");

        if (yearNodes.getLength() == 0) {
            System.out.println("No data found in " + filePath + ".");
            return;
        }

        String[] yearLabels = {"", "FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH"};
        String[] termKeys = {"1st Semester", "2nd Semester", "Short Term"};
        String[] termLabels = {"FIRST SEMESTER", "SECOND SEMESTER", "SHORT TERM"};

        int tableWidth = showGrades ? 115 : 100;

        System.out.println("\n" + "=".repeat(tableWidth));
        if (showGrades) {
            System.out.printf("  %-15s | %-60s | %-10s | %-10s%n", "Course Number", "Descriptive Title", "Grade", "Units");
        } else {
            System.out.printf("  %-15s | %-60s | %-10s%n", "Course Number", "Descriptive Title", "Units");
        }
        System.out.println("=".repeat(tableWidth));

        for (int i = 0; i < yearNodes.getLength(); i++) {
            Element yearElement = (Element) yearNodes.item(i);
            String yearLevel = yearElement.getAttribute("level");

            int yearIndex = 0;
            try {
                yearIndex = Integer.parseInt(yearLevel);
            } catch (NumberFormatException e) {
                yearIndex = 0;
            }
            String yearLabel = (yearIndex > 0 && yearIndex < yearLabels.length)
                    ? yearLabels[yearIndex] + " YEAR"
                    : "YEAR " + yearLevel;

            NodeList termNodes = yearElement.getElementsByTagName("Term");

            for (int j = 0; j < termNodes.getLength(); j++) {
                Element termElement = (Element) termNodes.item(j);
                String termName = termElement.getAttribute("name");

                NodeList courseNodes = termElement.getElementsByTagName("Course");
                int courseCount = courseNodes.getLength();

                if (courseCount == 0) continue;

                String termLabel = termName.toUpperCase();
                for (int t = 0; t < termKeys.length; t++) {
                    if (termKeys[t].equalsIgnoreCase(termName)) {
                        termLabel = termLabels[t];
                        break;
                    }
                }

                System.out.printf("  %s%n", yearLabel + " \u2014 " + termLabel);
                System.out.println("-".repeat(tableWidth));

                String[] courseNumbers = new String[courseCount];
                String[] titles = new String[courseCount];
                String[] grades = new String[courseCount];
                String[] units = new String[courseCount];

                for (int k = 0; k < courseCount; k++) {
                    Element course = (Element) courseNodes.item(k);

                    courseNumbers[k] = getTextValue(course, "CourseNumber");
                    titles[k] = getTextValue(course, "DescriptiveTitle");
                    grades[k] = getTextValue(course, "Grade");
                    units[k] = getTextValue(course, "Units");

                    if (titles[k].length() > 60) titles[k] = titles[k].substring(0, 57) + "...";
                }

                for (int k = 0; k < courseCount; k++) {
                    if (showGrades) {
                        System.out.printf("  %-15s | %-60s | %-10s | %-10s%n",
                                courseNumbers[k], titles[k], grades[k], units[k]);
                    } else {
                        System.out.printf("  %-15s | %-60s | %-10s%n",
                                courseNumbers[k], titles[k], units[k]);
                    }
                }

                System.out.println("-".repeat(tableWidth));
            }
        }

        System.out.println("=".repeat(tableWidth));
    }

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
                        System.out.println("\n===== Courses for Year " + yearLevel + ", " + termElement.getAttribute("name") + " =====");

                        System.out.printf("%-15s | %-65s | %-5s | %-20s | %-5s%n",
                                "Course Number", "Descriptive Title", "Units", "Prerequisites", "Grade");
                        System.out.println("-".repeat(125));

                        NodeList courseNodes = termElement.getElementsByTagName("Course");
                        for (int k = 0; k < courseNodes.getLength(); k++) {
                            Element course = (Element) courseNodes.item(k);

                            String cNumber = getTextValue(course, "CourseNumber");
                            String title = getTextValue(course, "DescriptiveTitle");
                            String units = getTextValue(course, "Units");
                            String prereq = getTextValue(course, "Prerequisites");
                            String grade = getTextValue(course, "Grade");

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

class GradeEditor {
    private final Document doc;
    private final String filePath;
    private final Scanner kbd;

    public GradeEditor(Document doc, String filePath, Scanner kbd) {
        this.doc = doc;
        this.filePath = filePath;
        this.kbd = kbd;
    }

    public void showGradeMenu() {
        System.out.println("\n===== GRADE MANAGEMENT =====");
        System.out.println("<1> Edit Grade");
        System.out.println("<2> Clear Grade");
        System.out.println("<3> Cancel");
        System.out.print("Select an option: ");

        String choice;
        while (true) {
            choice = Main.getFormattedInput(kbd);
            if (choice.matches("[1-3]")) break;
            System.out.print("Invalid choice. Select an option (1-3): ");
        }

        switch (choice) {
            case "1" -> {
                String courseNum;
                while (true) {
                    System.out.print("Enter Course Number to edit (e.g., 'CS 111') or 'exit' to cancel: ");
                    courseNum = Main.getFormattedInput(kbd);
                    if (courseNum.equalsIgnoreCase("exit")) break;

                    if (courseExists(courseNum)) {
                        String newGrade;
                        while (true) {
                            System.out.print("Enter new Grade (65-99): ");
                            newGrade = Main.getFormattedInput(kbd);

                            if (isValidGradeValue(newGrade)) {
                                updateGrade(courseNum, newGrade);
                                saveXMLFile();
                                break;
                            } else {
                                System.out.println("\n>>> Invalid Input: Grade must be a number between 65 and 99.");
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
                    String courseNum = Main.getFormattedInput(kbd);
                    if (courseNum.equalsIgnoreCase("exit")) break;

                    if (courseExists(courseNum)) {
                        boolean validResponse = false;
                        while (!validResponse) {
                            System.out.print("Clear the grade for " + courseNum + "? (Y/N): ");
                            String confirm = Main.getFormattedInput(kbd);

                            if (confirm.equalsIgnoreCase("Y")) {
                                updateGrade(courseNum, "");
                                System.out.println(">>> Grade cleared.\n");
                                saveXMLFile();
                                validResponse = true;
                            } else if (confirm.equalsIgnoreCase("N")) {
                                System.out.println(">>> Clear operation cancelled.");
                                validResponse = true;
                            } else {
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
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private boolean isValidGradeValue(String input) {
        try {
            int grade = Integer.parseInt(input);
            return grade >= 65 && grade <= 99;
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