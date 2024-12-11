package com.example.saveit.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saveit.R
import com.example.saveit.model.Gasto
import com.example.saveit.navigation.BottomNavigationBar
import com.example.saveit.navigation.Screens
import com.example.saveit.ui.theme.cardColor
import com.example.saveit.ui.theme.textColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ExpensesScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    if (userId == null) {
        Text("No estás logueado")
        return
    }

    val db = FirebaseFirestore.getInstance()
    val expenses = remember { mutableStateOf<List<Gasto>>(emptyList()) }

    // Cargar los gastos del usuario logueado
    LaunchedEffect(userId) {
        db.collection("gastos")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val gastosList = querySnapshot.documents.mapNotNull { document ->
                    // Crear un objeto Gasto incluyendo el documentId
                    val gasto = document.toObject(Gasto::class.java)
                    gasto?.apply {
                        // Añadir el documentId como campo adicional
                        id = document.id
                    }
                }
                expenses.value = gastosList
            }
            .addOnFailureListener { e ->
                Log.e("ExpenseList", "Error al cargar los gastos", e)
            }
    }

    // Interfaz de usuario
    Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {


        Row(
            verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente el texto y el ícono
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre el texto y el ícono
            modifier = Modifier.padding(bottom = 40.dp) // Añade el padding necesario
        ) {


            Text(
                text = "Recent Spending",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Icon(
                painter = painterResource(R.drawable.credit_card_icon),
                contentDescription = "Credit card icon",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified
            )
        }


        // Contenido principal
        Box(
            modifier = Modifier
                .weight(1f) // La lista ocupa el espacio disponible
                .fillMaxSize()
        ) {
            if (expenses.value.isEmpty()) {
                // Mostrar mensaje si no hay gastos
                Text(
                    text = "No expenses registered.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(expenses.value) { gasto ->
                        ExpenseItem(gasto, navController, db, expenses)
                    }
                }
            }

            // Botón flotante para añadir un nuevo gasto
            FloatingActionButton(
                onClick = { navController.navigate(Screens.AddExpenseScreen.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                containerColor = cardColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir Gasto",
                    tint = Color.White
                )
            }
        }

        // Barra de navegación inferior
        BottomNavigationBar(navController)
    }
}

@Composable
fun ExpenseItem(gasto: Gasto, navController: NavController, db: FirebaseFirestore, expenses: MutableState<List<Gasto>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con la inicial de la categoría
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFD9A3A3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gasto.categoria.firstOrNull()?.uppercase() ?: "",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del gasto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Mostrar el concepto como texto principal
                Text(
                    text = gasto.concepto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${gasto.cantidad} €",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Text(
                    text = gasto.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botones de editar y eliminar
            Row {
                // Botón de editar
                IconButton(onClick = {
                    navController.navigate(Screens.AddExpenseScreen.passGastoId(gasto.id))
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
                }

                // Botón de eliminar
                IconButton(onClick = {
                    // Eliminar usando el `id` del documento
                    db.collection("gastos").document(gasto.id)
                        .delete()
                        .addOnSuccessListener {
                            // Actualizar la lista de gastos después de eliminar
                            expenses.value = expenses.value.filterNot { it.id == gasto.id }
                            Toast.makeText(navController.context, "Expense removed", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ExpenseItem", "Error al eliminar el gasto", exception)
                            Toast.makeText(navController.context, "Error removing this expense", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = textColor)
                }
            }
        }
    }
}
