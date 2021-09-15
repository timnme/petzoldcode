class Adder {
    companion object {
        private const val BITNESS = 8
    }

    private data class R(
        val s: Signal, // sum
        val co: Signal, // carry out
    )

    /**
     * [op]: [OFF] - addition, [ON] - subtraction
     */
    suspend fun op(a: Int, b: Int, op: Signal): Int {
        val aBinary = a.toString(2)
        val bBinary = b.toString(2)
        val length = maxOf(aBinary.length, bBinary.length)
        if (length > BITNESS)
            throw IllegalArgumentException("Cannot operate on $length bit long numbers")

        fun String.norm(): Array<Signal> = when {
            this.length < BITNESS -> CharArray(BITNESS - this.length) { '0' } + toCharArray()
            else -> toCharArray()
        }
            .map {
                when (it) {
                    '0' -> OFF
                    '1' -> ON
                    else -> throw IllegalArgumentException("Must be '0' or '1' but was '$this'")
                }
            }
            .toTypedArray()

        return op(
            a = aBinary.norm(),
            b = bBinary.norm(),
            op = op,
        )
            .map {
                when (it) {
                    OFF -> '0'
                    ON -> '1'
                }
            }
            .joinToString("")
            .toInt(2)
    }

    private suspend fun op(a: Array<Signal>, b: Array<Signal>, op: Signal): Array<Signal> {
        val bInverted: Array<Signal> = b.map {
            // Inverted on subtraction (complement to one)
            XOR(op, it).output()
        }.toTypedArray()
        val result = Array<Signal>(BITNESS) { OFF }
        var carryIn: Signal = op
        for (i in (0 until BITNESS).reversed()) {
            val r: R = fullAdd(a[i], bInverted[i], carryIn)
            carryIn = r.co
            result[i] = r.s
        }
        return when (XOR(op, carryIn).output()) {
            ON -> throw IllegalStateException(
                when (op) {
                    ON -> "UNDERFLOW!"
                    OFF -> "OVERFLOW!"
                }
            )
            OFF -> result
        }
    }

    private suspend fun fullAdd(
        a: Signal, // first
        b: Signal, // second
        ci: Signal, // carry in
    ): R {
        val r1 = halfAdd(a, b)
        val r2 = halfAdd(ci, r1.s)
        return R(
            s = r2.s,
            co = OR(r1.co, r2.co).output(),
        )
    }

    private suspend fun halfAdd(
        a: Signal,
        b: Signal,
    ): R {
        return R(
            s = XOR(a, b).output(),
            co = AND(a, b).output(),
        )
    }
}
