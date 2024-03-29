package com.example.ajspire.collection.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.databinding.FragmentProfileBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.ui.BaseFragment
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModelFactory

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val dataStoreViewModel: DataStoreViewModel by viewModels {
        DataStoreViewModelFactory(activity?.application!!, activity?.appDataStore()!!)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

       /* activity?.let {
            dataStoreViewModel =
                ViewModelProvider(
                    this,
                    MyViewModelFactory(it.application, it.appDataStore())
                )[DataStoreViewModel::class.java]
        }*/
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
            loginResponse?.user?.let { user ->
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