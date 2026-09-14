package com.notanex.vivapp2.models

enum class AppLanguage(val code: String, val label: String, val fileName: String) {
    FRENCH("fr", "Français", "products_fr.json"),
    GERMAN("de", "Deutsch", "products_de.json"),
    ITALIAN("it", "Italiano", "products_it.json")
}