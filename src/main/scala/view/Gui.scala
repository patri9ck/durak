package view

import controller.Controller
import model.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.layout.*

import util.Observer
import scalafx.scene.image.{Image, ImageView}
import org.apache.batik.transcoder.{TranscoderInput, TranscoderOutput}
import java.nio.file.{Files, Paths}

class Gui(val controller: Controller, val controllable: Boolean) extends JFXApp3, Observer {

  controller.add(this)

  private var startMenuScene: VBox = _
  private var gameScene: BorderPane = _

  override def update(): Unit = {
    Platform.runLater(() => {
      startMenuScene.children = Seq()
    })
  }

  override def start(): Unit = {
    val playerCountComboBox = new ComboBox[String](Seq("2", "3", "4", "5", "6")) {
      value = "2"
    }
    val playerNamesVBox = new VBox()

    for (i <- 1 to 2) {
      playerNamesVBox.children.add(new TextField {
        promptText = s"Spieler $i"
      })
    }

    playerCountComboBox.delegate.setOnAction(_ => {
      val selectedValue = playerCountComboBox.value.value.toInt
      playerNamesVBox.children.clear()
      for (i <- 1 to selectedValue) {
        playerNamesVBox.children.add(new TextField {
          promptText = s"Spieler $i"
        })
      }
    })

    val amountCards = new ComboBox[String](Seq("2 Karten", "3 Karten", "4 Karten", "5 Karten", "6 Karten")) {
      value = "2 Karten"
    }
    val startButton = new Button("Spiel Starten") {
      style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
      onAction = _ => {
        stage.scene = new Scene {
          root = gameScene
        }
        stage.width = 800
        stage.height = 600
        stage.centerOnScreen()
      }
    }
    val logoDurak = new ImageView("file:src/main/resources/durak_logo.png") {
      fitWidth = 200
      preserveRatio = true
    }

    def createCardHBox(cards: List[Card]): HBox = {
      var selectedCard: Option[ImageView] = None
      val pngCards = cards.map { card =>
        val imageView = new ImageView(new Image(card.getPath)) {
          fitHeight = 150 // Initial height of the cards
          preserveRatio = true
        }

        imageView.onMouseClicked = _ => {
          selectedCard.foreach { card =>
            card.fitHeight = 150
          }

          imageView.fitHeight = 135
          selectedCard = Some(imageView)
        }

        imageView
      }

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = pngCards
      }
    }

    def showTrumpCard(): ImageView = {
      val trumpCard = Card(Rank.Ace, Suit.Spades)
      new Label("Trumpf-Karte:") {
        style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
      }

      new ImageView(new Image(trumpCard.getPath)) {
        fitHeight = 80
        preserveRatio = true
      }
    }

    def amountCardsInDeck(): Label = {
      val deckSize = 36
      new Label(deckSize.toString) {
        style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
      }
    }

    def showAttacker(): Label = {
      val attacker = "Spieler 1"
      new Label(attacker) {
        style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
      }
    }

    def showDefender(): Label = {
      val defender = "Spieler 2"
      new Label(defender) {
        style = "-fx-font-size: 13pt; -fx-font-weight: bold;"
      }
    }

    def navigationAreaHBox(): HBox = {
      val turn = Turn.FirstlyAttacking // Placeholder for controller.status.turn
      new HBox {
        spacing = 10
        alignment = Pos.Center
        style = "-fx-background-color: slategrey;"
        padding = Insets(40)
        children = turn match {
          case Turn.Defending => Seq(
            new Button("Karte verteidigen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            },
            new Button("Karte ziehen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            }
          )
          case Turn.FirstlyAttacking => Seq(
            new Button("Angreifen") {
              style = "-fx-background-color: #FCFCFD; -fx-border-radius: 4px; -fxbox-shadow: rgba(45, 35, 66, 0.4) 0 2px 4px,rgba(45, 35, 66, 0.3) 0 7px 13px -3px,#D6D6E7 0 -3px 0 inset; -fx-color: #FCFCFD; -fx-font-size: 16pt; -fx-font-weight: bold;"
            }
          )
        }
      }
    }

    def defendingTurnVBox(): VBox = {

      val defendingCards = List(Card(Rank.Ace, Suit.Clubs), Card(Rank.Two, Suit.Diamonds))
      val attackingCards = List(Card(Rank.Four, Suit.Spades), Card(Rank.Five, Suit.Clubs), Card(Rank.Six, Suit.Diamonds))

      new VBox {
        spacing = 10
        alignment = Pos.Center
        style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
        padding = Insets(40)
        children = Seq(
          new Label("Zu verteidigende Karten") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          createCardHBox(defendingCards),
          new Label("Deine Karten") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          createCardHBox(attackingCards),
          navigationAreaHBox()
        )
      }
    }

    def attackingTurnVBox(): VBox = {

      val attackingCards = List(Card(Rank.Four, Suit.Spades), Card(Rank.Five, Suit.Clubs), Card(Rank.Six, Suit.Diamonds))

      new VBox {
        spacing = 10
        alignment = Pos.Center
        style = "-fx-background-color: slategrey;  -fx-border-color: transparent white transparent transparent; -fx-border-width: 2px;"
        padding = Insets(40)
        children = Seq(
          new Label("Deine Karten") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
          },
          new Label("Mit welcher willst du angreifen?") {
            style = "-fx-font-size: 12pt; -fx-font-weight: bold;"
          },
          createCardHBox(attackingCards),
          navigationAreaHBox()
        )
      }
    }

    def infoAreaVBox(): VBox = {
      new VBox {
        spacing = 10
        padding = Insets(40)
        alignment = Pos.TopLeft
        style = "-fx-background-color: slategrey;"
        children = Seq(
          new Label("Spielinfo"){
            style = "-fx-font-size: 22pt; -fx-font-weight: bold;"},
          new Region,
          new Label("Trumpf:") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"},
          showTrumpCard(),
          new Label("Deckgröße:") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"},
          amountCardsInDeck(),
          new Label("Angreifer:") {
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"},
          showAttacker(),
          new Label("Verteidiger:"){
            style = "-fx-font-size: 16pt; -fx-font-weight: bold;"},
          showDefender()
        )
      }
    }

    gameScene = new BorderPane(){
      val turn = Turn.FirstlyAttacking // Placeholder for controller.status.turn
      center = turn match {
        case Turn.Defending => defendingTurnVBox()
        case Turn.FirstlyAttacking => attackingTurnVBox()
        case Turn.SecondlyAttacking => attackingTurnVBox()
        case _ => new VBox() // Default empty VBox or any other placeholder
      }
      right = infoAreaVBox()


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

    startMenuScene = new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: slategrey;"
      padding = Insets(40)
      children = Seq(
        logoDurak,
        new Label("Anzahl Spieler"){
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        playerCountComboBox,
        new Label("Spielernamen"){
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        playerNamesVBox,
        new Label("Kartenanzahl"){
          style = "-fx-font-size: 16pt; -fx-font-weight: bold;"
        },
        amountCards,
        new Region {
          prefHeight = 20
        },
        startButton
      )
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Durak"
      width = 400
      height = 600
      icons.add(new Image("file:src/main/resources/durak_logo.png"))
      scene = new Scene {
        root = startMenuScene
      }
    }

    continue()
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

  def initialize(): Unit = {}

  def chooseAttacking(): Unit = {}

  def ask(): Unit = {}
}