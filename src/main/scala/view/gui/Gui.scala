package view.gui

import controller.Controller
import model.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import util.Observer

class Gui(val controller: Controller, var controllable: Boolean = true) extends JFXApp3, Observer {

  controller.add(this)

  override def update(): Unit = {
    Platform.runLater(() =>
      if (controller.status.turn == Turn.Uninitialized) {
        initialize()
      } else if (controller.status.turn == Turn.Initialized) {
        begin()
      } else {
        continue()
      }
    )
  }


  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Durak"
      icons.add(new Image("file:src/main/resources/durak_logo.png"))
    }

    initialize()
  }

  def initialize(): Unit = {
    val playerAmountComboBox = new ComboBox[String](List("2", "3", "4", "5", "6")) {
      value = "2"
    }

    val cardAmounts = List("1 Karte", "2 Karten", "3 Karten", "4 Karten", "5 Karten", "6 Karten")

    val cardAmountComboBox = new ComboBox[String](cardAmounts) {
      value = cardAmounts.last
    }

    val namesVBox = new VBox()

    var nameTextFields = List[TextField](
      new TextField {
        promptText = s"Spieler 1"
      },
      new TextField {
        promptText = s"Spieler 2"
      }
    )

    nameTextFields.foreach(nameTextField => namesVBox.children.add(nameTextField))

    playerAmountComboBox.delegate.setOnAction(_ => {
      nameTextFields = List[TextField]()
      namesVBox.children.clear()

      for (i <- 1 to playerAmountComboBox.value.value.toInt) {
        val nameTextField = new TextField {
          promptText = s"Spieler $i"
        }

        nameTextFields = nameTextFields :+ nameTextField
        namesVBox.children.add(nameTextField)
      }
    })

    stage.scene = new Scene {
      root = new VBox {
        spacing = 10
        alignment = Pos.Center
        style = "-fx-background-color: slategrey;"
        padding = Insets(40)
        children = List(
          new ImageView("file:src/main/resources/durak_logo.png") {
            fitWidth = 200
            preserveRatio = true
          },
          new Label("Anzahl Spieler") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          playerAmountComboBox,
          new Label("Spielernamen") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          namesVBox,
          new Label("Kartenanzahl") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          cardAmountComboBox,
          new Region {
            prefHeight = 20
          },
          new Button("Spiel Starten") {
            style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            onAction = _ => {
              val names = nameTextFields.map(nameTextField => nameTextField.text.value)

              if (names.distinct.size == nameTextFields.size) {
                controller.initialize(cardAmounts.indexOf(cardAmountComboBox.value.value) + 1, names)
              } else {
                new Alert(Alert.AlertType.Error) {
                  title = "Fehler"
                  headerText = "Fehlerhafte Eingabe"
                  contentText = "Die Spielernamen müssen eindeutig sein."
                }.showAndWait()
              }
            }
          }
        )
      }
    }
  }

  def begin(): Unit = {
    val namesComboBox = new ComboBox[String](controller.status.players.map(_.name)) {
      value = controller.status.players.head.name
    }

    stage.scene = new Scene {
      root = new VBox {
        spacing = 10
        alignment = Pos.Center
        style = "-fx-background-color: slategrey;"
        children = List(
          new Label("Wer soll anfangen?") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          namesComboBox,
          new Button("Starten") {
            style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            onAction = _ => {
              controller.chooseAttacking(controller.status.players.find(_.name == namesComboBox.value.value).get)
            }
          }
        )
      }
    }
  }

  def continue(): Unit = {
    val turn = controller.status.turn
    val undefended = Card.toSelectableCards(controller.status.undefended)
    val defended = Card.toSelectableCards(controller.status.defended)
    val used = Card.toSelectableCards(controller.status.used)
    val own = Card.toSelectableCards(controller.current.get.cards)

    stage.scene = new Scene {
      root = new BorderPane() {
        center = if (turn == Turn.FirstlyAttacking || turn == Turn.SecondlyAttacking) {
          attackingVBox(own, used, undefended, defended, () => {

          }, card => {
            true
          })
        } else if (turn == Turn.Defending) {
          defendingVBox(own, used, undefended, defended, () => {

          }, (used, undefended) => {
            true
          })
        } else {
          VBox()
        }

        right = roundVBox()


        if (controllable) {
          top = new ToolBar {
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

      }
    }
  }

  def cardsHBox(cards: List[SelectableCard], selectable: Boolean): HBox = {
    var selectedImageView: Option[ImageView] = None
    var selectedCard: Option[SelectableCard] = None

    val pngCards = cards.map { card =>
      val imageView = new ImageView(new Image(card.card.getPath)) {
        fitHeight = 150
        preserveRatio = true
      }

      if (selectable) {
        imageView.onMouseClicked = _ => {
          selectedImageView.foreach(imageView => imageView.fitHeight = 150)
          selectedCard.foreach(card => card.selected = false)

          imageView.fitHeight = 135

          selectedImageView = Some(imageView)
          selectedCard = Some(card)
          selectedCard.foreach(card => card.selected = true)
        }
      }

      imageView
    }

    new HBox {
      spacing = 10
      alignment = Pos.Center
      children = pngCards
    }
  }

  def labeledCardsVBox(label: String, cards: List[SelectableCard], show: Boolean, selectable: Boolean): VBox = {
    new VBox {
      children = List(
        new Label(label) {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        cardsHBox(cards, selectable)
      )

      visible = show
    }
  }

  def roundCardsVBox(own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], selectable: Boolean): VBox = {
    new VBox {
      children = List(
        labeledCardsVBox("Zu Verteidigen", undefended, undefended.nonEmpty, selectable),
        labeledCardsVBox("Verteidigt", defended, defended.nonEmpty, false),
        labeledCardsVBox("Verwendet", used, used.nonEmpty, false),
        labeledCardsVBox("Deine Karten", own, own.nonEmpty, true)
      )
    }
  }

  def defendingVBox(own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: (Card, Card) => Boolean): VBox = {
    new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
      padding = Insets(40)
      children = List(
        roundCardsVBox(own, used, undefended, defended, true),
        new HBox {
          spacing = 10
          alignment = Pos.Center
          style = "-fx-background-color: slategrey;"
          padding = Insets(40)
          children = List(
            new Button("Verteidigen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            },
            new Button("Abbrechen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            }
          )
        }
      )
    }
  }

  def attackingVBox(own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: (Card) => Boolean): VBox = {
    new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
      padding = Insets(40)
      children = List(
        roundCardsVBox(own, used, undefended, defended, false),
        new HBox {
          spacing = 10
          alignment = Pos.Center
          style = "-fx-background-color: slategrey;"
          padding = Insets(40)
          children = List(
            new Button("Verteidigen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            },
            new Button("Abbrechen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            }
          )
        }
      )
    }
  }

  def roundVBox(): VBox = {
    new VBox {
      spacing = 10
      padding = Insets(40)
      alignment = Pos.TopLeft
      style = "-fx-background-color: slategrey;"
      children = List(
        new Label("Spielinfo") {
          style = "-fx-font-size: 22pt; -fx-font-weight: bold;"
        },
        new Region,
        new Label("Trumpf:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new Label("Trumpf-Karte:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new ImageView(new Image(controller.status.trump.get.getPath)) {
          fitHeight = 80
          preserveRatio = true
        },
        new Label("Deckgröße:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new Label(controller.status.stack.toString) {
          style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
        },
        new VBox {
          children = controller.status.players.map(player => new Label(s"${player.name}: ${player.turn}"))
        }
      )
    }
  }
}