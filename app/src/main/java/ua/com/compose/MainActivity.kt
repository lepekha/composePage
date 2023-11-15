package ua.com.composeimport android.content.Intentimport android.net.Uriimport android.os.Bundleimport android.os.Parcelableimport android.util.Logimport android.view.WindowManagerimport androidx.activity.ComponentActivityimport androidx.activity.compose.setContentimport androidx.compose.animation.EnterTransitionimport androidx.compose.animation.ExitTransitionimport androidx.compose.animation.animateContentSizeimport androidx.compose.animation.core.animateDpAsStateimport androidx.compose.animation.core.tweenimport androidx.compose.foundation.backgroundimport androidx.compose.foundation.clickableimport androidx.compose.foundation.interaction.MutableInteractionSourceimport androidx.compose.foundation.layout.Boximport androidx.compose.foundation.layout.Columnimport androidx.compose.foundation.layout.PaddingValuesimport androidx.compose.foundation.layout.Rowimport androidx.compose.foundation.layout.WindowInsetsimport androidx.compose.foundation.layout.fillMaxSizeimport androidx.compose.foundation.layout.fillMaxWidthimport androidx.compose.foundation.layout.heightimport androidx.compose.foundation.layout.paddingimport androidx.compose.foundation.layout.sizeimport androidx.compose.foundation.layout.statusBarsimport androidx.compose.foundation.layout.widthimport androidx.compose.foundation.layout.windowInsetsPaddingimport androidx.compose.foundation.layout.wrapContentHeightimport androidx.compose.foundation.shape.RoundedCornerShapeimport androidx.compose.material.icons.Iconsimport androidx.compose.material.icons.rounded.Settingsimport androidx.compose.material.ripple.rememberRippleimport androidx.compose.material3.BottomAppBarDefaults.ContentPaddingimport androidx.compose.material3.ButtonDefaultsimport androidx.compose.material3.ButtonDefaults.ContentPaddingimport androidx.compose.material3.FilledTonalButtonimport androidx.compose.material3.FilledTonalIconButtonimport androidx.compose.material3.Iconimport androidx.compose.material3.IconButtonimport androidx.compose.material3.IconButtonColorsimport androidx.compose.material3.MaterialThemeimport androidx.compose.material3.Surfaceimport androidx.compose.material3.Textimport androidx.compose.material3.TextFieldimport androidx.compose.runtime.DisposableEffectimport androidx.compose.runtime.LaunchedEffectimport androidx.compose.runtime.getValueimport androidx.compose.runtime.livedata.observeAsStateimport androidx.compose.runtime.mutableStateOfimport androidx.compose.runtime.rememberimport androidx.compose.runtime.setValueimport androidx.compose.ui.Alignmentimport androidx.compose.ui.Modifierimport androidx.compose.ui.draw.shadowimport androidx.compose.ui.graphics.Colorimport androidx.compose.ui.platform.ComposeViewimport androidx.compose.ui.platform.LocalLifecycleOwnerimport androidx.compose.ui.platform.LocalViewimport androidx.compose.ui.platform.ViewCompositionStrategyimport androidx.compose.ui.res.colorResourceimport androidx.compose.ui.res.painterResourceimport androidx.compose.ui.res.stringResourceimport androidx.compose.ui.text.font.FontWeightimport androidx.compose.ui.text.style.TextAlignimport androidx.compose.ui.unit.dpimport androidx.compose.ui.unit.spimport androidx.compose.ui.zIndeximport androidx.core.splashscreen.SplashScreen.Companion.installSplashScreenimport androidx.core.util.Consumerimport androidx.core.view.WindowCompatimport androidx.navigation.NavTypeimport androidx.navigation.Navigatorimport androidx.navigation.compose.NavHostimport androidx.navigation.compose.composableimport androidx.navigation.compose.rememberNavControllerimport androidx.navigation.navArgumentimport com.google.android.material.bottomsheet.BottomSheetDialogFragmentimport org.koin.android.scope.AndroidScopeComponentimport org.koin.androidx.compose.koinViewModelimport org.koin.androidx.scope.ScopeActivityimport org.koin.androidx.viewmodel.ext.android.viewModelimport ua.com.compose.data.ColorNamesimport ua.com.compose.data.EThemeimport ua.com.compose.extension.EVibrateimport ua.com.compose.extension.vibrateimport ua.com.compose.screens.EPanelimport ua.com.compose.screens.camera.CameraScreenimport ua.com.compose.screens.image.ImageScreenimport ua.com.compose.screens.palette.PaletteScreenimport ua.com.compose.screens.settings.SettingsScreenimport ua.com.compose.screens.settings.SettingsViewModelimport ua.com.compose.theme.ColorPickThemeclass MainActivity : ComponentActivity() {    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        installSplashScreen()        ColorNames.init(this)        WindowCompat.setDecorFitsSystemWindows(window, false)        setContent {                val settingsViewModel: SettingsViewModel = koinViewModel()                val theme by settingsViewModel.theme.observeAsState(Settings.theme)                ColorPickTheme(theme = theme) {                    Surface(                        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainerLowest                    ) {                        val uri = this@MainActivity.intent?.let { (it.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.toString()?.let { Uri.encode(it) } }                        val view = LocalView.current                        val lifecycleOwner = LocalLifecycleOwner.current                        val navController = rememberNavController()                        var activePanel by remember { mutableStateOf(Settings.startScreen) }                        var stateSettings by remember { mutableStateOf(false) }                        if (stateSettings) {                            SettingsScreen(theme = theme, viewModel = settingsViewModel) {                                stateSettings = false                            }                        }                        LaunchedEffect(key1 = uri) {                            uri?.let {                                activePanel = EPanel.IMAGE                                navController.navigate(EPanel.IMAGE.routWithParams(uri))                            }                        }                        Column(                            modifier = Modifier                                .windowInsetsPadding(WindowInsets.statusBars)                                .fillMaxSize()                        ) {                            Box(modifier = Modifier                                .fillMaxWidth()                                .wrapContentHeight()                                .shadow(5.dp)                                .zIndex(1f)                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)) {                                Column() {                                    Row(                                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier                                            .padding(start = 16.dp, top = 8.dp, end = 8.dp)                                    ) {                                        Text(                                            text = stringResource(id = R.string.app_name),                                            fontSize = 32.sp,                                            fontWeight = FontWeight(700),                                            color = MaterialTheme.colorScheme.onSurface,                                            modifier = Modifier.weight(1f),                                        )                                        IconButton(onClick = { stateSettings = true }) {                                            Icon(                                                imageVector = Icons.Rounded.Settings,                                                modifier = Modifier                                                    .size(32.dp)                                                    .padding(4.dp),                                                tint = MaterialTheme.colorScheme.onSurface,                                                contentDescription = null                                            )                                        }                                    }                                    Row(                                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier                                            .wrapContentHeight()                                            .padding(                                                start = 16.dp,                                                top = 16.dp,                                                end = 16.dp,                                                bottom = 24.dp                                            )                                    ) {                                        EPanel.values().filter { it.isVisible() }.forEach { panel ->                                            val interactionSource = remember { MutableInteractionSource() }                                            Box(                                                modifier = Modifier                                                    .wrapContentHeight()                                                    .weight(1f)                                                    .padding(4.dp, 8.dp, 4.dp, 8.dp)                                                    .clickable(                                                        interactionSource = interactionSource,                                                        indication = null,                                                        onClick = {                                                            if (navController.currentDestination?.route != panel.rout) {                                                                view.vibrate(EVibrate.BUTTON)                                                                activePanel = panel                                                                Settings.startScreen = panel                                                                navController.navigate(panel.rout)                                                            }                                                        }                                                    )                                            ) {                                                Column(                                                    horizontalAlignment = Alignment.CenterHorizontally,                                                    modifier = Modifier                                                        .wrapContentHeight()                                                        .fillMaxWidth()                                                ) {                                                    val icon = if (activePanel == panel) {                                                        painterResource(id = panel.iconResId)                                                    } else {                                                        painterResource(id = panel.iconFilledResId)                                                    }                                                    val colorContainer = if (activePanel == panel) {                                                        MaterialTheme.colorScheme.primary                                                    } else {                                                        Color.Transparent                                                    }                                                    val colorContent = if (activePanel == panel) {                                                        MaterialTheme.colorScheme.onPrimary                                                    } else {                                                        MaterialTheme.colorScheme.onSurface                                                    }                                                    val animateWidth by animateDpAsState(targetValue = if(activePanel == panel) 114.dp else 38.dp,                                                        tween(durationMillis = 300),                                                        label = "animateWidth"                                                    )                                                    Icon(                                                        painter = icon,                                                        tint = colorContent,                                                        modifier = Modifier                                                            .height(38.dp)                                                            .width(animateWidth)                                                            .background(                                                                colorContainer,                                                                RoundedCornerShape(20.dp)                                                            )                                                            .padding(6.dp),                                                        contentDescription = null                                                    )                                                    Text(                                                        modifier = Modifier                                                            .fillMaxWidth()                                                            .padding(top = 3.dp)                                                            .wrapContentHeight(),                                                        textAlign = TextAlign.Center,                                                        color = MaterialTheme.colorScheme.onSurface,                                                        text = stringResource(id = panel.titleResId),                                                        fontSize = 15.sp,                                                        fontWeight = FontWeight(600)                                                    )                                                }                                            }                                        }                                    }                                }                            }                            NavHost(                                modifier = Modifier                                    .fillMaxSize()                                    .zIndex(0f),                                navController = navController,                                startDestination = activePanel.rout,                                enterTransition = {                                    EnterTransition.None                                },                                popEnterTransition = {                                    EnterTransition.None                                },                                exitTransition = {                                    ExitTransition.None                                }                            ) {                                composable(EPanel.CAMERA.rout) {                                    CameraScreen(                                        viewModule = koinViewModel(),                                        lifecycleOwner = lifecycleOwner                                    )                                }                                composable(EPanel.IMAGE.rout) { ImageScreen(viewModule = koinViewModel()) }                                composable("screen_image/{uri}") {                                    ImageScreen(                                        viewModule = koinViewModel(),                                        uri = it.arguments?.getString("uri")                                    )                                }                                composable(EPanel.PALLETS.rout) { PaletteScreen(viewModule = koinViewModel()) }                            }                            DisposableEffect(Unit) {                                val listener = Consumer<Intent> {                                    (it.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {                                        navController.navigate(                                            route = EPanel.IMAGE.routWithParams(                                                Uri.encode(it.toString())                                            )                                        )                                    }                                }                                addOnNewIntentListener(listener)                                onDispose { removeOnNewIntentListener(listener) }                            }                        }                    }                }            }    }}