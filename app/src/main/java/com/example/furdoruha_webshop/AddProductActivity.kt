package com.example.furdoruha_webshop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

class AddProductActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val size = intent.getStringExtra("size") ?: ""
        val stock = intent.getStringExtra("stock") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        setContent {
            AddProductScreen(
                id = id,
                defaultName = name,
                defaultCategory = category,
                defaultPrice = price,
                defaultSize = size,
                defaultStock = stock,
                defaultImageUrl = imageUrl,
                onProductSaved = {
                    Toast.makeText(this, "Sikeresen mentve!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@Composable
fun AddProductScreen(
    id: String? = null,
    defaultName: String = "",
    defaultCategory: String = "",
    defaultPrice: String = "",
    defaultSize: String = "",
    defaultStock: String = "",
    defaultImageUrl: String = "",
    onProductSaved: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf(defaultName) }
    var category by remember { mutableStateOf(defaultCategory) }
    var price by remember { mutableStateOf(defaultPrice) }
    var size by remember { mutableStateOf(defaultSize) }
    var stock by remember { mutableStateOf(defaultStock) }
    var imageUrl by remember { mutableStateOf(defaultImageUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Név") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategória") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Ár") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Méret") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Készlet") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Kép URL") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                val productId = id ?: db.collection("products").document().id
                val product = Product(
                    id = productId,
                    name = name.trim(),
                    category = category.trim(),
                    price = price.toLongOrNull() ?: 0,
                    size = size.trim(),
                    stock = stock.toLongOrNull() ?: 0,
                    imageUrl = imageUrl.trim()
                )

                db.collection("products").document(productId).set(product)
                    .addOnSuccessListener {
                        Toast.makeText(context, if (id != null) "Sikeres frissítés!" else "Sikeres hozzáadás!", Toast.LENGTH_SHORT).show()
                        onProductSaved()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Hiba: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mentés")
        }
    }
}
