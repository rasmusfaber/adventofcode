package xyz.faber.adventofcode.util

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*


fun BufferedImage.saveAsPNG(fileName: String){
    ImageIO.write(this, "png", File(fileName))
}

fun BufferedImage.show() {
    val picLabel = JLabel(ImageIcon(this))
    val jPanel = JPanel()
    jPanel.add(picLabel)
    val f = JFrame()
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    f.size = Dimension(this.getWidth()+50, this.getHeight()+50)
    f.add(jPanel)
    f.isVisible = true
}