package ntdalbec.sequencinator

import android.os.Bundle
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class Manager(private val channelObserver: Observer) {
    private val channels = mutableListOf<Channel>()
    private val audioManager = AudioManager()

    var tempo = 120
        set(value) {
            if (value in 40..200) field = value
            else throw Exception("tempo must be between 40 and 200")
        }

    fun playNote(tone: Int, duration: Int, wave: WaveForms.Wave = WaveForms.Wave.SIN) {
        val note = Note(tone, duration)
        val noteData = note.asByteArray(tempo, wave)
        audioManager.playByteArray(noteData)
    }

    fun addNote(tone: Int, duration: Int, chanIndex: Int = 0) {
        val note = Note(tone, duration)
        channels[chanIndex].addNote(note)
    }

    fun play() {
        //TODO: use convolution instead of just using the first channel
        val channelData = channels[0].asByteArray(tempo)
        audioManager.playByteArray(channelData)
    }

    fun channelSize(chanIndex: Int = 0) = channels[chanIndex].size

    fun removeNoteAt(noteIndex: Int? = null, chanIndex: Int = 0) = channels[chanIndex].removeNoteAt(noteIndex)

    fun addChannel(wave: WaveForms.Wave = WaveForms.Wave.SIN) {
        val channel = Channel(wave)
        channel.addObserver(channelObserver)
        channels.add(channel)
    }

    fun removeChannelAt(index: Int = channels.size - 1) = channels.removeAt(index)

    fun putChannels(outBundle: Bundle) {
        outBundle.putParcelableArrayList("channels", ArrayList(channels))
    }

    fun getChannels(bundle: Bundle, o: Observer) {
        val chans = bundle.getParcelableArrayList<Channel>("channels")
        chans.forEach {
            it.addObserver(o)
            it.pubNotesChange()
        }
        channels.addAll(chans)
    }

    fun onDestroy() {
        audioManager.onDestroy()
    }
}