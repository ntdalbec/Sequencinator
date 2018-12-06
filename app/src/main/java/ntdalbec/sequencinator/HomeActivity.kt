package ntdalbec.sequencinator

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.ref.WeakReference

class HomeActivity : AppCompatActivity() {
    lateinit var db: SequencerStore
    val songs = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseManager.getInstance(application)

        val viewManager = LinearLayoutManager(this)
        val songAdapter = SongAdapter(songs, this::deleteSong, this::updateSong)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = songAdapter
        }

        LoadSongsTask(db, songs, songAdapter, progressBar).execute()

        floatingActionButton.setOnClickListener {
            CreateSongTask(db, this, progressBar, songAdapter).execute(getDefaultSongName())
        }
    }

    private fun getDefaultSongName(): String {
        var suffix = ""
        var suffixCount = 1
        var match: String? = null
        while(match == null) {
            val candidate = "$DEFAULT_SONG_NAME$suffix"

            match = if (songs.any { it.name == candidate })
                null
            else
                candidate

            suffix = "(${suffixCount++})"
        }
        return match
    }

    companion object {
        const val DEFAULT_SONG_NAME = "New song"
    }

    private fun deleteSong(song: Song) {
        Thread {
            db.deleteSong(song)
        }.start()
    }

    private fun updateSong(song: Song) {
        Thread {
            db.updateSong(song)
        }.start()
    }

    private class LoadSongsTask(val db: SequencerStore, val songs: MutableList<Song>, adapter: SongAdapter, progress: ProgressBar) : AsyncTask<Unit, Unit, Array<Song>>() {
        val adapterRef = WeakReference(adapter)
        val progRef = WeakReference(progress)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Unit?): Array<Song> {
            return db.songs
        }

        override fun onPostExecute(result: Array<Song>?) {
            progRef.get()?.visibility = View.INVISIBLE
            result?.also {
                songs.addAll(it)
                adapterRef.get()?.notifyDataSetChanged()
            }
        }
    }

    private class CreateSongTask(val db: SequencerStore, activity: HomeActivity, progress: ProgressBar, adapter: SongAdapter) : AsyncTask<String, Unit, Song>() {
        val activityRef = WeakReference(activity)
        val progRef = WeakReference(progress)
        val adapterRef = WeakReference(adapter)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): Song {
            return db.addSong(params[0])
        }

        override fun onPostExecute(result: Song?) {
            progRef.get()?.visibility = View.INVISIBLE
            result?.let {
                val activity = activityRef.get() ?: return
                activity.songs.add(it)
                adapterRef.get()?.apply {
                    notifyItemInserted(itemCount - 1)
                }
                val intent = Intent(activity, SequencerActivity::class.java)
                intent.putExtra(SongAdapter.SONG_ID_EXTRA, it.uid)
                activity.startActivity(intent)
            }
        }
    }
}
