package com.example.gamevault.onboarding.personal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.databinding.FragmentPersonalInfoBinding
import com.google.android.material.snackbar.Snackbar
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
            Snackbar.make(binding.root, "Guardado TODO", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        communicator.manageLoader(false)
        _binding = null
        super.onDestroyView()
    }
}
