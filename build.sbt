

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "durak"
  )

libraryDependencies += "org.playframework" %% "play-json" % "3.0.4"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0"
libraryDependencies += "com.google.inject" % "guice" % "7.0.0"

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
libraryDependencies ++= {
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}

coverageEnabled := true
coverageExcludedFiles := ".*Gui"



