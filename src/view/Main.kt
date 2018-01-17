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
    private val completedItem = MenuItem("as completed")
    private val incompletedItem = MenuItem("as incompleted")
    private val typeOfDrawingMenu = MenuButton(completedItem.text)
    private val tools = ToolBar(editButton, balanceFactorButton, typeOfDrawingMenu)
    private val map = ScapeGoatTree<Int, Int>(0.7)

    init {
        typeOfDrawingMenu.items.addAll(completedItem, incompletedItem)
        completedItem.onAction = EventHandler {
            setCompleting(true)
            typeOfDrawingMenu.text = completedItem.text
        }
        incompletedItem.onAction = EventHandler {
            setCompleting(false)
            typeOfDrawingMenu.text = incompletedItem.text
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
        println("Main.drawIncompleteTree")
        pane.children.clear()
        if (!pyramid.isEmpty()) {
            var oldRow = mutableListOf<Circle?>()
            for (Y in pyramid.lastIndex downTo 0) {
                val list = pyramid[Y]
                val newRow = mutableListOf<Circle?>()
                var lastX = 0.0
                for ((X, element) in list.withIndex()) {
                    if (element != null) {
                        val left = if (oldRow.isEmpty()) null else oldRow[2 * X]
                        val right = if (oldRow.isEmpty()) null else oldRow[2 * X + 1]
                        var x = if (left != null && right != null) {
                            (left.centerX + right.centerX) / 2
                        } else if (left != null) {
                            left.centerX + (INTERVAL / 2 + RADIUS)
                        } else if (right != null) {
                            right.centerX - (INTERVAL / 2 + RADIUS)
                        } else {
                            if (X % 2 == 1 && list[X - 1] == null) {
                                lastX + INTERVAL + RADIUS * 3 + INTERVAL / 2
                            } else {
                                lastX + INTERVAL + RADIUS * 2.5 + INTERVAL
                            }
                        }
                        lastX = x
                        val y = (DISTANCE + RADIUS * 2) * (Y + 1)
                        val circle = Circle(x, y, RADIUS)
                        circle.fill = FILL_COLOR
                        circle.stroke = STROKE_COLOR
                        circle.strokeWidth = STROKE_WIDTH
                        val text = Text(x - RADIUS, y, toFormatString(element))
                        newRow.add(circle)
                        pane.children.addAll(circle, text)
                        if (!oldRow.isEmpty()) {
                            if (left != null) {
                                val line = Line(x, y, left.centerX, left.centerY)
                                line.stroke = LINE_COLOR
                                line.strokeWidth = LINE_WIDTH
                                pane.children.add(line)
                                line.toBack()
                            }
                            if (right != null) {
                                val line = Line(circle.centerX, circle.centerY, right.centerX, right.centerY)
                                line.stroke = LINE_COLOR
                                line.strokeWidth = LINE_WIDTH
                                pane.children.add(line)
                                line.toBack()
                            }
                        }
                    } else {
                        newRow.add(null)
                    }
                }
                oldRow = newRow
            }
        }
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

    private fun findParent(x: Int, y: Int, pyramid: MutableList<MutableList<Int?>>): Int{
        var Y = 1
        var X = x / 2
        while (y - Y >= 0 && pyramid[y - Y][X] == null) {
            Y++
            X /= 2
        }
        return Y - 1
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

    fun setCompleting(newValue: Boolean) {
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