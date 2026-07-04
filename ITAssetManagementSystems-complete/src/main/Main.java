package main;

import dao.AssetDAO;
import dao.EmployeeDAO;
import dao.HelpdeskTicketDAO;
import java.util.List;
import java.util.Scanner;
import model.Asset;
import model.AssetAssignment;
import model.AssetStatus;
import model.Employee;
import model.HelpdeskTicket;
import model.TicketPriority;
import service.AssetService;
import service.EmployeeService;
import service.HelpdeskService;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static EmployeeService employeeService;
    private static AssetService assetService;
    private static HelpdeskService helpdeskService;

    public static void main(String[] args) {
        boolean databaseMode = args.length > 0 && "--db".equalsIgnoreCase(args[0]);
        configureServices(databaseMode);

        if (!databaseMode) {
            seedData();
        }

        System.out.println(databaseMode ? "Running in database mode." : "Running in memory mode.");
        runMenu();
    }

    private static void configureServices(boolean databaseMode) {
        if (databaseMode) {
            employeeService = new EmployeeService(new EmployeeDAO());
            assetService = new AssetService(new AssetDAO());
            helpdeskService = new HelpdeskService(new HelpdeskTicketDAO());
            return;
        }

        employeeService = new EmployeeService();
        assetService = new AssetService();
        helpdeskService = new HelpdeskService();
    }

    private static void runMenu() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose option: ");

            try {
                switch (choice) {
                    case 1:
                        addEmployee();
                        break;
                    case 2:
                        addAsset();
                        break;
                    case 3:
                        assignAsset();
                        break;
                    case 4:
                        returnAsset();
                        break;
                    case 5:
                        createTicket();
                        break;
                    case 6:
                        closeTicket();
                        break;
                    case 7:
                        listEmployees();
                        break;
                    case 8:
                        listAssets();
                        break;
                    case 9:
                        listAssignments();
                        break;
                    case 10:
                        listTickets();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                printError(exception);
            }
        }
    }

    private static void printError(RuntimeException exception) {
        System.out.println("Error: " + exception.getMessage());

        Throwable cause = exception.getCause();
        if (cause != null && cause.getMessage() != null) {
            System.out.println("Reason: " + cause.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("IT Asset & Helpdesk Management System");
        System.out.println("-------------------------------------");
        System.out.println("1. Add employee");
        System.out.println("2. Add asset");
        System.out.println("3. Assign asset");
        System.out.println("4. Return asset");
        System.out.println("5. Create helpdesk ticket");
        System.out.println("6. Close helpdesk ticket");
        System.out.println("7. List employees");
        System.out.println("8. List assets");
        System.out.println("9. List asset assignments");
        System.out.println("10. List helpdesk tickets");
        System.out.println("0. Exit");
    }

    private static void addEmployee() {
        int employeeId = readInt("Employee ID: ");
        String employeeName = readText("Employee name: ");
        String email = readText("Email: ");
        String phone = readText("Phone: ");
        int departmentId = readInt("Department ID: ");

        employeeService.addEmployee(new Employee(employeeId, employeeName, email, phone, departmentId));
        System.out.println("Employee added.");
    }

    private static void addAsset() {
        int assetId = readInt("Asset ID: ");
        String assetName = readText("Asset name: ");
        String assetType = readText("Asset type: ");
        String serialNumber = readText("Serial number: ");

        assetService.addAsset(new Asset(assetId, assetName, assetType, serialNumber, 0, AssetStatus.AVAILABLE));
        System.out.println("Asset added.");
    }

    private static void assignAsset() {
        int assetId = readInt("Asset ID: ");
        int employeeId = readInt("Employee ID: ");

        if (employeeService.findEmployeeById(employeeId) == null) {
            throw new IllegalArgumentException("Employee not found.");
        }

        AssetAssignment assignment = assetService.assignAsset(assetId, employeeId);
        System.out.println("Asset assigned: " + assignment);
    }

    private static void returnAsset() {
        int assetId = readInt("Asset ID: ");
        assetService.returnAsset(assetId);
        System.out.println("Asset returned.");
    }

    private static void createTicket() {
        int employeeId = readInt("Employee ID: ");
        int assetId = readInt("Asset ID: ");

        if (employeeService.findEmployeeById(employeeId) == null) {
            throw new IllegalArgumentException("Employee not found.");
        }
        if (assetService.findAssetById(assetId) == null) {
            throw new IllegalArgumentException("Asset not found.");
        }

        String title = readText("Issue title: ");
        String description = readText("Description: ");
        TicketPriority priority = readPriority();

        HelpdeskTicket ticket = helpdeskService.createTicket(
                employeeId,
                assetId,
                title,
                description,
                priority
        );
        System.out.println("Ticket created: " + ticket);
    }

    private static void closeTicket() {
        int ticketId = readInt("Ticket ID: ");
        helpdeskService.closeTicket(ticketId);
        System.out.println("Ticket closed.");
    }

    private static void listEmployees() {
        printList("Employees", employeeService.getAllEmployees());
    }

    private static void listAssets() {
        printList("Assets", assetService.getAllAssets());
    }

    private static void listAssignments() {
        printList("Asset Assignments", assetService.getAllAssignments());
    }

    private static void listTickets() {
        printList("Helpdesk Tickets", helpdeskService.getAllTickets());
    }

    private static <T> void printList(String title, List<T> items) {
        System.out.println();
        System.out.println(title);
        System.out.println("-".repeat(title.length()));

        if (items.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (T item : items) {
            System.out.println(item);
        }
    }

    private static TicketPriority readPriority() {
        System.out.println("Priority: 1. LOW  2. MEDIUM  3. HIGH  4. CRITICAL");
        int option = readInt("Choose priority: ");

        switch (option) {
            case 1:
                return TicketPriority.LOW;
            case 3:
                return TicketPriority.HIGH;
            case 4:
                return TicketPriority.CRITICAL;
            case 2:
            default:
                return TicketPriority.MEDIUM;
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Value cannot be empty.");
        }
    }

    private static void seedData() {
        employeeService.addEmployee(new Employee(101, "Chandru", "chandru@example.com", "9876543210", 10));
        employeeService.addEmployee(new Employee(102, "Priya", "priya@example.com", "9876501234", 20));

        assetService.addAsset(new Asset(201, "Laptop", "Electronics", "SN-LAP-201", 0, AssetStatus.AVAILABLE));
        assetService.addAsset(new Asset(202, "Monitor", "Electronics", "SN-MON-202", 0, AssetStatus.AVAILABLE));
    }
}
