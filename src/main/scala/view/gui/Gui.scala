package view.gui

import com.google.inject.Inject
import controller.Controller
import model.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.shape.StrokeType.Inside
import util.Observer

import scala.util.Random

class Gui @Inject()(val controller: Controller) extends JFXApp3, Observer {

  controller.add(this)

  private var controllable: Boolean = false

  override def update(): Unit = {
    Platform.runLater(() =>
      if (controller.status.turn == Turn.Uninitialized) {
        initialize()
      } else if (controller.isOver) {
        showGameOverScene()
      } else {
        continue()
      }
    )
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Durak"
      icons.add(new Image("file:src/main/resources/durak-logo-neu.png"))
      onCloseRequest = _ => {
        Platform.exit()
        System.exit(0)
      }
    }

    initialize()
  }

  def initialize(): Unit = {
    val playerAmountComboBox = new ComboBox[String](List("2", "3", "4", "5", "6")) {
      style = "-fx-font-family: 'Century Schoolbook';"
      value = "2"
    }

    val cardAmounts = List("1 Karte", "2 Karten", "3 Karten", "4 Karten", "5 Karten", "6 Karten")

    val cardAmountComboBox = new ComboBox[String](cardAmounts) {
      style = "-fx-font-family: 'Century Schoolbook';"
      value = cardAmounts.last
    }

    val namesVBox = new VBox()

    val attackingComboBox = new ComboBox[String](List("Zufällig")) {
      style = "-fx-font-family: 'Century Schoolbook';"
      value = "Zufällig"
      prefWidth = 200
    }

    var nameTextFields = createNameTextFields(2, attackingComboBox, namesVBox)

    playerAmountComboBox.delegate.setOnAction(_ => {
      nameTextFields = createNameTextFields(playerAmountComboBox.value.value.toInt, attackingComboBox, namesVBox)

      updateAttackingComboBox(attackingComboBox, nameTextFields)
    })


    val toolBar = createToolBar

    val errorLabel = new Label {
      style = "-fx-font-size: 8pt; -fx-font-weight: bold; -fx-text-fill: #dfdfdf; -fx-font-family: 'Century Schoolbook';"
    }

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

    val backgroundImage = new BackgroundImage(
      new Image("file:src/main/resources/Durak-main-menu.png"),
      BackgroundRepeat.NoRepeat,
      BackgroundRepeat.NoRepeat,
      BackgroundPosition.Center,
      new BackgroundSize(100, 100, true, true, true, false)
    )

    stage.width = 428
    stage.height = 760
    stage.resizable = false

    stage.scene = new Scene {
      root = new BorderPane() {
        background = new Background(Array(backgroundImage))
        center = new VBox {
          padding = Insets(170, 100, 0, 100)
          spacing = 10
          alignment = Pos.Center
          children = List(
            new Label("Anzahl Spieler") {
              style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #CCCCCC; -fx-font-family: 'Century Schoolbook';"
            },
            playerAmountComboBox,
            new Label("Spielernamen") {
              style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #CCCCCC; -fx-font-family: 'Century Schoolbook';"
            },
            namesVBox,
            new Label("Kartenanzahl") {
              style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #CCCCCC; -fx-font-family: 'Century Schoolbook';"
            },
            cardAmountComboBox,
            new Region {
              prefHeight = 20
            },
            new CheckBox("Steuerbar") {
              style = "-fx-font-size: 8pt; -fx-font-weight: bold; -fx-text-fill: #CCCCCC; -fx-font-family: 'Century Schoolbook';"
              onAction = _ => {
                controllable = !controllable

                toolBar.visible = controllable
                toolBar.managed = controllable
              }
              selected = controllable
            },
            new Region {
              prefHeight = 20
            },
            new Button("Spiel Starten") {
              style = buttonStyle
              prefWidth = 200
              prefHeight = 70

              //style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
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
        top = toolBar
      }
    }
    stage.centerOnScreen()
  }

  def createNameTextFields(amount: Int, attackingComboBox: ComboBox[String], namesVBox: VBox): List[TextField] = {
    namesVBox.children.clear()

    var nameTextFields = List[TextField]()

    for (i <- 1 to amount) {
      val nameTextField = new TextField {
        promptText = s"Spieler $i"
      }

      nameTextField.text.onChange { (_, _, _) =>
        updateAttackingComboBox(attackingComboBox, nameTextFields)
      }

      nameTextFields = nameTextFields :+ nameTextField
      namesVBox.children.add(nameTextField)
    }

    nameTextFields
  }

  def updateAttackingComboBox(attackingComboBox: ComboBox[String], nameTextFields: List[TextField]): Unit = {
    attackingComboBox.items = ObservableBuffer("Zufällig") ++ nameTextFields.map(_.text.value).filter(_.nonEmpty)
    attackingComboBox.value = "Zufällig"
  }

  def createToolBar: ToolBar = new ToolBar {
    visible = controllable
    managed = controllable
    background = null
    style = "-fx-background-color: transparent;"
    items = List(
      new Button("Rückgängig machen") {
        onAction = _ => controller.undo()
      },
      new Button("Wiederherstellen") {
        onAction = _ => controller.redo()
      },
      new Button("Laden") {
        onAction = _ => controller.load()
      },
      new Button("Speichern") {
        onAction = _ => controller.save()
      }
    )
  }

  def buttonStyle: String =
    "-fx-background-image: url('file:src/main/resources/button.png'); " +
      "-fx-background-size: auto; " +
      "-fx-background-repeat: no-repeat;" +
      "-fx-background-position: center;" +
      "-fx-text-fill: #CCCCCC; -fx-font-size: 16pt; -fx-font-weight: bold;" +
      "-fx-background-color: transparent;" +
      "-fx-border-color: transparent;" +
      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0, 0, 4);" +
      "-fx-font-family: 'Century Schoolbook';"

  def continue(): Unit = {
    val turn = controller.status.turn
    val undefended = Card.toSelectableCards(controller.status.undefended)
    val defended = Card.toSelectableCards(controller.status.defended)
    val used = Card.toSelectableCards(controller.status.used)
    val own = Card.toSelectableCards(controller.current.get.cards)

    val errorLabel = new Label {
      style = "-fx-font-size: 13pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
      wrapText = true
      maxWidth = 200
    }

    def getCardBack(suit: Suit): String = {
      suit match {
        case Suit.Hearts | Suit.Diamonds => "backside-red.png"
        case Suit.Clubs | Suit.Spades => "backside-blue.png"
        case _ => "backside-blue.png"
      }
    }

    val otherPlayerCards = controller.status.players.find(player => player != controller.current.get).map(_.cards).getOrElse(List())
    val cardImages = otherPlayerCards.map { card =>
      val cardBack = getCardBack(card.suit)
      new ImageView(new Image(s"file:src/main/resources/cards/$cardBack")) {
        fitWidth = 120
        fitHeight = 120
        preserveRatio = true
      }
    }

    stage.width = 1175
    stage.height = 832
    stage.resizable = false

    stage.scene = new Scene {
      root = new GridPane {
        background = new Background(Array(new BackgroundImage(
          new Image("file:src/main/resources/Durak-background.png"),
          BackgroundRepeat.NoRepeat,
          BackgroundRepeat.NoRepeat,
          BackgroundPosition.Center,
          new BackgroundSize(100, 100, true, true, true, false)
        )))

        columnConstraints = List(
          new ColumnConstraints {
            percentWidth = 80
          },
          new ColumnConstraints {
            percentWidth = 20
          }
        )

        rowConstraints = List(
          new RowConstraints {
            vgrow = Priority.Always
          },
          new RowConstraints {
            vgrow = Priority.Always
          },
          new RowConstraints {
            vgrow = Priority.Always
          },
          new RowConstraints {
            vgrow = Priority.Always
          }
        )
        add(new HBox {
          padding = Insets(0)
          children = createToolBar
        }, 0, 0)

        add(new HBox {
          padding = Insets(20, 0, 0, 0)
          children = cardImages
          alignment = Pos.Center
        }, 0, 1)

        add(createBoxForAttackingCards(controller.status.turn, defended, undefended, used), 0, 2)

        add(new VBox {
          spacing = 20
          children = List(
            createBoxForPlayerCards(controller.status.turn, controller.current.get.name, own, own.nonEmpty),
            createButtons(turn, controller.current.get.name, own, used, undefended, defended, deny, attack, errorLabel)
          )
        }, 0, 3)

        add(new HBox {
          children = List(
          )
        }, 1, 0)

        add(getInfoVBox(controller.status.trump.get, controller.status.stack, controller.status.players), 1, 1, 1, 2)

        add(new VBox {
          children = List(

            errorLabel
          )
          alignment = Pos.Center
        }, 1, 3)
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

  def createCardsHBox(cards: List[SelectableCard], selectable: Boolean): HBox = {
    var selectedImageView: Option[ImageView] = None
    var selectedCard: Option[SelectableCard] = None

    new HBox {
      spacing = 10
      padding = Insets(0, 0, 0, -200)
      alignment = Pos.Center
      children = cards.map { card =>
        val imageView = new ImageView(new Image(card.card.getPath)) {
          fitHeight = 120
          preserveRatio = true
        }

        if (selectable) {
          imageView.onMouseClicked = _ => {
            selectedImageView.foreach(imageView => imageView.fitHeight = 120)
            selectedCard.foreach(card => card.selected = false)

            imageView.fitHeight = 100

            selectedImageView = Some(imageView)
            selectedCard = Some(card)
            selectedCard.foreach(card => card.selected = true)
          }
        }

        imageView
      }
    }
  }

  def createLabeledCardsVBox(label: String, cards: List[SelectableCard], show: Boolean, selectable: Boolean): VBox = {
    new VBox {
      alignment = Pos.Center
      prefWidth = 400
      minWidth = 400
      children = List(
        new HBox {

          alignment = Pos.Center
          padding = Insets(0, 0, 0, 0)
          children = List(
            new Label(label) {
              style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
            })
        },
        new HBox {
          padding = Insets(0, 0, 0, 200)
          alignment = Pos.Center
          children = List(
            new Region {
              prefHeight = 10
            },
            createCardsHBox(cards, selectable)
          )
        }
      )
      visible = show
    }
  }

  def getInfoVBox(trump: Card, stack: List[Card], players: List[Player]): VBox = {
    new VBox {
      padding = Insets(80, 0, 0, 0)
      spacing = 10
      children = List(
        new Label("Trumpf-Karte:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
        },
        new ImageView(new Image(trump.getPath)) {
          fitHeight = 100
          preserveRatio = true
        },
        new Label("Deckgröße:") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
        },
        new Label(stack.size.toString) {
          style = "-fx-font-size: 13pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
        },
        new Label("Spieler") {
          style = "-fx-font-size: 16pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
        },
        new VBox {
          children = players.map(player => new Label(s"${player.name}") {
            style = "-fx-font-size: 13pt; -fx-font-weight: bold; -fx-text-fill: #575a57; -fx-font-family: 'Century Schoolbook';"
          }) //: ${player.turn.name}
          alignment = Pos.Center
        }
      )
      alignment = Pos.Center
    }
  }

  def createBoxForPlayerCards(turn: Turn, name: String, own: List[SelectableCard], selectable: Boolean): VBox = {
    turn match {
      case Turn.FirstlyAttacking | Turn.SecondlyAttacking | Turn.Defending =>
        createLabeledCardsVBox(s"Deine Karten $name", own, own.nonEmpty, selectable)
      case _ =>
        new VBox()
    }
  }

  def createBoxForAttackingCards(turn: Turn, defended: List[SelectableCard], undefended: List[SelectableCard], used: List[SelectableCard]): HBox = {
    turn match {
      case Turn.FirstlyAttacking | Turn.SecondlyAttacking | Turn.Defending =>
        new HBox {
          alignment = Pos.Center
          children = List(
            new VBox {
              alignment = Pos.Center
              children = List(
                new HBox {
                  alignment = Pos.CenterLeft
                  children = List(
                    createLabeledCardsVBox("Verteidigt", defended, defended.nonEmpty, false)
                  )
                },
                new HBox {
                  alignment = Pos.CenterLeft
                  children = List(
                    createLabeledCardsVBox("Verwendet", used, used.nonEmpty, false)
                  )
                }
              )
            },
            new HBox {
              alignment = Pos.CenterLeft
              children = List(
                createLabeledCardsVBox("Zu Verteidigen", undefended, undefended.nonEmpty, true)
              )
            }
          )
        }
      case _ =>
        new HBox()
    }
  }

  def createBoxForDefenderButtons(name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: (Card, Card) => Boolean, errorLabel: Label): HBox = {
    new HBox {
      spacing = 10
      alignment = Pos.Center
      children = List(
        new Button("Verteidigen") {
          style = buttonStyle
          prefWidth = 200
          prefHeight = 70
          onAction = _ => {
            val undefendedCard = undefended.find(_.selected)
            val ownCard = own.find(_.selected)

            if (undefendedCard.isDefined && ownCard.isDefined) {
              if (!chosen.apply(ownCard.get.card, undefendedCard.get.card)) {
                errorLabel.text = "Mit dieser Karte kannst du nicht verteidigen."
              }
            } else {
              errorLabel.text = "Du musst eine Karte zum Verteidigen und eine Karte zum Verwenden auswählen."
            }
          }
        },
        new Button("Aufnehmen") {
          style = buttonStyle
          prefWidth = 200
          prefHeight = 70
          onAction = _ => canceled.apply()
        }
      )
    }
  }

  def createBoxForAttackingButtons(name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: Card => Boolean, errorLabel: Label): HBox = {
    new HBox {
      spacing = 10
      alignment = Pos.Center
      if (defended.nonEmpty || undefended.nonEmpty) {
        children = List(
          new Button("Angreifen") {
            style = buttonStyle
            prefWidth = 200
            prefHeight = 70
            onAction = _ => {
              val ownCard = own.find(_.selected)

              if (ownCard.isDefined) {
                if (!chosen.apply(ownCard.get.card)) {
                  errorLabel.text = "Mit dieser Karte kannst du nicht angreifen."
                }
              } else {
                errorLabel.text = "Du musst eine Karte zum Angreifen auswählen."
              }
            }
          },
          new Button("Aufhören") {
            style = buttonStyle
            prefWidth = 200
            prefHeight = 70
            onAction = _ => canceled.apply()
          }
        )
      } else {
        children = List(
          new Button("Angreifen") {
            style = buttonStyle
            prefWidth = 200
            prefHeight = 70
            onAction = _ => {
              val ownCard = own.find(_.selected)

              if (ownCard.isDefined) {
                if (!chosen.apply(ownCard.get.card)) {
                  errorLabel.text = "Mit dieser Karte kannst du nicht angreifen."
                }
              } else {
                errorLabel.text = "Du musst eine Karte zum Angreifen auswählen."
              }
            }
          }
        )
      }
    }
  }

  def createButtons(turn: Turn, name: String, own: List[SelectableCard], used: List[SelectableCard], undefended: List[SelectableCard], defended: List[SelectableCard], canceled: () => Unit, chosen: Card => Boolean, errorLabel: Label): HBox = {
    if (turn == Turn.FirstlyAttacking || turn == Turn.SecondlyAttacking) {
      createBoxForAttackingButtons(controller.current.get.name, own, used, undefended, defended, deny, attack, errorLabel)
    } else if (turn == Turn.Defending) {
      createBoxForDefenderButtons(controller.current.get.name, own, used, undefended, defended, pickUp, defend, errorLabel)
    } else {
      HBox()
    }
  }

  def showGameOverScene(): Unit = {
    stage.scene = new Scene {
      root = new VBox {
        alignment = Pos.Center
        spacing = 20
        padding = Insets(20)
        children = List(
          new Label("Game Over") {
            style = "-fx-font-size: 24pt; -fx-font-weight: bold; -fx-text-fill: #FF0000; -fx-font-family: 'Century Schoolbook';"
          },
          new Button("Restart") {
            style = buttonStyle
            onAction = _ => {
              // Logic to restart the game
              initialize()
            }
          },
          new Button("Exit") {
            style = buttonStyle
            onAction = _ => {
              Platform.exit()
              System.exit(0)
            }
          }
        )
      }
    }
  }

}
