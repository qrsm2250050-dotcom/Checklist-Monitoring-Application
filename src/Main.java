import java.util.Scanner;
public class Main {
    public static Scanner kbd = new Scanner(System.in);
    public static String name = new String();

    pubic static void main(String[] args) {
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

}
