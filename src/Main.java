import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.File;

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

        while (true) {

            System.out.println("\n===== MAIN MENU =====");
            System.out.println("4. Edit checklist");
            System.out.print("Select an option: ");

            String choice = kbd.nextLine();

            switch (choice) {

                case "4" -> {

                    System.out.println("\n--- Edit Checklist ---");
                    System.out.println("1. Add course");
                    System.out.println("2. Remove course");
                    System.out.print("Select an option: ");

                    String subChoice = kbd.nextLine();

                    if (subChoice.equals("1")) {

                        printTermMenu();

                        System.out.print("\nSelect term (1-12): ");
                        String termChoice = kbd.nextLine();

                        String[] termData = getYearAndTerm(termChoice);

                        if (termData == null) {
                            System.out.println("Invalid selection.");
                            continue;
                        }

                        String year = termData[0];
                        String term = termData[1];

                        System.out.print("Enter Course Number: ");
                        String courseNum = kbd.nextLine();

                        System.out.print("Enter Descriptive Title: ");
                        String title = kbd.nextLine();

                        System.out.print("Enter Units: ");
                        String units = kbd.nextLine();

                        System.out.print("Enter Prerequisites: ");
                        String prereq = kbd.nextLine();

                        boolean added = addCourse(
                                doc,
                                year,
                                term,
                                courseNum,
                                title,
                                units,
                                prereq
                        );

                        if (added) {

                            saveXMLDocument(doc, filePath);

                            System.out.println(">>> Course successfully added!");

                            displayTermCoursesAlphabetically(doc, year, term);

                        } else {
                            System.out.println(">>> Failed to add course.");
                        }

                    } else if (subChoice.equals("2")) {

                        printTermMenu();

                        System.out.print("\nSelect term (1-12): ");
                        String termChoice = kbd.nextLine();

                        String[] termData = getYearAndTerm(termChoice);

                        if (termData == null) {
                            System.out.println("Invalid selection.");
                            continue;
                        }

                        String year = termData[0];
                        String term = termData[1];

                        NodeList yearNodes = doc.getElementsByTagName("Year");

                        List<Element> courseList = new ArrayList<>();
                        Element targetTermElement = null;

                        for (int i = 0; i < yearNodes.getLength(); i++) {

                            Element yearElement = (Element) yearNodes.item(i);

                            if (yearElement.getAttribute("level").equals(year)) {

                                NodeList termNodes = yearElement.getElementsByTagName("Term");

                                for (int j = 0; j < termNodes.getLength(); j++) {

                                    Element termElement = (Element) termNodes.item(j);

                                    if (termElement.getAttribute("name").equalsIgnoreCase(term)) {

                                        targetTermElement = termElement;

                                        NodeList courses = termElement.getElementsByTagName("Course");

                                        for (int k = 0; k < courses.getLength(); k++) {

                                            Node node = courses.item(k);

                                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                                courseList.add((Element) node);
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                        }

                        if (courseList.isEmpty()) {
                            System.out.println("No courses found.");
                            continue;
                        }

                        courseList.sort(
                                Comparator.comparing(
                                        c -> getTextValue(c, "CourseNumber").toUpperCase()
                                )
                        );

                        System.out.println("\n--- Select Course to Remove ---");

                        for (int i = 0; i < courseList.size(); i++) {

                            String courseNum = getTextValue(
                                    courseList.get(i),
                                    "CourseNumber"
                            );

                            String title = getTextValue(
                                    courseList.get(i),
                                    "DescriptiveTitle"
                            );

                            System.out.println(
                                    (i + 1) + ". " + courseNum + " - " + title
                            );
                        }

                        System.out.print("Enter choice: ");

                        int removeIndex;

                        try {
                            removeIndex = Integer.parseInt(kbd.nextLine()) - 1;
                        } catch (Exception e) {
                            System.out.println("Invalid input.");
                            continue;
                        }

                        if (removeIndex < 0 || removeIndex >= courseList.size()) {
                            System.out.println("Invalid selection.");
                            continue;
                        }

                        Element selectedCourse = courseList.get(removeIndex);

                        targetTermElement.removeChild(selectedCourse);

                        saveXMLDocument(doc, filePath);

                        System.out.println(">>> Course successfully removed!");

                        displayTermCoursesAlphabetically(doc, year, term);

                    } else {
                        System.out.println("Invalid choice.");
                    }
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void printTermMenu() {

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

    public static String[] getYearAndTerm(String choice) {

        return switch (choice) {

            case "1" -> new String[]{"1", "1st Semester"};
            case "2" -> new String[]{"1", "2nd Semester"};
            case "3" -> new String[]{"1", "Short Term"};

            case "4" -> new String[]{"2", "1st Semester"};
            case "5" -> new String[]{"2", "2nd Semester"};
            case "6" -> new String[]{"2", "Short Term"};

            case "7" -> new String[]{"3", "1st Semester"};
            case "8" -> new String[]{"3", "2nd Semester"};
            case "9" -> new String[]{"3", "Short Term"};

            case "10" -> new String[]{"4", "1st Semester"};
            case "11" -> new String[]{"4", "2nd Semester"};
            case "12" -> new String[]{"4", "Short Term"};

            default -> null;
        };
    }

    public static void saveXMLDocument(Document doc, String filePath) {

        try {

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();

            Transformer transformer =
                    transformerFactory.newTransformer();

            transformer.setOutputProperty(
                    javax.xml.transform.OutputKeys.INDENT,
                    "yes"
            );

            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);

            System.out.println(
                    ">>> Changes successfully saved to " + filePath
            );

        } catch (Exception e) {

            System.out.println(
                    "Error saving XML file: " + e.getMessage()
            );
        }
    }

    public static String getTextValue(
            Element parentElement,
            String tagName
    ) {

        NodeList list = parentElement.getElementsByTagName(tagName);

        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }

        return "";
    }

    public static boolean addCourse(
            Document doc,
            String yearLevel,
            String termName,
            String courseNum,
            String title,
            String units,
            String prereq
    ) {

        NodeList yearNodes = doc.getElementsByTagName("Year");

        for (int i = 0; i < yearNodes.getLength(); i++) {

            Element yearElement = (Element) yearNodes.item(i);

            if (yearElement.getAttribute("level").equals(yearLevel)) {

                NodeList termNodes =
                        yearElement.getElementsByTagName("Term");

                for (int j = 0; j < termNodes.getLength(); j++) {

                    Element termElement =
                            (Element) termNodes.item(j);

                    if (
                            termElement.getAttribute("name")
                                    .equalsIgnoreCase(termName)
                    ) {

                        Element newCourse =
                                doc.createElement("Course");

                        Element cNum =
                                doc.createElement("CourseNumber");

                        cNum.appendChild(
                                doc.createTextNode(courseNum)
                        );

                        newCourse.appendChild(cNum);

                        Element cTitle =
                                doc.createElement("DescriptiveTitle");

                        cTitle.appendChild(
                                doc.createTextNode(title)
                        );

                        newCourse.appendChild(cTitle);

                        Element cUnits =
                                doc.createElement("Units");

                        cUnits.appendChild(
                                doc.createTextNode(units)
                        );

                        newCourse.appendChild(cUnits);

                        Element cPrereq =
                                doc.createElement("Prerequisites");

                        cPrereq.appendChild(
                                doc.createTextNode(prereq)
                        );

                        newCourse.appendChild(cPrereq);

                        Element cGrade =
                                doc.createElement("Grade");

                        cGrade.appendChild(
                                doc.createTextNode("NO GRADES YET")
                        );

                        newCourse.appendChild(cGrade);

                        termElement.appendChild(newCourse);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void displayTermCoursesAlphabetically(
            Document doc,
            String yearLevel,
            String termName
    ) {

        NodeList yearNodes = doc.getElementsByTagName("Year");

        for (int i = 0; i < yearNodes.getLength(); i++) {

            Element yearElement = (Element) yearNodes.item(i);

            if (yearElement.getAttribute("level").equals(yearLevel)) {

                NodeList termNodes =
                        yearElement.getElementsByTagName("Term");

                for (int j = 0; j < termNodes.getLength(); j++) {

                    Element termElement =
                            (Element) termNodes.item(j);

                    if (
                            termElement.getAttribute("name")
                                    .equalsIgnoreCase(termName)
                    ) {

                        NodeList courseNodes =
                                termElement.getElementsByTagName("Course");

                        List<Element> courseList =
                                new ArrayList<>();

                        for (int k = 0; k < courseNodes.getLength(); k++) {

                            Node node = courseNodes.item(k);

                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                courseList.add((Element) node);
                            }
                        }

                        courseList.sort(
                                Comparator.comparing(
                                        c -> getTextValue(
                                                c,
                                                "CourseNumber"
                                        ).toUpperCase()
                                )
                        );

                        System.out.println(
                                "\n--- Updated Course List ---"
                        );

                        System.out.printf(
                                "%-15s | %-65s | %-5s | %-20s | %-15s%n",
                                "Course Number",
                                "Descriptive Title",
                                "Units",
                                "Prerequisites",
                                "Grade"
                        );

                        System.out.println("-".repeat(130));

                        for (Element course : courseList) {

                            String cNumber =
                                    getTextValue(course, "CourseNumber");

                            String title =
                                    getTextValue(course, "DescriptiveTitle");

                            String units =
                                    getTextValue(course, "Units");

                            String prereq =
                                    getTextValue(course, "Prerequisites");

                            String grade =
                                    getTextValue(course, "Grade");

                            if (title.length() > 65) {
                                title = title.substring(0, 62) + "...";
                            }

                            if (prereq.length() > 20) {
                                prereq = prereq.substring(0, 17) + "...";
                            }

                            System.out.printf(
                                    "%-15s | %-65s | %-5s | %-20s | %-15s%n",
                                    cNumber,
                                    title,
                                    units,
                                    prereq,
                                    grade
                            );
                        }

                        System.out.println("-".repeat(130));

                        return;
                    }
                }
            }
        }

        System.out.println(
                "Could not find entries for Year "
                        + yearLevel
                        + " and Term "
                        + termName
        );
    }
}