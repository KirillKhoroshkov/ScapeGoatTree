package view

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.stage.Stage
import main.ScapeGoatTree

class Main : Application() {
    val borderPane = BorderPane()
    val pane = Pane()
    val scrollPane = ScrollPane(pane)
    val scene = Scene(borderPane, 700.0, 500.0)
    val editButton = Button("Edit")
    val balanceFactorButton = Button("Balance factor: 0.7")
    val tools = ToolBar(editButton, balanceFactorButton)
    val map = ScapeGoatTree<Int, Int>(0.7)

    init {
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

    fun drawTree(pyramid: MutableList<MutableList<String?>>){
        pane.children.clear()
        if (!pyramid.isEmpty()){
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
                        val text = Text(x - RADIUS, y, element)
                        newRow.add(circle)
                        pane.children.addAll(circle, text)
                        if (!oldRow.isEmpty()){
                            val parentCircle = oldRow[X/2]!!
                            val line = Line(circle.centerX, circle.centerY, parentCircle.centerX, parentCircle.centerY)
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

    private fun degree2(index: Int): Int{
        if (index < 0){
            throw IllegalArgumentException()
        } else {
            var result = 1
            for (i in 1..index){
                result *= 2
            }
            return result
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}