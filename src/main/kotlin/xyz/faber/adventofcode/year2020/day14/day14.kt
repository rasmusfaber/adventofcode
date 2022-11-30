package xyz.faber.adventofcode.year2020.day14

import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.powerset

class Day14 {
    val input = getInputFromLines(2020, 14)
    /*val input = ("mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X\n" +
            "mem[8] = 11\n" +
            "mem[7] = 101\n" +
            "mem[8] = 0").lines()*/
    /*val input = ("mask = 000000000000000000000000000000X1001X\n" +
            "mem[42] = 100\n" +
            "mask = 00000000000000000000000000000000X0XX\n" +
            "mem[26] = 1").lines()*/

    fun part1() {
        var mask1 = 0L
        var mask2 = 0L
        val mem = mutableMapOf<Int, Long>()
        for (l in input) {
            val sp = l.split(" = ")
            if(sp[0]=="mask"){
                mask1 = sp[1].replace("X", "0").toLong(2)
                mask2 = sp[1].replace("X", "1").toLong(2)
                //println(mask1.toString(2) +" "+ mask2.toString(2))
            }else{
                val p = sp[0].replace("mem[", "").replace("]", "").toInt()
                mem[p] = (sp[1].toLong() or mask1) and mask2
                //println(mem[p])
            }
        }
        val res = mem.values.sum() // not 9716275610060
        println(res)
    }


    fun part2() {
        var mask = 0L
        var mask3set = emptySet<Int>()
        val mem = mutableMapOf<Long, Long>()
        for (l in input) {
            val sp = l.split(" = ")
            if(sp[0]=="mask"){
                mask = sp[1].replace("X", "0").toLong(2)
                mask3set = sp[1].withIndex().filter { it.value=='X' }.map{sp[1].length-1-it.index}.toSet()
                //println(sp[1])
                //println(mask.toString(2))
            }else{
                for (set in mask3set.powerset()) {
                    var p = sp[0].replace("mem[", "").replace("]", "").toLong()
                    //println(p.toString(2))
                    p = p or mask
                    for(i in mask3set){
                        if(i in set){
                            p = p or (1L shl i)
                        }
                        else{
                            p = p and (1L shl i).inv()
                        }
                    }
                    mem[p] = sp[1].toLong()
                    //println(p.toString()+ " "+ p.toString(2) + " " +mem[p])
                }
            }
        }
        val res = mem.values.sum()
        println(res)
    }

}

fun main(args: Array<String>) {
    val d = Day14()
    d.part1()
    d.part2()
}
