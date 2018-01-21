package view

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import main.ScapeGoatEntry
import java.lang.Exception
import java.util.*

object EditController {

    var deque: Deque<ScapeGoatEntry<Int, Circle>> = ArrayDeque()

    fun display(main: Main) {
        val stage = Stage()
        stage.title = "Edit"
        val pane = Pane()
        val textForKey = Text("Key:")
        textForKey.layoutX = 10.0
        textForKey.layoutY = 27.0
        val textFieldForKey = TextField()
        textFieldForKey.layoutX = 60.0
        textFieldForKey.layoutY = 10.0
        val message = Text()
        message.layoutX = 10.0
        message.layoutY = 60.0
        val putButton = Button("    Put    ")
        putButton.layoutX = 10.0
        putButton.layoutY = 80.0
        val removeButton = Button("  Remove  ")
        removeButton.layoutX = 100.0
        removeButton.layoutY = 80.0
        val clearButton = Button("  Clear    ")
        clearButton.layoutX = 200.0
        clearButton.layoutY = 80.0
        val cancelButton = Button("  Cancel   ")
        cancelButton.layoutX = 300.0
        cancelButton.layoutY = 80.0
        cancelButton.onAction = EventHandler {
            setColor(main, false)
            stage.close()
        }
        pane.children.addAll(message,
                textFieldForKey,
                textForKey,
                putButton,
                removeButton,
                clearButton,
                cancelButton)
        textFieldForKey.onAction = EventHandler {
            setColor(main, false)
            try {
                val key = textFieldForKey.text.toInt()
                val newDeque = main.getDequeTo(key)
                deque = newDeque
                setColor(main, true)
            } catch (ex: Exception){
                message.text = "Enter simple number"
            }
        }
        putButton.onAction = EventHandler {
            try {
                val key = textFieldForKey.text.toInt()
                textFieldForKey.text = ""
                message.text = "Return: " + circleToString(main.put(key))
                setColor(main, false)
            } catch (ex: Exception) {
                textFieldForKey.text = ""
                message.text = ex.toString()
            }
        }
        removeButton.onAction = EventHandler {
            try {
                val key = textFieldForKey.text.toInt()
                textFieldForKey.text = ""
                setColor(main, false)
                message.text = "Return: " + circleToString(main.remove(key))
            } catch (ex: Exception) {
                textFieldForKey.text = ""
                message.text = ex.toString()
            }
        }
        clearButton.onAction = EventHandler {
            main.clear()
        }
        val scene = Scene(pane, 500.0, 130.0)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.scene = scene
        stage.show()
    }

    private fun circleToString(circle: Circle?): String{
        if (circle != null) {
            return "x: ${circle.centerX}, y: ${circle.centerY}"
        } else {
            return "null"
        }
    }

    private fun setColor(main: Main, isActive: Boolean){
        for (element in deque) {
            main.setColorOf(element.key, isActive)
        }
    }
}
