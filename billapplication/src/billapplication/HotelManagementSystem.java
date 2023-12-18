package billapplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelManagementSystem {
	
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args)  throws ClassNotFoundException,SQLException{
    	Class.forName("com.mysql.cj.jdbc.Driver");
        try (
        		
        		
        		Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("Menu:");
                System.out.println("1. Add item to billing");
                System.out.println("2. Show billing");
              System.out.println("3. insert new items");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1:
                        addToBilling(connection, scanner);
                        break;
                    case 2:
                        showBilling(connection);
                        break;
                    case 3:
                        insertNewItem(connection, scanner);
                        break; 
                  
                        
                    case 4:
                    	System.out.println("Exiting...");
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 3);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertNewItem(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter new item name: ");
        String itemName = scanner.nextLine();

        // Check for duplicate item in Menu
        String checkDuplicateQuery = "SELECT COUNT(*) FROM Menu WHERE item_name = ?";
        try (PreparedStatement checkDuplicateStmt = connection.prepareStatement(checkDuplicateQuery)) {
            checkDuplicateStmt.setString(1, itemName);
            ResultSet duplicateResult = checkDuplicateStmt.executeQuery();
            duplicateResult.next();
            int duplicateCount = duplicateResult.getInt(1);

            if (duplicateCount > 0) {
                System.out.println("Item already exists in the Menu. Cannot add duplicate items.");
            } else {
                System.out.print("Enter new item price: ");
                double itemPrice = scanner.nextDouble();

                // Insert new item into the Menu table
                String insertQuery = "INSERT INTO Menu (item_name, price) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, itemName);
                    insertStmt.setDouble(2, itemPrice);
                    insertStmt.executeUpdate();
                    System.out.println("New item added to the Menu.");
                }
            }
        }
    }

   
  

	private static void addToBilling(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Menu List:");
        System.out.println("Item ID\tItem Name\tPrice");

        // Fetch menu items from database
        String selectQuery = "SELECT item_id, item_name, price FROM Menu";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = selectStmt.executeQuery();
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                double itemPrice = resultSet.getDouble("price");
                System.out.println(itemId + "\t" + itemName + "\t" + itemPrice);
            }
        }

        System.out.print("Enter item ID to add to billing: ");
        int itemId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        // Fetch item details
        String itemQuery = "SELECT item_name, price FROM Menu WHERE item_id = ?";
        try (PreparedStatement itemStmt = connection.prepareStatement(itemQuery)) {
            itemStmt.setInt(1, itemId);
            ResultSet itemResult = itemStmt.executeQuery();
            
            if (itemResult.next()) {
                String itemName = itemResult.getString("item_name");
                double price = itemResult.getDouble("price");
                double totalPrice = price * quantity;

                // Insert item into billing table
                String insertQuery = "INSERT INTO Billing (item_name, quantity, price, total_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, itemName);
                    insertStmt.setInt(2, quantity);
                    insertStmt.setDouble(3, price);
                    insertStmt.setDouble(4, totalPrice);
                    insertStmt.executeUpdate();
                    System.out.println("Item added to billing.");
                }
            } else {
                System.out.println("Item not found.");
            }
        }
    }

  
    private static void showBilling(Connection connection) throws SQLException {
        System.out.println("Billing:");

        // Fetch items from billing table
        String selectQuery = "SELECT * FROM Billing";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = selectStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("No items in billing.");
            } else {
                System.out.println("Item Name\tQuantity\tPrice\tTotal Price");
                do {
                    String itemName = resultSet.getString("item_name");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    double totalPrice = resultSet.getDouble("total_price");
                    System.out.println(itemName + "\t" + quantity + "\t" + price + "\t" + totalPrice);
                
            }
                while (resultSet.next());
            }
            
        }
    }
}
        
