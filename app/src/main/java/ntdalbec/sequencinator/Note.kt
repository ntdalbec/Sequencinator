package ntdalbec.sequencinator

import android.os.Parcel
import android.os.Parcelable


data class Note( val tone: Int, val duration: Int ) : Parcelable {

    val frequency: Int
        get() = (Math.pow(2.0, (tone - 49) / 12.0) * 440).toInt()

    fun getTimingInms(tempo: Int) = (60f / tempo * 1000 * (duration / 4f)).toInt()

    fun asByteArray(tempo: Int, wave: WaveForms.Wave) : ByteArray {
        val size = (getTimingInms(tempo) / 1000f * SAMPLE_RATE).toInt()

        return if (tone == 0) ByteArray(size)
        else {
            val gapped = ByteArray(size) { if (it < size - gapSize) wave(frequency, it) else 0 }
            trim(gapped)
        }
    }

    fun trim(arr: ByteArray) : ByteArray {
        var localMinIndex = -1
        for (i in arr.size - 2 downTo 0) {
            if (arr[i - 1] > arr[i] && arr[i] < arr[i + 1]) {
                localMinIndex = i
                break
            }
        }

        return arr.dropLast(arr.size - localMinIndex).toByteArray()
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(tone)
        parcel.writeInt(duration)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }

        val gapSize = SAMPLE_RATE / 1000 * 30

        const val WHOLE = 16
        const val HALF = 8
        const val QUARTER = 4
        const val EIGHTH = 2
        const val SIXTEENTH = 1
    }
}