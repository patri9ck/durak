package view

import controller.Controller
import model.{Card, Turn}
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.BorderPane
import scalafx.scene.shape.SVGPath

import java.nio.file.{Files, Paths}

class Gui(val controller: Controller, val step: Boolean) extends JFXApp3 {

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Durak"
      scene = new Scene {
        val borderPane: BorderPane = new BorderPane() {
          center = getCardSvg(Card(model.Rank.Ace, model.Suit.Spades))
        }

        if (step) {
          borderPane.top = new ToolBar {
            items = List(
              new Button("Undo") {
                onAction = _ => controller.undo()
              },
              new Button("Redo") {
                onAction = _ => controller.redo()
              }
            )
          }
        }

        root = borderPane
      }
    }

    continue()
  }

  def getCardSvg(card: Card): SVGPath =
    new SVGPath() {
      content = Files.readString(Paths.get(card.getPath))
    }


  def continue(): Unit = {
    if (controller.status.turn == Turn.Uninitialized) {
      initialize()
    } else if (controller.status.turn == Turn.Initialized) {
      chooseAttacking()
    } else {
      ask()
    }
  }

  def initialize(): Unit = {

  }

  def chooseAttacking(): Unit = {

  }

  def ask(): Unit = {

  }
}