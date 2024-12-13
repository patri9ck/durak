package view.gui

import controller.Controller
import model.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.collections.ObservableBuffer
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

    val attackingComboBox = new ComboBox[String](List("Zufällig")) {
      value = "Zufällig"
      prefWidth = 200
    }

    nameTextFields.foreach { nameTextField =>
      namesVBox.children.add(nameTextField)
      nameTextField.text.onChange { (_, _, _) =>
        attackingComboBox.items = ObservableBuffer("Zufällig") ++ nameTextFields.map(_.text.value).filter(_.nonEmpty)
        attackingComboBox.value = "Zufällig"
      }
    }

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

    val errorLabel = new Label

    val errorVBox = new VBox {
      alignment = Pos.Center

      children = List(
        new Region {
          prefHeight = 20
        },
        errorLabel
      )

      visible = false
    }

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

              if (names.distinct.size != nameTextFields.size) {
                errorLabel.text = "Die Namen müssen einzigartig sein."
                errorVBox.visible = true
              } else if (names.exists(_.isBlank)) {
                errorLabel.text = "Es müssen alle Namen gesetzt sein sein."
                errorVBox.visible = true

              } else {
                if (attackingComboBox.value.value == "Zufällig") {
                  controller.initialize(cardAmounts.indexOf(cardAmountComboBox.value.value) + 1, names)
                } else {
                  controller.initialize(cardAmounts.indexOf(cardAmountComboBox.value.value) + 1, names, attackingComboBox.value.value)
                }
              }
            }
          },
          new Region {
            prefHeight = 20
          },
          attackingComboBox,
          errorVBox
        )
      }
    }
    stage.centerOnScreen()
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
          attackingVBox(controller.current.get.name, own, used, undefended, defended, deny, attack)
        } else if (turn == Turn.Defending) {
          defendingVBox(controller.current.get.name, own, used, undefended, defended, pickUp, defend)
        } else {
          VBox()
        }

        right = roundVBox(controller.status.trump.get, controller.status.stack, controller.status.players)

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
    stage.centerOnScreen()
  }

  def deny(): Unit = {
    controller.deny()
  }

  def attack(card: Card): Boolean = {
    if (controller.canAttack(card)) {
      controller.attack(card)

      return true
    }

    false
  }

  def pickUp(): Unit = {
    controller.pickUp()
  }

  def defend(used: Card, undefended: Card): Boolean = {
    if (controller.canDefend(used, undefended)) {
      controller.defend(used, undefended)

      return true
    }

    false
  }

  def cardsHBox(cards: List[SelectableCard], selectable: Boolean): HBox = {
    var selectedImageView: Option[ImageView] = None
    var selectedCard: Option[SelectableCard] = None

    new HBox {
      spacing = 10
      alignment = Pos.Center
      children = cards.map { card =>
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

  def roundCardsVBox(name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], selectable: Boolean): VBox = {
    new VBox {
      children = List(
        labeledCardsVBox("Zu Verteidigen", undefended, undefended.nonEmpty, selectable),
        labeledCardsVBox("Verteidigt", defended, defended.nonEmpty, false),
        labeledCardsVBox("Verwendet", used, used.nonEmpty, false),
        labeledCardsVBox(s"Deine Karten $name", own, own.nonEmpty, true)
      )
    }
  }

  def defendingVBox(name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: (Card, Card) => Boolean): VBox = {
    val errorLabel = new Label

    val errorVBox = new VBox {
      alignment = Pos.Center

      children = List(
        new Region {
          prefHeight = 20
        },
        errorLabel
      )

      visible = false
    }

    new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
      padding = Insets(40)
      children = List(
        roundCardsVBox(name, own, used, undefended, defended, true),
        new HBox {
          spacing = 10
          alignment = Pos.Center
          style = "-fx-background-color: slategrey;"
          padding = Insets(40)
          children = List(
            new Button("Verteidigen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
              onAction = _ => {
                val undefendedCard = undefended.find(_.selected)
                val ownCard = own.find(_.selected)

                if (undefendedCard.isDefined && ownCard.isDefined) {
                  if (!chosen.apply(ownCard.get.card, undefendedCard.get.card)) {
                    errorLabel.text = "Mit dieser Karte kannst du nicht verteidigen."
                    errorVBox.visible = true
                  }
                } else {
                  errorLabel.text = "Du musst eine Karte zum Verteidigen und eine Karte zum Verwenden auswählen."
                  errorVBox.visible = true
                }
              }
            },
            new Button("Aufnehmen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
              onAction = _ => canceled.apply()
            }
          )
        },
        errorVBox
      )
    }
  }

  def attackingVBox(name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: (Card) => Boolean): VBox = {
    val errorLabel = new Label

    val errorVBox = new VBox {
      alignment = Pos.Center

      children = List(
        new Region {
          prefHeight = 20
        },
        errorLabel
      )

      visible = false
    }

    new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
      padding = Insets(40)
      children = List(
        roundCardsVBox(name, own, used, undefended, defended, false),
        new HBox {
          spacing = 10
          alignment = Pos.Center
          style = "-fx-background-color: slategrey;"
          padding = Insets(40)
          children = List(
            new Button("Angreifen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
              onAction = _ => {
                val ownCard = own.find(_.selected)

                if (ownCard.isDefined) {
                  if (!chosen.apply(ownCard.get.card)) {
                    errorLabel.text = "Mit dieser Karte kannst du nicht angreifen."
                    errorVBox.visible = true
                  }
                } else {
                  errorLabel.text = "Du musst eine Karte zum Angreifen auswählen."
                  errorVBox.visible = true
                }
              }
            },
            new Button("Aufhören") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
              visible = defended.nonEmpty || undefended.nonEmpty
              onAction = _ => canceled.apply()
            }
          )
        },
        errorVBox
      )
    }
  }

  def roundVBox(trump: Card, stack: List[Card], players: List[Player]): VBox = {
    new VBox {
      spacing = 10
      padding = Insets(40)
      alignment = Pos.TopLeft
      style = "-fx-background-color: slategrey;"
      children = List(
        new Label("Spielinfo") {
          style = "-fx-font-size: 24pt; -fx-font-weight: bold;"
        },
        new Region,
        new Label("Trumpf-Karte:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new ImageView(new Image(trump.getPath)) {
          fitHeight = 80
          preserveRatio = true
        },
        new Label("Deckgröße:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new Label(stack.size.toString) {
          style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
        },
        new Label("Spieler") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        new VBox {
          children = players.map(player => new Label(s"${player.name}: ${player.turn}"))
          style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
        }
      )
    }
  }
}