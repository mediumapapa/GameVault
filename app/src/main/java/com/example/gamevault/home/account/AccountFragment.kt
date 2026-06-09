package com.example.gamevault.home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gamevault.core.ResponseService
import com.example.gamevault.databinding.FragmentAccountBinding
import com.example.gamevault.onboarding.personal.model.UserProfile
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        viewModel.loadProfile()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> bindProfile(state.data)
                        is ResponseService.Error ->
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        else -> {}
                    }
                }
            }
        }
    }

    /** Pinta la información de la cuenta guardada en el registro. */
    private fun bindProfile(profile: UserProfile) {
        val fullName = profile.fullName().ifBlank { "Gamer" }
        binding.tvUserName.text = fullName
        binding.tvUserEmail.text = profile.email
        binding.imgAvatar.text = fullName.take(1).uppercase()
        binding.tvFullName.text = fullName
        binding.tvPhone.text = profile.phone.ifBlank { "Sin registrar" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
