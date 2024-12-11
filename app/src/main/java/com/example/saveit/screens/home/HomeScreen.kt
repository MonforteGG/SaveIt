package com.example.saveit.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.saveit.R
import com.example.saveit.model.Gasto
import com.example.saveit.navigation.BottomNavigationBar
import com.example.saveit.ui.theme.cardColor
import com.example.saveit.ui.theme.textColor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid

    var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val result = firestore.collection("gastos")
                .whereEqualTo("uid", currentUserUid)
                .get().await()
            gastos = result.map { document ->
                Gasto(
                    id = document.id,
                    concepto = document.getString("concepto") ?: "",
                    categoria = document.getString("categoria") ?: "",
                    cantidad = document.getDouble("cantidad") ?: 0.0,
                    fecha = document.getString("fecha") ?: "",
                    uid = document.getString("uid") ?: ""
                )
            }
        } catch (e: Exception) {
            gastos = emptyList()
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(90.dp))

                val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()
                val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()

                val gastosPorMes = (1..12).map { month ->
                    month to gastos.filter {
                        val fechaPartes = it.fecha.split("/")
                        if (fechaPartes.size < 3) return@filter false
                        val gastoMonth = fechaPartes[1].toIntOrNull() ?: return@filter false
                        val gastoYear = fechaPartes[2].toIntOrNull() ?: return@filter false
                        gastoMonth == month && gastoYear == currentYear
                    }.sumOf { it.cantidad }
                }.filter { it.second > 0 }

                val gastosMes = gastos.filter {
                    val fechaPartes = it.fecha.split("/")
                    if (fechaPartes.size < 3) return@filter false
                    val gastoMonth = fechaPartes[1].toIntOrNull() ?: return@filter false
                    val gastoYear = fechaPartes[2].toIntOrNull() ?: return@filter false
                    gastoMonth == currentMonth && gastoYear == currentYear
                }.sumOf { it.cantidad }

                val gastosAnio = gastos.filter {
                    val fechaPartes = it.fecha.split("/")
                    if (fechaPartes.size < 3) return@filter false
                    val gastoYear = fechaPartes[2].toIntOrNull() ?: return@filter false
                    gastoYear == currentYear
                }.sumOf { it.cantidad }


                Row(
                    verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente el texto y el ícono
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre el texto y el ícono
                    modifier = Modifier.padding(bottom = 28.dp) // Añade el padding necesario
                ) {

                    Text(
                        text = "Overview",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Icon(
                        painter = painterResource(R.drawable.eyes_icon),
                        contentDescription = "Eyes Icon",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(cardColor), // Rosado más suave
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Monthly Expenses:", fontSize = 18.sp, style = TextStyle(fontWeight = FontWeight.SemiBold), color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$gastosMes €", fontSize = 18.sp, color = textColor, style = TextStyle(fontWeight = FontWeight.Bold)) // Rojo tenue
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(cardColor),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Annual Expenses:", fontSize = 18.sp, style = TextStyle(fontWeight = FontWeight.SemiBold), color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$gastosAnio €", fontSize = 18.sp, color = textColor, style = TextStyle(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente el texto y el ícono
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre el texto y el ícono
                    modifier = Modifier.padding(bottom = 8.dp) // Añade el padding necesario
                ) {


                    Text(
                        text = "Expense Trends",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Icon(
                        painter = painterResource(R.drawable.chart_icon),
                        contentDescription = "Eyes Icon",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 26.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(Color(0xFFFFF4F4)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AndroidView(
                        factory = { context ->
                            BarChart(context).apply {
                                val entries = gastosPorMes.mapIndexed { index, (_, cantidad) ->
                                    BarEntry(index.toFloat(), cantidad.toFloat())
                                }

                                val dataSet = BarDataSet(entries, "")
                                dataSet.color = textColor.toArgb()
                                dataSet.setValueTextColor(Color.White.toArgb())

                                val barData = BarData(dataSet)
                                barData.barWidth = 0.9f

                                this.data = barData

                                val meses = gastosPorMes.map { it.first }
                                this.xAxis.valueFormatter = IndexAxisValueFormatter(
                                    meses.map { month ->
                                        SimpleDateFormat("MMM", Locale.getDefault()).format(
                                            Calendar.getInstance().apply { set(Calendar.MONTH, month - 1) }.time
                                        )
                                    }
                                )
                                this.xAxis.granularity = 1f
                                this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                this.xAxis.textColor = Color.White.toArgb()

                                this.axisLeft.textColor = Color.White.toArgb()
                                this.axisRight.textColor = Color.White.toArgb()

                                // Cambiar el color de los valores encima de las barras
                                this.extraRightOffset = 15f // Añadir un pequeño margen en el lado derecho
                                this.setDrawValueAboveBar(true) // Asegura que los valores estén encima de las barras



                                this.setDrawGridBackground(false)
                                this.setBackgroundColor(cardColor.toArgb())
                                this.setGridBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))

                                this.legend.isEnabled = false
                                this.description.isEnabled = false
                                this.setFitBars(true)
                                this.invalidate()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.01f))

        BottomNavigationBar(navController = navController)
    }
}
