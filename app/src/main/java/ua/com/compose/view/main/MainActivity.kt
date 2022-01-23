package ua.com.compose.view.mainimport android.content.Intentimport android.graphics.drawable.Drawableimport android.net.Uriimport android.os.Bundleimport android.os.Parcelableimport android.widget.Toastimport androidx.core.splashscreen.SplashScreen.Companion.installSplashScreenimport androidx.core.view.isVisibleimport androidx.recyclerview.widget.RecyclerViewimport com.google.android.material.bottomsheet.BottomSheetDialogFragmentimport ua.com.compose.navigator.backimport ua.com.compose.navigator.clearAllFragmentsimport ua.com.compose.file_storage.FileStorageimport ua.com.compose.Rimport ua.com.compose.mvp.BaseMvpActivityimport ua.com.compose.mvp.BaseMvpViewimport ua.com.compose.dialog.DialogManagerimport ua.com.compose.extension.*import ua.com.compose.view.main.main.FragmentMainimport ua.com.compose.mvp.data.Menuimport kotlinx.android.synthetic.main.activity_main.*import org.koin.android.ext.android.getKoinimport org.koin.core.qualifier.namedimport ua.com.compose.navigator.replaceimport ua.com.compose.view.main.info.ImageInfoFragmentimport java.lang.ref.WeakReferenceimport ua.com.compose.instagram_grid.view.main.InstagramGridFragmentimport ua.com.compose.instagram_no_crop.view.main.InstagramCropFragmentimport ua.com.compose.instagram_panorama.view.main.InstagramPanoramaFragmentimport ua.com.compose.instagram_planer.view.main.InstagramPlanerFragmentimport ua.com.compose.view.custom.SpanningLinearLayoutManagerclass MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {    companion object {        private const val ACTION_INSTAGRAM_PLANER = "ua.com.compose.action.INSTAGRAM_PLANER"        private const val ACTION_INSTAGRAM_CROP = "ua.com.compose.action.INSTAGRAM_CROP"        private const val ACTION_INSTAGRAM_GRID = "ua.com.compose.action.INSTAGRAM_GRID"        private const val ACTION_INSTAGRAM_PANORAMA = "ua.com.compose.action.INSTAGRAM_PANORAMA"    }    override val presenter: MainPresenter by lazy {        val scope = getKoin().getOrCreateScope(                "app", named("app"))        scope.get()    }    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        installSplashScreen()        DialogManager.init(context = WeakReference(this))        FileStorage.init(context = this)        setContentView(R.layout.activity_main)        bottomMenu.isEnabled = false        alert.setVibrate(EVibrate.BUTTON)        window.navigationBarColor = getCurrentContext().getColorFromAttr(R.attr.color_1)        supportFragmentManager.replace(fragment = ImageInfoFragment.newInstance(null), containerId = R.id.id_fragment_header, addToBackStack = false)        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)        goToActionFragment()        btnBack.setVibrate(EVibrate.BUTTON)        btnBack.setOnClickListener {            backPress(byBack = true)        }        txtTitle.setOnClickListener {            btnBack.performClick()        }        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    private fun goToActionFragment(){        when(intent.action){            ACTION_INSTAGRAM_PLANER -> {                supportFragmentManager.replace(fragment = InstagramPlanerFragment.newInstance(), addToBackStack = true)            }            ACTION_INSTAGRAM_CROP -> {                supportFragmentManager.replace(fragment = InstagramCropFragment.newInstance(), addToBackStack = true)            }            ACTION_INSTAGRAM_GRID -> {                supportFragmentManager.replace(fragment = InstagramGridFragment.newInstance(), addToBackStack = true)            }            ACTION_INSTAGRAM_PANORAMA -> {                supportFragmentManager.replace(fragment = InstagramPanoramaFragment.newInstance(), addToBackStack = true)            }        }    }    override fun onNewIntent(intent: Intent?) {        super.onNewIntent(intent)        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    fun setVisibleHeader(isVisible: Boolean){        id_fragment_header.isVisible = isVisible    }    private fun handleSendImage(intent: Intent) {        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->            closeAllDialogs()            supportFragmentManager.clearAllFragments()            supportFragmentManager.replace(fragment = ImageInfoFragment.newInstance(uri), containerId = R.id.id_fragment_header, addToBackStack = false)            supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)        }    }    private var currentBottomMenu = mutableListOf<Menu>()    override fun setupBottomMenu(menu: MutableList<Menu>) {        currentBottomMenu = menu.filter { it.isVisible.invoke() }.toMutableList()        if(currentBottomMenu.isEmpty()){            if(bottomMenu.isEnabled != currentBottomMenu.isNotEmpty()){                bottomMenu.isEnabled = false                bottomMenuContainer.isVisible = false                this.window.navigationBarColor = this.getColorFromAttr(R.attr.color_1)            }        }else{            bottomMenu.layoutManager = SpanningLinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)            bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu)            if(bottomMenu.isEnabled != currentBottomMenu.isNotEmpty()){                bottomMenu.isEnabled = true                bottomMenuContainer.isVisible = true                this.window.navigationBarColor = this.getColorFromAttr(R.attr.color_3)            }        }    }    override fun setVisibleBottomMenu(isVisible: Boolean){        bottomMenuContainer.isVisible = isVisible    }    override fun isVisibleBottomMenu() = bottomMenuContainer.isVisible    override fun setVisibleBack(isVisible: Boolean) {        btnBack.isVisible = isVisible        btnBack.isClickable = isVisible        txtTitle.isClickable = isVisible    }    override fun setTitle(title: String, startDrawable: Drawable?) {        txtTitle.text = title        txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null)    }    override fun showAlert(srtResId: Int) {        Toast.makeText(this, this.getString(srtResId), Toast.LENGTH_SHORT).show()    }    override fun backPress(byBack: Boolean) : Boolean {        val backInFragment = (supportFragmentManager.fragments.lastOrNull() as? BaseMvpView)?.backPress(byBack) ?: false        if( !backInFragment && !supportFragmentManager.back() && !byBack){            finishApplication()        }        return true    }    override fun updateBottomMenu() {        bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu.filter { it.isVisible.invoke() }.toMutableList())        bottomMenu.adapter?.notifyDataSetChanged()    }    private fun closeAllDialogs(){        this.supportFragmentManager.fragments.filterIsInstance<BottomSheetDialogFragment>().forEach {            it.dismissAllowingStateLoss()        }    }    override fun onBackPressed() {        backPress(byBack = false)    }    override fun backToMain() {        supportFragmentManager.clearAllFragments()        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)    }    override fun onResume() {        super.onResume()    }    fun bottomSheetCreate(){        this.window.navigationBarColor = this.getColorFromAttr(ua.com.compose.gallery.R.attr.color_5)    }    fun bottomSheetDestroy(){        this.window.navigationBarColor = this.getColorFromAttr(ua.com.compose.gallery.R.attr.color_1)    }    override fun finishApplication() {        finish()    }    override fun onDestroy() {        super.onDestroy()        getKoin().getScopeOrNull("app")?.close()    }}