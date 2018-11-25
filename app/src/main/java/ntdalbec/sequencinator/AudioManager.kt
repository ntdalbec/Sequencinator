package ntdalbec.sequencinator

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class AudioManager {
    fun playByteArray(buf: ByteArray) {
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_DEFAULT,
            AudioFormat.ENCODING_PCM_8BIT,
            buf.size,
            AudioTrack.MODE_STATIC
        )

        audioTrack.write(buf, 0, buf.size)
        audioTrack.play()
    }
}