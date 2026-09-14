import json
from pathlib import Path
from typing import Any

import openpyxl


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

EXCEL_SOURCES = {
    "fr": "form16.006_formulaire_de_commande_viva_F.xlsx",
    "de": "form16.006_bestellformular_aprov_D.xlsx",
    "it": "form16.006_formulario_per_ordinazione_viveri_I.xlsx",
}

# By default, JSON files are written next to this Python script.
# Change this to your Android project's Compose Resources directory if needed.
OUTPUT_DIR = Path(
    "/Users/rezajabbir/AndroidStudioProjects/vivapp2/"
    "shared/src/commonMain/composeResources/files"
)

# Sheet containing imported SAP data is not the product sheet.
EXCLUDED_SHEETS = {"LBA_SAP_Import"}

# Excel columns used by the product tables (1-based).
# The workbook structure is:
# A  = product/category name
# I  = SAP number
# L  = minimum order quantity
# M  = unit
# N  = packaging
# S  = net price
# U  = billing unit
# AA = maximum order quantity
# AB = weight in kg
COL = {
    "name": 1,          # A
    "sap": 9,           # I
    "min_order_qty": 12,  # L
    "unit": 13,         # M
    "packaging": 14,    # N
    "net_price": 19,    # S
    "billing_unit": 21, # U
    "max_order_qty": 27, # AA
    "weight_kg": 28,    # AB
}


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def is_empty(value: Any) -> bool:
    return value is None or str(value).strip() == ""


def to_float(value: Any, default: float = 0.0) -> float:
    """Safely convert an Excel value to float."""
    if is_empty(value):
        return default

    if isinstance(value, (int, float)):
        return float(value)

    text = str(value).strip().replace(",", ".")
    try:
        return float(text)
    except ValueError:
        return default


def to_int(value: Any, default: int = 0) -> int:
    """Safely convert an Excel value to int."""
    if is_empty(value):
        return default

    try:
        return int(round(to_float(value)))
    except (TypeError, ValueError):
        return default


def normalize_sap(raw_sap: Any) -> str:
    """
    Convert SAP values such as:
        25490379
        2549.0379
        "25490379.0"
    to the format:
        "2549.0379"

    If the value cannot be interpreted as a number, return an empty string.
    """
    if is_empty(raw_sap):
        return ""

    try:
        value = float(str(raw_sap).strip().replace(",", "."))
    except ValueError:
        return ""

    # SAP values in the source workbook are numeric identifiers.
    value_as_int = int(round(value))
    text = str(value_as_int)

    # Expected SAP format is 8 digits => XXXX.XXXX
    if len(text) == 8:
        return f"{text[:4]}.{text[4:]}"

    # Keep unexpected formats rather than corrupting them.
    return text


def select_product_sheet(workbook: openpyxl.Workbook) -> str:
    """
    Select the first sheet that is not the SAP import sheet.
    """
    candidates = [
        sheet_name
        for sheet_name in workbook.sheetnames
        if sheet_name not in EXCLUDED_SHEETS
    ]

    if not candidates:
        raise RuntimeError(
            f"No product sheet found. Available sheets: {workbook.sheetnames}"
        )

    return candidates[0]


# ---------------------------------------------------------------------------
# Parsing
# ---------------------------------------------------------------------------

def parse_workbook(filepath: Path) -> list[dict[str, Any]]:
    """
    Parse one Excel workbook into a list of product dictionaries.

    A row with a name but no SAP number is treated as a category header.
    A row with both a product name and SAP number is treated as a product.
    """
    if not filepath.exists():
        raise FileNotFoundError(f"Excel file not found: {filepath}")

    workbook = openpyxl.load_workbook(
        filename=filepath,
        read_only=True,
        data_only=True,
    )

    try:
        sheet_name = select_product_sheet(workbook)
        worksheet = workbook[sheet_name]

        products: list[dict[str, Any]] = []
        current_category = ""

        for row_number, row in enumerate(
            worksheet.iter_rows(values_only=True),
            start=1,
        ):
            # Skip completely empty rows.
            if not any(not is_empty(value) for value in row):
                continue

            name = row[COL["name"] - 1]
            raw_sap = row[COL["sap"] - 1]

            # A line with a name but no SAP is a category heading.
            if not is_empty(name) and is_empty(raw_sap):
                current_category = str(name).strip()
                continue

            # Product rows need both a name and a SAP number.
            if is_empty(name) or is_empty(raw_sap):
                continue

            sap_number = normalize_sap(raw_sap)

            # Ignore rows where the SAP value isn't a valid product identifier.
            if not sap_number:
                continue

            product = {
                "sapNumber": sap_number,
                "name": str(name).strip(),
                "category": current_category,
                "minOrderQty": to_int(
                    row[COL["min_order_qty"] - 1],
                    default=1,
                ),
                "unit": str(
                    row[COL["unit"] - 1] or ""
                ).strip(),
                "packaging": str(
                    row[COL["packaging"] - 1] or ""
                ).strip(),
                "netPrice": to_float(
                    row[COL["net_price"] - 1],
                    default=0.0,
                ),
                "billingUnit": str(
                    row[COL["billing_unit"] - 1] or ""
                ).strip(),
                "weightKg": to_float(
                    row[COL["weight_kg"] - 1],
                    default=0.0,
                ),
                "maxOrderQty": to_int(
                    row[COL["max_order_qty"] - 1],
                    default=0,
                ),
            }

            products.append(product)

        return products

    finally:
        workbook.close()


def export_json(products: list[dict[str, Any]], output_path: Path) -> None:
    """Write products to a UTF-8 JSON file."""
    output_path.parent.mkdir(parents=True, exist_ok=True)

    with output_path.open("w", encoding="utf-8") as file:
        json.dump(
            products,
            file,
            ensure_ascii=False,
            indent=2,
        )
        file.write("\n")


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main() -> None:
    # Resolve relative Excel paths from the directory containing this script.
    base_dir = Path(__file__).resolve().parent

    output_dir = OUTPUT_DIR
    if not output_dir.is_absolute():
        output_dir = base_dir / output_dir

    total_products = 0

    for language, source_filename in EXCEL_SOURCES.items():
        source_path = Path(source_filename)

        if not source_path.is_absolute():
            source_path = base_dir / source_path

        products = parse_workbook(source_path)

        output_path = output_dir / f"products_{language}.json"
        export_json(products, output_path)

        print(
            f"[{language}] Exported {len(products)} products "
            f"to {output_path}"
        )

        total_products += len(products)

    print(f"Done. Exported {total_products} products in total.")


if __name__ == "__main__":
    main()