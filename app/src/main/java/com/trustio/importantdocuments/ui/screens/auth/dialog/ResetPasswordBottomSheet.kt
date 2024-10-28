//package com.trustio.importantdocuments.ui.screens.auth.dialog
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.lifecycleScope
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.trustio.importantdocuments.databinding.ResetPasswordBinding
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class ResetPasswordBottomSheet : BottomSheetDialogFragment() {
//
//    private var _binding: ResetPasswordBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = ResetPasswordBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            resetPasswordButton.setOnClickListener {
//                val phoneNumber = emailInput.getUnmaskedText()
//
//                // Validate phone number
//                if (phoneNumber.length != 12) {
//                    errorMessage.visibility = View.VISIBLE
//                } else {
//                    errorMessage.visibility = View.GONE
//                    resetPasswordButton.visibility = View.GONE
//                    progressSpinner.visibility = View.VISIBLE
//
//                    // Launch coroutine to simulate async password reset
//                    lifecycleScope.launch {
//                        val result = resetPasswordAsync()
//                        withContext(Dispatchers.Main) {
//                            progressSpinner.visibility = View.GONE
//                            resetPasswordButton.visibility = View.VISIBLE
//
//                            if (result) {
//                                dismiss()  // Close the dialog on success
//                            } else {
//                                errorMessage.text = "Failed to reset password"
//                                errorMessage.visibility = View.VISIBLE
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Simulated async function for password reset using delay
//    private suspend fun resetPasswordAsync(): Boolean {
//        return withContext(Dispatchers.IO) {
//            delay(2000)  // Simulate network delay or API call
//            true  // Assume success for now; can modify based on real response
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
