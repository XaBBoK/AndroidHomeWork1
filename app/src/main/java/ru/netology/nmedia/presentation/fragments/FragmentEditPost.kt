package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.utils.load
import ru.netology.nmedia.utils.setActionBarTitle
import ru.netology.nmedia.utils.viewBinding


const val INTENT_EXTRA_POST = "POST-DATA"

class FragmentEditPost : Fragment(R.layout.fragment_edit_post) {

    private val binding: FragmentEditPostBinding by viewBinding(FragmentEditPostBinding::bind)
    private lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private val viewModel by viewModels<PostViewModel>({ requireParentFragment() })


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.post = arguments?.getParcelable(
            INTENT_EXTRA_POST
        ) ?: Post()

        photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(requireContext(), "photo error", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.changePhoto(uri.toFile(), uri)
                    }
                }
            }

        //меняем ActionBar Title на "Новый" или "Редактирование"
        setActionBarTitle(getString(if (binding.post?.id == NON_EXISTING_POST_ID) R.string.label_new_post_title else R.string.label_post_edit_title))

        setupListeners()
        loadDraft()
        setupSubscribe()
    }


    //загрузка черновика НОВОГО сообщения
    private fun loadDraft() {
        if (binding.post == null) {
            binding.post = viewModel.draft?.copy()
        } else
            viewModel.draft = null
    }


    private fun setupSubscribe() {
        //сохранение черновика НОВОГО сообщения

        /*viewModel.edited.observe(viewLifecycleOwner) {
            it?.isNewPost()?.let {post ->
                viewModel.draft = post.copy()
            }


        }*/

        viewModel.fragmentEditPostEdited.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media.uri == null) {
                binding.previewContainer.visibility = View.GONE

                binding.post?.withBaseUrls()?.attachment?.takeIf { it.type == AttachmentType.IMAGE }?.let {
                    binding.preview.load(
                        url = it.url,
                        placeholder = R.drawable.ic_loading_placeholder
                    )

                    binding.previewContainer.visibility = View.VISIBLE
                }

                return@observe
            }

            binding.previewContainer.visibility = View.VISIBLE
            binding.preview.setImageURI(media.uri)
        }
    }


    private fun setupListeners() {
        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        binding.gallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        binding.photo.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.new_post -> {
                        binding.post?.let { post ->
                            //очистка черновика НОВОГО сообщения
                            post.isNewPost()?.let {
                                viewModel.draft = null
                            }

                            try {
                                //binding.ok.isEnabled = false
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.sendming_message_toast),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                viewModel.addOrEditPost(post)
                            } catch (e: Exception) {
                                //binding.ok.isEnabled = true
                                Log.e("addOrEditPost", e.message.toString())
                            }
                        }

                        true
                    }
                    else -> false
                }


        }, viewLifecycleOwner)

        //обработка нажатия кнопки Назад
        //сохранение реализовано через editPostViewModel.data.observe
        //        requireActivity().onBackPressedDispatcher.addCallback(this) {
        //            findNavController().navigateUp()
        //        }
    }
}