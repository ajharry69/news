package com.xently.media.utils

val VIDEO_FORMATS = setOf("mp4", "mov", "webm", "flv", "ogg", "ogv", "vob")

val IMAGE_FORMATS = setOf(
    "bmp", "dib", "gif", "tif", "tiff", "jfif", "jpe", "jpg", "jpeg", "pbm", "pgm", "ppm", "pnm",
    "png", "apng", "blp", "bufr", "cur", "pcx", "dcx", "dds", "ps", "eps", "fit", "fits", "fli",
    "flc", "ftc", "ftu", "gbr", "grib", "h5", "hdf", "jp2", "j2k", "jpc", "jpf", "jpx", "j2c",
    "icns", "ico", "im", "iim", "mpg", "mpeg", "mpo", "msp", "palm", "pcd", "pdf", "pxr", "psd",
    "bw", "rgb", "rgba", "sgi", "ras", "tga", "icb", "vda", "vst", "webp", "wmf", "emf", "xbm",
    "xpm"
)

val VIDEO_FORMATS_STR = VIDEO_FORMATS.joinToString("|")
val IMAGE_FORMATS_STR = IMAGE_FORMATS.joinToString("|")