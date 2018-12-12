package ntdalbec.sequencinator

import android.app.Application
import android.os.Bundle
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class SequenceManager(
    private val channelObserver: Observer,
    private val songId: UUID,
    appContext: Application ) {

    private val channels = mutableListOf<Channel>()
    private val audioManager = AudioManager()
    private val db: SequencerStore

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
        channels.getOrNull(chanIndex)?.addNote(note)
    }

    fun play() {
        //TODO: use convolution instead of just using the first channel
        channels.getOrNull(0)?.let {
            val channelData = it.asByteArray(tempo)
            audioManager.playByteArray(channelData)
        }
    }

    fun channelSize(chanIndex: Int = 0) = channels.getOrNull(chanIndex)?.size ?: 0

    fun removeNoteAt(noteIndex: Int? = null, chanIndex: Int = 0) = channels.getOrNull(chanIndex)?.removeNoteAt(noteIndex)

    fun addChannel(wave: WaveForms.Wave = WaveForms.Wave.SIN) {
        val channel = Channel(wave)
        channel.addObserver(channelObserver)
        db.addChannel(channel, songId)
        channels.add(channel)
    }

    fun removeChannelAt(index: Int = channels.size - 1) {
        val channel = channels.removeAt(index)
        db.deleteChannel(channel, songId)
    }

    fun pubChannelChanges() = channels.forEach { it.pubNotesChange() }

    fun putChannelsToBundle(outBundle: Bundle) {
        outBundle.putParcelableArrayList("channels", ArrayList(channels))
    }

    fun loadChannelsFromBundle(bundle: Bundle) {
        val chans = bundle.getParcelableArrayList<Channel>("channels") ?: return
        channels.addAll(chans)
    }

    fun loadChannelsFromDb(): Boolean {
        val chans = db.getChannelsBySong(songId)
        return channels.addAll(chans)
    }

    fun attachObserver() {
        channels.forEach {
            it.deleteObservers()
            it.addObserver(channelObserver)
            it.pubNotesChange()
        }
    }

    fun onStop() {
        Thread {
            channels.forEach { db.updateChannel(it, songId) }
        }.start()
    }

    fun onDestroy() {
        audioManager.onDestroy()
    }

    init {
        db = DatabaseManager.getInstance(appContext)
    }
}