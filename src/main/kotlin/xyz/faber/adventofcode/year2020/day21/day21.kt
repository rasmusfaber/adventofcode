package xyz.faber.adventofcode.year2020.day21

import xyz.faber.adventofcode.util.parseInputFromLines

class Day21 {
    val input = parseInputFromLines(2020, 21, "(.*) \\(contains (.*)\\)").map { (a,b)->a.split(" ").toSet() to b.split(", ").toSet() }
    /*val input = ("mxmxvkd kfcds sqjhc nhms (contains dairy, fish)\n" +
            "trh fvjkl sbzzf mxmxvkd (contains dairy)\n" +
            "sqjhc fvjkl (contains soy)\n" +
            "sqjhc mxmxvkd sbzzf (contains fish)").lines().map{"(.*) \\(contains (.*)\\)".toRegex().find(it)!!.destructured}.map { (a,b)->a.split(" ").toSet() to b.split(", ").toSet() }*/

    fun part1() {
        val foods = input.flatMap { it.first }.toSet()
        val ingredients = input.flatMap { it.second }.toSet()

        val possibleFoods = ingredients.map{
            val possible = foods.toMutableSet()
            val relevant = input.filter { l->l.second.contains(it) }
            relevant.forEach { possible.removeAll(foods - it.first) }
            it to possible
        }
        val translation = mutableMapOf<String, String>()

        var done = false

        while(!done){
            val next = possibleFoods.firstOrNull{it.second.size==1 && it.first !in translation.keys}
            if(next==null){
                done = true
            }else{
                possibleFoods.filter{it.first!=next.first}.forEach{
                        it.second.remove(next.second.single())
                }
                translation[next.first] = next.second.single()
            }
        }
        val notAllergens = foods.minus(possibleFoods.flatMap { it.second }).toSet()

        val res = input.flatMap { it.first }.count { it in notAllergens }
        println(res) // not 2272
    }


    fun part2() {
        val foods = input.flatMap { it.first }.toSet()
        val ingredients = input.flatMap { it.second }.toSet()

        val possibleFoods = ingredients.map{
            val possible = foods.toMutableSet()
            val relevant = input.filter { l->l.second.contains(it) }
            relevant.forEach { possible.removeAll(foods - it.first) }
            it to possible
        }
        val translation = mutableMapOf<String, String>()

        var done = false

        while(!done){
            val next = possibleFoods.firstOrNull{it.second.size==1 && it.first !in translation.keys}
            if(next==null){
                done = true
            }else{
                possibleFoods.filter{it.first!=next.first}.forEach{
                    it.second.remove(next.second.single())
                }
                translation[next.first] = next.second.single()
            }
        }
        val notAllergens = foods.minus(possibleFoods.flatMap { it.second }).toSet()

        val res = translation.entries.sortedBy { it.key }.map { it.value }.joinToString(",")
        println(res) // not 2272
    }

}

fun main(args: Array<String>) {
    val d = Day21()
    d.part1()
    d.part2()
}
