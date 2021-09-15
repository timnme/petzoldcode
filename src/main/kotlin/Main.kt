import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    OR(
        AND(
            ON,
            OR(
                AND(
                    OFF,
                    OR(
                        OFF,
                        OFF,
                    ),
                ),
                AND(
                    ON,
                    INV(
                        OFF,
                    ),
                ),
            ),
        ),
        OFF,
    ).display()

    val adder = Adder()

    suspend fun addAndPrint(a: Int, b: Int, op: Signal) = println(
        "$a ${
            when (op) {
                ON -> "-"
                OFF -> "+"
            }
        } $b = ${
            try {
                adder.op(a = a, b = b, op = op)
            } catch (e: Exception) {
                e.message
            }
        }"
    )

    addAndPrint(0, 0, OFF)
    addAndPrint(0, 2, OFF)
    addAndPrint(2, 2, OFF)
    addAndPrint(254, 1, OFF)
    addAndPrint(254, 2, OFF)
    println()
    addAndPrint(2, 1, ON)
    addAndPrint(33, 12, ON)
    addAndPrint(0, 1, ON)
    addAndPrint(1, 22, ON)
    println()
    addAndPrint(256, 22, OFF)
    addAndPrint(1, 1000, ON)
    println()
    addAndPrint(-1, 1, OFF)
}

