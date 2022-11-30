package xyz.faber.adventofcode.util

fun String.extractNumbers(): List<Int> {
    val regex = "-?\\d+".toRegex()
    return regex.findAll(this).map { it.value.toInt() }.toList()
}
