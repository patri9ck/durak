import org.scoverage.coveralls.Imports.CoverallsKeys._
import org.scoverage.coveralls.TravisPro

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "durak"
  )

coverageExcludedPackages := ".*prototype.*"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"

coverallsService := Some(TravisPro)
