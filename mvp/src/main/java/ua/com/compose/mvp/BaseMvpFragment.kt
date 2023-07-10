package ua.com.compose.mvp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment

abstract class BaseMvpFragment<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : AppCompatDialogFragment(), BaseMvpView {

    protected abstract val presenter: T
    private var view: BaseMvpActivity<BaseMvpView, BaseMvpPresenterImpl<BaseMvpView>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view = this as V)
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(createBottomMenu())
    }

    override fun showAlert(srtResId: Int) {
        (activity as BaseMvpActivity<*, *>).showAlert(srtResId)
    }

    override fun getCurrentContext(): Context {
        return (activity as BaseMvpActivity<*, *>).getCurrentContext()
    }


    override fun getCurrentActivity(): androidx.fragment.app.FragmentActivity {
        return (activity as BaseMvpActivity<*, *>).getCurrentActivity()
    }

    override fun setTitle(title: String, startDrawable: Drawable?) {
        (activity as BaseMvpActivity<*, *>).setTitle(title, startDrawable)
    }

    override fun setVisibleBottomMenu(isVisible: Boolean) {
        (activity as BaseMvpActivity<*, *>).setVisibleBottomMenu(isVisible)
    }

    override fun setBottomMenuColor(color: Int) {
        (activity as BaseMvpActivity<*, *>).setBottomMenuColor(color)
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }

    override fun updateBottomMenu() {
        (activity as BaseMvpActivity<*, *>).updateBottomMenu()
    }

    override fun backToMain() {
        (activity as BaseMvpActivity<*, *>).backToMain()
    }
}