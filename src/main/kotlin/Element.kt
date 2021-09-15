object NonReachableException : Exception("YOU DIED")

interface Element {
    suspend fun output(): Signal

    suspend fun display() {
        println(output())
    }
}

sealed class Signal : Element {
    override fun toString(): String = when (this) {
        OFF -> "OFF"
        ON -> "ON"
    }
}

object ON : Signal() {
    override suspend fun output(): Signal = ON
}

object OFF : Signal() {
    override suspend fun output(): Signal = OFF
}

class BUF(
    private val input: Element,
) : Element {
    override suspend fun output(): Signal = input.output()
}

class INV(
    private val input: Element,
) : Element {
    override suspend fun output(): Signal = when (input.output()) {
        ON -> OFF
        OFF -> ON
    }
}

abstract class Gate(
    input: Array<out Element>,
) : Element {
    init {
        val inputSize = input.size
        if (inputSize < 2) throw Exception("Input count must be greater than 1 but was $inputSize")
    }

    protected class Relay(
        private val v: Signal,
        private val input: Signal,
    ) : Element {
        companion object {
            suspend fun of(input: Element) = Relay(ON, input.output())
        }

        override suspend fun output(): Signal =
            if (v.output() is ON && input.output() is ON) ON
            else OFF

        suspend fun series(next: Element): Relay {
            return Relay(output(), next.output())
        }

        suspend fun parallel(next: Element): Relay {
            return of(next)
        }
    }
}

class AND(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal {
        var cR = Relay.of(input[0])
        var cO = cR.output()
        for (i in 1..input.lastIndex) {
            val nR = cR.series(input[i])
            val nO = nR.output()
            cO = when {
                cO is ON && nO is ON -> ON
                cO is ON && nO is OFF -> OFF
                cO is OFF && nO is ON -> OFF
                cO is OFF && nO is OFF -> OFF
                else -> throw NonReachableException
            }
            cR = nR
        }
        return cO
    }
}

class NAND(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal = INV(AND(*input)).output()
}

class OR(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal {
        var cR = Relay.of(input[0])
        var cO = cR.output()
        for (i in 1..input.lastIndex) {
            val nR = cR.parallel(input[i])
            val nO = nR.output()
            cO = when {
                cO is ON && nO is ON -> ON
                cO is ON && nO is OFF -> ON
                cO is OFF && nO is ON -> ON
                cO is OFF && nO is OFF -> OFF
                else -> throw NonReachableException
            }
            cR = nR
        }
        return cO
    }
}

class NOR(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal = INV(OR(*input)).output()
}

class XOR(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal = AND(OR(*input), NAND(*input)).output()
}

class XNOR(
    private vararg val input: Element,
) : Gate(input) {
    override suspend fun output(): Signal = INV(XOR(*input)).output()
}

// Double output
interface DOElement : Element {
    suspend fun output2(): Signal
}

class Oscillator {

}

class Trigger(
    data: Element,
    clock: Element,
) : DOElement {
    override suspend fun output(): Signal {
        TODO("Not yet implemented")
    }

    override suspend fun output2(): Signal {
        return INV(output()).output()
    }
}