/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.parcel.Parcelize
import me.zhanghai.android.files.R
import me.zhanghai.android.files.databinding.CreateArchiveDialogBinding
import me.zhanghai.android.files.databinding.CreateArchiveDialogMd2Binding
import me.zhanghai.android.files.databinding.CreateArchiveDialogTypeIncludeBinding
import me.zhanghai.android.files.databinding.FileNameDialogBinding
import me.zhanghai.android.files.databinding.FileNameDialogMd2Binding
import me.zhanghai.android.files.file.FileItem
import me.zhanghai.android.files.settings.Settings
import me.zhanghai.android.files.util.ParcelableArgs
import me.zhanghai.android.files.util.args
import me.zhanghai.android.files.util.putArgs
import me.zhanghai.android.files.util.setTextWithSelection
import me.zhanghai.android.files.util.show
import me.zhanghai.android.files.util.valueCompat
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.util.LinkedHashSet

class CreateArchiveDialogFragment : FileNameDialogFragment() {
    private val args by args<Args>()

    override val binding: Binding
        get() = super.binding as Binding

    override val listener: Listener
        get() = super.listener as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (savedInstanceState == null) {
            val files = args.files
            var name: String? = null
            if (files.size == 1) {
                name = files.single().path.fileName.toString()
            } else {
                val parent = files.mapTo(mutableSetOf()) { it.path.parent }.singleOrNull()
                if (parent != null && parent.nameCount > 0) {
                    name = parent.fileName.toString()
                }
            }
            name?.let { binding.nameEdit.setTextWithSelection(it) }
        }
        return dialog
    }

    @StringRes
    override val titleRes: Int = R.string.file_create_archive_title

    override fun onInflateBinding(inflater: LayoutInflater): FileNameDialogFragment.Binding =
        Binding.inflate(inflater)

    override val name: String
        get() {
            val extension = when (val typeId = binding.typeGroup.checkedRadioButtonId) {
                R.id.zipRadio -> "zip"
                R.id.tarXzRadio -> "tar.xz"
                R.id.sevenZRadio -> "7z"
                else -> throw AssertionError(typeId)
            }
            return "${super.name}.$extension"
        }

    override fun onOk(name: String) {
        val archiveType: String
        val compressorType: String?
        when (val typeId = binding.typeGroup.checkedRadioButtonId) {
            R.id.zipRadio -> {
                archiveType = ArchiveStreamFactory.ZIP
                compressorType = null
            }
            R.id.tarXzRadio -> {
                archiveType = ArchiveStreamFactory.TAR
                compressorType = CompressorStreamFactory.XZ
            }
            R.id.sevenZRadio -> {
                archiveType = ArchiveStreamFactory.SEVEN_Z
                compressorType = null
            }
            else -> throw AssertionError(typeId)
        }
        listener.archive(args.files, name, archiveType, compressorType)
    }

    companion object {
        fun show(files: LinkedHashSet<FileItem>, fragment: Fragment) {
            CreateArchiveDialogFragment().putArgs(Args(files)).show(fragment)
        }
    }

    @Parcelize
    class Args(val files: LinkedHashSet<FileItem>) : ParcelableArgs

    protected class Binding private constructor(
        root: View,
        nameLayout: TextInputLayout,
        nameEdit: EditText,
        val typeGroup: RadioGroup
    ) : FileNameDialogFragment.Binding(root, nameLayout, nameEdit) {
        companion object {
            fun inflate(inflater: LayoutInflater): Binding =
                if (Settings.MATERIAL_DESIGN_2.valueCompat) {
                    val binding = CreateArchiveDialogMd2Binding.inflate(inflater)
                    val fileNameDialogBinding = FileNameDialogMd2Binding.bind(binding.root)
                    val typeBinding = CreateArchiveDialogTypeIncludeBinding.bind(binding.root)
                    Binding(
                        binding.root, fileNameDialogBinding.nameLayout,
                        fileNameDialogBinding.nameEdit, typeBinding.typeGroup
                    )
                } else {
                    val binding = CreateArchiveDialogBinding.inflate(inflater)
                    val fileNameDialogBinding = FileNameDialogBinding.bind(binding.root)
                    val typeBinding = CreateArchiveDialogTypeIncludeBinding.bind(binding.root)
                    Binding(
                        binding.root, fileNameDialogBinding.nameLayout,
                        fileNameDialogBinding.nameEdit, typeBinding.typeGroup
                    )
                }
        }
    }

    interface Listener : FileNameDialogFragment.Listener {
        fun archive(
            files: LinkedHashSet<FileItem>,
            name: String,
            archiveType: String,
            compressorType: String?
        )
    }
}
