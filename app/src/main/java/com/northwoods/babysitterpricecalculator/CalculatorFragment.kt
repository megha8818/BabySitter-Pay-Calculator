package com.northwoods.babysitterpricecalculator

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.northwoods.babysitterpricecalculator.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment(R.layout.fragment_calculator) {
    private var binding: FragmentCalculatorBinding? = null
    private val viewModel: CalculatorFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCalculatorBinding.bind(view).apply {
            calculateButton.setOnClickListener {
                viewModel.calculateCharge(startTimePicker, endTimePicker, bedTimePicker)
            }
        }
        viewModel.totalChargeData.observe(viewLifecycleOwner) { value ->
           binding.totalChargeValue.text = getString(R.string.amount_format, value)
        }
        viewModel.error.observe(viewLifecycleOwner) { value ->
            when(value){
                is CalculatorFragmentViewModel.DataError.TimeError -> createPositiveButtonsDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title),
                    message = getString(R.string.time_error_info),
                    positiveButton = getString(R.string.dialog_ok)
                ) {
                    it.dismiss()
                }.show()
                is CalculatorFragmentViewModel.DataError.CalculateError -> createPositiveButtonsDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title),
                    message = getString(R.string.calculate_error_info),
                    positiveButton = getString(R.string.dialog_ok)
                ) {
                    it.dismiss()
                }.show()
            }
        }
        this.binding = binding
    }

    private fun createPositiveButtonsDialog(
        context: Context,
        title: String,
        message: String,
        positiveButton: String,
        onPositiveClicked: (DialogInterface) -> Unit
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ -> onPositiveClicked(dialog) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}