package xyz.faber.adventofcode.util

data class Pos4D(val x: Int, val y: Int, val z: Int, val w: Int) {
    override fun toString(): String {
        return "($x, $y, $z $w)"
    }


}