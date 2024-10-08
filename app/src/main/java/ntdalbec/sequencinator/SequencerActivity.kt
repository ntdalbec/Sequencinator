package ntdalbec.sequencinator

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_sequencer.*
import java.lang.ref.WeakReference
import java.util.*

class SequencerActivity : AppCompatActivity() {
    private lateinit var sequenceManager: SequenceManager
    private var currentDuration = Note.QUARTER
    private val STARTING_KEY = 35
    private val ENDING_KEY = 64


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_sequencer)

        val uuidString = intent.getStringExtra(SongAdapter.SONG_ID_EXTRA)
        val songId = UUID.fromString(uuidString)

        sequenceManager = SequenceManager(song_view, songId, application)

        if (savedInstanceState != null) {
            sequenceManager.loadChannelsFromBundle(savedInstanceState)
            sequenceManager.attachObserver()
        } else {
            LoadChannelsTask(sequenceManager, progressBar).execute()
        }

        attachKeyButtons(STARTING_KEY..ENDING_KEY, key_section)

        whole_note_button.setOnClickListener { changeDuration(Note.WHOLE, it) }
        half_note_button.setOnClickListener { changeDuration(Note.HALF, it) }
        quarter_note_button.setOnClickListener { changeDuration(Note.QUARTER, it) }
        eighth_note_button.setOnClickListener { changeDuration(Note.EIGHTH, it) }
        sixteenth_note_button.setOnClickListener { changeDuration(Note.SIXTEENTH, it) }

        whole_rest_button.setOnClickListener { sequenceManager.addNote(0, Note.WHOLE) }
        half_rest_button.setOnClickListener { sequenceManager.addNote(0, Note.HALF) }
        quarter_rest_button.setOnClickListener { sequenceManager.addNote(0, Note.QUARTER) }
        eighth_rest_button.setOnClickListener { sequenceManager.addNote(0, Note.EIGHTH) }
        sixteenth_rest_button.setOnClickListener { sequenceManager.addNote(0, Note.SIXTEENTH) }

        play_button.setOnClickListener { PlayTask(sequenceManager, progressBar).execute() }

        remove_button.setOnClickListener {
            if (sequenceManager.channelSize() != 0) {
                sequenceManager.removeNoteAt()
            }
        }

        song_view.startTone = STARTING_KEY
        song_view.endTone = ENDING_KEY
    }

    override fun onStop() {
        sequenceManager.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        sequenceManager.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        sequenceManager.putChannelsToBundle(outState)
        super.onSaveInstanceState(outState)
    }

    private fun attachKeyButtons(toneRange: IntRange, parent: ViewGroup) {
        for (tone in toneRange) {
            val button = layoutInflater.inflate(R.layout.key_button, parent,false) as Button
            button.text = getKeyNameFromTone(tone)
            button.setOnClickListener { oneKeyPress(tone) }
            parent.addView(button)
        }
    }

    private fun getKeyNameFromTone(tone: Int) : String {
        val key = when (tone % 12) {
            1    -> "A"
            2    -> "A#"
            3    -> "B"
            4    -> "C"
            5    -> "C#"
            6    -> "D"
            7    -> "D#"
            8    -> "E"
            9    -> "F"
            10   -> "F#"
            11   -> "G"
            else -> "G#"
        }

        val octave = (tone + 9) / 12

        return "$key$octave"
    }

    private fun oneKeyPress(tone: Int, chanIndex: Int = 0) {
        if (tone < song_view.startTone) {
            song_view.startTone = tone
        } else if (tone > song_view.endTone) {
            song_view.endTone = tone
        }

        sequenceManager.playNote(tone, currentDuration)
        sequenceManager.addNote(tone, currentDuration, chanIndex)
    }

    private fun pxToDp(px: Float) = px / resources.displayMetrics.density

    private fun changeDuration(duration: Int, view: View) {
        currentDuration = duration

        arrayOf(
            whole_note_button,
            half_note_button,
            quarter_note_button,
            eighth_note_button,
            sixteenth_note_button
        ).forEach { it.setBackgroundColor(0x00_00_00_00) }

        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
    }

    private class LoadChannelsTask(val sm: SequenceManager, progress: ProgressBar) : AsyncTask<Unit, Unit, Unit>() {
        val progRef = WeakReference(progress)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Unit?) {
            if (!sm.loadChannelsFromDb()) {
                sm.addChannel()
            }
        }

        override fun onPostExecute(result: Unit?) {
            progRef.get()?.visibility = View.INVISIBLE
            sm.attachObserver()
        }
    }

    private class PlayTask(val sm: SequenceManager, progress: ProgressBar) : AsyncTask<Unit, Unit, Unit>() {
        val progRef = WeakReference(progress)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Unit?) {
            sm.play()
        }

        override fun onPostExecute(result: Unit?) {
            progRef.get()?.visibility = View.INVISIBLE
        }
    }
}
