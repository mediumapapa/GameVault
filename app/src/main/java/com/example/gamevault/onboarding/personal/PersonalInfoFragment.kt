package com.example.gamevault.onboarding.personal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.core.ResponseService
import com.example.gamevault.databinding.FragmentPersonalInfoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class PersonalInfoFragment : Fragment() {
    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonalInfoViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupDatePicker()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnContinuar.isEnabled = false
        binding.nombreTiet.addTextChangedListener { validateAndEnable() }
        binding.apellidosTiet.addTextChangedListener { validateAndEnable() }
        binding.celularTiet.addTextChangedListener { validateAndEnable() }
        binding.fechaNacimientoTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val nombre = binding.nombreTiet.text.toString().trim()
        val apellidos = binding.apellidosTiet.text.toString().trim()
        val celular = binding.celularTiet.text.toString().trim()
        val fechaNacimiento = binding.fechaNacimientoTiet.text.toString().trim()

        binding.nombreTil.error = viewModel.validateName(nombre)
        binding.apellidosTil.error = viewModel.validateName(apellidos)
        binding.celularTil.error = viewModel.validatePhone(celular)
        binding.fechaNacimientoTil.error = viewModel.validateBirthDate(fechaNacimiento)
        binding.btnContinuar.isEnabled = viewModel.isFormValid(
            nombre,
            apellidos,
            celular,
            fechaNacimiento
        )
    }

    private fun setupDatePicker() {
        val openPicker = View.OnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.fechaNacimientoTiet.setText(
                        "%02d / %02d / %04d".format(day, month + 1, year)
                    )
                },
                calendar.get(Calendar.YEAR) - 18,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
        binding.fechaNacimientoTiet.setOnClickListener(openPicker)
        binding.fechaNacimientoTil.setEndIconOnClickListener { openPicker.onClick(binding.fechaNacimientoTiet) }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnContinuar.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Snackbar.make(binding.root, "Sesion invalida", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.saveProfile(
                uid = uid,
                nombre = binding.nombreTiet.text.toString().trim(),
                apellidos = binding.apellidosTiet.text.toString().trim(),
                celular = binding.celularTiet.text.toString().trim(),
                fechaNacimiento = binding.fechaNacimientoTiet.text.toString().trim()
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnContinuar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            Snackbar.make(
                                binding.root,
                                "Perfil guardado correctamente",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            viewModel.clearSaveState()
                            validateAndEnable()
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                            viewModel.clearSaveState()
                            validateAndEnable()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        communicator.manageLoader(false)
        _binding = null
        super.onDestroyView()
    }
}
