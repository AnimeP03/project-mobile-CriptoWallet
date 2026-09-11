package com.example.criptowallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoinScreen(coinDao: CoinDao, api: Network.PriceApi, onNavigateBack: () -> Unit){
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var prices by remember { mutableStateOf(emptyList<Network.Coin>()) }
    var selected by remember { mutableStateOf(Network.Coin(name = "", price = 0.0, symbol = "")) }
    var quantity by remember { mutableStateOf("")}
    var paid by remember { mutableStateOf("")}

    LaunchedEffect(Unit) {
        try {
            prices = api.getPrices()
            selected = prices[0]
            paid = selected.price.toString()
        } catch (e: Exception){
            println("Error internet or api")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(text = "Seleziona la criptovaluta", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val text = "${selected.name} (${selected.symbol})"
            OutlinedTextField(
                value = text,
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardDark, unfocusedContainerColor = CardDark,
                    focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                    focusedIndicatorColor = Green, unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CardDark)
            ) {
                prices.forEach { coin ->
                    val name = coin.name
                    DropdownMenuItem(
                        text = { Text("$name", color = TextLight) },
                        onClick = {
                            selected = coin
                            expanded = false
                            paid = selected.price.toString()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        Text(text = "Prezzo API :${selected.price}", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantità acquistata", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardDark, unfocusedContainerColor = CardDark,
                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                focusedIndicatorColor = Green, unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = paid,
            onValueChange = { paid = it },
            label = { Text("Prezzo unitario pagato ($)", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardDark, unfocusedContainerColor = CardDark,
                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                focusedIndicatorColor = Green, unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        val qty = quantity.toDoubleOrNull() ?: 0.0
        val price = paid.toDoubleOrNull() ?: 0.0
        val totale = qty * price
        Text(
            text = "Prezzo totale pagato: ${String.format("%.1f",totale)}",
            color = Orange,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {

                if (qty > 0 ) {
                    scope.launch {
                        val exist = coinDao.findCoin(selected.name, price)
                        val newCoin = if (exist != null) {
                            exist.copy(quantity = exist.quantity + qty)
                        } else {
                            CoinHolding(
                                name = selected.name,
                                symbol = selected.symbol,
                                quantity = qty,
                                pricePaid = price
                            )
                        }
                        coinDao.insert(newCoin)
                        onNavigateBack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Salva nel Portafoglio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}