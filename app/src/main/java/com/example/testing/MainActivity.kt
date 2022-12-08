// Code For enabling dark mode across all Android versions with a dedicated dark mode toggle(specific to the app)

// Please replace the name of package with your  project name
package com.example.testing

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.lightColors
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Justify
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class DarkModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This sets the @Composable function as the root view of the activity.
        // This is meant to replace the .xml file that we would typically set using the setContent(R.id.xml_file) method.
        setContent {
            // Reacting to state changes is the core behavior of Compose
            // @remember helps to calculate the value passed to it only during the first composition. It then
            // returns the same value for every subsequent composition.
            // @mutableStateOf as an observable value where updates to this variable will redraw all
            // the composable functions. "only the composable that depend on this will be redraw while the
            // rest remain unchanged making it more efficient".
            val enableDarkMode = remember { mutableStateOf( false) }
            CustomTheme(enableDarkMode) {
                ThemedDrawerAppComponent(enableDarkMode)
            }
        }
    }
}


@Composable
fun CustomTheme(enableDarkMode: MutableState<Boolean>, children: @Composable() () -> Unit) {
    // In this case, I'm just showing an example of how you can override any of the values that
    // are a part of the Palette even though I'm just using the default values itself.
    val lightColors = lightColors(
        primary = Color(0xFF6200EE),
        primaryVariant = Color(0xFF3700B3),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF03DAC5),
        secondaryVariant = Color(0xFF0000FF),
        onSecondary = Color(0xFF000000),
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFF000000),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF000000),
        error = Color(0xFFB00020),
        onError = Color(0xFFFFFFFF)
    )

    // darkColors is a default implementation
    val darkColors = darkColors()
    val colors = if (enableDarkMode.value) darkColors else lightColors

    // Data class holding typography definitions as defined by the
    // Material typography specification
    // https://material.io/design/typography/the-type-system.html#type-scale
    val typography = Typography(
        body1 = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            textIndent = TextIndent(firstLine = 16.sp),
            textAlign = Justify
        )
    )

    // A MaterialTheme comprises of colors, typography and the child composables that are going
    // to make use of this styling.
    MaterialTheme(colors = colors, content = children, typography = typography)
}


@Composable
fun ThemedDrawerAppComponent(enableDarkMode: MutableState<Boolean>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentScreen = remember { mutableStateOf(ThemedDrawerAppScreen.Screen1) }
    val scope = rememberCoroutineScope()

    // It's a common pattern used across multiple apps where you see a drawer on the left
    // of the screen.
    ModalDrawer(
        // Drawer state indicates whether the drawer is open or closed.
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            //drawerContent accepts a composable to represent the view/layout that will be displayed
            // when the drawer is open.
            ThemedDrawerContentComponent(
                currentScreen = currentScreen,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        },
        content = {
            // bodyContent takes a composable to represent the view/layout to display on the
            // screen. We select the appropriate screen based on the value stored in currentScreen.
            ThemedBodyContentComponent(
                currentScreen = currentScreen.value,
                enableDarkMode = enableDarkMode,
                openDrawer = {
                    scope.launch { drawerState.open() }
                }
            )
        }
    )
}

@Composable
fun ThemedDrawerContentComponent(
    currentScreen: MutableState<ThemedDrawerAppScreen>,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        // Column with clickable modifier wraps the child composable and enables it to react to a
        // click through the onClick callback similar to the onClick listener that we are accustomed
        // to on Android.
        // Here, we just update the currentScreen variable to hold the appropriate value based on
        // the row that is clicked i.e if the first row is clicked, we set the value of
        // currentScreen to DrawerAppScreen.Screen1, when second row is clicked we set it to
        // DrawerAppScreen.Screen2 and so on and so forth.
        Column(
            modifier = Modifier.clickable(onClick = {
                currentScreen.value = ThemedDrawerAppScreen.Screen1
                // We also close the drawer when an option from the drawer is selected.
                closeDrawer()
            }), content = {
                Text(text = ThemedDrawerAppScreen.Screen1.name, modifier = Modifier.padding(16.dp))
            }
        )

        Column(
            modifier = Modifier.clickable(
                onClick = {
                    currentScreen.value = ThemedDrawerAppScreen.Screen2
                    closeDrawer()
                }
            ), content = {
                Text(text = ThemedDrawerAppScreen.Screen2.name, modifier = Modifier.padding(16.dp))
            }
        )

        Column(
            modifier = Modifier.clickable {
                currentScreen.value = ThemedDrawerAppScreen.Screen3
                closeDrawer()
            },
            content = {
                Text(text = ThemedDrawerAppScreen.Screen3.name, modifier = Modifier.padding(16.dp))
            }
        )
    }
}

