package wav.boop.sample

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

interface FileNameGenerator {
    fun generateFileName(): String
}


class MemoryFileNameGenerator(
    private val word1: Set<String>,
    private val word2: Set<String>,
    private val word3: Set<String>
): FileNameGenerator {
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ssZ", Locale.US)
    override fun generateFileName(): String {
        val three = word1.random()
        val word = word2.filter { it != three }.random()
        val phrase = word3.filter { !arrayOf(three, word).contains(it) }.random()
        return "${dateFormat.format(Date.from(Instant.now()))}-${three}_${word}_${phrase}.wav"
    }
}

val three: Set<String> = setOf(
    "psychedelic",
    "post",
    "extravagant",
    "3d",
    "angry",
    "camp",
    "irradiated",
    "polish",
    "aesthetic",
    "dreamy",
    "orchestral",
    "neo",
    "intelligent",
    "hungarian",
    "dark",
    "slowed",
    "electronic",
    "lofi",
    "ironic",
    "acoustic",
    "unplugged",
    "based",
    "a-capella",
    "generative",
    "experimental",
    "uncut",
    "unhinged",
    "24k",
    "choral"
)

val word: Set<String> = setOf(
    "synth",
    "shoe",
    "jaguar",
    "corn",
    "filibuster",
    "dad",
    "vapor",
    "alternative",
    "indie",
    "gizzard",
    "yodel",
    "noise",
    "doom",
    "bedroom",
    "flamingo",
    "trip",
    "acid",
    "cheeseburger",
    "math",
    "dream",
    "alt",
    "psychedelic",
    "psych",
    "brit",
    "lofi",
    "chill",
    "space",
    "drone",
    "dance",
    "ambient",
    "kraut",
    "club",
    "electro",
    "reverbed",
    "glitch",
    "mustard",
    "sludge",
    "tofu",
    "elevator",
    "surf",
    "experimental",
    "field"
)

val phrase: Set<String> = setOf(
    "core",
    "punk",
    "wave",
    "jazz",
    "funk",
    "rock",
    "folk",
    "metal",
    "soul",
    "house",
    "hop",
    "pop",
    "bop",
    "ensemble",
    "swag",
    "nova",
    "disco",
    "swing",
    "groove",
    "ska",
    "beat",
    "zone",
    "gaze",
    "banger",
    "quartet",
    "chiptune",
    "noise",
    "music",
    "tune",
    "band",
    "psychedelia"
)

val nameGenerator = MemoryFileNameGenerator(three, word, phrase)