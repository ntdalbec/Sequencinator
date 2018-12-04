package ntdalbec.sequencinator

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class SongAdapter(private val data: Array<Song>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SongViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.song_view, parent, false) as TextView
        return SongViewHolder(textView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SongViewHolder, pos: Int) {
        holder.tv.text = data[pos].name
        holder.tv.setOnClickListener {
            val intent = Intent(holder.tv.context, SequencerActivity::class.java)
            intent.putExtra(SONG_ID_EXTRA, data[pos].uid.toString())
            startActivity(holder.tv.context, intent, null)
        }
    }

    companion object {
        const val SONG_ID_EXTRA = "song_id_extra"
    }

    class SongViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)
}