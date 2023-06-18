package ua.com.compose.other_color_pick.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.ImageInfoViewModule
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.extension.*
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.navigator.replace
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentMainBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.main.camera.CameraFragment
import ua.com.compose.other_color_pick.main.image.ImageFragment
import ua.com.compose.other_color_pick.main.palette.PaletteFragment
import java.lang.ref.WeakReference


class ColorPickFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ColorPickFragment {
            return ColorPickFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    private var binding: ModuleOtherColorPickFragmentMainBinding? = null

    private val viewModule: ColorPickViewModule by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentMainBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("")


        ColorNames.init(requireContext())

        binding?.btnColorType?.setOnClickListener {
            val key = DialogChip.show(fm = childFragmentManager, list = EColorType.values().map { it.title() }, selected = EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)).title())
            childFragmentManager.setFragmentResultListener(
                    key,
                    viewLifecycleOwner
            ) { _, bundle ->
                val position = bundle.getInt(DialogChip.BUNDLE_KEY_ANSWER_POSITION, -1)
                viewModule.changeColorType(colorType = EColorType.values()[position])
            }
        }

        binding?.btnCamera?.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding?.tabCamera)
        }

        binding?.btnImage?.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding?.tabImage)
        }

        binding?.btnPalette?.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding?.tabPalette)
        }
        if((arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri) != null){
            selectScreen(binding?.tabImage)
        }else{
            val panel = EPanel.valueOfKey(prefs.get(key = SharedPreferencesKey.KEY_PANEL_ID, defaultValue = EPanel.IMAGE.id))
            val view = when(panel) {
                EPanel.CAMERA -> binding?.tabCamera
                EPanel.IMAGE -> binding?.tabImage
                EPanel.PALLETS -> binding?.tabPalette
            }
            selectScreen(tabView = view)
        }
    }

    private var prevTab: WeakReference<View>? = null
    private fun selectScreen(tabView: View? = null) {
        prevTab?.get()?.toggle()
        prevTab = WeakReference(tabView)
        tabView?.toggle()

        when {
            tabView?.id == binding?.tabCamera?.id -> {
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.CAMERA.id)
                childFragmentManager.replace(CameraFragment.newInstance(), binding?.content?.id ?: -1, addToBackStack = false)
            }
            tabView?.id == binding?.tabImage?.id-> {
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.IMAGE.id)
                childFragmentManager.replace(ImageFragment.newInstance(arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri), binding?.content?.id ?: -1, addToBackStack = false)
            }
            tabView?.id == binding?.tabPalette?.id  -> {
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.PALLETS.id)
                childFragmentManager.replace(PaletteFragment.newInstance(), binding?.content?.id ?: -1, addToBackStack = false)
            }
        }
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.COLOR_PICK.close()
    }
}

