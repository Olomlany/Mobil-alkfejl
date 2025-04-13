package com.example.furdoruha_webshop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductListScreen()
        }
    }
}

@Composable
fun ProductListScreen() {
    val db = FirebaseFirestore.getInstance()
    var products by remember { mutableStateOf(listOf<Product>()) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val productList = result.mapNotNull { it.toObject(Product::class.java) }
                products = productList
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("Kijelentkezés")
        }

        Text("Termékek listája", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(context, AddProductActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Új termék hozzáadása")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Név: ${product.name}")
                        Text("Kategória: ${product.category}")
                        Text("Ár: ${product.price} Ft")
                        Text("Méret: ${product.size}")
                        Text("Készlet: ${product.stock} db")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                val intent = Intent(context, AddProductActivity::class.java).apply {
                                    putExtra("editProductId", product.id)
                                }
                                context.startActivity(intent)
                            }) {
                                Text("Szerkesztés")
                            }

                            Button(onClick = {
                                db.collection("products").document(product.id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Törölve", Toast.LENGTH_SHORT).show()
                                        products = products.filter { it.id != product.id }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Hiba: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }) {
                                Text("Törlés")
                            }
                        }
                    }
                }
            }
        }
    }
}
