## CLI (Command Line Interface)

### Command line options

```
options:
  -h, --help            show this help message and exit

Main options:
  {download,save,web,sync,meta,url}
                        The operation to perform.
                        download: Download the songs to the disk and embed metadata.
                        save: Saves the songs metadata to a file for further use.
                        web: Starts a web interface to simplify the download process.
                        sync: Removes songs that are no longer present, downloads new ones
                        meta: Update your audio files with metadata
                        url: Get the download URL for songs

  query                 Spotify/YouTube URL for a song/playlist/album/artist/etc. to download.
                        For album searching, include 'album:' and optional 'artist:' tags
                        (ie. 'album:the album name' or 'artist:the artist album: the album').
                        For manual audio matching, you can use the format 'YouTubeURL|SpotifyURL'
                        You can only use album/playlist/tracks urls when downloading/matching youtube urls.
                        When using youtube url without spotify url, you won't be able to use `--fetch-albums` option.

  --audio [{youtube,youtube-music} ...]
                        The audio provider to use. You can provide more than one for fallback.
  --lyrics [{genius,musixmatch,azlyrics,synced} ...]
                        The lyrics provider to use. You can provide more than one for fallback.
  --config              Use the config file to download songs. It's located under C:\Users\user\.spotdl\config.json or ~/.spotdl/config.json under linux
  --search-query SEARCH_QUERY
                        The search query to use, available variables: {title}, {artists}, {artist}, {album}, {album-artist}, {genre}, {disc-number}, {disc-count}, {duration}, {year}, {original-date},
                        {track-number}, {tracks-count}, {isrc}, {track-id}, {publisher}, {list-length}, {list-position}, {list-name}, {output-ext}
  --dont-filter-results
                        Disable filtering results.

Spotify options:
  --user-auth           Login to Spotify using OAuth.
  --client-id CLIENT_ID
                        The client id to use when logging in to Spotify.
  --client-secret CLIENT_SECRET
                        The client secret to use when logging in to Spotify.
  --auth-token AUTH_TOKEN
                        The authorization token to use directly to log in to Spotify.
  --cache-path CACHE_PATH
                        The path where spotipy cache file will be stored.
  --no-cache            Disable caching (both requests and token).
  --cookie-file COOKIE_FILE
                        Path to cookies file.
  --max-retries MAX_RETRIES
                        The maximum number of retries to perform when getting metadata.

FFmpeg options:
  --ffmpeg FFMPEG       The ffmpeg executable to use.
  --threads THREADS     The number of threads to use when downloading songs.
  --bitrate {auto,disable,8k,16k,24k,32k,40k,48k,64k,80k,96k,112k,128k,160k,192k,224k,256k,320k,0,1,2,3,4,5,6,7,8,9}
                        The constant/variable bitrate to use for the output file. Values from 0 to 9 are variable bitrates. Auto will use the bitrate of the original file. Disable will disable the
                        bitrate option. (In case of m4a and opus files, this option will skip the conversion)
  --ffmpeg-args FFMPEG_ARGS
                        Additional ffmpeg arguments passed as a string.

Output options:
  --format {mp3,flac,ogg,opus,m4a}
                        The format to download the song in.
  --save-file SAVE_FILE
                        The file to save/load the songs data from/to. It has to end with .spotdl. If combined with the download operation, it will save the songs data to the file. Required for
                        save/preload/sync
  --preload             Preload the download url to speed up the download process.
  --output OUTPUT       Specify the downloaded file name format, available variables: {title}, {artists}, {artist}, {album}, {album-artist}, {genre}, {disc-number}, {disc-count}, {duration}, {year},
                        {original-date}, {track-number}, {tracks-count}, {isrc}, {track-id}, {publisher}, {list-length}, {list-position}, {list-name}, {output-ext}
  --m3u [M3U]           Name of the m3u file to save the songs to. Defaults to {list[0]}.m3u If you want to generate a m3u for each list in the query use {list}, If you want to generate a m3u file
                        based on the first list in the query use {list[0]}, (0 is the first list in the query, 1 is the second, etc. songs don't count towards the list number)
  --overwrite {metadata,skip,force}
                        How to handle existing/duplicate files. (When combined with --scan-for-songs force will remove all duplicates, and metadata will only apply metadata to the latest song and
                        will remove the rest. )
  --restrict            Restrict filenames to ASCII only
  --print-errors        Print errors (wrong songs, failed downloads etc) on exit, useful for long playlist
  --sponsor-block       Use the sponsor block to download songs from yt/ytm.
  --archive ARCHIVE     Specify the file name for an archive of already downloaded songs
  --playlist-numbering  Sets each track in a playlist to have the playlist's name as its album, and album art as the playlist's icon
  --scan-for-songs      Scan the output directory for existing files. This option should be combined with the --overwrite option to control how existing files are handled. (Output directory is the
                        last directory that is not a template variable in the output template)
  --fetch-albums        Fetch all albums from songs in query
  --id3-separator ID3_SEPARATOR
                        Change the separator used in the id3 tags. Only supported for mp3 files.
  --ytm-data            Use ytm data instead of spotify data when downloading using ytm link

Web options:
  --host HOST           The host to use for the web server.
  --port PORT           The port to run the web server on.
  --keep-alive          Keep the web server alive even when no clients are connected.
  --allowed-origins [ALLOWED_ORIGINS ...]
                        The allowed origins for the web server.
  --web-use-output-dir  Use the output directory instead of the session directory for downloads. (This might cause issues if you have multiple users using the web-ui at the same time)

Misc options:
  --log-level {CRITICAL,FATAL,ERROR,WARN,WARNING,INFO,MATCH,DEBUG,NOTSET}
                        Select log level.
  --simple-tui          Use a simple tui.
  --headless            Run in headless mode.

Other options:
  --download-ffmpeg     Download ffmpeg to spotdl directory.
  --generate-config     Generate a config file. This will overwrite current config if present.
  --check-for-updates   Check for new version.
  --profile             Run in profile mode. Useful for debugging.
  --version, -v         Show the version number and exit.
```