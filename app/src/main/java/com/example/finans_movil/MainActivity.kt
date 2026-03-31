package com.example.finans_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.finans_movil.ui.theme.FinansmovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinansmovilTheme {
                MainView()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    FinansmovilTheme {
        TransactionView("egreso")
    }
}