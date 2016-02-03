package mavigator.dashboard.ui

import rx._
import scalatags.JsDom.all._
import mavigator.util.Environment
import mavigator.dashboard.MavlinkSocket
import mavigator.dashboard.ui.instruments._
import org.mavlink.messages._
import mavigator.dashboard.rxutil._

class Layout(socket: MavlinkSocket)(implicit env: Environment) {

  private def panel(contents: Frag*) = div(`class` := "d-panel")(contents: _*)

  private def mode(name: String, kind: String, on: Boolean = false) = div(`class` := s"mode $kind ${if (!on) "off"}")(name)

  val modes = div(
    mode("MANUAL", "warning", true),
    mode("STABILIZED", "info", true),
    mode("GUIDED", "success", true),
    mode("AUTO", "success", true))

  val infos = div(
    mode("BAY", "info"),
    mode("RECOVERY", "danger"),
    mode("NOGPS", "warning", true),
    mode("OVERLOAD", "danger", true),
    mode("BATTERY", "danger", false),
    mode("LINK", "danger", true),
    mode("SOCKET", "danger", true),
    div(style := "float: right")(mode("CRITICAL", "danger", true)))

  val map = iframe(
    "frameborder".attr := "0",
    "scrolling".attr := "no",
    "marginheight".attr := "0",
    "marginwidth".attr := "0",
    src := "http://www.openstreetmap.org/export/embed.html?bbox=6.5611016750335684%2C46.51718501017836%2C6.570038795471191%2C46.520577350893525&amp;layer=mapnik")

  val feed = div(style := "width: 100%; height: 100%; color: #ffffff; background-color: #c2c2c2; text-align: center;")

  val altimeter = new Altimeter(
    Var(0.0)
  )
  val horizon = new Horizon(socket.message.collect((0.0, 0.0)) {
    case att: Attitude => (att.pitch, att.roll)
  })
  val compass = new Compass(socket.message.collect(0.0) {
    case att: Attitude => att.yaw
  })
  val motor0 = new Generic(0, 50, 100, "%", socket.message.collect(0.0) {
    case s: ServoOutputRaw => 100 * (s.servo1Raw - 1000) / 1000
  })
  val motor1 = new Generic(0, 50, 100, "%", socket.message.collect(0.0) {
    case s: ServoOutputRaw => 100 * (s.servo2Raw - 1000) / 1000
  })
  val motor2 = new Generic(0, 50, 100, "%", socket.message.collect(0.0) {
    case s: ServoOutputRaw => 100 * (s.servo3Raw - 1000) / 1000
  })
  val motor3 = new Generic(0, 50, 100, "%", socket.message.collect(0.0) {
    case s: ServoOutputRaw => 100 * (s.servo4Raw - 1000) / 1000
  })
  val powerDistribution = new Distribution(
    socket.message.collect((0.0, 0.0, 0.0, 0.0)) {
      case s: ServoOutputRaw =>
        (
          1.0 * (s.servo1Raw - 1000) / 1000,
          1.0 * (s.servo2Raw - 1000) / 1000,
          1.0 * (s.servo3Raw - 1000) / 1000,
          1.0 * (s.servo4Raw - 1000) / 1000
        )
    }
  )
  val batteryLevel = new Bar(
    Var(0.0)
  )

  val top = header(
    div("Flight Control Panel"),
    div((new Clock).element),
    div("UAV " + socket.remoteSystemId)
  )

  val left = div(
    panel(
      table(`class` := "table-instrument")(
        thead("Communication"),
        tbody(
          tr(
            td("Uplink RSSI"),
            td("89"),
            td("Socket"),
            td("5ms")
          ),
          tr(
            td("Something else"),
            td("unknown"),
            td("Heartbeat"),
            td(i(`class` := "fa fa-heart heartbeat"))
          )
        )
      ),
      table(`class` := "table-instrument")(
        thead("Packets"),
        tbody(
          tr(
            td("OK"),
            Rx { td(socket.stats.packets()) },
            td("CRC"),
            Rx { td(socket.stats.crcErrors()) },
            td("OFLW"),
            Rx { td(socket.stats.overflows()) },
            td("BID"),
            Rx { td(socket.stats.wrongIds()) }
          ),
          tr(
            td("Ratio"),
            Rx {
              import socket.stats._
              val sum = packets() + crcErrors() + overflows() + wrongIds()
              td(1.0 * packets() / sum formatted "%.2f")
            },
            td(),
            td(),
            td(),
            td(),
            td(),
            td()
          )
        )
      )
    ),
    panel(
      table(`class` := "table-instrument")(
        tbody(
          tr(
            td(compass.element),
            td(horizon.element),
            td(altimeter.element),
            td(altimeter.element)
          )
        )
      )
    ),
    panel(
      div(style := "width: 50%; display: inline-block;")(
        table(`class` := "table-instrument")(
          tbody(
            tr(
              td(motor1.element),
              td(),
              td(motor0.element)
            ),
            tr(
              td(),
              td(powerDistribution.element),
              td()
            ),
            tr(
              td(motor2.element),
              td(),
              td(motor3.element)
            )
          )
        )
      ),
      div(style := "width: 50%; display: inline-block;")(
        table(`class` := "table-instrument")(
          thead("Power"),
          tbody(
            tr(
              td("VHIGH"),
              td("12.6V"),
              td("VLOW"),
              td("9V")
            ),
            tr(
              td("Voltage"),
              td("11.2V"),
              td("Remaining"),
              td("80%")
            ),
            tr(
              td("Flight"),
              td("05:00"),
              td("Endurance"),
              td("12:00")
            )
          )
        ),
        table(`class` := "table-instrument")(
          thead("Navigation"),
          tbody(
            tr(
              td("Satellites"),
              td("5"),
              td("Precision"),
              td("10cm")
            ),
            tr(
              td("LON"),
              td(""),
              td("LAT"),
              td("")
            ),
            tr(
              td("GSpeed"),
              td("3 m/s"),
              td(),
              td()
            ),
            tr(
              td("Travelled"),
              td("5000m"),
              td("Home"),
              td("1200m")
            )
          )
        )
      )
    )
  )



  val hud = {
    def overlay(name: String, z: Int, thinnerThanWide: Boolean = false) = {
      val direction = if (thinnerThanWide) "row" else "column"
      div(
        style:=
          "position: absolute; left: 0; right: 0; top: 0; bottom: 0;" +
          s"display: flex; align-content: center; align-items: stretch; flex-direction: $direction;"+
          s"z-index: $z;"
      )(
       `object`(`type`:="image/svg+xml", "data".attr:=env.asset("images/hud/" + name + ".svg"), style := "flex: 1 1 100%;")
      )

    }

    Seq(
      overlay("horizon", 0),
      overlay("roll", 1)
    )
  }



  val element = div(`class` := "d-container d-column", style:="width: 100%; height: 100%;")(
    div(`class` := "d-above")(
      top
    ),
    div(`class` := "d-above d-container d-row")(
      panel(modes),
      panel(infos)
    ),
    div(`class` := "d-container d-row")(
      div(`class` := "d-container d-left")(
        left
      ),
      div(`class` := "d-main", style:="position: relative;")(
        (new Hud(horizon.value)).element
      )
    )
  ).render

}
