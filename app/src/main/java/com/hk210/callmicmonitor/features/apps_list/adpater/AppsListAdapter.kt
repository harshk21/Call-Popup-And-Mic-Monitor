package com.hk210.callmicmonitor.features.apps_list.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hk210.callmicmonitor.R
import com.hk210.callmicmonitor.databinding.AppsListItemBinding
import com.hk210.callmicmonitor.features.apps_list.model.AppsInfo

class AppsListAdapter(private val context: Context, private val onClickListener: (String) -> Unit) :
    ListAdapter<AppsInfo, AppsListAdapter.AppViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AppsListItemBinding.inflate(inflater, parent, false)
        return AppViewHolder(binding, context, onClickListener)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount(): Int = currentList.size

    class AppViewHolder(
        private val binding: AppsListItemBinding,
        private val context: Context,
        private val onClickListener: (String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppsInfo) {
            binding.appName.text = appInfo.appName
            binding.appIcon.setImageDrawable(appInfo.icon)
            setBackgroundColor(appInfo)
            setAppStatus(appInfo)
            setSeeMoreClickListener(appInfo.packageName)
        }

        private fun setAppStatus(appInfo: AppsInfo) {
            val status =
                if (appInfo.hasBackgroundAccess) context.getString(R.string.bg_access_detected) else context.getString(
                    R.string.bg_access_not_detected
                )
            binding.appStatus.text = status
        }

        private fun setBackgroundColor(appInfo: AppsInfo) {
            val color = if (appInfo.hasMicrophoneAccess && appInfo.hasBackgroundAccess) {
                R.color.green
            } else {
                android.R.color.transparent
            }
            binding.root.setBackgroundColor(ContextCompat.getColor(context, color))
        }

        private fun setSeeMoreClickListener(packageName: String) {
            binding.openAppInfo.setOnClickListener {
                onClickListener.invoke(packageName)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppsInfo>() {
        override fun areItemsTheSame(oldItem: AppsInfo, newItem: AppsInfo) =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: AppsInfo, newItem: AppsInfo) = oldItem == newItem
    }
}
