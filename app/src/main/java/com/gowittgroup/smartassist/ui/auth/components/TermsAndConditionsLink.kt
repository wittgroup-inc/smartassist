package com.gowittgroup.smartassist.ui.auth.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.gowittgroup.smartassist.R

@Composable
internal fun TermsAndConditionsLink(
    termsAndConditionClick: (String) -> Unit
) {
    val termsUrl =
        stringResource(R.string.privacy_policy_link)

    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.terms_and_conditions_text_part_one))
        pushStringAnnotation(tag = "URL", annotation = termsUrl)
        withStyle(style = SpanStyle(color = Color.Blue, fontSize = 14.sp)) {
            append(stringResource(R.string.terms_and_conditions_text_part_two))
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->

            annotatedString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { url ->
                    termsAndConditionClick(url.item)
                }
        }
    )
}