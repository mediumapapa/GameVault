package com.example.gamevault.home.gameDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.gamevault.R
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.GameDetail
import com.example.gamevault.databinding.FragmentGameDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment() {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GameDetailViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private var gameId: Int = 0
    private var currentDetail: GameDetail? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameId = arguments?.getInt("gameId") ?: 0

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        observeDetail()
        viewModel.loadDetail(gameId)
    }

    private fun observeDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> communicator.manageLoader(true)
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            currentDetail = state.data
                            bindDetail(state.data)
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun bindDetail(game: GameDetail) {
        binding.tvTitle.text = game.name
        binding.tvMeta.text = buildString {
            game.released?.let { append("Lanzado el ").append(it).append(" · ") }
            append(game.developerName())
        }
        binding.ratingBar.rating = game.rating.toFloat()
        binding.tvScore.text = getString(
            R.string.detail_score_format, game.rating, game.ratingsCount
        )

        game.metacritic?.let {
            binding.tvMetacritic.visibility = View.VISIBLE
            binding.tvMetacritic.text = getString(R.string.detail_metacritic_format, it)
        }
        game.esrbRating?.let {
            binding.chipEsrb.visibility = View.VISIBLE
            binding.chipEsrb.text = it.name
        }

        bindChips(game)

        binding.tvDescription.text =
            game.descriptionRaw?.takeIf { it.isNotBlank() } ?: "Sin descripción disponible."
        binding.tvDeveloper.text = game.developerName()
        binding.tvPlaytime.text = if (game.playtime > 0) {
            getString(R.string.detail_playtime_format, game.playtime)
        } else {
            "N/D"
        }

        com.bumptech.glide.Glide.with(binding.imgHero)
            .load(game.backgroundImage)
            .centerCrop()
            .into(binding.imgHero)
    }

    private fun bindChips(game: GameDetail) {
        binding.chipGroupGenres.removeAllViews()
        game.genres.forEach { genre ->
            binding.chipGroupGenres.addView(buildChip(genre.name, isGenre = true))
        }
        binding.chipGroupPlatforms.removeAllViews()
        game.platforms.forEach { wrapper ->
            binding.chipGroupPlatforms.addView(buildChip(wrapper.platform.name, isGenre = false))
        }
    }

    private fun buildChip(text: String, isGenre: Boolean): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            isClickable = false
            isCheckable = false
            textSize = 11f
            val bg = if (isGenre) R.color.stats_bg else R.color.surface_muted
            chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), bg)
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isGenre) R.color.primary else R.color.text_primary
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
