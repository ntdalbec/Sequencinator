package ntdalbec.sequencinator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sequencer.*

class SequencerActivity : AppCompatActivity() {
    private var manager: Manager? = null
    private var currentDuration = Note.QUARTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequencer)

        manager = Manager(song_view)

        if (savedInstanceState != null) {
            manager!!.getChannels(savedInstanceState, song_view)
        } else {
            manager!!.addChannel()
        }

        c_key.setOnClickListener { addAndPlayNote(40) }
        d_key.setOnClickListener { addAndPlayNote(42) }
        e_key.setOnClickListener { addAndPlayNote(44) }
        f_key.setOnClickListener { addAndPlayNote(45) }
        g_key.setOnClickListener { addAndPlayNote(47) }
        a_key.setOnClickListener { addAndPlayNote(49) }
        b_key.setOnClickListener { addAndPlayNote(51) }

        whole_note_button.setOnClickListener { currentDuration = Note.WHOLE }
        half_note_button.setOnClickListener { currentDuration = Note.HALF }
        quarter_note_button.setOnClickListener { currentDuration = Note.QUARTER }
        eighth_note_button.setOnClickListener { currentDuration = Note.EIGHTH }
        sixteenth_note_button.setOnClickListener { currentDuration = Note.SIXTEENTH }

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

}
