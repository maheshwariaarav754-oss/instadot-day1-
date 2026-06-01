package com.instadot.inventory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class. Handles the menu loop and user I/O only.
 * Business logic is delegated to Inventory and Product.
 */
public class Application {

    private static final String LINE = "=".repeat(78);

    private final Inventory inventory;
    private final Scanner scanner;

    public Application() {
        inventory = new Inventory();
        scanner = new Scanner(System.in);
    }

    // ---------------------------------------------------------------
    //  Entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("     INVENTORY MANAGEMENT SYSTEM");
        System.out.println("     Insta Dot Analytics - Java Full Stack Internship");
        System.out.println(LINE);
        System.out.println("     Compiled with JDK 25 | OOP Console Application");
        System.out.println(LINE);
        new Application().run();
    }

    // ---------------------------------------------------------------
    //  Menu loop
    // ---------------------------------------------------------------

    private void run() {
        boolean running = true;
        while (running) {
            showLowStockAlert();
            printMenu();
            int choice = readInt("Enter your choice", 1, 7);

            try {
                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> updateProduct();
                    case 3 -> removeProduct();
                    case 4 -> viewSummary();
                    case 5 -> searchProduct();
                    case 6 -> exportCsv();
                    case 7 -> running = false;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
        }

        System.out.println("\n  Goodbye! Thank you for using the Inventory Management System.");
        scanner.close();
    }

    // ---------------------------------------------------------------
    //  Menu display
    // ---------------------------------------------------------------

    private void printMenu() {
        System.out.println();
        System.out.println("  " + "-".repeat(50));
        System.out.println("   1. Add Product");
        System.out.println("   2. Update Product");
        System.out.println("   3. Remove Product");
        System.out.println("   4. View Inventory Summary");
        System.out.println("   5. Search Product by Name");
        System.out.println("   6. Export Summary to CSV");
        System.out.println("   7. Exit");
        System.out.println("  " + "-".repeat(50));
    }

    private void showLowStockAlert() {
        List<Product> low = inventory.getLowStockProducts();
        if (!low.isEmpty()) {
            System.out.println();
            System.out.println("  *** LOW-STOCK ALERT: " + low.size() + " product(s) below " + Product.LOW_STOCK_THRESHOLD + " units ***");
            for (Product p : low) {
                System.out.printf("  ***   [%-8s] %-24s  only %d left%n",
                        p.getSku(), p.getName(), p.getQuantity());
            }
        }
    }

    // ---------------------------------------------------------------
    //  Menu actions
    // ---------------------------------------------------------------

    private void addProduct() {
        System.out.println("\n  --- Add Product ---");
        String sku = readString("SKU").toUpperCase();
        if (inventory.findBySku(sku) != null) {
            System.out.println("  [!] Product with SKU '" + sku + "' already exists.");
            return;
        }
        String name = readString("Name");
        String cat  = readString("Category (or press Enter to skip)");
        int qty     = readInt("Quantity", 0, Integer.MAX_VALUE);
        double price = readDouble("Unit Price", 0, Double.MAX_VALUE);

        inventory.addProduct(new Product(sku, name, cat, qty, price));
        System.out.println("  [+] Product added successfully.");
    }

    private void updateProduct() {
        System.out.println("\n  --- Update Product ---");
        String sku = readString("SKU of product to update").toUpperCase();
        Product p = inventory.findBySku(sku);
        if (p == null) {
            System.out.println("  [!] No product found with SKU '" + sku + "'.");
            return;
        }
        System.out.println("  Current: ID=" + p.getId() + ", Name=" + p.getName()
                + ", Qty=" + p.getQuantity() + ", Price=$" + p.getUnitPrice());

        String qtyInput = readOptional("New quantity (Enter to skip)");
        String priceInput = readOptional("New unit price (Enter to skip)");

        Integer newQty = null;
        Double  newPrice = null;

        if (!qtyInput.isBlank()) {
            try {
                newQty = Integer.parseInt(qtyInput);
                if (newQty < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid quantity. Update cancelled.");
                return;
            }
        }
        if (!priceInput.isBlank()) {
            try {
                newPrice = Double.parseDouble(priceInput);
                if (newPrice < 0) throw new NumberFormatException();
                newPrice = Math.round(newPrice * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid price. Update cancelled.");
                return;
            }
        }
        if (newQty == null && newPrice == null) {
            System.out.println("  [-] No changes made.");
            return;
        }

        inventory.updateProductBySKU(sku, newQty, newPrice);
        Product updated = inventory.findBySku(sku);
        System.out.println("  [+] Updated: ID=" + updated.getId() + ", Qty=" + updated.getQuantity()
                + ", Price=$" + updated.getUnitPrice());
    }

    private void removeProduct() {
        System.out.println("\n  --- Remove Product ---");
        String sku = readString("SKU of product to remove").toUpperCase();
        if (inventory.removeProductBySKU(sku)) {
            System.out.println("  [+] Product removed successfully.");
        } else {
            System.out.println("  [!] No product found with SKU '" + sku + "'.");
        }
    }

    private void viewSummary() {
        System.out.println("\n  --- Inventory Summary ---");
        List<Product> all = inventory.getAllProducts();
        if (all.isEmpty()) {
            System.out.println("  Inventory is empty. Add products first.");
            return;
        }

        System.out.println("  Sort by: 1. Name  2. Total Value (desc)  3. Category  0. As entered");
        int sort = readInt("Your choice", 0, 3);

        List<Product> sorted = switch (sort) {
            case 1 -> inventory.listProductsSortedByName();
            case 2 -> inventory.listProductsSortedByValue();
            case 3 -> inventory.listProductsSortedByCategory();
            default -> all;
        };

        printTable(sorted);
        System.out.printf("%n  Products: %d  |  Total units: %d  |  Grand total: $%,.2f%n",
                inventory.getProductCount(), inventory.getTotalUnits(), inventory.computeGrandTotal());
    }

    private void searchProduct() {
        System.out.println("\n  --- Search Product by Name ---");
        String query = readString("Search term");
        List<Product> results = inventory.searchByName(query);
        if (results.isEmpty()) {
            System.out.println("  No products found matching \"" + query + "\".");
            return;
        }
        System.out.println("  Found " + results.size() + " product(s):");
        printTable(results);
    }

    private void exportCsv() {
        System.out.println("\n  --- Export Summary to CSV ---");
        List<Product> all = inventory.getAllProducts();
        if (all.isEmpty()) {
            System.out.println("  Inventory is empty. Nothing to export.");
            return;
        }
        String path = readOptional("File path (Enter for auto-name)");
        try {
            java.nio.file.Path result = inventory.exportCSV(path);
            System.out.println("  [+] Exported to: " + result);
        } catch (IOException e) {
            System.out.println("  [!] Export failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    //  Table printer
    // ---------------------------------------------------------------

    private void printTable(List<Product> list) {
        String fmt = "  %3s | %-8s | %-24s | %5s | %9s | %10s";
        String header = String.format(fmt, "ID", "SKU", "Name", "Qty", "UnitPrice", "Total");
        String sep = "  " + "-".repeat(header.length() - 2);

        System.out.println(sep);
        System.out.println(header);
        System.out.println(sep);
        for (Product p : list) {
            System.out.println("  " + p);
        }
        System.out.println(sep);
    }

    // ---------------------------------------------------------------
    //  Input helpers
    // ---------------------------------------------------------------

    private String readString(String prompt) {
        System.out.print("  " + prompt + ": ");
        String input = scanner.nextLine().trim();
        while (input.isBlank()) {
            System.out.print("  [!] Cannot be empty. " + prompt + ": ");
            input = scanner.nextLine().trim();
        }
        return input;
    }

    private String readOptional(String prompt) {
        System.out.print("  " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val < min || val > max) {
                    System.out.printf("  [!] Enter between %d and %d.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                val = Math.round(val * 100.0) / 100.0;
                if (val < min || val > max) {
                    System.out.printf("  [!] Enter between %.2f and %.2f.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }
}
