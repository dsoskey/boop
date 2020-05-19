package wav.boop.pitch

// changeover is between B and C
enum class NoteLetter(val rank: Int, val text: String) {
    C(1, "c"),
    C_SHARP(2, "c#"),
    D_FLAT(2, "d\u266D"),
    D(3, "d"),
    D_SHARP(4, "d#"),
    E_FLAT(4, "e\u266D"),
    E(5, "e"),
    F(6, "f"),
    F_SHARP(7, "f#"),
    G_FLAT(7, "g\u266D"),
    G(8, "g"),
    G_SHARP(9, "g#"),
    A_FLAT(9, "a\u266D"),
    A(10, "a"),
    A_SHARP(11, "a#"),
    B_FLAT(11, "b\u266D"),
    B(12, "b")
}