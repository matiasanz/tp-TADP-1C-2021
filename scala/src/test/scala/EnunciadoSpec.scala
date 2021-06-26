import Dominio.{ApuestaSimple, Distribuciones, Simulaciones}
import Juegos._
import org.scalactic.TripleEqualsSupport
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

class EnunciadoSpec extends AnyFreeSpec {

    val errorProbabilidad: Double = 0.01
    def be_aprox(exacto: Double) = ===(aprox(exacto))
    def aprox(exacto: Double): TripleEqualsSupport.Spread[Double] = exacto+-errorProbabilidad

    "Jugadas y Apuestas" - {
        "Tirar una moneda" - {
            "Crear una jugada a duplicar si sale cara" in {
                val montoPorResultado = JugadaMoneda(CARA).montoPorResultado(20, _)
                montoPorResultado(CARA) should be(40)
                montoPorResultado(CRUZ) should be(0)
            }

            "Combinar varias apuestas" - {
                val apuestaColor = ApuestaSimple(AColor(ROJO), 25)
                val apuestaDocena = ApuestaSimple(ADocena(2), 10)
                val apuestaNumero = ApuestaSimple(ANumero(23), 30)
                val apuestaCompuesta = apuestaColor.compuestaCon(apuestaDocena).compuestaCon(apuestaNumero)


                "combinadas si se cumple uno" in {
                    apuestaCompuesta.gananciaPorResultado(3) should be(50)
                }

                "combinadas si se cumplen dos de tres" in {
                    apuestaCompuesta.gananciaPorResultado(14) should be(80)
                }

                "combinadas si se cumplen todos" in {
                    apuestaCompuesta.gananciaPorResultado(23) should be(1160)
                }
            }
        }
    }

    "Resultados de los juegos" - {
        "Moneda comun tiene igual probabilidad de salir una u otra" in {
            MonedaComun.resultadosPosibles should contain only((CARA, 0.5), (CRUZ, 0.5))
        }

        "Moneda cargada solo para cara" in {
            val monedaCargada = MonedaCargada(Distribuciones.eventoSeguro(CARA))
            monedaCargada.probabilidadDe(CARA) should be(1)
            monedaCargada.probabilidadDe(CRUZ) should be(0)

            monedaCargada.resultadosPosibles should not contain(CRUZ)
        }

        "Distribuciones" - {
            "Ponderada" in {
                val (rdo1, rdo2) = (true, false)
                val sucesos: Map[Boolean, Double] = Map((rdo1, 2.0/3), (rdo2, 1.0/3))
                val ponderada = Distribuciones.ponderada(sucesos)

                ponderada(rdo1) should be_aprox(0.66)
                ponderada(rdo2) should be_aprox(0.33)
            }
        }
    }

    "Jugando un juego" - {
        "Distribucion de ganancias" - {
            "Por jugar con moneda comun" in {
                val apuesta = ApuestaSimple(JugadaMoneda(CARA), 30)
                MonedaComun.distribucionDeGananciasPor(apuesta) should contain only((60, .5), (0, .5))
            }

            "Por jugar a ruleta" in {
                val apuesta = ApuestaSimple(ANumero(1), 10)
                val distribucion = Ruleta.distribucionDeGananciasPor(apuesta)

                distribucion.size should be(2)
                distribucion(360) should be_aprox(0.027) //1.0/37
                distribucion(0) should be_aprox(0.972) //36.0/37
            }
        }
    }


}
