package ntdalbec.sequencinator

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.AsyncTask

class AudioManager {
    private val audioTrack: AudioTrack
    private var writeTask: WriteTask? = null
    private var nextBuf: ByteArray? = null

    fun playByteArray(buf: ByteArray) {
        if (writeTask == null) {
            writeTask = WriteTask().execute(buf) as WriteTask
        } else {
            audioTrack.pause()
            audioTrack.flush()
            audioTrack.play()
            nextBuf = buf
        }
    }

    fun onDestroy() {
        audioTrack.flush()
        audioTrack.release()
    }

    init {
        val minBuffer = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT )

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT,
            minBuffer,
            AudioTrack.MODE_STREAM
        )

        audioTrack.play()
    }

    private inner class WriteTask : AsyncTask<ByteArray, Unit, Unit>() {
        override fun doInBackground(vararg params: ByteArray?) {
            val buf = params.getOrNull(0) ?: return
            audioTrack.write(buf, 0, buf.size)
        }

        override fun onPostExecute(result: Unit?) {
            if (nextBuf == null) {
                writeTask = null
            } else {
                writeTask = WriteTask().execute(nextBuf) as WriteTask
                nextBuf = null
            }
            super.onPostExecute(result)
        }

    }
}