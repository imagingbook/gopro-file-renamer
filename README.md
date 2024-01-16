# GoPro File Renamer

This is a simple Java tool for renaming GoPro files.

## Why rename GoPro files?
When recording longer **takes**, GoPro cameras split the videos into shorter **clips** (smaller than 4GB) in regular intervals.
Unfortunately, the resulting files are named in a weird way, such that their temporal order does not correspond to
the **lexicographic order** of the filenames.
For example, the successive segments of two video **takes** numbered `0527` and `0528`, respectively, are named
```
GH010527.MP4        // take 0527
GH020527.MP4
GH030527.MP4
```
and
```
GH010528.MP4        // take 0528
GH020528.MP4
GH030528.MP4
```
i.e., **clip** numbers `01`, `02`, `03` etc. are inserted _before_ the corresponding **take** number.
If you list the recorded files in a file browser in lexicographic order, the result is
```
GH010527.MP4
GH010528.MP4
GH020527.MP4
GH020528.MP4
GH030527.MP4
GH030528.MP4
```
that is, the clips belonging to different takes are scrambled and clips belonging to the same take do not show in succession!

One could argue that this is not a big issue because files can be listed by **creation time** instead, thus preserving
the temporal order. However, GoPro cams are not reliable at keeping their time setting. Removing the battery
briefly (at least on my Hero 7 black) may easily cause the camera to reset its internal clock, meaning that all file
dates are wrong. Under such circumstances the correct order of files is difficult to restore.

**Remark:** I have no clue what motivated GoPro to adopt this strange naming scheme. I suspect it was simply a mistake and
not much consideration was spent on it. That's not really a good excuse, though ...

## What this tool does
This program renames GoPro files in a specified directory (recursively) to
```
GH010527.MP4 ---> 052701-GH010527.MP4
GH020527.MP4 ---> 052702-GH020527.MP4
GH030527.MP4 ---> 052703-GH030527.MP4
...
```
thereby returning them to proper lexicographic order while still preserving the original file names.
In addition, all associated GoPro files are renamed as well, including
`.MP4`, `.THM` and `.LRV` files.
The creation dates of the files are not changed.
Note that **no backup copies** of the files are made during renaming because of the large size of the video files.

## How to use
