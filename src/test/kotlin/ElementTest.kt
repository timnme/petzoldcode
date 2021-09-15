import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ElementTest {
    @Test
    fun tests() = runBlocking {
        ON assertOutputIs ON
        OFF assertOutputIs OFF

        BUF(ON) assertOutputIs ON
        BUF(OFF) assertOutputIs OFF

        INV(OFF) assertOutputIs ON
        INV(ON) assertOutputIs OFF

        AND(ON, ON) assertOutputIs ON
        AND(ON, OFF) assertOutputIs OFF
        AND(OFF, ON) assertOutputIs OFF
        AND(OFF, OFF) assertOutputIs OFF

        NAND(ON, ON) assertOutputIs OFF
        NAND(ON, OFF) assertOutputIs ON
        NAND(OFF, ON) assertOutputIs ON
        NAND(OFF, OFF) assertOutputIs ON

        OR(ON, ON) assertOutputIs ON
        OR(ON, OFF) assertOutputIs ON
        OR(OFF, ON) assertOutputIs ON
        OR(OFF, OFF) assertOutputIs OFF

        NOR(ON, ON) assertOutputIs OFF
        NOR(ON, OFF) assertOutputIs OFF
        NOR(OFF, ON) assertOutputIs OFF
        NOR(OFF, OFF) assertOutputIs ON

        XOR(ON, ON) assertOutputIs OFF
        XOR(ON, OFF) assertOutputIs ON
        XOR(OFF, ON) assertOutputIs ON
        XOR(OFF, OFF) assertOutputIs OFF

        XNOR(ON, ON) assertOutputIs ON
        XNOR(ON, OFF) assertOutputIs OFF
        XNOR(OFF, ON) assertOutputIs OFF
        XNOR(OFF, OFF) assertOutputIs ON
    }

    private suspend infix fun Element.assertOutputIs(expected: Signal) {
        assertEquals(expected, output())
    }
}