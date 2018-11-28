package de.unihannover.se.tauben2.view.recycler


interface RecyclerItem {
    enum class Type(val id: Int) { HEADER(0), ITEM(1) }

    fun getType(): Type

    class Header(val title: String) : RecyclerItem {
        override fun getType() = RecyclerItem.Type.HEADER
    }
}