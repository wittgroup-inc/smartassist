package com.gowittgroup.smartassist.ui.components

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.core.text.HtmlCompat

@Composable
fun HtmlTextWithCustomTabs(
    modifier: Modifier = Modifier,
    context: Context,
    html: String,
    style: TextStyle,
    maxLines: Int,
    overflow: TextOverflow
) {
    val annotatedText = buildAnnotatedString {
        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)

        var lastPos = 0
        val urlSpans = spanned.getSpans(0, spanned.length, android.text.style.URLSpan::class.java)

        urlSpans.forEach { urlSpan ->
            val start = spanned.getSpanStart(urlSpan)
            val end = spanned.getSpanEnd(urlSpan)

            // Append text before the link if any
            if (lastPos < start) {
                append(spanned.subSequence(lastPos, start))
            }

            // Append the clickable link text
            val linkText = spanned.subSequence(start, end).toString()
            withLink(
                LinkAnnotation.Url(
                    urlSpan.url,
                    TextLinkStyles(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
                ) {
                    val url = (it as LinkAnnotation.Url).url
                    // log some metrics
                    openInCustomTab(context, url)
                }
            ) {
                append(linkText)
            }

            lastPos = end
        }

        // Append any remaining text after the last link
        if (lastPos < spanned.length) {
            append(spanned.subSequence(lastPos, spanned.length))
        }

        // If there were no links at all, append the full text
        if (urlSpans.isEmpty()) {
            append(spanned)
        }
    }

    Text(
        text = annotatedText,
        style = style,
        modifier = modifier,
        overflow = overflow,
        maxLines = maxLines
    )
}


fun openInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}
