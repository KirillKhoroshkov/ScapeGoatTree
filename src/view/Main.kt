package view

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import javafx.stage.Stage
import main.ScapeGoatTree

class Main : Application() {
    val borderPane = BorderPane()
    val text = Text("...")
    val scrollPane = ScrollPane(text)
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

    fun print(message : String){
        text.text = message
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}