import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class Main{

    public static void main(String[] args) {
        MainSystem mainSystem = new MainSystem();

        // Create an initial admin user
        mainSystem.createAdmin("admin1", "password123", "Alice Smith", 1, null,
                new String[] { "add_ride", "remove_ride", "view_reports" });

        // Add some initial rides
        mainSystem.addRide("Roller Coaster", 101, 24);
        mainSystem.addRide("Ferris Wheel", 102, 40);

        // Create some visitors
        mainSystem.createVisitor("Bob Johnson", 201, false);
        mainSystem.createVisitor("Eve Williams", 202, true);

        // Record some transactions
        mainSystem.recordTransaction("Ticket", 50);
        mainSystem.recordTransaction("Food", 20);
        mainSystem.recordTransaction("VIP Ticket", 100);

        // Run the application
        mainSystem.run();
    }
}

class MainSystem {
    private List<Admin> admins;
    private List<Ride> rides;
    private List<Visitor> visitors;
    private List<Transaction> transactions;
    private Scanner scanner;

    public MainSystem() {
        this.admins = new ArrayList<>();
        this.rides = new ArrayList<>();
        this.visitors = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void createAdmin(String username, String password, String name, int adminId, String email,
                            String[] permissions) {
        String hashedPassword = hashPassword(password);
        Admin admin = new Admin(username, hashedPassword, name, adminId, email, permissions);
        this.admins.add(admin);
        System.out.println("Admin '" + name + "' created successfully.");
    }

    public Admin authenticateAdmin(String username, String password) {
        for (Admin admin : this.admins) {
            if (admin.getUsername().equals(username) && admin.verifyPassword(password)) {
                return admin;
            }
        }
        System.out.println("Authentication failed.");
        return null;
    }

    public void addRide(String name, int rideId, int capacity) {
        Ride ride = new Ride(name, rideId, capacity);
        this.rides.add(ride);
        System.out.println("Ride '" + name + "' added successfully.");
    }

    public void removeRide(int rideId) {
        for (int i = 0; i < this.rides.size(); i++) {
            Ride ride = this.rides.get(i);
            if (ride.getRideId() == rideId) {
                this.rides.remove(i);
                System.out.println("Ride with ID " + rideId + " removed successfully.");
                return;
            }
        }
        System.out.println("Ride with ID " + rideId + " not found.");
    }

    public void createVisitor(String name, int visitorId, boolean isVip) {
        Visitor visitor = isVip ? new VIPVisitor(name, visitorId) : new Visitor(name, visitorId);
        this.visitors.add(visitor);
        System.out.println("Visitor '" + name + "' created successfully.");
    }

    public void recordTransaction(String item, double amount) {
        Transaction transaction = new Transaction(item, amount, new Date());
        this.transactions.add(transaction);
    }

    public void run() {
        System.out.println("Welcome to the Theme Park Management System!");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Admin Login");
            System.out.println("2. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    Admin admin = this.authenticateAdmin(username, password);

                    if (admin != null) {
                        System.out.println("Welcome, " + admin.getName() + "!");
                        adminMenu(admin);
                    }
                    break;
                case "2":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Ride");
            System.out.println("2. Remove Ride");
            System.out.println("3. Generate Sales Report");
            System.out.println("4. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    if (admin.hasPermission("add_ride")) {
                        System.out.print("Ride Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Ride ID: ");
                        int rideId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ride Capacity: ");
                        int capacity = Integer.parseInt(scanner.nextLine());
                        this.addRide(name, rideId, capacity);
                    } else {
                        System.out.println("You do not have permission to add rides.");
                    }
                    break;
                case "2":
                    if (admin.hasPermission("remove_ride")) {
                        System.out.print("Ride ID to Remove: ");
                        int rideId = Integer.parseInt(scanner.nextLine());
                        this.removeRide(rideId);
                    } else {
                        System.out.println("You do not have permission to remove rides.");
                    }
                    break;
                case "3":
                    if (admin.hasPermission("view_reports")) {
                        admin.generateReport("sales", this.transactions);
                    } else {
                        System.out.println("You do not have permission to view reports.");
                    }
                    break;
                case "4":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
