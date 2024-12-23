import org.scoverage.coveralls.Imports.CoverallsKeys.coverallsService
import org.scoverage.coveralls.TravisPro

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "durak"
  )

coverageExcludedPackages := ".*prototype.*"

libraryDependencies += "org.apache.xmlgraphics" % "batik-all" % "1.18"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"


coverageEnabled := true
libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}
coverallsService := Some(TravisPro)
