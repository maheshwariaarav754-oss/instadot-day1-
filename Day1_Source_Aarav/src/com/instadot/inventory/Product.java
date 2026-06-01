package com.instadot.inventory;

import java.util.Objects;

/**
 * Represents a single product in the inventory system.
 * Encapsulates id, name, SKU, quantity, unit price, and optional category.
 */
public class Product {

    private static int nextId = 1;

    private final int id;
    private String sku;
    private String name;
    private String category;
    private int quantity;
    private double unitPrice;

    /** Threshold below which a low-stock alert is shown. */
    public static final int LOW_STOCK_THRESHOLD = 5;

    /**
     * Parameterized constructor. Auto-assigns a unique id.
     *
     * @param sku       unique product code (non-blank)
     * @param name      display name (non-blank)
     * @param category  product category (blank becomes "Uncategorized")
     * @param quantity  stock count (must be >= 0)
     * @param unitPrice per-unit price (must be >= 0)
     */
    public Product(String sku, String name, String category, int quantity, double unitPrice) {
        this.id = nextId++;
        setSku(sku);
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    // --- Getters ---

    public int getId()                    { return id; }
    public String getSku()                { return sku; }
    public String getName()               { return name; }
    public String getCategory()           { return category; }
    public int getQuantity()              { return quantity; }
    public double getUnitPrice()          { return unitPrice; }

    /** Returns the total inventory value of this product. */
    public double getTotalValue()         { return quantity * unitPrice; }

    /** Returns true when stock is at or below the low-stock threshold. */
    public boolean isLowStock()           { return quantity <= LOW_STOCK_THRESHOLD; }

    // --- Setters with validation ---

    public void setSku(String sku) {
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("SKU cannot be empty.");
        this.sku = sku.trim().toUpperCase();
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Product name cannot be empty.");
        this.name = name.trim();
    }

    public void setCategory(String category) {
        this.category = (category == null || category.isBlank()) ? "Uncategorized" : category.trim();
    }

    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative.");
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0)
            throw new IllegalArgumentException("Unit price cannot be negative.");
        this.unitPrice = unitPrice;
    }

    // --- Equality ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return sku.equalsIgnoreCase(p.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku.toLowerCase());
    }

    /**
     * Formatted multi-line representation for console tables.
     * Columns: ID, SKU, Name, Qty, Unit Price, Total.
     */
    @Override
    public String toString() {
        return String.format("%3d | %-8s | %-24s | %5d | $%8.2f | $%10.2f%s",
                id, sku, name, quantity, unitPrice, getTotalValue(),
                isLowStock() ? "  *** LOW ***" : "");
    }

    /** Returns a CSV row matching the header: ID,SKU,Name,Category,Qty,UnitPrice,TotalValue. */
    public String toCsvRow() {
        return String.format("%d,%s,%s,%s,%d,%.2f,%.2f",
                id, sku, escapeCsv(name), escapeCsv(category),
                quantity, unitPrice, getTotalValue());
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
