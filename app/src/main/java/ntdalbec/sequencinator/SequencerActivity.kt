package ntdalbec.sequencinator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sequencer.*

class SequencerActivity : AppCompatActivity() {
    private val manager = Manager()
    private var currentDuration = Note.QUARTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequencer)

        manager.addChannel()

        c_key.setOnClickListener { addAndPlayNote(262) }
        d_key.setOnClickListener { addAndPlayNote(294) }
        e_key.setOnClickListener { addAndPlayNote(330) }
        f_key.setOnClickListener { addAndPlayNote(349) }
        g_key.setOnClickListener { addAndPlayNote(392) }
        a_key.setOnClickListener { addAndPlayNote(440) }
        b_key.setOnClickListener { addAndPlayNote(494) }

        whole_note_button.setOnClickListener { currentDuration = Note.WHOLE }
        half_note_button.setOnClickListener { currentDuration = Note.HALF }
        quarter_note_button.setOnClickListener { currentDuration = Note.QUARTER }
        eighth_note_button.setOnClickListener { currentDuration = Note.EIGHTH }
        sixteenth_note_button.setOnClickListener { currentDuration = Note.SIXTEENTH }

        play_button.setOnClickListener { manager.play() }
    }

    private fun addAndPlayNote(frequency: Int, chanIndex: Int = 0) {
        manager.playTone(frequency, currentDuration)
        manager.addNote(frequency, currentDuration, chanIndex)

        val noteText = TextView(this)
        noteText.text = "${frequency}hz $currentDuration"

        song_view.addView(noteText)
    }
}
