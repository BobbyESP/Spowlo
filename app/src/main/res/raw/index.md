# spotDL v4

Download your Spotify playlists and songs along with album art and metadata

[![MIT License](https://img.shields.io/github/license/spotdl/spotify-downloader?color=44CC11&style=flat-square)](https://github.com/spotDL/spotify-downloader/blob/master/LICENSE)
[![PyPI version](https://img.shields.io/pypi/pyversions/spotDL?color=%2344CC11&style=flat-square)](https://pypi.org/project/spotdl/)
![GitHub commits since latest release (by date)](https://img.shields.io/github/commits-since/spotDL/spotify-downloader/latest?color=44CC11&style=flat-square)
[![PyPi downloads](https://img.shields.io/pypi/dw/spotDL?label=downloads@pypi&color=344CC11&style=flat-square)](https://pypi.org/project/spotdl/)
![Contributors](https://img.shields.io/github/contributors/spotDL/spotify-downloader?style=flat-square)
[![Discord](https://img.shields.io/discord/771628785447337985?label=discord&logo=discord&style=flat-square)](https://discord.gg/xCa23pwJWY)

> A new and improved version of spotDL: still the fastest, easiest and most accurate
> command-line music downloader

______________________________________________________________________

**[Read the documentation on ReadTheDocs!](http://spotdl.rtfd.io/)**

______________________________________________________________________

## Prerequisites

- [Visual C++ 2019 redistributable](https://docs.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170#visual-studio-2015-2017-2019-and-2022)
  **(on Windows)**
- Python 3.7 or above (added to PATH)

> **_YouTube Music must be available in your country for spotDL to work. This is because we use
> YouTube Music to filter search results. You can check if YouTube Music is available in your
> country, by visiting [YouTube Music](https://music.youtube.com)._**

## Installation

Refer to our [Installation Guide](https://spotdl.rtfd.io/en/latest/installation/) for more
details

- Python (**Recommended**)
    - _spotDL_ can be installed by running `pip install spotdl`.
  > On some systems you might have to change `pip` to `pip3`.

### Other options

- Prebuilt Executable
    - You can download the latest version from the
      [Releases Tab](https://github.com/spotDL/spotify-downloader/releases)
- On Termux
    - `curl -L https://raw.githubusercontent.com/spotDL/spotify-downloader/master/scripts/termux.sh | sh`
- Arch
    - There is an Arch User Repository (AUR) package for
      [spotDL](https://aur.archlinux.org/packages/python-spotdl/).
- Docker
    - Build image:

      ```bash
      docker build -t spotdl .
      ```

    - Launch container with spotDL parameters (see section below). You need to create mapped
      volume to access song files

      ```bash
      docker run --rm -v $(pwd):/music spotdl download [trackUrl]
      ```

### Installing FFmpeg

If using FFmpeg only for spotDL, you can install FFmpeg to your local directory.
`spotdl --download-ffmpeg` will download FFmpeg to your spotDL installation directory.

We recommend the above option, but if you want to install FFmpeg system-wide,

- [Windows Tutorial](https://windowsloop.com/install-ffmpeg-windows-10/)
- OSX - `brew install ffmpeg`
- Linux - `sudo apt install ffmpeg` or use your distro's package manager

## Usage

To get started right away:

```sh
spotdl download [urls]
```

To start the Web UI:

```sh
spotdl web
```

You can run _spotDL_ as a package if running it as a script doesn't work:

```sh
python -m spotdl [urls]
```

## Music sourcing and audio quality

Our app downloads music from YouTube as a source for music downloads. This method is used to avoid any issues related to downloading music from Spotify.

> **Note**
> Users are responsible for their actions and potential legal consequences. We do not support unauthorized downloading of copyrighted material and take no responsibility for user actions.

### Audio quality

Spotdl downloads music from YouTube and is designed to always download the highest possible bitrate, which is 128 kbps for regular users and 256 kbps for YouTube Music premium users.

Check [Audio Formats](USAGE#audio-formats-and-quality) page for more info.

## Contributing

Interested in contributing? Check out our [CONTRIBUTING.md](CONTRIBUTING) to find
resources around contributing along with a guide on how to set up a development environment.

## License

This project is Licensed under the [MIT](https://github.com/spotDL/spotify-downloader/blob/master/LICENSE) License.