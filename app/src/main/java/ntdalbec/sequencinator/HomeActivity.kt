package ntdalbec.sequencinator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var db: SequencerStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseManager.getInstance(application)

        val viewManager = LinearLayoutManager(this)
        val songAdapter = SongAdapter(db.songs)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = songAdapter
        }

        floatingActionButton.setOnClickListener {
            val id = db
                .addSong("test name")
                .uid
                .toString()
            val intent = Intent(this, SequencerActivity::class.java)
            intent.putExtra(SongAdapter.SONG_ID_EXTRA, id)
            startActivity(intent)
        }
    }
}
