package com.hk210.callmicmonitor.util.loader

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.hk210.callmicmonitor.databinding.LoaderLayoutBinding

class Loader(context: Context) : Dialog(context) {

    private var _binding: LoaderLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoaderLayoutBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)
            window?.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}
