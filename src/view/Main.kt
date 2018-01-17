package view

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.stage.Stage
import main.ScapeGoatTree

class Main : Application() {
    private val borderPane = BorderPane()
    private val pane = Pane()
    private val scrollPane = ScrollPane(pane)
    val scene = Scene(borderPane, 700.0, 500.0)
    private val editButton = Button("Edit")
    private val balanceFactorButton = Button("Balance factor: 0.7")
    private var isComplete = true
    private val fullSizeItem = MenuItem("Full size")
    private val compressedSizeItem = MenuItem("Compressed")
    private val sizeMenu = MenuButton(fullSizeItem.text)
    private val tools = ToolBar(editButton, balanceFactorButton, sizeMenu)
    private val map = ScapeGoatTree<Int, Int>(0.7)

    init {
        sizeMenu.items.addAll(fullSizeItem, compressedSizeItem)
        fullSizeItem.onAction = EventHandler {
            setDrawFormat(true)
            sizeMenu.text = fullSizeItem.text
        }
        compressedSizeItem.onAction = EventHandler {
            setDrawFormat(false)
            sizeMenu.text = compressedSizeItem.text
        }
        editButton.onAction = EventHandler {
            EditController.display(this)
        }
        balanceFactorButton.onAction = EventHandler {
            BalanceFactorController.display(this)
        }
        tools.orientation = Orientation.HORIZONTAL
        borderPane.top = tools
        borderPane.center = scrollPane
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "ScapeGoatTree"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun drawTree() {
        if (isComplete) {
            drawCompleteTree(treeToPyramid(map))
        } else {
            drawIncompleteTree(treeToPyramid(map))
        }
    }

    private fun drawCompleteTree(pyramid: MutableList<MutableList<Int?>>) {
        pane.children.clear()
        if (!pyramid.isEmpty()) {
            val height = pyramid.size
            var oldRow = mutableListOf<Circle?>()
            for ((Y, list) in pyramid.withIndex()) {
                val newRow = mutableListOf<Circle?>()
                for ((X, element) in list.withIndex()) {
                    if (element != null) {
                        val shift = (RADIUS + INTERVAL / 2) * (degree2(height - Y - 1) - 1) * (2 * X + 1)
                        val x = (INTERVAL + RADIUS * 2) * (X + 1) + shift
                        val y = (DISTANCE + RADIUS * 2) * (Y + 1)
                        val circle = Circle(x, y, RADIUS)
                        circle.fill = FILL_COLOR
                        circle.stroke = STROKE_COLOR
                        circle.strokeWidth = STROKE_WIDTH
                        val text = Text(x - RADIUS, y, toFormatString(element))
                        newRow.add(circle)
                        pane.children.addAll(circle, text)
                        if (!oldRow.isEmpty()) {
                            val parentCircle = oldRow[X / 2]!!
                            val line = Line(x, y, parentCircle.centerX, parentCircle.centerY)
                            line.stroke = LINE_COLOR
                            line.strokeWidth = LINE_WIDTH
                            pane.children.add(line)
                            line.toBack()
                        }
                    } else {
                        newRow.add(null)
                    }
                }
                oldRow = newRow
            }
        }
    }

    private fun drawIncompleteTree(pyramid: MutableList<MutableList<Int?>>) {
        pane.children.clear()
        if (!pyramid.isEmpty()) {
            val height = pyramid.size
            var oldRow = mutableListOf<Circle?>()
            for ((Y, list) in pyramid.withIndex()) {
                val newRow = mutableListOf<Circle?>()
                var pass = 0
                for ((X, element) in list.withIndex()) {
                    if (element != null) {
                        val shift = (RADIUS + INTERVAL / 2) * (degree2(height - Y - 1) - 1) * (2 * X + 1) -
                                leftPass(X, Y, pyramid)
                        val x = (INTERVAL + RADIUS * 2) * (X + 1) + shift
                        val y = (DISTANCE + RADIUS * 2) * (Y + 1)
                        val circle = Circle(x, y, RADIUS)
                        circle.fill = FILL_COLOR
                        circle.stroke = STROKE_COLOR
                        circle.strokeWidth = STROKE_WIDTH
                        val text = Text(x - RADIUS, y, toFormatString(element))
                        newRow.add(circle)
                        pane.children.addAll(circle, text)
                        if (!oldRow.isEmpty()) {
                            val parentCircle = oldRow[X / 2]!!
                            val line = Line(x, y, parentCircle.centerX, parentCircle.centerY)
                            line.stroke = LINE_COLOR
                            line.strokeWidth = LINE_WIDTH
                            pane.children.add(line)
                            line.toBack()
                        }
                    } else {
                        pass++
                        newRow.add(null)
                    }
                }
                oldRow = newRow
            }
        }
    }

    private fun leftPass(x: Int, y: Int, pyramid: MutableList<MutableList<Int?>>): Double {
        val to = if (y == pyramid.lastIndex) {
            x + 1
        } else {
            degree2(pyramid.lastIndex - y - 1) * (2 * x + 1)
        }
        println(to)
        val count = (0 until to).count { pyramid.last()[it] == null }
        return (RADIUS + INTERVAL / 2) * (count)
    }

    private fun degree2(index: Int): Int {
        if (index < 0) {
            throw IllegalArgumentException()
        } else {
            var result = 1
            for (i in 1..index) {
                result *= 2
            }
            return result
        }
    }

    fun setBalanceFactor(newValue: Double) {
        map.balanceFactor = newValue
        balanceFactorButton.text = "Balance factor: $newValue"
        drawTree()
    }

    fun put(key: Int): Int? {
        val message = map.put(key, key)
        drawTree()
        return message
    }

    fun remove(key: Int): Int? {
        val message = map.remove(key)
        drawTree()
        return message
    }

    fun clear() {
        map.clear()
        drawTree()
    }

    private fun setDrawFormat(newValue: Boolean) {
        isComplete = newValue
        drawTree()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}