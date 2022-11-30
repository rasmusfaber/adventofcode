package xyz.faber.adventofcode.year2020.day4

import xyz.faber.adventofcode.util.getInput

class Day4 {
    val input = getInput(2020, 4)

    fun part1() {
        val passports = input.split("\n\n")
        val res = passports.count { check(it) }
        println(res)
    }

    val mandatory = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

    fun check(s: String): Boolean {
        val entries = s.split(" ", "\n").filter { it.isNotBlank() }.map { it.split(":") }.map { it[0] to it[1] }.toMap()
        return mandatory.all { entries.contains(it) }
    }

    fun check2(s: String): Boolean {
        try {
            val entries = s.split(" ", "\n").filter { it.isNotBlank() }.map { it.split(":") }.map { it[0] to it[1] }.toMap()
            if (!mandatory.all { entries.contains(it) }) {
                return false
            }
            val byr = entries["byr"]!!.toInt()
            if(byr !in 1920..2002){
                return false
            }
            val iyr = entries["iyr"]!!.toInt()
            if(iyr !in 2010..2020){
                return false
            }
            val eyr = entries["eyr"]!!.toInt()
            if(eyr !in 2020..2030){
                return false
            }
            val hgt = entries["hgt"]!!
            val (hgt1, hgt2) = "(\\d+)(.+)".toRegex().matchEntire(hgt)!!.destructured
            when(hgt2){
                "cm" -> if(hgt1.toInt() !in 150..193) return false
                "in" -> if(hgt1.toInt() !in 59..76) return false
                else->throw IllegalArgumentException(hgt2)
            }
            val hcl = entries["hcl"]!!
            if(!hcl.matches("#[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]".toRegex())){
                return false
            }
            val ecl = entries["ecl"]!!
            if(ecl !in setOf("amb", "blu", "brn", "gry", "grn", "hzl","oth")){
                return false
            }
            val pid = entries["pid"]!!
            if(!pid.matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d".toRegex())){
                return false
            }

        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun part2() {
        val passports = input.split("\n\n")
        val res = passports.count { check2(it) }
        println(res)
    }
}

fun main(args: Array<String>) {
    val d = Day4()
    d.part1()
    d.part2()
}
