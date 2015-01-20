import sbt._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Dependencies {

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.6"

  val flow = "com.github.jodersky" %% "flow" % "2.0.9"
  val flowNative = "com.github.jodersky" % "flow-native" % "2.0.9"

  val bootstrap = "org.webjars" % "bootstrap" % "3.3.1"
  val fontawesome = "org.webjars" % "font-awesome" % "4.2.0"
  val jquery = "org.webjars" % "jquery" % "2.1.3"

  val dom = "org.scala-lang.modules.scalajs" %%%! "scalajs-dom" % "0.6"
  val tag = "com.scalatags" %%%! "scalatags" % "0.4.1"
  val rx = "com.scalarx" %%%! "scalarx" % "0.2.6"
  
}