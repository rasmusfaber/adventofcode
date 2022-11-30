package xyz.faber.adventofcode.util

fun findMax(predicate: (Long) -> Boolean): Long = findMax(predicate, 1L)

fun findMax(predicate: (Long) -> Boolean, guess: Long): Long {
    var low: Long
    var high: Long
    if (predicate(guess)) {
        low = guess
        var highguess = guess * 2
        while (predicate(highguess)) {
            highguess *= 2
        }
        high = highguess - 1
    } else {
        high = guess - 1
        var lowguess = guess / 2
        while (predicate(lowguess) && lowguess != 0L) {
            lowguess /= 2
        }
        low = lowguess
    }
    while (low != high) {
        var mid = (low + high) / 2
        if (mid == low) {
            mid++
        }
        if (predicate(mid)) {
            low = mid
        } else {
            high = mid - 1
        }
    }
    return high
}