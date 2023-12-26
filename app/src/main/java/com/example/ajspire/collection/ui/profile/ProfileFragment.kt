package com.example.ajspire.collection.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.base.DataStoreViewModel
import com.example.ajspire.collection.base.MyViewModelFactory
import com.example.ajspire.collection.databinding.FragmentProfileBinding
import com.example.ajspire.collection.extensions.appDataStore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreViewModel: DataStoreViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        activity?.let {
            dataStoreViewModel =
                ViewModelProvider(
                    this,
                    MyViewModelFactory(it.application, it.appDataStore())
                )[DataStoreViewModel::class.java]
        }
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreViewModel.getUserDetails()
    }

    private fun setObserver() {
        dataStoreViewModel.userDetails.observe(viewLifecycleOwner) { loginResponse ->
            loginResponse.user.let { user ->
                binding.etMobileNumber.setText(user.mobile_no)
                binding.etUserName.setText(user.name)
                binding.etEmail.setText(user.email)
                binding.etAddress.setText(user.address)
                binding.etMobileNumber.setText(user.mobile_no)
                binding.etPassword.setText(user.password)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}