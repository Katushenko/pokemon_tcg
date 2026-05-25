package com.pokemontcg.collector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pokemontcg.collector.ui.navigation.AppNavigation
import com.pokemontcg.collector.ui.theme.PokemonTCGTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonTCGTheme {
                AppNavigation()
            }
        }
    }
}