/**
 * Passed the corresponding screen composable based on the current screen that's active.
 */
@Composable
fun ThemedBodyContentComponent(
    currentScreen: ThemedDrawerAppScreen,
    enableDarkMode: MutableState<Boolean>,
    openDrawer: () -> Unit
) {
    val onCheckChanged = { _: Boolean ->
        enableDarkMode.value = !enableDarkMode.value
    }
    when (currentScreen) {
        ThemedDrawerAppScreen.Screen1 -> ThemedScreen1Component(
            enableDarkMode.value,
            openDrawer,
            onCheckChanged
        )
        ThemedDrawerAppScreen.Screen2 -> ThemedScreen2Component(
            enableDarkMode.value,
            openDrawer,
            onCheckChanged
        )
        ThemedDrawerAppScreen.Screen3 -> ThemedScreen3Component(
            enableDarkMode.value,
            openDrawer,
            onCheckChanged
        )
    }
}


@Composable
fun ThemedScreen1Component(
    enableDarkMode: Boolean,
    openDrawer: () -> Unit,
    onCheckChanged: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // slots for a title, navigation icon, and actions. Also known as the action bar.
        TopAppBar(
            // The Text composable is pre-defined by the Compose UI library; you can use this
            // composable to render text on the screen
            title = { Text("Screen 1") },
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // A pre-defined composable that's capable of rendering a switch. It honors the Material
                // Design specification.
                Switch(checked = enableDarkMode, onCheckedChange = onCheckChanged)
                Text(
                    text = "Enable Dark Mode",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Surface(modifier = Modifier.weight(1f), color = MaterialTheme.colors.surface) {
            Text(
                text = "Geeks for geeks : Geeks learning from geeks ",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun ThemedScreen2Component(
    enableDarkMode: Boolean,
    openDrawer: () -> Unit,
    onCheckChanged: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            // The Text composable is pre-defined by the Compose UI library; you can use this
            // composable to render text on the screen
            title = { Text("Screen 2") },
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // A pre-defined composable that's capable of rendering a switch. It honors the Material
                // Design specification.
                Switch(checked = enableDarkMode, onCheckedChange = onCheckChanged)
                Text(
                    text = "Enable Dark Mode", style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Surface(modifier = Modifier.weight(1f)) {
            Text(
                text = "GFG : GeeksforGeeks was founded by Sandeep Jain",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun ThemedScreen3Component(
    enableDarkMode: Boolean,
    openDrawer: () -> Unit,
    onCheckChanged: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // It has slots for a title, navigation icon, and actions. Also known as the action bar.
        TopAppBar(
            // The Text composable is pre-defined by the Compose UI library; you can use this
            // composable to render text on the screen
            title = { Text("Screen 3") },
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // A pre-defined composable that's capable of rendering a switch. It honors the Material
                // Design specification.
                Switch(checked = enableDarkMode, onCheckedChange = onCheckChanged)
                Text(
                    text = "Enable Dark Mode", style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Surface(modifier = Modifier.weight(1f)) {
            Text(
                text = "Address: A-143, 9th Floor, Sovereign Corporate Tower Sector-136, Noida, Uttar Pradesh - 201305 ",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
* Creating an enum class for ModelDrawer screens
*/
enum class ThemedDrawerAppScreen {
    Screen1,
    Screen2,
    Screen3
}

/**
* Significance of @preview and composable annotations :
*/
@Preview
@Composable
fun CustomThemeLightPreview() {
    CustomTheme(enableDarkMode = remember { mutableStateOf(false) }) {
        Card {
            Text("Preview Text", modifier = Modifier.padding(32.dp))
        }
    }
}

@Preview
@Composable
fun CustomThemeDarkPreview() {
    CustomTheme(enableDarkMode = remember { mutableStateOf(true) }) {
        Card {
            Text("Preview Text", modifier = Modifier.padding(32.dp))
        }
    }
}