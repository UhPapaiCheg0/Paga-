package com.pagae.app.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "pagae.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE profiles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, contact TEXT NOT NULL DEFAULT '')")
        db.execSQL("CREATE TABLE foods (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL NOT NULL, emoji TEXT NOT NULL DEFAULT '')")
        db.execSQL("CREATE TABLE sales (id INTEGER PRIMARY KEY AUTOINCREMENT, profile_id INTEGER NOT NULL, food_name TEXT NOT NULL, price REAL NOT NULL, date TEXT NOT NULL)")
        db.execSQL("CREATE TABLE payments (id INTEGER PRIMARY KEY AUTOINCREMENT, profile_id INTEGER NOT NULL, amount REAL NOT NULL, date TEXT NOT NULL)")
    }

    /**
     * Migrações incrementais: nunca apagar tabelas durante uma atualização.
     * A versão 2 não altera o esquema; ela apenas remove o comportamento destrutivo
     * que existia na implementação anterior.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var version = oldVersion
        if (version < 2) {
            // v1 -> v2: preserva integralmente profiles, foods, sales e payments.
            version = 2
        }
        // Para futuras versões, adicionar blocos if (version < N) com ALTER TABLE
        // ou CREATE TABLE. Não usar DROP TABLE para migrar dados existentes.
    }

    // ---------------- Pessoas ----------------
    fun addPerson(name: String, contact: String): Long {
        val cv = ContentValues().apply {
            put("name", name)
            put("contact", contact)
        }
        return writableDatabase.insert("profiles", null, cv)
    }

    fun updatePersonName(id: Long, name: String) {
        val cv = ContentValues().apply { put("name", name) }
        writableDatabase.update("profiles", cv, "id = ?", arrayOf(id.toString()))
    }

    fun updatePersonContact(id: Long, contact: String) {
        val cv = ContentValues().apply { put("contact", contact) }
        writableDatabase.update("profiles", cv, "id = ?", arrayOf(id.toString()))
    }

    fun deletePersons(ids: Collection<Long>) {
        val db = writableDatabase
        for (id in ids) {
            db.delete("sales", "profile_id = ?", arrayOf(id.toString()))
            db.delete("payments", "profile_id = ?", arrayOf(id.toString()))
            db.delete("profiles", "id = ?", arrayOf(id.toString()))
        }
    }

    fun searchPeople(query: String): List<Person> {
        val c = readableDatabase.rawQuery(
            "SELECT id, name, contact FROM profiles WHERE name LIKE ? ORDER BY name",
            arrayOf("%$query%")
        )
        val list = mutableListOf<Person>()
        c.use {
            while (it.moveToNext()) {
                list.add(Person(it.getLong(0), it.getString(1), it.getString(2)))
            }
        }
        return list
    }

    // ---------------- Comidas ----------------
    fun addFood(name: String, price: Double, emoji: String): Long {
        val cv = ContentValues().apply {
            put("name", name)
            put("price", price)
            put("emoji", emoji)
        }
        return writableDatabase.insert("foods", null, cv)
    }

    fun updateFood(id: Long, name: String, price: Double, emoji: String) {
        val cv = ContentValues().apply {
            put("name", name)
            put("price", price)
            put("emoji", emoji)
        }
        writableDatabase.update("foods", cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteFoods(ids: Collection<Long>) {
        for (id in ids) {
            writableDatabase.delete("foods", "id = ?", arrayOf(id.toString()))
        }
    }

    fun searchFoods(query: String): List<Food> {
        val c = readableDatabase.rawQuery(
            "SELECT id, name, price, emoji FROM foods WHERE name LIKE ? ORDER BY name",
            arrayOf("%$query%")
        )
        val list = mutableListOf<Food>()
        c.use {
            while (it.moveToNext()) {
                list.add(Food(it.getLong(0), it.getString(1), it.getDouble(2), it.getString(3)))
            }
        }
        return list
    }

    // ---------------- Vendas / Saldos ----------------
    fun addSale(profileId: Long, foodName: String, price: Double, date: String) {
        val cv = ContentValues().apply {
            put("profile_id", profileId)
            put("food_name", foodName)
            put("price", price)
            put("date", date)
        }
        writableDatabase.insert("sales", null, cv)
    }

    /** Subtrai um valor específico do saldo do perfil (registra um pagamento parcial). */
    fun payAmount(profileId: Long, amount: Double, date: String) {
        val cv = ContentValues().apply {
            put("profile_id", profileId)
            put("amount", amount)
            put("date", date)
        }
        writableDatabase.insert("payments", null, cv)
    }

    /** Registra a quitação como pagamento, mantendo compras e pagamentos no histórico. */
    fun clearProfile(profileId: Long) {
        val current = totals("").firstOrNull { it.id == profileId }?.total ?: 0.0
        if (current > 0.0) {
            val date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date())
            payAmount(profileId, current, date)
        }
    }

    fun totals(query: String): List<TotalRow> {
        val sql = ("SELECT p.id, p.name, "
                + "COALESCE((SELECT SUM(s.price) FROM sales s WHERE s.profile_id = p.id), 0) "
                + "- COALESCE((SELECT SUM(pay.amount) FROM payments pay WHERE pay.profile_id = p.id), 0) "
                + "FROM profiles p WHERE p.name LIKE ? ORDER BY p.name")
        val c = readableDatabase.rawQuery(sql, arrayOf("%$query%"))
        val list = mutableListOf<TotalRow>()
        c.use {
            while (it.moveToNext()) {
                list.add(TotalRow(it.getLong(0), it.getString(1), it.getDouble(2)))
            }
        }
        return list
    }

    data class ReportSummary(
        val salesTotal: Double,
        val paymentsTotal: Double,
        val currentDebt: Double,
        val salesCount: Int,
        val peopleCount: Int
    )

    data class MonthlySales(val month: String, val total: Double)

    fun reportSummary(): ReportSummary {
        val db = readableDatabase
        fun scalar(sql: String): Double = db.rawQuery(sql, null).use { c ->
            if (c.moveToFirst()) c.getDouble(0) else 0.0
        }
        val sales = scalar("SELECT COALESCE(SUM(price), 0) FROM sales")
        val payments = scalar("SELECT COALESCE(SUM(amount), 0) FROM payments")
        val debt = sales - payments
        val count = scalar("SELECT COUNT(*) FROM sales").toInt()
        val people = scalar("SELECT COUNT(*) FROM profiles").toInt()
        return ReportSummary(sales, payments, debt, count, people)
    }

    /** Datas legadas são armazenadas como dd/MM/yyyy HH:mm. */
    fun monthlySales(): List<MonthlySales> {
        val c = readableDatabase.rawQuery(
            "SELECT substr(date, 7, 4) || '-' || substr(date, 4, 2) AS month_key, " +
                "SUM(price) FROM sales WHERE length(date) >= 10 " +
                "GROUP BY month_key ORDER BY month_key DESC LIMIT 6", null
        )
        val result = mutableListOf<MonthlySales>()
        c.use {
            while (it.moveToNext()) {
                val key = it.getString(0)
                val parts = key.split("-")
                val label = if (parts.size == 2) parts[1] + "/" + parts[0] else key
                result.add(MonthlySales(label, it.getDouble(1)))
            }
        }
        return result.reversed()
    }

    fun history(profileId: Long): List<HistoryRow> {
        val c = readableDatabase.rawQuery(
            "SELECT date, food_name, price FROM sales WHERE profile_id = ? ORDER BY id DESC",
            arrayOf(profileId.toString())
        )
        val list = mutableListOf<HistoryRow>()
        c.use {
            while (it.moveToNext()) {
                list.add(HistoryRow(it.getString(0), it.getString(1), it.getDouble(2)))
            }
        }
        return list
    }
}
