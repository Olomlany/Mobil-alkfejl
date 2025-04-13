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
        setContent {
            AddProductScreen(
                onProductSaved = {
                    Toast.makeText(this, "Sikeresen hozzáadva!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@Composable
fun AddProductScreen(onProductSaved: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Név") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategória") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Ár") })
        OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Méret") })
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Készlet") })
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Kép URL") })

        Button(
            onClick = {
                Toast.makeText(context, "Mentés gomb megnyomva", Toast.LENGTH_SHORT).show()

                val id = db.collection("products").document().id
                val product = Product(
                    id = id,
                    name = name,
                    category = category,
                    price = price.toIntOrNull() ?: 0,
                    size = size,
                    stock = stock.toIntOrNull() ?: 0,
                    imageUrl = imageUrl
                )

                db.collection("products").document(id).set(product)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Sikeres feltöltés!", Toast.LENGTH_SHORT).show()
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
