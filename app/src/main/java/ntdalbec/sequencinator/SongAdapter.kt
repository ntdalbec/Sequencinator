package ntdalbec.sequencinator

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class SongAdapter( private val data: MutableList<Song>, private val onSongDelete: (Song) -> Unit)
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
        val deleteButton = holder.rootLayout.findViewById<ImageButton>(R.id.deleteButton)
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
    }

    companion object {
        const val SONG_ID_EXTRA = "song_id_extra"
    }

    class SongViewHolder(val rootLayout: LinearLayout) : RecyclerView.ViewHolder(rootLayout)
}