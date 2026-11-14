package com.pagae.app.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatMoney(v: Double): String = "R$ " + String.format(Locale("pt", "BR"), "%.2f", v)

fun parseMoney(s: String): Double? = s.trim().replace(",", ".").toDoubleOrNull()

fun nowString(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
