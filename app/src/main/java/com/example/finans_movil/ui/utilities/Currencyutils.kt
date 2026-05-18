package com.example.finans_movil.ui.utilities

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

// Formateador base (locale colombiano)
val copFormatter: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
    maximumFractionDigits = 0
    minimumFractionDigits = 0
    isGroupingUsed        = true
}

fun formatCOP(amount: Double)  = "$ ${copFormatter.format(amount)} COP"
fun formatCOPLong(value: Long) = "$ ${copFormatter.format(value)}"

// VisualTransformation compartida
object ThousandsSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits    = text.text.filter { it.isDigit() }
        val formatted = if (digits.isEmpty()) "" else copFormatter.format(digits.toLong())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val partial = digits.take(offset)
                return if (partial.isEmpty()) 0
                else copFormatter.format(partial.toLong()).length
            }
            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count { it.isDigit() }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}