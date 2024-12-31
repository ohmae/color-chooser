package net.mm2d.color.chooser.sample.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.ColorChooserDialog
import net.mm2d.color.chooser.compose.Tab
import net.mm2d.color.chooser.sample.compose.ui.theme.SampleTheme

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                )
                            },
                        )
                    },
                ) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
) {
    var color by rememberSaveable { mutableIntStateOf(Color.Red.toArgb()) }
    var tabs by rememberSaveable { mutableStateOf(emptyList<Tab>()) }
    var initialTab by rememberSaveable { mutableStateOf(Tab.PALETTE) }
    var withAlpha by rememberSaveable { mutableStateOf(false) }
    var show by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(144.dp, 72.dp)
                .background(Color(color)),
        )
        Text(
            text = "without Alpha",
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.PALETTE
                    withAlpha = false
                    show = true
                },
            ) {
                Text(text = "DEFAULT")
            }
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.PALETTE
                    withAlpha = false
                    show = true
                },
            ) {
                Text(text = "PALETTE")
            }
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.HSV
                    withAlpha = false
                    show = true
                },
            ) {
                Text(text = "HSV")
            }
            Button(
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.RGB
                    withAlpha = false
                    show = true
                },
            ) {
                Text(text = "RGB")
            }
        }
        Text(
            text = "with Alpha",
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.PALETTE
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "DEFAULT")
            }
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.PALETTE
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "PALETTE")
            }
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.HSV
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "HSV")
            }
            Button(
                onClick = {
                    tabs = emptyList()
                    initialTab = Tab.RGB
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "RGB")
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    tabs = listOf(Tab.RGB, Tab.HSV, Tab.PALETTE, Tab.M3)
                    initialTab = Tab.RGB
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "REORDER1")
            }
            Button(
                onClick = {
                    tabs = listOf(Tab.RGB, Tab.HSV)
                    initialTab = Tab.RGB
                    withAlpha = true
                    show = true
                },
            ) {
                Text(text = "REORDER2")
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Button(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                },
            ) {
                Text(text = "LIGHT THEME")
            }
            Button(
                onClick = {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                },
            ) {
                Text(text = "DARK THEME")
            }
        }
    }
    if (!show) return
    if (tabs.isEmpty()) {
        ColorChooserDialog(
            initialColor = Color(color),
            onDismissRequest = {
                show = false
            },
            onChooseColor = {
                color = it.toArgb()
            },
            withAlpha = withAlpha,
            initialTab = initialTab,
        )
    } else {
        ColorChooserDialog(
            initialColor = Color(color),
            onDismissRequest = {
                show = false
            },
            onChooseColor = {
                color = it.toArgb()
            },
            withAlpha = withAlpha,
            initialTab = initialTab,
            tabs = tabs,
        )
    }
}
