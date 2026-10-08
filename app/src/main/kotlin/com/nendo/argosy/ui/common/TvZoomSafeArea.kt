package com.nendo.argosy.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.nendo.argosy.ui.theme.LocalUiScale
import com.nendo.argosy.ui.theme.aspectRatioClassOf

private const val TV_ASPECT_RATIO = 16f / 9f

@Composable
fun TvZoomSafeArea(enabled: Boolean, content: @Composable () -> Unit) {
    if (!enabled) {
        content()
        return
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val safeWidth = minOf(maxWidth, maxHeight * TV_ASPECT_RATIO)
        if (safeWidth == maxWidth) {
            content()
            return@BoxWithConstraints
        }

        val configuration = LocalConfiguration.current
        val uiScale = LocalUiScale.current
        val safeConfiguration = remember(configuration, safeWidth, maxHeight) {
            Configuration(configuration).apply {
                screenWidthDp = safeWidth.value.toInt().coerceAtLeast(1)
                screenHeightDp = maxHeight.value.toInt().coerceAtLeast(1)
                smallestScreenWidthDp = minOf(screenWidthDp, screenHeightDp)
            }
        }

        Box(
            modifier = Modifier
                .width(safeWidth)
                .fillMaxHeight()
                .align(Alignment.Center)
        ) {
            CompositionLocalProvider(
                LocalConfiguration provides safeConfiguration,
                LocalUiScale provides uiScale.copy(
                    aspectRatioClass = aspectRatioClassOf(
                        safeConfiguration.screenWidthDp,
                        safeConfiguration.screenHeightDp
                    )
                ),
                content = content
            )
        }
    }
}
