# GoPro File Renamer

This is a simple Java tool for renaming GoPro files (Hero6 to Hero12).

## Why rename GoPro files?
When recording longer takes, GoPro cameras split the videos into shorter clips or "chapters" (typically of size 2-12 GB)
in regular intervals.
Unfortunately, the resulting files are named in a rather peculiar way, such that their temporal order does not correspond to
the **lexicographic order** of the filenames.
For example, the successive chapters of two videos numbered `0527` and `0528`, respectively, are named
```
GH010527.MP4        // video 0527
GH020527.MP4
GH030527.MP4
```
and
```
GH010528.MP4        // video 0528
GH020528.MP4
GH030528.MP4
```
i.e., the **chapter** numbers `01`, `02`, `03` etc. are inserted _before_ the corresponding **video** number.
If the recorded files are listed in lexicographic order, the result is
```
GH010527.MP4
GH010528.MP4
GH020527.MP4
GH020528.MP4
GH030527.MP4
GH030528.MP4
```
that is, the chapters of different videos are scrambled and chapters belonging to the same video do not show in succession!

One could argue that this is no big deal because files can be listed by **creation time** instead, thus preserving
the temporal order. However, GoPro cams are quite prone to loosing their time setting. Just removing the battery
briefly (at least on a Hero 7 black) may easily cause the camera to reset its internal clock, rendering all subsequent file
dates wrong. Under such circumstances the correct order of files is difficult to restore.

**Remark:** I have no idea what motivated GoPro to adopt this peculiar naming scheme. 
If it was not simply a mistake its real intentions are well hidden. 

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

**Notes:** 
* This tool only works for files produced with GoPro **Hero6** to **Hero12** cams. Earlier models using different file naming conventions
  are not supported (see https://community.gopro.com/s/article/GoPro-Camera-File-Naming-Convention for details).
* **No backup copies** of the files are made during renaming because of the large size of the video files.

## How to use

Coming soon ....
