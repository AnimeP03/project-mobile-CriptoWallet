package com.example.criptowallet

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import kotlinx.coroutines.launch
import kotlin.math.exp

val BackGroundDark = Color(0xFF1E2630)
val CardDark = Color(0xFF263340)
val Green = Color(0xFF48CFCB)
val Orange = Color(0xFFFBBF24)
val Red = Color(0xFFEF4444)
val TextLight = Color.White
val TextMuted = Color(0xFF8B9CB0)
val ButtonDark = Color(0xFF324152)

@SuppressLint("DefaultLocale")
@Composable
fun WalletScreen(coinDao: CoinDao, api: Network.PriceApi, onNavigateToAddCoin: () -> Unit) {
    var coins by remember { mutableStateOf(emptyList<CoinHolding>()) }
    var prices by remember { mutableStateOf(emptyList<Network.Coin>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coins = coinDao.getAll()
        try {
            prices = api.getPrices()
        } catch (e: Exception){
            println("Error internet or api")
        }
    }

    val paid = coins.sumOf { it.pricePaid * it.quantity }
    val value = coins.sumOf { coin -> (prices.find {it.name == coin.name}?.price ?: 0.0) * coin.quantity }
    val pl = value- paid
    val plPercent = if (paid > 0) (pl / paid) * 100 else 0.0

    val plColor = if (pl >= 0) Green else Red
    val plSign = if (pl >= 0) "+" else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGroundDark)
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Portfolio P/L", color = TextMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$plSign$${String.format("%.1f", pl)}",
                        color = plColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "($plSign${String.format("%.1f", plPercent)}%)",
                        color = plColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                prices = api.getPrices()
                                coins = coinDao.getAll()
                            } catch (e: Exception) {
                                println("Error internet or api")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(coins) { coin ->
                CoinItem(
                    coin = coin,
                    currPrice = prices.find { it.name == coin.name }?.price ?: 0.0,
                    onDelete = {
                        scope.launch {
                            coinDao.delete(coin)
                            coins = coinDao.getAll()
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onNavigateToAddCoin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ Add Coin", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CoinItem(coin: CoinHolding, currPrice:Double, onDelete:() -> Unit){
    var expanded by remember {mutableStateOf(false)}

    val paid = coin.pricePaid * coin.quantity
    val value = currPrice * coin.quantity
    val pl = value- paid
    val plPercent = if (paid > 0) (pl / paid) * 100 else 0.0

    val plColor = if (pl >= 0) Green else Red
    val plSign = if (pl >= 0) "+" else ""

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Orange, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            coin.symbol,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            coin.name,
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "qty: ${coin.quantity}",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
                Button(
                    onClick = { expanded = !expanded },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (expanded) Green else ButtonDark,
                        contentColor = if (expanded) Color.Black else TextMuted
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (expanded) "Show less" else "Show more")
                }
            }
            if (expanded){
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Curr price (paid): $${String.format("%.1f",paid)}", color = TextLight, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("Curr value (now): $${String.format("%.1f", value)}", color = TextLight, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)

                        Row {
                            Text("P/L:   ", color = TextLight, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            Text(
                                "$plSign$${String.format("%.1f", pl)}   ($plSign${String.format("%.1f", plPercent)}%)",
                                color = plColor,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = TextLight),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}