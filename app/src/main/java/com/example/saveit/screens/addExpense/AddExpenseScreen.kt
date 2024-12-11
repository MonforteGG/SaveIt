package com.example.saveit.screens.addExpense

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saveit.R
import com.example.saveit.model.Gasto
import com.example.saveit.navigation.BottomNavigationBar
import com.example.saveit.ui.theme.cardColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun AddExpenseScreen(navController: NavController, gastoId: String?) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Verificar si el usuario está logueado
    if (userId == null) {
        Text("No estás logueado")
        return
    }

    var concept by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var isUpdateMode by remember { mutableStateOf(false) }

    val categories = listOf("Housing", "Transportation", "Food", "Entertainment", "Other")// Categorías predefinidas

    // Fecha seleccionada con un DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Si estamos editando, cargar los datos del gasto
    LaunchedEffect(gastoId) {
        if (!gastoId.isNullOrEmpty()) {
            db.collection("gastos").document(gastoId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val gasto = document.toObject(Gasto::class.java)
                        if (gasto != null) {
                            concept = gasto.concepto
                            category = gasto.categoria
                            quantity = gasto.cantidad.toString()
                            selectedDate = gasto.fecha
                            isUpdateMode = true
                        }
                    }
                }
        }
    }

    // Componente principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la pantalla

            Row(
                verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente el texto y el ícono
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre el texto y el ícono
                modifier = Modifier.padding(bottom = 40.dp) // Añade el padding necesario
            ) {


                Text(
                    text = if (isUpdateMode) "Update what you spent" else "Track your new expense",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Icon(
                    painter = painterResource(R.drawable.money_wings_icon),
                    contentDescription = "Credit card icon",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )
            }

            // Campo para el concepto
            TextField(
                value = concept,
                onValueChange = { concept = it },
                label = { Text("Concept", color = Color.LightGray) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = cardColor,
                    focusedContainerColor = cardColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent

                ),
            )

            // Categoría (desplegable) y cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Lista desplegable para categoría
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = category,
                        onValueChange = {  },
                        label = { Text("Category", color = Color.LightGray) },
                        readOnly = true,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir categorías", tint = Color.White)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = cardColor,
                            focusedContainerColor = cardColor,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent

                        ),
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = cardColor
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }

                            )
                        }
                    }
                }

                // Cantidad
                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Amount", color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = cardColor,
                        focusedContainerColor = cardColor,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent

                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de fecha
            Button(
                onClick = { datePickerDialog.show() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = cardColor)
            ) {
                Text(
                    text = if (selectedDate.isEmpty()) "Select Date" else selectedDate, color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para añadir o actualizar
            Button(
                onClick = {
                    // Intentamos convertir la cantidad introducida con coma como separador decimal
                    val normalizedQuantity = quantity.replace(',', '.')

                    if (concept.isNotEmpty() && category.isNotEmpty() && normalizedQuantity.toDoubleOrNull() != null && selectedDate.isNotEmpty()) {
                        val expense = hashMapOf(
                            "concepto" to concept,
                            "categoria" to category,
                            "cantidad" to normalizedQuantity.toDouble(),
                            "fecha" to selectedDate,
                            "uid" to userId
                        )

                        coroutineScope.launch {
                            try {
                                if (isUpdateMode) {
                                    // Actualizar el gasto en Firebase
                                    db.collection("gastos").document(gastoId!!).set(expense).await()
                                } else {
                                    // Guardar el gasto en Firebase
                                    db.collection("gastos")
                                        .add(expense)
                                        .await()  // Usar .await() para hacer la llamada asíncrona
                                }

                                // Si la inserción o actualización es exitosa, regresar a la pantalla anterior
                                navController.popBackStack()
                            } catch (e: Exception) {
                                // Si ocurre un error, mostrar un mensaje
                                Toast.makeText(context, "Error al agregar o actualizar el gasto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = if (isUpdateMode) "Update" else "Add",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Barra de navegación inferior
        BottomNavigationBar(navController = navController)
    }
}
