package ntdalbec.sequencinator

import android.util.Log
import java.lang.Exception

class Manager {
    private val channels = mutableListOf<Channel>()
    private val audioManager = AudioManager()

    var tempo = 120
        set(value) {
            if (value in 40..200) field = value
            else throw Exception("tempo must be between 40 and 200")
        }

    fun playTone(frequency: Int, duration: Int, wave: WaveForms.Wave = WaveForms.Wave.SIN) {
        val note = Note(frequency, duration)
        val tone = note.asByteArray(tempo, wave)
        Log.i(LOG_TAG, "random tone len: ${tone.size}")
        audioManager.playByteArray(tone)
    }

    fun addNote(frequency: Int, duration: Int, chanIndex: Int = 0) {
        val note = Note(frequency, duration)
        channels[chanIndex].addNote(note)
    }

    fun play() {
        //TODO: use convolution instead of just using the first channel
        val toneData = channels[0].asByteArray(tempo)
        Log.i(LOG_TAG, "Total data len: ${toneData.size}")
        audioManager.playByteArray(toneData)
    }

    fun removeNoteAt(noteIndex: Int? = null, chanIndex: Int = 0) = channels[chanIndex].removeNoteAt(noteIndex)

    fun addChannel(wave: WaveForms.Wave = WaveForms.Wave.SIN) = channels.add(Channel(wave))

    fun removeChannelAt(index: Int = channels.size - 1) = channels.removeAt(index)
}