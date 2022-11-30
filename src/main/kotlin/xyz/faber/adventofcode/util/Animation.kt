package xyz.faber.adventofcode.util

import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.screen.Screen

class Animation {
    var screen: Screen? = null
    val layers = mutableListOf<Layer>()
    var started = false;

    fun start() {
        val tileGrid = SwingApplications.startTileGrid()
        screen = Screens.createScreenFor(tileGrid)
        screen!!.display()
        started = true
    }

    fun <T> frame(map: XYMap<T>, pos: Pos, tileMapper: (T) -> Tile, posTile: Tile) {
        val delta = Pos(-map.minx, -map.miny)
        val layer = map.toLayer(tileMapper, delta)
        layer.setAbsoluteTileAt((pos + delta).toPosition(), posTile)
        addFrame(layer)
    }

    fun <T> frame(map: XYMap<T>, tileMapper: (T) -> Tile) {
        val delta = Pos(-map.minx, -map.miny)
        val layer = map.toLayer(tileMapper, delta)
        addFrame(layer)
    }

    private fun addFrame(layer: Layer) {
        if (started) {
            screen!!.pushLayer(layer)
        }
        layers.add(layer)
    }

    @JvmName("frameFromCharMap")
    fun frame(map: XYMap<Char>) = frame(map, ::charTileMapper)

    @JvmName("frameFromCharMap")
    fun frame(map: XYMap<Char>, pos: Pos) = frame(map, pos, ::charTileMapper, posTile.value)

    @JvmName("frameFromIntMap")
    fun frame(map: XYMap<Int>) = frame(map, ::intToColorMapper)

    @JvmName("frameFromIntMap")
    fun frame(map: XYMap<Int>, pos: Pos) = frame(map, pos, ::intToColorMapper, posTile.value)

    @JvmName("frameFromLongMap")
    fun frame(map: XYMap<Long>) = frame(map, ::longToColorMapper)

    @JvmName("frameFromLongMap")
    fun frame(map: XYMap<Long>, pos: Pos) = frame(map, pos, ::longToColorMapper, posTile.value)

    fun run(delayMs: Long) {
        if (!started) {
            start()
        }
        while (true) {
            for (layer in layers) {
                screen!!.popLayer()
                screen!!.pushLayer(layer)
                Thread.sleep(delayMs)
            }
        }
    }

    fun loop(delayMs: Long) {
        while (true) {
            run(delayMs)
        }
    }
}

val tileset = CP437TilesetResources.rexPaint16x16()

val posTile = lazy {
    Tiles.newBuilder()
            .withName("pos")
            .withCharacter('*')
            .withTileset(tileset)
            .withForegroundColor(TileColors.fromString("blue"))
            .buildCharacterTile()
}

fun charTileMapper(c: Char): Tile = characterTile(c)

private fun characterTile(c: Char): CharacterTile {
    return Tiles.newBuilder()
            .withName(c.toString())
            .withCharacter(c)
            .withTileset(tileset)
            .buildCharacterTile()
}

fun intToColorMapper(v: Int) = longToColorMapper(v.toLong())

fun longToColorMapper(v: Long): Tile = Tiles.newBuilder()
        .withName(v.toString())
        .withBackgroundColor(when (v) {
            0L -> TileColors.fromString("black")
            1L -> TileColors.fromString("white")
            2L -> TileColors.fromString("blue")
            -1L -> TileColors.fromString("yellow")
            else -> {
                println("Missing color definition: $v")
                TileColors.fromString("red")
            }
        })
        .build()

fun Pos.toPosition(): Position = Positions.create(this.x, this.y)

fun <T> XYMap<T>.toLayer(tileMapper: (T) -> Tile, delta: Pos): Layer {
    val layer = Layers.newBuilder()
            .build()
    this.positions().forEach { layer.setTileAt((it + delta).toPosition(), tileMapper(this[it])) }
    return layer
}
