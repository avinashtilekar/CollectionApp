package com.example.ajspire.collection.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtHeading.text = getString(R.string.client_name)
        binding.txtTitle.text = getString(R.string.app_name)+ " "+BuildConfig.VERSION_NAME
        binding.txtFooter.text = "${BuildConfig.BUILD_DATE_TIME} \n\n${getString(R.string.ajspire_tec)}"

        binding.btnSync.setOnClickListener {
            Log.d("Sync Click","Sync Click")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}