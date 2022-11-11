package ru.netology.nmedia

import java.text.DecimalFormatSymbols
import kotlin.math.absoluteValue


fun formatNumber(number: Int): String {
    val abbreviations = arrayOf("", "K", "M", "T")
    var abbreviationsIndex = 0
    val absNumber = number.absoluteValue
    var numberA = absNumber

    while (numberA > 1000) {
        numberA /= 1000
        abbreviationsIndex++
    }

    if (abbreviationsIndex >= abbreviations.size) abbreviationsIndex = 0

    var mainNumber: String = numberA.toString()
    var secondNumber: String? = null

    if (abbreviationsIndex > 0) {
        val cuttedNumber = absNumber.toString().dropLast((abbreviationsIndex - 1) * 3).dropLast(2)
        mainNumber = cuttedNumber.dropLast(1)
        secondNumber =
            if (cuttedNumber.last().toString() == "0") null else cuttedNumber.last().toString()
    }

    return "${if (number < 0) "-" else ""}${mainNumber}${if (secondNumber != null) "${DecimalFormatSymbols.getInstance().decimalSeparator}$secondNumber" else ""}${abbreviations[abbreviationsIndex]}"
}
