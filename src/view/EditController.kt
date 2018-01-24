package view

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import java.lang.Exception

object EditController {

    var deque = mutableListOf<Int>()

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
        val removeButton = Button("  Remove  ")
        val findButton = Button("  Find   ")
        val clearButton = Button("  Clear    ")
        val cancelButton = Button("  Cancel   ")
        val buttons = HBox(putButton, removeButton, findButton, clearButton, cancelButton)
        buttons.spacing = 5.0
        buttons.layoutX = 10.0
        buttons.layoutY = 80.0
        cancelButton.onAction = EventHandler {
            dyeInDeActiveColor(main)
            stage.close()
        }
        pane.children.addAll(message,
                textFieldForKey,
                textForKey,
                buttons
        )
        findButton.onAction = EventHandler {
            Thread {
                try {
                    dyeInDeActiveColor(main)
                    val key = textFieldForKey.text.toInt()
                    val newDeque = main.getDequeTo(key).map { it.key }
                    deque = newDeque as MutableList<Int>
                    dyeInActiveColor(main)
                    if (deque.isEmpty() || deque.last() != key) {
                        dyeInDeActiveColor(main)
                    }
                    message.text = "Return: " + if (deque.last() == key) key else null
                } catch (ex: Exception) {
                    message.text = "Enter simple number"
                }
            }.start()
        }
        textFieldForKey.onAction = findButton.onAction
        putButton.onAction = EventHandler {
            Thread {
                try {
                    val key = textFieldForKey.text.toInt()
                    textFieldForKey.text = ""
                    find(key, main)
                    dyeInDeActiveColor(main)
                    Platform.runLater {
                        val rez = main.put(key)
                        if (rez == null) {
                            deque.add(key)
                        }
                        message.text = "Return: " + if (rez != null) key else null
                        dyeInActiveColor(key, main)
                    }
                } catch (ex: Exception) {
                    textFieldForKey.text = ""
                    message.text = ex.toString()
                }
            }.start()
        }
        removeButton.onAction = EventHandler {
            Thread {
                try {
                    val key = textFieldForKey.text.toInt()
                    textFieldForKey.text = ""
                    find(key, main)
                    dyeInDeActiveColor(main)
                    Platform.runLater { message.text = "Return: " + if (main.remove(key) != null) key else null }
                } catch (ex: Exception) {
                    textFieldForKey.text = ""
                    message.text = ex.toString()
                }
            }.start()
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

    private fun find(key: Int, main: Main) {
        dyeInDeActiveColor(main)
        val newDeque = main.getDequeTo(key).map { it.key }
        deque = newDeque as MutableList<Int>
        dyeInActiveColor(main)
    }

    private fun dyeInActiveColor(key: Int, main: Main) {
        main.setColorOf(key, true)
    }

    private fun dyeInActiveColor(main: Main) {
        if (!deque.isEmpty()) {
            var previous = deque[0]
            for (element in deque) {
                main.setColorOf(previous, false)
                previous = element
                main.setColorOf(element, true)
                Thread.sleep(400)
            }
        }
    }

    private fun dyeInDeActiveColor(main: Main) {
        if (!deque.isEmpty()) {
            main.setColorOf(deque.last(), false)
        }
    }
}