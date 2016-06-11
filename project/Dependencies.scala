package mavigator

import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {

  final val AkkaVersion = "2.4.7"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % AkkaVersion
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % AkkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion

  val reactiveStreams = "org.reactivestreams" % "reactive-streams" % "1.0.0"

  final val FlowVersion = "3.0.1"
  val flow = "ch.jodersky" %% "flow-core" % FlowVersion
  val flowNative = "ch.jodersky" % "flow-native" % FlowVersion % Runtime
  val flowStream = "ch.jodersky" %% "flow-stream" % FlowVersion

  val jsDom = Def.setting{"org.scala-js" %%% "scalajs-dom" % "0.9.0"}
  val scalatags = Def.setting{"com.lihaoyi" %%% "scalatags" % "0.5.4"}
  val scalarx = Def.setting{"com.lihaoyi" %%% "scalarx" % "0.3.1"}

}
