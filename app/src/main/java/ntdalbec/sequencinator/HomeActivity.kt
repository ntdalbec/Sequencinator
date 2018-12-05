package ntdalbec.sequencinator

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.ref.WeakReference

class HomeActivity : AppCompatActivity() {
    lateinit var db: SequencerStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseManager.getInstance(application)

        val viewManager = LinearLayoutManager(this)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        LoadSongsTask(db, this::deleteSong, recyclerView, progressBar).execute()

        floatingActionButton.setOnClickListener { CreateSongTask(db, this, progressBar).execute("test name") }
    }

    fun deleteSong(song: Song) {
        Thread {
            db.deleteSong(song)
        }.start()
    }

    private class LoadSongsTask(val db: SequencerStore, val onDeleteSong: (Song) -> Unit, rec: RecyclerView, progress: ProgressBar) : AsyncTask<Unit, Unit, Array<Song>>() {
        val recRef = WeakReference(rec)
        val progRef = WeakReference(progress)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Unit?): Array<Song> {
            return db.songs
        }

        override fun onPostExecute(result: Array<Song>?) {
            progRef.get()?.visibility = View.INVISIBLE
            result?.let {
                val songAdapter = SongAdapter(it.toMutableList(), onDeleteSong)
                recRef.get()?.adapter = songAdapter
            }
        }
    }

    private class CreateSongTask(val db: SequencerStore, context: Context, progress: ProgressBar) : AsyncTask<String, Unit, String>() {
        val contextRef = WeakReference(context)
        val progRef = WeakReference(progress)

        override fun onPreExecute() {
            progRef.get()?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String {
            return db.addSong(params[0]).uid
        }

        override fun onPostExecute(result: String?) {
            progRef.get()?.visibility = View.INVISIBLE
            result?.let {
                val context = contextRef.get() ?: return
                val intent = Intent(context, SequencerActivity::class.java)
                intent.putExtra(SongAdapter.SONG_ID_EXTRA, it)
                context.startActivity(intent)
            }
        }
    }
}
