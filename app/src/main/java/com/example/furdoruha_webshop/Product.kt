package com.example.furdoruha_webshop

data class Product(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Long = 0,
    val size: String = "",
    val stock: Long = 0,
    val imageUrl: String = ""
)
