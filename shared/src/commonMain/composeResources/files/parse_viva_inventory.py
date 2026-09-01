"""
Parse the "Commande" sheet of viva_inventory.xlsx into a JSON list of items.

How the sheet is structured (columns are 0-indexed here):
  col 0  -> Article / Groupe de produit   (item name, OR a category header
                                            when the SAP N° column is empty)
  col 8  -> SAP N°                        (SAP article number)
  col 11 -> Unité de commande minimale    (minimum order quantity)
  col 12 -> unit code for that minimum order (e.g. "roul", "pce", "port")
  col 13 -> Emballage collectif           (packaging description)
  col 18 -> Prix net                      (net price)
  col 20 -> Unité de facturation          (billing unit)
  col 26 -> Max Best Menge                (maximum order quantity)
  col 27 -> Gewicht                       (weight in kg)

Rows where col 0 has a value but col 8 (SAP N°) is empty are category
header rows (e.g. "Boissons", "Snacks", ...) rather than actual items;
every item row below such a header belongs to that category, until the
next header row is found.

Run:
    python3 parse_viva_inventory.py
"""

import json
import openpyxl

SOURCE_FILE = "viva_inventory.xlsx"
SHEET_NAME = "Commande"
OUTPUT_FILE = "products.json"

# Data starts right after the "Article / Groupe de produit ... Prix net ..."
# header row.
FIRST_DATA_ROW = 14

# Column indices (0-based) inside each row tuple returned by openpyxl.
COL_NAME = 0
COL_SAP = 8
COL_MIN_ORDER_QTY = 11
COL_UNIT = 12
COL_PACKAGING = 13
COL_NET_PRICE = 18
COL_BILLING_UNIT = 20
COL_MAX_ORDER_QTY = 26
COL_WEIGHT_KG = 27


def parse_inventory(path: str, sheet_name: str = SHEET_NAME) -> list[dict]:
    wb = openpyxl.load_workbook(path, data_only=True)
    ws = wb[sheet_name]

    items = []
    current_category = None

    # values_only=True (used below) discards each cell's display format,
    # and openpyxl gives us the SAP N° as a plain float. Floats don't
    # remember trailing zeros (2584.5460 and 2584.546 are the exact same
    # float value), so str(sap) can silently drop a trailing zero that
    # Excel's "0.0000" format would normally show. To reproduce the SAP
    # number exactly as displayed in Excel, we format every SAP value to
    # 4 decimal places ourselves instead of relying on str().
    for row in ws.iter_rows(min_row=FIRST_DATA_ROW, values_only=True):
        name = row[COL_NAME]
        sap = row[COL_SAP]

        if name is None:
            # Fully empty row, skip.
            continue

        if sap is None:
            # No SAP number -> this is a category header row (or, further
            # down the sheet, footer/contact info). We only want to keep
            # updating the category while we're still in the item table;
            # once the item table ends there won't be any more SAP-numbered
            # rows anyway, so this is safe.
            current_category = name
            continue

        items.append({
            "sapNumber": f"{sap:.4f}",
            "name": name,
            "category": current_category,
            "minOrderQty": row[COL_MIN_ORDER_QTY] or 0,
            "unit": row[COL_UNIT] or "",
            "packaging": row[COL_PACKAGING] or "",
            "netPrice": float(row[COL_NET_PRICE]) if row[COL_NET_PRICE] is not None else 0.0,
            "billingUnit": row[COL_BILLING_UNIT] or "",
            "weightKg": float(row[COL_WEIGHT_KG]) if row[COL_WEIGHT_KG] is not None else 0.0,
            "maxOrderQty": row[COL_MAX_ORDER_QTY] or 0,
        })

    return items


def main():
    items = parse_inventory(SOURCE_FILE)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(items, f, ensure_ascii=False, indent=2)
    print(f"Parsed {len(items)} items -> {OUTPUT_FILE}")


if __name__ == "__main__":
    main()