package com.pagae.app.db

data class Person(val id: Long, val name: String, val contact: String)
data class Food(val id: Long, val name: String, val price: Double, val emoji: String)
data class TotalRow(val id: Long, val name: String, val total: Double)
data class HistoryRow(val date: String, val food: String, val price: Double)
