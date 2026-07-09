package com.job.july09application

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.job.july09application.databinding.FragmentSecondBinding

class SecondFragment : Fragment(R.layout.fragment_second), AppBarTextReceiver {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private var currentText: CharSequence = "두 번째 Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSecondBinding.bind(view)
        binding.textView.text = currentText
        binding.backButton.setOnClickListener {
            (requireActivity() as MainActivity).showMainFragment()
        }
    }

    override fun updateText(text: CharSequence) {
        currentText = text
        _binding?.textView?.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
