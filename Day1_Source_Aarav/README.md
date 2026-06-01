# Inventory Management System

A professional Java console application for managing product inventory — built for Insta Dot Analytics Day 1 Internship.

## Prerequisites

- **JDK 17+** (tested on JDK 25)

## How to Compile and Run

```bash
# From the Day1_Source_Aarav directory:

# Compile
javac -d out src/com/instadot/inventory/*.java

# Run
java -cp out com.instadot.inventory.Application
```

## How to Open Frontend

Open `frontend/index.html` in any modern browser. No server required.

## Menu Options

1. Add Product
2. Update Product
3. Remove Product
4. View Inventory Summary (sortable by name / value / category)
5. Search Product by Name
6. Export Summary to CSV
7. Exit

## Sample Run Transcript

```
==============================================================================
     INVENTORY MANAGEMENT SYSTEM
     Insta Dot Analytics - Java Full Stack Internship
==============================================================================
  --------------------------------------------------
   1. Add Product
   2. Update Product
   3. Remove Product
   4. View Inventory Summary
   5. Search Product by Name
   6. Export Summary to CSV
   7. Exit
  --------------------------------------------------
  Enter your choice: 1

  --- Add Product ---
  SKU: LAP001
  Name: Dell XPS 13
  Category (or press Enter to skip): Laptops
  Quantity: 10
  Unit Price: 1200.00
  [+] Product added successfully.

  --- Add Product ---
  SKU: MON002
  Name: Dell 27 Monitor
  Category (or press Enter to skip): Monitors
  Quantity: 3
  Unit Price: 350.00
  [+] Product added successfully.

  --- Add Product ---
  SKU: MSE004
  Name: Wireless Mouse
  Category (or press Enter to skip): Accessories
  Quantity: 2
  Unit Price: 25.50
  [+] Product added successfully.

  *** LOW-STOCK ALERT: 2 product(s) below 5 units ***
  ***   [MON002  ] Dell 27 Monitor           only 3 left
  ***   [MSE004  ] Wireless Mouse            only 2 left

  --- Update Product ---
  SKU of product to update: MON002
  Current: ID=2, Name=Dell 27 Monitor, Qty=3, Price=$350.0
  New quantity (Enter to skip): 8
  New unit price (Enter to skip):
  [+] Updated: ID=2, Qty=8, Price=$350.0

  --- Inventory Summary ---
  Sort by: 1. Name  2. Total Value (desc)  3. Category  0. As entered
  Your choice: 1
  --------------------------------------------------------------------------
   ID | SKU      | Name                     |   Qty | UnitPrice |      Total
  --------------------------------------------------------------------------
    2 | MON002   | Dell 27 Monitor          |     8 | $  350.00 | $   2800.00
    1 | LAP001   | Dell XPS 13              |    10 | $ 1200.00 | $  12000.00
    3 | MSE004   | Wireless Mouse           |     2 | $   25.50 | $     51.00  *** LOW ***
  --------------------------------------------------------------------------

  Products: 3  |  Total units: 20  |  Grand total: $14,851.00

  --- Export Summary to CSV ---
  File path (Enter for auto-name):
  [+] Exported to: ...\inventory_export_20260601_153326.csv

  Enter your choice: 7
  Goodbye!
```

## Testing Checklist

| # | Test | Input | Expected |
|---|------|-------|----------|
| 1 | Add product | SKU=LAP001, Name=Dell XPS 13, Qty=10, Price=1200.00 | Added successfully |
| 2 | Add duplicate SKU | SKU=LAP001, any values | "already exists" message |
| 3 | Update product | SKU=MON002, Qty=8 | Quantity changes from 3 to 8 |
| 4 | Low-stock alert | MSE004 with Qty=2 | Alert shown at menu |
| 5 | Search by name | "dell" | Finds Dell XPS 13 + Dell 27 Monitor |
| 6 | Invalid quantity | Enter "abc" | Reprompts with error message |
| 7 | Export CSV | (Enter for auto-name) | File created with header + rows |
| 8 | Remove product | SKU=MON002 | Product removed |
| 9 | View summary | Sort=1 (name) | Table sorted alphabetically |
| 10 | Exit | Option 7 | "Goodbye!" printed |

## Project Structure

```
Day1_Source_Aarav/
+-- src/com/instadot/inventory/
    +-- Product.java          # Data model with auto-increment ID
    +-- Inventory.java        # Business logic: CRUD, search, sort, CSV export
    +-- Application.java      # Menu loop and user I/O only
+-- frontend/
    +-- index.html            # Semantic HTML5 dashboard landing page
    +-- style.css             # CSS Grid + Flexbox, responsive
+-- README.md
```

## Deliverables

- `Day1_InventoryDashboard_Aarav.pdf` — Documentation with screenshots
- `Day1_Source_Aarav.zip` — Source code archive (src/ + frontend/)
