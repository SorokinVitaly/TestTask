package com.example.testtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarBackground = 0xFFE68200.toInt()
        val navigationBarBackground = 0xFF000000.toInt()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                statusBarBackground,
                statusBarBackground
            ),
            navigationBarStyle = SystemBarStyle.dark(
                navigationBarBackground
            )
        )
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            Column(modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.activity_gray_background))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.app_bar))
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(64.dp)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_back_ios_new_24),
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = if (state.isMainScreen) {
                            stringResource(R.string.default_title)
                        } else {
                            state.title
                        }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(8) { index ->
                        Text(
                            text = "Item $index",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}