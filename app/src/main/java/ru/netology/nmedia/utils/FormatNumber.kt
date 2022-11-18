package ru.netology.nmedia.utils

import java.text.DecimalFormatSymbols
import kotlin.math.absoluteValue

fun formatNumber(inputNumber: Int): String {
    val abbreviations = arrayOf("", "K", "M", "T")
    var abbreviationsIndex = 0
    var number = inputNumber.absoluteValue

    while (number > 1000) {
        number /= 1000
        abbreviationsIndex++
    }

    if (abbreviationsIndex >= abbreviations.size) abbreviationsIndex = 0

    var mainNumber: String = number.toString()
    var secondNumber: String? = null

    if (abbreviationsIndex > 0) {
        val cuttedNumber =
            inputNumber.absoluteValue.toString().dropLast((abbreviationsIndex - 1) * 3).dropLast(2)
        mainNumber = cuttedNumber.dropLast(1)
        secondNumber =
            if (cuttedNumber.last().toString() == "0") null else cuttedNumber.last().toString()
    }

    return "${if (inputNumber < 0) "-" else ""}${mainNumber}${if (secondNumber != null) "${DecimalFormatSymbols.getInstance().decimalSeparator}$secondNumber" else ""}${abbreviations[abbreviationsIndex]}"
}