package com.fauran.diplom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fauran.diplom.ui.theme.DiplomTheme
import dagger.hilt.android.AndroidEntryPoint
import com.fauran.diplom.navigation.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiplomTheme {
                // A surface container using the 'background' color from the theme
                Navigation()
            }
        }
    }
}

