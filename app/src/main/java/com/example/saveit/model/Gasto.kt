package com.example.saveit.model

data class Gasto(
    var id: String = "",
    val concepto: String = "",
    val categoria: String = "",
    val cantidad: Double = 0.0,
    val fecha: String = "",
    val uid: String = ""
)