package ntdalbec.sequencinator

import android.util.Log


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

    fun getTimingInms(tempo: Int) : Int {
        val timing = (60f / tempo * 1000 * (duration / 4f)).toInt()
        Log.i(LOG_TAG, "note timing: $timing")
        return timing
    }

    fun asByteArray(tempo: Int, wave: WaveForms.Wave) : ByteArray {
        val size = (getTimingInms(tempo) / 1000f * SAMPLE_RATE).toInt()
        return ByteArray(size) { wave(frequency, it) }
    }
}