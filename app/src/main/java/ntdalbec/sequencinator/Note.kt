package ntdalbec.sequencinator

import android.os.Parcel
import android.os.Parcelable


data class Note( val tone: Int, val duration: Int ) : Parcelable {

    val frequency: Int
        get() = (Math.pow(2.0, (tone - 49) / 12.0) * 440).toInt()

    fun getTimingInms(tempo: Int) = (60f / tempo * 1000 * (duration / 4f)).toInt()

    fun asByteArray(tempo: Int, wave: WaveForms.Wave) : ByteArray {
        val size = (getTimingInms(tempo) / 1000f * SAMPLE_RATE).toInt()
        return ByteArray(size) { wave(frequency, it) }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(tone)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }

        const val WHOLE = 16
        const val HALF = 8
        const val QUARTER = 4
        const val EIGHTH = 2
        const val SIXTEENTH = 1
    }
}