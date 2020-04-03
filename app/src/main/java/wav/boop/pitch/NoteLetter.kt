package wav.boop.pitch

// changeover is between B and C
enum class NoteLetter(val rank: Int) {
    C(1),
    C_SHARP(2),
    D_FLAT(2),
    D(3),
    D_SHARP(4),
    E_FLAT(4),
    E(5),
    F(6),
    F_SHARP(7),
    G_FLAT(7),
    G(8),
    G_SHARP(9),
    A_FLAT(9),
    A(10),
    A_SHARP(11),
    B_FLAT(11),
    B(12)
}