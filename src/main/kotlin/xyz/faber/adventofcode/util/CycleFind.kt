package xyz.faber.adventofcode.util


fun <T> List<T>.findCycle(): Pair<Int, Int> {
    var p1 = this.size - 1
    var p2 = p1 - 1
    var maxLength = 0
    var maxCycleLength = 0
    var cycleLength = 1
    while (p2 >= p1 / 2) {
        var i = 0
        while (p2>=i && this[p1 - i] == this[p2 - i]) {
            i++
        }
        if (i >= cycleLength) {
            if (i > maxLength) {
                maxLength = (i / cycleLength) * cycleLength
                maxCycleLength = cycleLength
            }
        }
        p2--
        cycleLength++
    }
    return Pair(this.size - maxLength - maxCycleLength, maxCycleLength)
}


