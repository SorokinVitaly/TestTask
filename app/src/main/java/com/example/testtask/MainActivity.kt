package com.example.testtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val headerTextModifier = Modifier.padding(10.dp)
    private val headerTextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 20.sp,
        color = Color.White
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarBackground = ContextCompat.getColor(this, R.color.status_bar_background)
        val navigationBarBackground = Color.Black.toArgb()
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
            MainScreen(state)
        }
    }

    @Composable
    fun MainScreen(state: ScreenState) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxSize()
                .background(colorResource(R.color.activity_gray_background))
        ) {
            TopBar(state)
            if (state.isMainScreen) {
                MemoryBar(state)
            }
            val paddingTop = if (state.isMainScreen) 16.dp else dimensionResource(R.dimen.dividers)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorResource(R.color.activity_gray_background))
                    .padding(
                        top = paddingTop,
                        bottom = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dividers))
            ) {
                items(25) { index ->
                    Text(
                        text = "Item $index",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun TopBar(state: ScreenState) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.app_bar))
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(64.dp)
        ) {
            if (state.isMainScreen) {
                Text(
                    text = stringResource(R.string.default_title),
                    modifier = headerTextModifier,
                    style = headerTextStyle
                )
            } else {
                IconButton(onClick = viewModel::onBackPressed) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_back_24),
                        tint = Color.White,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = state.title,
                    modifier = headerTextModifier,
                    style = headerTextStyle
                )
            }
        }
    }


    @Composable
    fun MemoryBar(state: ScreenState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.cell_background))
                .padding(12.dp)
                .height(28.dp)
        ) {
            val progress = (state.totalMemory - state.freeMemory) / state.totalMemory
            val fontSize = dimensionResource(R.dimen.cell_text).value.sp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.device_memory),
                    fontSize = fontSize
                )
                Text(
                    text = stringResource(R.string.free_memory, state.freeMemory),
                    fontSize = fontSize
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = colorResource(R.color.app_bar),
                trackColor = Color.Gray,
                strokeCap = StrokeCap.Square,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Spacer(modifier = Modifier
                .height(10.dp),
            )
        }
    }
}