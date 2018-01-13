package view

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import java.lang.Exception

object EditController{

    fun display(main: Main){
        val stage = Stage()
        stage.title = "Edit"
        val pane = Pane()
        val textForKey = Text("Key:")
        textForKey.layoutX = 10.0
        textForKey.layoutY = 27.0
        val textFieldForKey = TextField()
        textFieldForKey.layoutX = 60.0
        textFieldForKey.layoutY = 10.0
        val textFieldForValue = TextField()
        textFieldForValue.layoutX = 320.0
        textFieldForValue.layoutY = 10.0
        val textForValue = Text("Value?:")
        textForValue.layoutX = 250.0
        textForValue.layoutY = 27.0
        val message = Text()
        message.layoutX = 10.0
        message.layoutY = 60.0
        val putButton = Button("    Put    ")
        putButton.layoutX = 10.0
        putButton.layoutY = 80.0
        val removeButton = Button("  Remove   ")
        removeButton.layoutX = 100.0
        removeButton.layoutY = 80.0
        val cancelButton = Button("  Cancel   ")
        cancelButton.layoutX = 210.0
        cancelButton.layoutY = 80.0
        cancelButton.onAction = EventHandler { stage.close() }
        pane.children.addAll(message,
                textFieldForKey,
                textFieldForValue,
                textForKey,
                textForValue,
                putButton,
                removeButton,
                cancelButton)
        putButton.onAction = EventHandler {
            try {
                val key = textFieldForKey.text.toInt()
                val value = if (textFieldForValue.text != "") textFieldForValue.text.toInt() else key
                message.text = "Return: " + main.map.put(key, value)
                textFieldForKey.text = ""
                textFieldForValue.text = ""
                main.print(treeToString(main.map))
            }
            catch (ex : Exception){
                textFieldForKey.text = ""
                textFieldForValue.text = ""
                message.text = ex.toString()
            }
        }
        removeButton.onAction = EventHandler {
            try {
                val key = textFieldForKey.text.toInt()
                message.text = "Return: " + main.map.remove(key)
                textFieldForKey.text = ""
                textFieldForValue.text = ""
                main.print(treeToString(main.map))
            }
            catch (ex : Exception){
                textFieldForKey.text = ""
                textFieldForValue.text = ""
                message.text = ex.toString()
            }
        }
        val scene = Scene(pane, 570.0, 130.0)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.initOwner(main.scene.window)
        stage.scene = scene
        stage.show()
    }
}