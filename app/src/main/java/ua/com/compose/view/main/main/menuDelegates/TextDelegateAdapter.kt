package ua.com.compose.view.main.main.menuDelegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import com.inhelp.base.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import com.inhelp.extension.EVibrate
import com.inhelp.extension.setVibrate
import kotlinx.android.synthetic.main.element_menu_text.view.*

class TextDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            this.root.setOnClickListener {
                innerItem.onPress()
            }
            this.root.setVibrate(type = EVibrate.BUTTON)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Text)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Text)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_text, parent, false)) {

        lateinit var innerItem: DynamicMenu.Text
        val root = itemView.root
        val txtTitle = itemView.txtTitle

        fun bind(item: DynamicMenu.Text) {
            innerItem = item
            txtTitle.setText(item.titleResId)
        }
    }
}