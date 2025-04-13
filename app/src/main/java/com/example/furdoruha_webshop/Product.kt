package com.example.furdoruha_webshop

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val size: String = "",
    val stock: Int = 0,
    val imageUrl: String = ""
)
