/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.viewer.text

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import me.zhanghai.android.files.R
import me.zhanghai.android.files.compat.AlertDialogBuilderCompat
import me.zhanghai.android.files.util.show

class ConfirmReloadDialogFragment : AppCompatDialogFragment() {
    private val listener: Listener
        get() = requireParentFragment() as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialogBuilderCompat.create(requireContext(), theme)
            .setMessage(R.string.text_editor_reload_message)
            .setPositiveButton(R.string.keep_editing, null)
            .setNegativeButton(R.string.reload) { _, _ -> listener.reload() }
            .create()
    }

    companion object {
        fun show(fragment: Fragment) {
            ConfirmReloadDialogFragment().show(fragment)
        }
    }

    interface Listener {
        fun reload()
    }
}
