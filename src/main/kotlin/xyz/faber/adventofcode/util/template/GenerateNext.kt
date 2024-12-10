package xyz.faber.adventofcode.util.template

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

fun detectNextDayAndYear(baseDir: String): Pair<Int, Int> {
    val yearDirs = Files.list(Paths.get(baseDir))
        .filter { Files.isDirectory(it) && it.fileName.toString().matches(Regex("year\\d{4}")) }
        .map { it.fileName.toString().removePrefix("year").toInt() }
        .sorted()
        .toList()

    val lastYear = yearDirs.lastOrNull() ?: return LocalDate.now().year to 1
    val lastYearDir = Paths.get(baseDir, "year$lastYear")

    val days = Files.list(lastYearDir)
        .filter { Files.isDirectory(it) && it.fileName.toString().matches(Regex("day\\d+")) }
        .map { it.fileName.toString().removePrefix("day").toInt() }
        .sorted()
        .toList()

    val nextDay = (days.maxOrNull() ?: 0) + 1
    return if (nextDay > 25) lastYear + 1 to 1 else lastYear to nextDay
}

fun generateBoilerplateWithThymeleaf(year: Int, day: Int): String {
    // Load the Thymeleaf engine
    val templateEngine = TemplateEngine()

    // Set the context with variables
    val context = Context().apply {
        setVariable("year", year)
        setVariable("day", day)
    }

    // Read the template file
    val template = Files.readString(Paths.get("src/main/resources/template.kt"))

    // Process the template
    return templateEngine.process(template, context)
}

fun main() {
    val baseDir = "src/main/kotlin/xyz/faber/adventofcode"

    val (year, day) = detectNextDayAndYear(baseDir)

    val generatedCode = generateBoilerplateWithThymeleaf(year, day)

    val outputDir = Paths.get("$baseDir/year$year/day$day")
    Files.createDirectories(outputDir)

    val outputFile = outputDir.resolve("Day$day.kt")
    if (Files.exists(outputFile)) {
        println("File already exists: $outputFile")
        return
    }

    Files.writeString(outputFile, generatedCode)
    println("Generated file: $outputFile")
}