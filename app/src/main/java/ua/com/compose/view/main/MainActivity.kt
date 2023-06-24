package ua.com.compose.view.mainimport android.app.Activityimport android.content.Intentimport android.content.res.Resourcesimport android.graphics.Rectimport android.graphics.drawable.Drawableimport android.net.Uriimport android.os.Bundleimport android.os.Parcelableimport android.widget.Toastimport androidx.core.splashscreen.SplashScreen.Companion.installSplashScreenimport androidx.core.view.WindowCompatimport androidx.core.view.isVisibleimport androidx.recyclerview.widget.RecyclerViewimport com.google.android.material.bottomsheet.BottomSheetDialogFragmentimport kotlinx.android.synthetic.main.activity_main.*import org.koin.android.ext.android.getKoinimport org.koin.core.qualifier.namedimport ua.com.compose.Rimport ua.com.compose.dialog.DialogManagerimport ua.com.compose.extension.*import ua.com.compose.file_storage.FileStorageimport ua.com.compose.mvp.BaseMvpActivityimport ua.com.compose.mvp.BaseMvpViewimport ua.com.compose.mvp.data.Menuimport ua.com.compose.navigator.backimport ua.com.compose.navigator.clearAllFragmentsimport ua.com.compose.navigator.replaceimport ua.com.compose.other_color_pick.main.ColorPickFragmentimport ua.com.compose.view.custom.SpanningLinearLayoutManagerimport java.lang.ref.WeakReferenceclass MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {    override val presenter: MainPresenter by lazy {        val scope = getKoin().getOrCreateScope(                "app", named("app"))        scope.get()    }    fun getStatusbarHeight(activity: Activity): Int {        val rectangle = Rect()        val window = activity.window        window.decorView.getWindowVisibleDisplayFrame(rectangle)        val statusBarHeight = rectangle.top        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")        if (resourceId > 0) {            return activity.resources.getDimensionPixelSize(resourceId)        }        return statusBarHeight    }    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        installSplashScreen()        WindowCompat.setDecorFitsSystemWindows(window, false)        DialogManager.init(context = WeakReference(this))        FileStorage.init(context = this)        setContentView(R.layout.activity_main)        root.setPaddingTop(getStatusbarHeight(this))        bottomMenu.isEnabled = false        alert.setVibrate(EVibrate.BUTTON)//        window.navigationBarColor = getCurrentContext().getColor(R.color.color_main_header)        supportFragmentManager.replace(fragment = ColorPickFragment.newInstance(null), addToBackStack = true)        closeAllDialogs()        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    override fun onNewIntent(intent: Intent?) {        super.onNewIntent(intent)        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    private fun handleSendImage(intent: Intent) {        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->            closeAllDialogs()            supportFragmentManager.clearAllFragments()            supportFragmentManager.replace(fragment = ColorPickFragment.newInstance(uri), addToBackStack = true)        }    }    fun bottomMenuView() = bottomMenuContainer    private var currentBottomMenu = mutableListOf<Menu>()    override fun setupBottomMenu(menu: MutableList<Menu>) {        bottomMenuContainer.setMarginBottom(bottomMenuContainer.context.navigationBarHeight())        currentBottomMenu = menu.filter { it.isVisible.invoke() }.toMutableList()        if(currentBottomMenu.isEmpty()){            bottomMenu.isEnabled = false            bottomMenuContainer.isVisible = false//            this.window.navigationBarColor = this.getColorFromAttr(R.attr.color_2)        }else{            bottomMenu.layoutManager = SpanningLinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)            bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu)            if(bottomMenu.isEnabled != currentBottomMenu.isNotEmpty()){                bottomMenu.isEnabled = true                bottomMenuContainer.isVisible = true//                bottomMenuContainer.setBackgroundColor(this.getColorFromAttr(R.attr.color_3))//                this.window.navigationBarColor = this.getColorFromAttr(R.attr.color_3)            }        }    }    override fun setBottomMenuColor(color: Int) {//        bottomMenuContainer.setBackgroundColor(color)        this.window.navigationBarColor = color    }    override fun setVisibleBottomMenu(isVisible: Boolean){        bottomMenuContainer.isVisible = isVisible    }    override fun isVisibleBottomMenu() = bottomMenuContainer.isVisible    override fun setTitle(title: String, startDrawable: Drawable?) {        txtTitle.text = title        txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null)    }    override fun showAlert(srtResId: Int) {        Toast.makeText(this, this.getString(srtResId), Toast.LENGTH_SHORT).show()    }    override fun backPress(byBack: Boolean) : Boolean {        val backInFragment = (supportFragmentManager.fragments.filterIsInstance<BaseMvpView>().lastOrNull() as? BaseMvpView)?.backPress(byBack) ?: false        if( !backInFragment && !supportFragmentManager.back() && !byBack){            finishApplication()        }        return true    }    override fun updateBottomMenu() {        bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu.filter { it.isVisible.invoke() }.toMutableList())        bottomMenu.adapter?.notifyDataSetChanged()    }    private fun closeAllDialogs(){        this.supportFragmentManager.fragments.filterIsInstance<BottomSheetDialogFragment>().forEach {            it.dismissAllowingStateLoss()        }    }    override fun onBackPressed() {        backPress(byBack = false)    }    override fun backToMain() {        supportFragmentManager.clearAllFragments()        supportFragmentManager.replace(fragment = ColorPickFragment(), addToBackStack = true)    }    override fun onResume() {        super.onResume()    }    override fun finishApplication() {        finish()    }    override fun onDestroy() {        super.onDestroy()        getKoin().getScopeOrNull("app")?.close()    }}