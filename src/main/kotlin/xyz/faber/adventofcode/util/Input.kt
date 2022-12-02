package xyz.faber.adventofcode.util

import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.text.StringEscapeUtils
import java.io.File

//val overwriteInput = System.getProperty("user.home")+"/tmp/day25alt2.txt"
val overwriteInput = ""

fun getInput(year: Int, day: Int): String {
    if (overwriteInput.isNotEmpty()) {
        return File(overwriteInput).readText()
    }
    val basePath = when {
        SystemUtils.IS_OS_WINDOWS -> "c:\\temp\\advent"
        SystemUtils.IS_OS_LINUX -> System.getProperty("user.home") + "/tmp/advent"
        else -> System.getProperty("java.io.tmpdir") + "/advent"
    }
    File(basePath).mkdirs();
    val file = File(basePath + "/${year}_$day.txt")
    if (file.exists()) {
        return file.readText()
    }
    val cookiefile = File(basePath + "/sessioncookie")
    if (!cookiefile.exists()) {
        throw IllegalStateException("No session cookie found. Please add ${cookiefile.absolutePath} with the session cookie")
    }
    val sessionCookie = cookiefile.readText().trim()
    val (_, _, result) = "https://adventofcode.com/$year/day/$day/input"
        .httpGet()
        .header(Headers.COOKIE, "session=${sessionCookie}")
        .response()
    val (bytes, error) = result
    if (error != null) {
        throw IllegalStateException("Error getting input: $error")
    }
    val text = String(bytes!!)
    if (text.startsWith("Please")) {
        throw IllegalArgumentException(text)
    }
    file.writeText(text)
    return text
}

fun getTest(year: Int, day: Int): String? {
    if (overwriteInput.isNotEmpty()) {
        return File(overwriteInput).readText()
    }
    val basePath = when {
        SystemUtils.IS_OS_WINDOWS -> "c:\\temp\\advent"
        SystemUtils.IS_OS_LINUX -> System.getProperty("user.home") + "/tmp/advent"
        else -> System.getProperty("java.io.tmpdir") + "/advent"
    }
    File(basePath).mkdirs();
    val file = File(basePath + "/${year}_$day-test.txt")
    if (file.exists()) {
        return file.readText()
    }
    val cookiefile = File(basePath + "/sessioncookie")
    if (!cookiefile.exists()) {
        throw IllegalStateException("No session cookie found. Please add ${cookiefile.absolutePath} with the session cookie")
    }
    val sessionCookie = cookiefile.readText().trim()
    val (_, _, result) = "https://adventofcode.com/$year/day/$day"
        .httpGet()
        .header(Headers.COOKIE, "session=${sessionCookie}")
        .response()
    val (bytes, error) = result
    if (error != null) {
        throw IllegalStateException("Error getting input: $error")
    }
    val text = String(bytes!!)
    if (text.startsWith("Please")) {
        throw IllegalArgumentException(text)
    }
    val match = """<pre><code>(.*?)</code></pre>""".toRegex(RegexOption.DOT_MATCHES_ALL).find(text) ?: return null
    val (escapedTestData) = match.destructured
    val testData = StringEscapeUtils.unescapeHtml4(escapedTestData)
    file.writeText(testData)
    return testData
}

fun getInputFromLines(year: Int, day: Int): List<String> = getInput(year, day).lines().dropLastWhile { it.isBlank() }

fun parseInputFromLines(year: Int, day: Int, regex: String) = parseInputFromLines(year, day, regex.toRegex())

fun parseInputFromLines(year: Int, day: Int, regex: Regex): List<MatchResult.Destructured> = getInputFromLines(year, day).map { regex.find(it)!!.destructured!! }

fun getInputFromCsv(year: Int, day: Int): List<String> = getInput(year, day).split(",")

fun getInputIntsFromCsv(year: Int, day: Int): List<Int> = getInputFromCsv(year, day).map { it.trim().toInt() }

fun getInputLongsFromCsv(year: Int, day: Int): List<Long> = getInputFromCsv(year, day).map { it.trim().toLong() }

fun getProgram(year: Int, day: Int): List<Long> = getInputLongsFromCsv(year, day)

fun getInputAsDigits(year: Int, day: Int): List<Int> = getInput(year, day).filter { it.isDigit() }.map { it.toString().toInt() }

fun Collection<String>.parse(regex: String): List<MatchResult.Destructured> {
    val regex = regex.toRegex()
    return this.mapNotNull { regex.find(it) }.map { it.destructured }
}

fun String.parse(regex: String): MatchResult.Destructured {
    val regex = regex.toRegex()
    return regex.find(this)!!.destructured!!
}