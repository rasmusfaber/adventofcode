package xyz.faber.adventofcode.util

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.LoadLibs
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage


fun BufferedImage.detectText(): String {
    val tesseract = Tesseract()
    val dataDir = LoadLibs.extractTessResources("tessdata")

    tesseract.setDatapath(dataDir.absolutePath)
    return tesseract.doOCR(this)
}

fun BufferedImage.detectText(rect: Rectangle): String {
    val tesseract = Tesseract()
    val dataDir = LoadLibs.extractTessResources("tessdata")

    tesseract.setDatapath(dataDir.absolutePath)
    return tesseract.doOCR(this, rect)
}

fun <T> XYMap<T>.detectText(mapper: (T) -> Boolean) = this.toImage(mapper).detectText()

fun XYMap<Boolean>.detectText() = this.detectText { it }

fun <T> XYMap<T>.toImage(mapper: (T) -> Boolean): BufferedImage {
    val scale = 3
    val margin = 10
    val image = BufferedImage(this.dimx * scale + margin * 2, this.dimy * scale + margin * 2, BufferedImage.TYPE_INT_RGB)
    image.drawRectangle(0, 0, image.width, image.height, Color.WHITE)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            image.setRGB(x, y, Color.WHITE.rgb)
        }
    }

    val mapFunc: (Pos) -> Boolean = {
        if (this.isInBounds(it)) {
            mapper(this[it])
        } else {
            false
        }
    }
    this.positions().forEach {
        if (mapFunc(it)) {
            image.drawRectangle(it.x * scale + margin, it.y * scale + margin, scale, scale, Color.BLACK)
        } else {
            // Smoothen
            smoothen(image, mapFunc, it, Pos(-1,-1), scale, margin, Color.BLACK)
            smoothen(image, mapFunc, it, Pos(-1,1), scale, margin, Color.BLACK)
            smoothen(image, mapFunc, it, Pos(1,-1), scale, margin, Color.BLACK)
            smoothen(image, mapFunc, it, Pos(1,1), scale, margin, Color.BLACK)
        }
    }
    return image
}

private fun smoothen(image: BufferedImage, mapFunc: (Pos) -> Boolean, p: Pos, delta: Pos, scale: Int, margin: Int, color: Color) {
    if (mapFunc(p + Pos(delta.x, 0)) && mapFunc(p + Pos(0, delta.y)) && !mapFunc(p + delta)) {
        var smoothCorner = Pos(p.x * scale + margin, p.y * scale + margin)
        if (delta.x == 1) {
            smoothCorner += Pos(scale - 1, 0)
        }
        if (delta.y == 1) {
            smoothCorner += Pos(0, scale - 1)
        }
        image.setRGB(smoothCorner.x, smoothCorner.y, color.rgb)
    }

}


private fun BufferedImage.drawRectangle(startx: Int, starty: Int, width: Int, height: Int, color: Color) {
    for (x in 0 until width) {
        for (y in 0 until height) {
            this.setRGB(startx + x, starty + y, color.rgb)
        }
    }
}
