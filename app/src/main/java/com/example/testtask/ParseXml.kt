package com.example.testtask

import android.content.Context
import org.xmlpull.v1.XmlPullParser


data class ParsedRegion(
    val name: String,
    val map: Boolean,
    val downloadName: String,
    val children: List<ParsedRegion>
)

fun parseEurope(context: Context): List<ParsedRegion> {
    val parser = context.resources.getXml(R.xml.regions)
    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG &&
            parser.name == "region" &&
            parser.getAttributeValue(null, "name") == "europe"
        ) {
            return parseChildren(parser, null, null)
        }
    }
    return emptyList()
}

private fun parseChildren(
    parser: XmlPullParser,
    inheritedPrefix: String?,
    inheritedSuffix: String?
): List<ParsedRegion> {
    val result = mutableListOf<ParsedRegion>()
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType == XmlPullParser.START_TAG &&
            parser.name == "region"
        ) {
            result += parseRegion(
                parser = parser,
                inheritedPrefix = inheritedPrefix,
                inheritedSuffix = inheritedSuffix
            )
        }
    }
    return result
}

private fun parseRegion(
    parser: XmlPullParser,
    inheritedPrefix: String?,
    inheritedSuffix: String?
): ParsedRegion {
    val name = parser.getAttributeValue(null, "name")
    val map = parser
        .getAttributeValue(null, "map")
        ?.let { it == "yes" }
        ?: true
    val downloadPrefix =
        getAttribute(parser, "download_prefix", name)
            ?: inheritedPrefix
    val downloadSuffix =
        getAttribute(parser, "download_suffix", name)
            ?: inheritedSuffix
    val downloadName = buildDownloadName(
        name = name,
        prefix = downloadPrefix,
        suffix = downloadSuffix
    )
    val childPrefix =
        getAttribute(parser, "inner_download_prefix", name)
            ?: inheritedPrefix
    val childSuffix =
        getAttribute(parser, "inner_download_suffix", name)
            ?: inheritedSuffix
    val children = parseChildren(
        parser = parser,
        inheritedPrefix = childPrefix,
        inheritedSuffix = childSuffix
    )
    return ParsedRegion(
        name = name,
        map = map,
        downloadName = downloadName,
        children = children
    )
}

private fun getAttribute(
    parser: XmlPullParser,
    attributeName: String,
    regionName: String
): String? =
    parser.getAttributeValue(null, attributeName)
        ?.let { value ->
            if (value == "\$name") {
                regionName
            } else {
                value
            }
        }

private fun buildDownloadName(
    name: String,
    prefix: String?,
    suffix: String?
): String = when {
    prefix != null && suffix != null -> "${prefix}_${name}_${suffix}"
    suffix != null -> "${name}_${suffix}"
    prefix != null -> "${prefix}_${name}"
    else -> name
}