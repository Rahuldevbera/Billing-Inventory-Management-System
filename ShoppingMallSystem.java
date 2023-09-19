import java.io.*;
import java.util.*;

class Product implements Serializable {
    int id;
    String name;
    float price;
    int quantity;
}

public class ShoppingMallSystem {
    private static final String DATABASE_FILE = "database.dat";
    private static final List<Product> products = new ArrayList<>();

    public static void main(String[] args) {
        loadProducts();

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.println("\n-----Shopping Mall Bill & Inventory Management System-----");
            System.out.println("1. Customer");
            System.out.println("2. Administrator");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    customer();
                    break;
                case 2:
                    admin();
                    break;
                case 3:
                    System.out.println("\nThank you for using our system!\n");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nInvalid choice. Enter again!");
            }
        }
    }

    static void addProduct() {
        Scanner scanner = new Scanner(System.in);
        Product p = new Product();
        System.out.print("Enter product id: ");
        p.id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter product name: ");
        p.name = scanner.nextLine();
        System.out.print("Enter product price: ");
        p.price = scanner.nextFloat();
        System.out.print("Enter product quantity: ");
        p.quantity = scanner.nextInt();

        products.add(p);
        saveProducts();

        System.out.println("\nProduct added successfully!");
    }

    static void deleteProduct(int id) {
        for (Product p : products) {
            if (p.id == id) {
                products.remove(p);
                saveProducts();
                System.out.println("\nProduct with ID " + id + " deleted successfully!");
                return;
            }
        }
        System.out.println("\nProduct not found! Please enter a valid ID.");
    }

    static void displayProducts() {
        System.out.println("\nID\tName\tPrice\tQty");
        for (Product p : products) {
            System.out.printf("%d\t%s\t%.2f\t%d\n", p.id, p.name, p.price, p.quantity);
        }
    }

    static void generateBill() {
        displayProducts();
        int id, quantity, choice;
        float total = 0;
        int found = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nEnter the ID of the item: ");
            id = scanner.nextInt();
            System.out.print("Enter the quantity: ");
            quantity = scanner.nextInt();

            for (Product p : products) {
                if (p.id == id && quantity <= p.quantity) {
                    found = 1;
                    total += p.price * quantity;
                    p.quantity -= quantity;
                    saveProducts();
                }
            }

            if (found == 0) {
                System.out.println("\nItem not found, item out of stock, or insufficient quantity!");
            } else {
                System.out.print("\nDo you want to purchase more items? (1 for yes, 0 for no): ");
                choice = scanner.nextInt();
                if (choice == 0) {
                    break;
                }
            }
        }
        System.out.printf("\nTotal amount: %.2f\n\n", total);
    }

    static void admin() {
        int choice = 0;
        int id, qty;
        float price;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n-----Admin Menu-----");
            System.out.println("1. Add Products");
            System.out.println("2. Delete Products");
            System.out.println("3. Display Products");
            System.out.println("4. Update Quantity");
            System.out.println("5. Update Price");
            System.out.println("6. Go back to main menu");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    System.out.print("\nEnter the ID to delete product: ");
                    id = scanner.nextInt();
                    deleteProduct(id);
                    break;
                case 3:
                    displayProducts();
                    break;
                case 4:
                    System.out.print("\nEnter the ID to update product quantity: ");
                    id = scanner.nextInt();
                    System.out.print("Enter the new quantity: ");
                    qty = scanner.nextInt();
                    updateQuantity(id, qty);
                    break;
                case 5:
                    System.out.print("\nEnter the ID to update product price: ");
                    id = scanner.nextInt();
                    System.out.print("Enter the new price: ");
                    price = scanner.nextFloat();
                    updatePrice(id, price);
                    break;
                case 6:
                    return;
                case 7:
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nInvalid choice. Enter again!");
            }
        }
    }

    static void updateQuantity(int id, int newQuantity) {
        for (Product p : products) {
            if (p.id == id) {
                p.quantity = newQuantity;
                saveProducts();
                System.out.println("\nProduct quantity updated successfully!");
                return;
            }
        }
        System.out.println("\nProduct not found! Invalid ID.");
    }

    static void updatePrice(int id, float price) {
        for (Product p : products) {
            if (p.id == id) {
                p.price = price;
                saveProducts();
                System.out.println("\nProduct price updated successfully!");
                return;
            }
        }
        System.out.println("\nProduct not found! Invalid ID.");
    }

    static void customer() {
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n-----Customer Menu-----");
            System.out.println("1. Display Products");
            System.out.println("2. Generate Bill");
            System.out.println("3. Go back to main menu");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    displayProducts();
                    break;
                case 2:
                    generateBill();
                    break;
                case 3:
                    return;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nInvalid choice. Enter again!");
            }
        }
    }

    private static void loadProducts() {
        try {
            FileInputStream fileInputStream = new FileInputStream(DATABASE_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            products.clear();
            while (true) {
                try {
                    Product p = (Product) objectInputStream.readObject();
                    products.add(p);
                } catch (EOFException e) {
                    break;
                }
            }
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            // Ignore errors for now
        }
    }

    private static void saveProducts() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(DATABASE_FILE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (Product p : products) {
                objectOutputStream.writeObject(p);
            }
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.println("\nError writing file: " + e.getMessage());
        }
    }
}
