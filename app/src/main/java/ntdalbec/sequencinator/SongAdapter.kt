package ntdalbec.sequencinator

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*

class SongAdapter( private val data: MutableList<Song>,
                   private val onSongDelete: (Song) -> Unit,
                   private val onSongUpdate: (Song) -> Unit )
    : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SongViewHolder {
        val songView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.song_view, parent, false) as LinearLayout
        return SongViewHolder(songView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SongViewHolder, pos: Int) {
        val nameText = holder.rootLayout.findViewById<TextView>(R.id.nameText)
        val nameEdit = holder.rootLayout.findViewById<EditText>(R.id.nameEdit)
        val deleteButton = holder.rootLayout.findViewById<ImageButton>(R.id.deleteButton)
        val renameButton = holder.rootLayout.findViewById<ImageButton>(R.id.renameButton)
        val nameSwitcher = holder.rootLayout.findViewById<ViewSwitcher>(R.id.nameSwitcher)
        val song = data[pos]

        nameText.text = song.name
        nameText.setOnClickListener {
            val intent = Intent(it.context, SequencerActivity::class.java)
            intent.putExtra(SONG_ID_EXTRA, song.uid)
            startActivity(it.context, intent, null)
        }

        deleteButton.setOnClickListener {
            onSongDelete(song)
            val currentPos = data.indexOf(song)
            data.removeAt(currentPos)
            notifyItemRemoved(currentPos)
        }

        renameButton.setOnClickListener {
            nameSwitcher.showNext()
            nameEdit.setText(nameText.text)
        }

        nameEdit.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newName = nameEdit.text
                v.clearFocus()
                song.name = newName.toString()
                onSongUpdate(song)
                nameSwitcher.showNext()
                nameText.text = newName
            }
            false
        }
    }

    companion object {
        const val SONG_ID_EXTRA = "song_id_extra"
    }

    class SongViewHolder(val rootLayout: LinearLayout) : RecyclerView.ViewHolder(rootLayout)
}