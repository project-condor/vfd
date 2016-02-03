package mavigator

import sbt._
import sbt.Keys._
import sbt.Project.projectToRef

object MavigatorBuild extends Build {

  // settings common to all projects
  val defaultSettings = Seq(
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-feature", "-deprecation")
  )

  // root super-project
  lazy val root = Project(
    id = "root",
    base = file("."),
    aggregate = Seq(bindings, uav, server, dashboard)
  )

  // empty project that uses SbtMavlink to generate protocol bindings
  lazy val bindings = Project(
    id = "mavigator-bindings",
    base = file("mavigator-bindings")
  )

  // main akka http server project
  lazy val server = Project(
    id = "mavigator-server",
    base = file("mavigator-server"),
    dependencies = Seq(bindings, uav)
  )

  // communication backend
  lazy val uav = Project(
    id = "mavigator-uav",
    base = file("mavigator-uav"),
    dependencies = Seq(bindings)
  )

  // main cockpit front-end
  lazy val dashboard = Project(
    id = "mavigator-dashboard",
    base = file("mavigator-dashboard"),
    dependencies = Seq(bindings)
  )

  /*
  // landing page providing selection of drone
  lazy val index = Project(
    id = "mavigator-index",
    base = file("mavigator-index"),
    dependencies = Seq(bindings)
  )
 */
}
