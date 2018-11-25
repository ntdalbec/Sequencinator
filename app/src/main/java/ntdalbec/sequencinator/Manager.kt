package ntdalbec.sequencinator

import android.provider.MediaStore
import java.lang.Exception

class Manager {
    private val channels = mutableListOf<Channel>()
    private val audioManager = AudioManager()

    var tempo = 120
        set(value) {
            if (value in 40..200) field = value
            else throw Exception("tempo must be between 40 and 200")
        }

    fun playTone(frequency: Int, duration: Int, wave: WaveForms.Wave) {
        val toneSize = (duration / 1000f * SAMPLE_RATE).toInt()
        val tone = ByteArray(toneSize) { wave(frequency, it) }
        audioManager.playByteArray(tone)
    }

    fun addNote(frequency: Int, duration: Int, chanIndex: Int = 0) {
        val note = Note(frequency, duration)
        channels[chanIndex].addNote(note)
    }

    fun play() {
        //TODO: use convolution instead of just using the first channel
        val toneData = channels[0].asByteArray()
        audioManager.playByteArray(toneData)
    }

    fun removeNoteAt(noteIndex: Int? = null, chanIndex: Int) = channels[chanIndex].removeNoteAt(noteIndex)

    fun addChannel(wave: WaveForms.Wave = WaveForms.Wave.SIN) = channels.add(Channel(wave))

    fun removeChannelAt(index: Int = channels.size - 1) = channels.removeAt(index)
}