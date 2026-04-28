package com.example.finans_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.finans_movil.Data.Local.AppDatabase
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.ui.theme.FinansmovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "finans_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val repository = BankRepository(db.accountDao())
        enableEdgeToEdge()
        setContent {
            FinansmovilTheme {
                MainView(repository)
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