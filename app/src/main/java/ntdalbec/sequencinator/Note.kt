package ntdalbec.sequencinator

import java.util.*

data class Note (
    val frequency: Int,
    val duration: Int
    ){
    companion object {
        const val WHOLE = 16
        const val HALF = 8
        const val QUARTER = 4
        const val EIGHTH = 2
        const val SIXTEENTH = 1
    }
}