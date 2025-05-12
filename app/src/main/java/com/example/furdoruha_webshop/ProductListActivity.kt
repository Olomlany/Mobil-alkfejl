package com.example.furdoruha_webshop

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

const val CHANNEL_ID = "furdoaruha_ertesites_csatorna"
const val NOTIFICATION_ID = 1
const val ALARM_NOTIFICATION_ID = 2

class ProductListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

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
    val notificationManager = NotificationManagerCompat.from(context)

    LaunchedEffect(Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val productList = result.mapNotNull { doc ->
                    val product = doc.toObject(Product::class.java)
                    product.id = doc.id
                    product
                }
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                db.collection("products")
                    .whereEqualTo("category", "Bikini")
                    .get()
                    .addOnSuccessListener { result ->
                        products = result.mapNotNull { it.toObject(Product::class.java) }
                    }
            }) {
                Text("Csak Bikini")
            }

            Button(onClick = {
                db.collection("products")
                    .orderBy("price")
                    .get()
                    .addOnSuccessListener { result ->
                        products = result.mapNotNull { it.toObject(Product::class.java) }
                    }
            }) {
                Text("Ár szerint")
            }

            Button(onClick = {
                db.collection("products")
                    .orderBy("id")
                    .limit(5)
                    .get()
                    .addOnSuccessListener { result ->
                        products = result.mapNotNull { it.toObject(Product::class.java) }
                    }
            }) {
                Text("Utolsó 5")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(products) { index, product ->
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = index) {
                    delay(100L * index)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn()
                ) {
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
                                        putExtra("id", product.id)
                                        putExtra("name", product.name)
                                        putExtra("category", product.category)
                                        putExtra("price", product.price)
                                        putExtra("size", product.size)
                                        putExtra("stock", product.stock)
                                        putExtra("imageUrl", product.imageUrl)
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

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Fürdőaruha értesítések"
                    val descriptionText = "Termékekkel kapcsolatos információk"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                    }
                    val notificationManagerSystem = context.getSystemService(NotificationManager::class.java)
                    notificationManagerSystem?.createNotificationChannel(channel)
                }

                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Az értesítések engedélyezése szükséges!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                try {
                    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Fürdőaruha Webshop")
                        .setContentText("Nézd meg az új termékeket!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Hiba: nincs engedély az értesítéshez!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Küldj értesítést")
        }

        Button(
            onClick = {
                val intent = Intent(context, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val triggerAtMillis = System.currentTimeMillis() + 60_000

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)

                Toast.makeText(context, "Időzített értesítés beállítva!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Időzített értesítés (1 perc)")
        }
    }
}
