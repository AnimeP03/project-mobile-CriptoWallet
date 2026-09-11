package com.example.criptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.criptowallet.ui.theme.CriptoWalletTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

enum class Screen {
    Wallet,
    AddCoin
}

class MainActivity : ComponentActivity() {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val retrofit = Retrofit.Builder()
        // quanto uso il mio telefono
        .baseUrl("http://192.168.1.15:5000/")
        // quando si usa il emulatore
        //.baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    val api = retrofit.create(Network.PriceApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val coinDao = database.coinDao()

        enableEdgeToEdge()
        setContent {
            CriptoWalletTheme {
                Start(coinDao)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Start(coinDao: CoinDao){
        var currentScreen by remember { mutableStateOf(Screen.Wallet)}

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(if (currentScreen == Screen.Wallet) "Il mio Portafoglio" else "Aggiungi Moneta")
                    },
                    navigationIcon = {
                        if (currentScreen == Screen.AddCoin) {
                            IconButton(onClick = { currentScreen = Screen.Wallet }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E2630)
                    )
                )
            },
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    Screen.Wallet -> WalletScreen(
                        coinDao = coinDao,
                        api = api,
                        onNavigateToAddCoin = {currentScreen = Screen.AddCoin})
                    Screen.AddCoin -> AddCoinScreen(
                        coinDao = coinDao,
                        api = api,
                        onNavigateBack = { currentScreen = Screen.Wallet }
                    )
                }
            }
        }
    }
}