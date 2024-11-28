import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.tui.Tui
import view.{View, ViewCreator, ViewType}

class DurakSpec extends AnyWordSpec with Matchers {

  class MockView extends View {
    var started = false

    override def start(): Unit = started = true

    override def update(): Unit = {}
  }

  "Durak" should {
    "main(String)" should {
      "should use ViewCreator.apply(ViewType) as default" in {
        Durak.viewCreator.apply(ViewType.Tui) should be(Tui)
        Durak.viewCreator.apply(ViewType.Gui) should be(null)
      }

      "start the view" in {
        val viewCreator = Durak.viewCreator
        val mockView = MockView()
        
        try {
          Durak.viewCreator = _ => (step) => mockView
          
          Durak.main("", false)
        } finally {
          Durak.viewCreator = viewCreator
        }

        mockView.started should be(true)
      }

      "create the Gui if specified" in {
        val viewCreator = Durak.viewCreator
        val mockView = MockView()

        var outerViewType: ViewType = null

        try {
          Durak.viewCreator = innerViewType => {
            outerViewType = innerViewType
            (step) => mockView
          }

          Durak.main("gui", false)
        } finally {
          Durak.viewCreator = viewCreator
        }

        outerViewType should be(ViewType.Gui)
      }

      "create the Tui as default" in {
        val viewCreator = Durak.viewCreator
        val mockView = MockView()

        var outerViewType: ViewType = null
        
        try {
          Durak.viewCreator = innerViewType => {
            outerViewType = innerViewType
            (step) => mockView
          }

          Durak.main("", false)
        } finally {
          Durak.viewCreator = viewCreator
        }

        outerViewType should be(ViewType.Tui)
      }
    }
  }
}