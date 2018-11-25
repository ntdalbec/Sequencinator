package ntdalbec.sequencinator

class Channel(private val wave: WaveForms.Wave) {
    private val notes = mutableListOf<Note>()

    fun addNote(note: Note) = notes.add(note)

    fun removeNoteAt(index: Int?) {
        notes.removeAt(index ?: notes.size - 1)
    }

    fun asByteArray() : ByteArray {
        return notes.fold(ByteArray(0)) { acc, note ->
            val ( frequency, duration ) = note
            val size = (duration / 1000f * SAMPLE_RATE).toInt()
            val noteData = ByteArray(size) { wave(frequency, duration) }
            return byteArrayOf(*acc, *noteData)
        }
    }
}