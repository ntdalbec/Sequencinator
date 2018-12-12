package ntdalbec.sequencinator

const val SAMPLE_RATE = 48000
const val LOG_TAG = "LOG_TAG"

object WaveForms {
    fun sinWave(frequency: Int, x: Int): Byte =
        ((Math.sin(2 * Math.PI * x * frequency / SAMPLE_RATE) + 1) / 2 * 255).toByte()

    enum class Wave : (Int, Int) ->  Byte {
        SIN {
            override fun invoke(p1: Int, p2: Int): Byte = sinWave(p1, p2)
        }
    }
}