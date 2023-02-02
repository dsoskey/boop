package wav.boop.sample

// Do I need some base class to extend T to ensure serialization
// Should Savable or should T have fileName?
data class Savable<T> (var isSaved: Boolean, val data: T)
