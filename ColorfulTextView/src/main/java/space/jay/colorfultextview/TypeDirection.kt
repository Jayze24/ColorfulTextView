package space.jay.colorfultextview

enum class TypeDirection(val value: Int) {
    RIGHT(0), LEFT(1), UP(2), DOWN(3);

    internal companion object {
        fun getType(value: Int): TypeDirection {
            return when (value) {
                0 -> RIGHT
                1 -> LEFT
                2 -> UP
                else -> DOWN
            }
        }
    }
}