package com.example.cooldrop

interface AppDestination {
    val route: String
}

object FileTransfer : AppDestination {
    override val route = "file_transfer"
}

object NameSelect : AppDestination {
    override val route = "name_select"
}