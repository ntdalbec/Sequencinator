package ntdalbec.sequencinator

import android.app.Application
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_sequencer.*
import java.lang.ref.WeakReference
import java.util.*

class SequencerActivity : AppCompatActivity() {
    private lateinit var sequenceManager: SequenceManager
    private var currentDuration = Note.QUARTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

        c_key.setOnClickListener { addAndPlayNote(40) }
        c_sharp_key.setOnClickListener { addAndPlayNote(41) }
        d_key.setOnClickListener { addAndPlayNote(42) }
        d_sharp_key.setOnClickListener { addAndPlayNote(43) }
        e_key.setOnClickListener { addAndPlayNote(44) }
        f_key.setOnClickListener { addAndPlayNote(45) }
        f_sharp_key.setOnClickListener { addAndPlayNote(46) }
        g_key.setOnClickListener { addAndPlayNote(47) }
        g_sharp_key.setOnClickListener { addAndPlayNote(48) }
        a_key.setOnClickListener { addAndPlayNote(49) }
        a_sharp_key.setOnClickListener { addAndPlayNote(50) }
        b_key.setOnClickListener { addAndPlayNote(51) }

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

        play_button.setOnClickListener { sequenceManager.play() }

        remove_button.setOnClickListener {
            if (sequenceManager.channelSize() != 0) {
                sequenceManager.removeNoteAt()
            }
        }

        song_view.startTone = 40
        song_view.endTone = 51
    }

    override fun onDestroy() {
        sequenceManager.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        sequenceManager.putChannelsToBundle(outState)
        super.onSaveInstanceState(outState)
    }

    private fun addAndPlayNote(tone: Int, chanIndex: Int = 0) {
        sequenceManager.playNote(tone, currentDuration)
        sequenceManager.addNote(tone, currentDuration, chanIndex)
    }

    private fun changeDuration(duration: Int, view: View) {
        currentDuration = duration

        arrayOf(
            whole_note_button,
            half_note_button,
            quarter_note_button,
            eighth_note_button,
            sixteenth_note_button
        ).forEach { it.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)) }

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
}
