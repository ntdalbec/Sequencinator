package ntdalbec.sequencinator

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_sequencer.*

class SequencerActivity : AppCompatActivity() {
    private var manager: Manager? = null
    private var currentDuration = Note.QUARTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_sequencer)

        manager = Manager(song_view)

        if (savedInstanceState != null) {
            manager!!.getChannels(savedInstanceState, song_view)
        } else {
            manager!!.addChannel()
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

        whole_rest_button.setOnClickListener { manager!!.addNote(0, Note.WHOLE) }
        half_rest_button.setOnClickListener { manager!!.addNote(0, Note.HALF) }
        quarter_rest_button.setOnClickListener { manager!!.addNote(0, Note.QUARTER) }
        eighth_rest_button.setOnClickListener { manager!!.addNote(0, Note.EIGHTH) }
        sixteenth_rest_button.setOnClickListener { manager!!.addNote(0, Note.SIXTEENTH) }

        play_button.setOnClickListener { manager!!.play() }

        remove_button.setOnClickListener {
            if (manager!!.channelSize() != 0) {
                manager!!.removeNoteAt()
            }
        }

        song_view.startTone = 40
        song_view.endTone = 51
    }



    override fun onDestroy() {
        manager?.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.let {
            manager?.putChannels(it)
        }
        super.onSaveInstanceState(outState)
    }

    private fun addAndPlayNote(tone: Int, chanIndex: Int = 0) {
        manager!!.playNote(tone, currentDuration)
        manager!!.addNote(tone, currentDuration, chanIndex)
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

}
