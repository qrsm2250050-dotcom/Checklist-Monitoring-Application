import java.util.Scanner;
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
import java.io.File;
public class Main {
    public static Scanner kbd = new Scanner(System.in);
    public static String name = new String();
    public static String filePath = "Data.xml";
    public static void main(String[] args) {
        // xml part
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());


        } catch (Exception e) {
            e.printStackTrace();
        }
        // end of xml part
        System.out.println("--------------------------------------");
        System.out.println("Welcome to your Checklist Monitoring Application");
        System.out.println("Please enter your information.");
        System.out.println();

        System.out.print("Enter name: ");
        name = kbd.nextLine();
        System.out.println("Year Level: ");
        String yrLevel = kbd.nextLine();
        switch (yrLevel) {
            case '1',"first year","first" -> {
                (int) yrLevel = "1";
                break;
            }
            case '2',"second year","second" -> {
                (int) yrLevel = "1";
                break;
            }
            case '3',"third year","third" -> {
                (int) yrLevel = "1";
                break;
            }
            case '4',"fourth year","fourth" -> {
                (int) yrLevel = "1";
                break;
            }
            case '5',"fifth year","fifth" -> {
                (int) yrLevel = "1";
                break;
            }

        }


    }
    // xml methods
    public static boolean updateCourseGrade(Document doc, String targetCourse, String newGrade) {
        NodeList courseList = doc.getElementsByTagName("Course");
        boolean updated = false;

        for (int i = 0; i < courseList.getLength(); i++) {
            Node courseNode = courseList.item(i);

            if (courseNode.getNodeType() == Node.ELEMENT_NODE) {
                Element courseElement = (Element) courseNode;

                String courseNumber = courseElement.getElementsByTagName("CourseNumber")
                        .item(0).getTextContent().trim();

                if (courseNumber.equals(targetCourse)) {
                    Node gradeNode = courseElement.getElementsByTagName("Grade").item(0);
                    gradeNode.setTextContent(newGrade);
                    System.out.println("Updated grade for " + targetCourse + " to " + newGrade);
                    updated = true;
                    break;
                }
            }
        }
        return updated;
    }

    public static void saveXMLDocument(Document doc, String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);
        } catch (Exception e) {
            System.err.println("Error saving the XML file: " + e.getMessage());
        }
    // end of xml methods

}
}