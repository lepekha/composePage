package ua.com.compose.mvp.data


interface Menu{
    var isVisible: () -> Boolean
    var isEnabled: Boolean
    var color: Int?
}

class BottomMenu(var iconResId: Int, var id: Int = -1, val onPress: () -> Unit): Menu {
    override var isVisible: () -> Boolean = { true }
    override var isEnabled: Boolean = true
    override var color: Int? = null
}

class TextMenu(var text: String, var id: Int = -1, val onPress: () -> Unit): Menu {
    override var isVisible: () -> Boolean = { true }
    override var isEnabled: Boolean = true
    override var color: Int? = null
}