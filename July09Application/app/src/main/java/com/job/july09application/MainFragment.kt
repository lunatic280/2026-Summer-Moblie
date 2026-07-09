package com.job.july09application

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.job.july09application.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main), AppBarTextReceiver {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var currentText: CharSequence = "첫 번째 Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        binding.textView.text = currentText
        binding.nextButton.setOnClickListener {
            (requireActivity() as MainActivity).showSecondFragment()
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
