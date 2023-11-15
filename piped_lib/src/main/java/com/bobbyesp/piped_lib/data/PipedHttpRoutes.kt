package com.bobbyesp.piped_lib.data

object PipedHttpRoutes {
    const val BASE_URL = "https://pipedapi.kavin.rocks/"

    object Streams {
        const val STREAMS = "streams/{videoId}"
    }

    object Comments {
        const val COMMENTS = "comments/{videoId}"
        const val NEXT_PAGE_COMMENTS = "/{nextPage}/comments/{videoId}"
    }

    object Trending {
        const val TRENDING = "trending"
    }

    object Channels {
        const val CHANNEL = "channel/{channelId}"
        const val CHANNEL_BY_NAME = "c/{name}"
        const val CHANNEL_NEXT_PAGE = "/{nextPage}/channel/{channelId}"
    }

    object Users {
        const val USER = "user/{userName}"
    }

    object Playlists {
        const val PLAYLIST = "playlist/{playlistId}"
        const val PLAYLIST_NEXT_PAGE = "/{nextPage}/playlist/{playlistId}"
    }
    object Suggestions {
        const val SUGGESTIONS = "suggestions"
    }
    object Sponsors {
        const val SPONSORS = "sponsors/{videoId}"
    }
}