package ntdalbec.sequencinator

import android.util.Log

class Channel(private val wave: WaveForms.Wave) {
    private val notes = mutableListOf<Note>()

    fun addNote(note: Note) = notes.add(note)

    fun removeNoteAt(index: Int?) {
        notes.removeAt(index ?: notes.size - 1)
    }

    fun asByteArray(tempo: Int) : ByteArray {
        Log.i(LOG_TAG, "note list length ${notes.size}")

        var arr = byteArrayOf()
        for (note in notes) {
            val tone = note.asByteArray(tempo, wave)
            arr = byteArrayOf(*arr, *tone)
        }

        return arr

//        return notes.fold(ByteArray(0)) { acc, note ->
//            val tone = note.asByteArray(tempo, wave)
//            Log.i(LOG_TAG, note.toString())
//            Log.i(LOG_TAG, "note data len: ${tone.size}")
//            return byteArrayOf(*acc, *tone)
//        }
    }
}