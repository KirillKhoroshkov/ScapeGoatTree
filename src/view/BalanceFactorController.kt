package view

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import java.lang.Exception

object BalanceFactorController {

    fun display(main: Main) {
        val stage = Stage()
        stage.title = "Balance Factor"
        val pane = Pane()
        val textForBalanceFactor = Text("New Balance Factor:")
        textForBalanceFactor.layoutX = 10.0
        textForBalanceFactor.layoutY = 27.0
        val textFieldForBalanceFactor = TextField()
        textFieldForBalanceFactor.layoutX = 200.0
        textFieldForBalanceFactor.layoutY = 10.0
        val message = Text()
        message.layoutX = 10.0
        message.layoutY = 60.0
        val setButton = Button("Edit balance factor")
        setButton.layoutX = 10.0
        setButton.layoutY = 80.0
        val cancelButton = Button("  Cancel   ")
        cancelButton.layoutX = 170.0
        cancelButton.layoutY = 80.0
        cancelButton.onAction = EventHandler { stage.close() }
        pane.children.addAll(message,
                textFieldForBalanceFactor,
                textForBalanceFactor,
                setButton,
                cancelButton)
        setButton.onAction = EventHandler {
            try {
                val newValue = textFieldForBalanceFactor.text.toDouble()
                main.map.balanceFactor = newValue
                textFieldForBalanceFactor.text = ""
                main.balanceFactorButton.text = "Balance factor: $newValue"
                main.drawTree(intTreeToPyramidOfString(main.map))
            } catch (ex: Exception) {
                textFieldForBalanceFactor.text = ""
                message.text = ex.toString()
            }
        }
        val scene = Scene(pane, 500.0, 130.0)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.initOwner(main.scene.window)
        stage.scene = scene
        stage.show()
    }
}