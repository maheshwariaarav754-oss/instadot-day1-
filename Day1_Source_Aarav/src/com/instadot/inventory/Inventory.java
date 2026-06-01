package com.instadot.inventory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages a List<Product> collection.
 * Provides CRUD operations, search, sorting, calculations, and CSV export.
 */
public class Inventory {

    private final List<Product> products = new ArrayList<>();

    // ---------------------------------------------------------------
    //  CRUD
    // ---------------------------------------------------------------

    /**
     * Adds a product to the inventory. Rejects duplicate SKUs.
     * @return true if added, false if SKU already exists
     */
    public boolean addProduct(Product product) {
        if (findBySku(product.getSku()) != null) {
            return false;
        }
        return products.add(product);
    }

    /**
     * Updates quantity and/or unit price for the product matching the given SKU.
     * @return true if found and updated, false otherwise
     */
    public boolean updateProductBySKU(String sku, Integer newQuantity, Double newUnitPrice) {
        Product p = findBySku(sku);
        if (p == null) return false;

        if (newQuantity != null)  p.setQuantity(newQuantity);
        if (newUnitPrice != null) p.setUnitPrice(newUnitPrice);
        return true;
    }

    /**
     * Removes the product with the given SKU.
     * @return true if removed, false if not found
     */
    public boolean removeProductBySKU(String sku) {
        Product p = findBySku(sku);
        return p != null && products.remove(p);
    }

    /** Returns a product by SKU, or null if not found. */
    public Product findBySku(String sku) {
        String upper = sku.toUpperCase();
        for (Product p : products) {
            if (p.getSku().equals(upper)) return p;
        }
        return null;
    }

    /**
     * Searches for products whose name contains the query (case-insensitive).
     */
    public List<Product> searchByName(String query) {
        String lower = query.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------
    //  Listing
    // ---------------------------------------------------------------

    /** Returns all products sorted alphabetically by name. */
    public List<Product> listProductsSortedByName() {
        return products.stream()
                .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /** Returns all products sorted by total value descending. */
    public List<Product> listProductsSortedByValue() {
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getTotalValue).reversed())
                .collect(Collectors.toList());
    }

    /** Returns all products sorted by category then name. */
    public List<Product> listProductsSortedByCategory() {
        return products.stream()
                .sorted(Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /** Returns an unmodifiable view of all products in insertion order. */
    public List<Product> getAllProducts() {
        return List.copyOf(products);
    }

    // ---------------------------------------------------------------
    //  Calculations
    // ---------------------------------------------------------------

    /** Returns the sum of all product total values. */
    public double computeGrandTotal() {
        return products.stream()
                .mapToDouble(Product::getTotalValue)
                .sum();
    }

    /** Returns the number of unique products. */
    public int getProductCount() {
        return products.size();
    }

    /** Returns the total number of individual units across all products. */
    public int getTotalUnits() {
        return products.stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

    /** Returns products with stock at or below the low-stock threshold. */
    public List<Product> getLowStockProducts() {
        return products.stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    /** Returns a sorted set of all distinct categories. */
    public Set<String> getCategories() {
        return products.stream()
                .map(Product::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Applies a discount percentage to all products in the matching category.
     * @return number of products updated
     */
    public int applyCategoryDiscount(String category, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100)
            throw new IllegalArgumentException("Discount must be between 0 and 100.");

        String lower = category.toLowerCase();
        int count = 0;
        for (Product p : products) {
            if (p.getCategory().toLowerCase().contains(lower)) {
                double newPrice = p.getUnitPrice() * (1 - discountPercent / 100.0);
                p.setUnitPrice(Math.round(newPrice * 100.0) / 100.0);
                count++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------
    //  CSV export
    // ---------------------------------------------------------------

    /**
     * Exports all products to a CSV file.
     *
     * @param filePath target path; if blank, uses "inventory_export_<timestamp>.csv"
     * @return the absolute path of the created file
     * @throws IOException if writing fails
     */
    public Path exportCSV(String filePath) throws IOException {
        Path path = (filePath == null || filePath.isBlank())
                ? Paths.get("inventory_export_" + timestamp() + ".csv")
                : Paths.get(filePath);

        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            w.write("ID,SKU,Name,Category,Quantity,UnitPrice,TotalValue");
            w.newLine();
            for (Product p : products) {
                w.write(p.toCsvRow());
                w.newLine();
            }
        }
        return path.toAbsolutePath();
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
