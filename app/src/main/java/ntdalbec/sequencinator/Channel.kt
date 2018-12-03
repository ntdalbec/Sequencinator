package ntdalbec.sequencinator

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.*

class Channel(
    private val wave: WaveForms.Wave,
    private val notes: MutableList<Note> = mutableListOf(),
    private val id: UUID = UUID.randomUUID())
    : Observable(), Parcelable {

    val size: Int
        get() = notes.size

    fun addNote(note: Note) {
        notes.add(note)
        pubNotesChange()
    }

    fun removeNoteAt(index: Int?) {
        notes.removeAt(index ?: notes.size - 1)
        pubNotesChange()
    }

    fun asByteArray(tempo: Int) : ByteArray {
        var arr = byteArrayOf()

        for (note in notes) {
            val noteData = note.asByteArray(tempo, wave)
            arr = byteArrayOf(*arr, *noteData)
        }

        return arr
    }

    fun pubNotesChange() {
        setChanged()
        notifyObservers(notes.toList())
        clearChanged()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(wave.name)
        parcel.writeTypedList(notes)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(WaveForms.Wave.valueOf(parcel.readString()!!)) {
        parcel.readTypedList(notes, Note.CREATOR)
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}