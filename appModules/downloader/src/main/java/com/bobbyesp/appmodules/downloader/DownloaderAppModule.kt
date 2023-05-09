package com.bobbyesp.appmodules.downloader

import com.bobbyesp.appmodules.core.BottomNavigationCapable
import com.bobbyesp.appmodules.core.DestNode
import com.bobbyesp.appmodules.core.NestedAppEntry

abstract class DownloaderAppModule: NestedAppEntry, BottomNavigationCapable {
    override val graphRoute = Routes.NavGraph
    override val startDestination = Routes.Downloader.url

    internal object Arguments {
    }
    internal object Routes {
        const val NavGraph = "@downloader"

        val Downloader = DestNode("downloader")
    }
}