package ua.com.compose.view.mainimport android.content.Intentimport android.graphics.drawable.Drawableimport android.net.Uriimport android.os.Bundleimport android.os.Parcelableimport android.widget.Toastimport androidx.core.view.isVisibleimport androidx.fragment.app.FragmentManagerimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport ua.com.compose.navigator.backimport ua.com.compose.navigator.clearAllFragmentsimport ua.com.compose.file_storage.FileStorageimport ua.com.compose.Rimport ua.com.compose.mvp.BaseMvpActivityimport ua.com.compose.mvp.BaseMvpViewimport ua.com.compose.dialog.DialogManagerimport ua.com.compose.extension.*import ua.com.compose.view.main.main.FragmentMainimport ua.com.compose.mvp.data.Menuimport kotlinx.android.synthetic.main.activity_main.*import org.koin.android.ext.android.getKoinimport org.koin.core.qualifier.namedimport ua.com.compose.navigator.replaceimport ua.com.compose.view.main.history.ImageHistoryimport ua.com.compose.view.main.history.ImageHistoryFragmentimport java.lang.ref.WeakReferenceclass MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {    override val presenter: MainPresenter by lazy {        val scope = getKoin().getOrCreateScope(                "app", named("app"))        scope.get()    }    private val fragmentHistory by lazy {        ImageHistoryFragment.newInstance(uri = null).apply {            supportFragmentManager.beginTransaction().replace(R.id.id_fragment_header, this).commit()        }    }    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        DialogManager.init(context = WeakReference(this))        FileStorage.init(context = this)        setContentView(R.layout.activity_main)        bottomMenu.isEnabled = false        alert.setVibrate(EVibrate.BUTTON)        window.navigationBarColor = getCurrentContext().getColorFromAttr(R.attr.color_1)        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)        supportFragmentManager.replace(fragment = ImageHistoryFragment.newInstance(null), containerId = R.id.id_fragment_header, addToBackStack = false)        btnBack.setOnClickListener {            onBackPressed()        }        txtTitle.setOnClickListener {            if(supportFragmentManager.fragments.last() is FragmentMain){                if((infoClickCounter == 10)){                    Toast.makeText(applicationContext, getString(R.string.module_main_app_info), Toast.LENGTH_LONG).show()                    infoClickCounter = 0                }else{                    infoClickCounter++                }            }        }        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    override fun onNewIntent(intent: Intent?) {        super.onNewIntent(intent)        if(intent?.action == Intent.ACTION_SEND) {            if (intent.type?.startsWith("image/") == true) {                handleSendImage(intent)            }        }    }    fun setVisibleHeader(isVisible: Boolean){        id_fragment_header.isVisible = isVisible    }    private fun handleSendImage(intent: Intent) {        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->            supportFragmentManager.replace(fragment = ImageHistoryFragment.newInstance(uri), containerId = R.id.id_fragment_header, addToBackStack = false)        }    }    private var infoClickCounter = 0    private var currentBottomMenu = mutableListOf<Menu>()    override fun setupBottomMenu(menu: MutableList<Menu>) {        currentBottomMenu = menu.filter { it.isVisible }.toMutableList()        if(currentBottomMenu.isEmpty()){            if(bottomMenu.isEnabled != currentBottomMenu.isNotEmpty()){                bottomMenu.isEnabled = false                bottomMenu.animateMargin(bottom = -(55.dp))            }        }else{            bottomMenu.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)            bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu)            if(bottomMenu.isEnabled != currentBottomMenu.isNotEmpty()){                bottomMenu.isEnabled = true                bottomMenu.animateMargin(bottom = 55.dp)            }        }    }    override fun setVisibleBack(isVisible: Boolean) {        btnBack.isVisible = isVisible    }    override fun setTitle(title: String, startDrawable: Drawable?) {        txtTitle.text = title        txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null)    }    private val hideRunnable = Runnable {        alert.setOnClickListener(null)        alert.setClickable(false)        alert.animateMargin(top = (-75).dp)    }    override fun showAlert(srtResId: Int) {        txtAlert.setText(srtResId)        if(!alert.isClickable){            alert.removeCallbacks(hideRunnable)            alert.animateMargin(top = 75.dp)            alert.postDelayed(hideRunnable, 2000)            alert.setOnClickListener {                alert.removeCallbacks(hideRunnable)                alert.post(hideRunnable)            }        }else{            alert.removeCallbacks(hideRunnable)            alert.postDelayed(hideRunnable, 2000)        }    }    override fun backPress() : Boolean {        val backInFragment = (supportFragmentManager.fragments.lastOrNull() as? BaseMvpView)?.backPress() ?: false        if( !backInFragment && !supportFragmentManager.back()){            finishApplication()        }        return true    }    override fun updateBottomMenu() {        bottomMenu.adapter = BottomMenuRvAdapter(menu = currentBottomMenu.filter { it.isVisible }.toMutableList())        bottomMenu.adapter?.notifyDataSetChanged()    }    override fun onBackPressed() {        backPress()    }    override fun backToMain() {        supportFragmentManager.clearAllFragments()        supportFragmentManager.replace(fragment = FragmentMain(), addToBackStack = true)    }    override fun onResume() {        super.onResume()    }    fun bottomSheetCreate(){        this.window.navigationBarColor = this.getColorFromAttr(ua.com.compose.gallery.R.attr.color_5)    }    fun bottomSheetDestroy(){        this.window.navigationBarColor = this.getColorFromAttr(ua.com.compose.gallery.R.attr.color_1)    }    override fun finishApplication() {        finish()    }    override fun onDestroy() {        super.onDestroy()        getKoin().getScopeOrNull("app")?.close()    }}