package xyz.faber.adventofcode.year2020.day10

import xyz.faber.adventofcode.util.getInputFromLines

class Day10 {
    val input = getInputFromLines(2020, 10).filter { it.isNotBlank() }.map{it.toInt()}
    //val input = "16\n10\n15\n5\n1\n11\n7\n19\n6\n12\n4".lines().filter { it.isNotBlank() }.map{it.toInt()}
    //val input = "28\n33\n18\n42\n31\n14\n46\n20\n48\n47\n24\n23\n49\n45\n19\n38\n39\n11\n1\n32\n25\n35\n8\n17\n7\n9\n4\n2\n34\n10\n3".lines().filter { it.isNotBlank() }.map{it.toInt()}

    fun part1() {
        val max = input.maxOrNull()!!
        val sorted = (input+0+(max+3)).sorted()
        val differences = sorted.windowed(2, 1).map{it[1]-it[0]}
        val step1s = differences.count { it==1 }
        val step3s = differences.count { it==3 }
        println(step1s.toLong()*step3s)
    }


    fun part2() {
        var res = 1L
        val max = input.maxOrNull()!!
        val sorted = (input+0+(max+3)).sorted()
        val differences = sorted.windowed(2, 1).map{it[1]-it[0]}
        val sequencesof1s = differences.joinToString("").split("3").map { it.length }
        println(differences)
        println(sequencesof1s)
        /*for(i in 0..differences.size-2){
            if(differences[i]==1&& differences[i+1]==1){
                res *=2
            }
            if(differences[i]==1&& differences[i+1]==2){
                res *=2
            }
            if(differences[i]==1&& differences[i+1]==3){
                //
            }
            if(differences[i]==2&& differences[i+1]==1){
                res *=2
            }
            if(differences[i]==2&& differences[i+1]==2){
                //
            }
            if(differences[i]==2&& differences[i+1]==3){
                //
            }
        }*/
        for (i in sequencesof1s) {
            res*=count(i)
        }
        val step1s = differences.count { it==1 }
        val step2s = differences.count { it==1 }
        val step3s = differences.count { it==3 }
        println(step1s.toLong())
        println(step2s)
        println(step3s)
        println(res)

        // not 2251799813685248
        // not 7256820784037888
    }

    fun count(i: Int):Long{
        return when(i){
            0->1
            1->1
            2->2
            3->4
            else->count(i-1)+count(i-2)+count(i-3)
        }
    }

}

fun main(args: Array<String>) {
    val d = Day10()
    d.part1()
    d.part2()
}
