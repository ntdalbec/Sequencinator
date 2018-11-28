package ntdalbec.sequencinator

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class AudioManager {
    //TODO:  The work should be handled off the main thread
    private val audioTrack: AudioTrack

    fun playByteArray(buf: ByteArray) {
        audioTrack.write(buf, 0, buf.size)
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
}