package com.hk210.callmicmonitor.util.loader

import android.content.Context

object LoaderUtils {

    private var loader: Loader? = null

    fun showDialog(
        context: Context?,
        isCancelable: Boolean
    ) {
        hideDialog()
        if (context != null) {
            try {
                loader = Loader(context)
                loader?.let { loader ->
                    loader.setCanceledOnTouchOutside(true)
                    loader.setCancelable(isCancelable)
                    loader.show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hideDialog() {
        if (loader != null && loader?.isShowing!!) {
            loader = try {
                loader?.dismiss()
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}
