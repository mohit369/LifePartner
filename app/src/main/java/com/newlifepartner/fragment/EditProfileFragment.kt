package com.newlifepartner.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.newlifepartner.MainActivity
import com.newlifepartner.adapter.CountrySpinnerAdapter
import com.newlifepartner.databinding.FragmentEditProfileBinding
import com.newlifepartner.modal.City
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.CustomProgressBar
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    val gender = arrayOf("Male", "Female", "Others")
    var cityList:ArrayList<City> = ArrayList()
    private lateinit var preferences: MySharedPreferences
    private var selectedCountry = ""
    private var type = ""
    private val args by navArgs<EditProfileFragmentArgs>()
    private lateinit var progressDialog: CustomProgressBar
    private val imageUriList = mutableListOf<Uri>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        preferences = MySharedPreferences.getInstance(requireContext())
        progressDialog = CustomProgressBar(requireContext())
        checkPermission()
        setUpInitialValues()
        setUpData()

        binding.btnSave.setOnClickListener {
            progressDialog.show()
            uploadImage(imageUriList)

        }

        binding.img1.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }
        binding.img2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher2.launch(intent)
        }

        binding.img3.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher3.launch(intent)
        }
        binding.img4.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher4.launch(intent)
        }

        binding.img5.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher5.launch(intent)
        }
        binding.img6.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher6.launch(intent)
        }

        return binding.root
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img1.setImageURI(selectedImageUri)

            }
        }
    }

    private val imagePickerLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img2.setImageURI(selectedImageUri)

            }
        }
    }

    private val imagePickerLauncher3 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img3.setImageURI(selectedImageUri)

            }
        }
    }

    private val imagePickerLauncher4 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img4.setImageURI(selectedImageUri)

            }
        }
    }

    private val imagePickerLauncher5 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img5.setImageURI(selectedImageUri)

            }
        }
    }

    private val imagePickerLauncher6 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri)
                binding.img6.setImageURI(selectedImageUri)

            }
        }
    }

    private fun setUpData() {
        args.profile?.profile?.let {
            binding.nameEdt.setText(it.name)
            binding.bioEdt.setText(it.bio)
            binding.ageEdt.setText(it.age)
            binding.interestedTxt.setText(it.interest)
            binding.hobbiesEdt.setText(it.hobbies)
            binding.castEdt.setText(it.caste)
            binding.religionEdt.setText(it.religion)
            binding.relationshipEdt.setText(it.relationshipStatus)
        }
    }

    private fun checkValidAndSave() {
        val userId = preferences.getStringValue(Constant.USER_ID)
        val name = binding.nameEdt.text.toString()
        val age = binding.ageEdt.text.toString()
        val bio = binding.bioEdt.text.toString()
        val interested = binding.interestedTxt.text.toString()
        val hobbies = binding.hobbiesEdt.text.toString()
        val cast = binding.castEdt.text.toString()
        val religion = binding.religionEdt.text.toString()
        val relationship = binding.relationshipEdt.text.toString()

        if (name.isEmpty() || age.isEmpty() || bio.isEmpty() || interested.isEmpty() || hobbies.isEmpty() || cast.isEmpty() || religion.isEmpty()||relationship.isEmpty()||selectedCountry.isEmpty()||type.isEmpty()){
            Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.updateUserDetail(userId,name,age,type,bio,interested,hobbies,relationship,cast,religion,selectedCountry) }
            withContext(Dispatchers.Main) {
               progressDialog.dismiss()
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            Toast.makeText(requireContext(),"Update Successfully",Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun setUpInitialValues() {
        cityList = (requireActivity() as MainActivity).cityList
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,gender)
        binding.genderSpinner.adapter = genderAdapter

        val cityAdapter = CountrySpinnerAdapter(requireContext(),cityList)
        binding.citySpinner.adapter = cityAdapter
        val position = cityList.indexOfFirst {
                it.city == args.profile?.profile?.city
        }
        binding.citySpinner.setSelection(position)
        binding.genderSpinner.setSelection(genderAdapter.getPosition(args.profile?.profile?.gender))


        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = cityList[position].id

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = gender[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isMediaImagesPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
                requestMediaImagesPermission(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }else{
            if (!isMediaImagesPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestMediaImagesPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, you can access media images
            } else {
                showPermissionExplanationOrNavigateToSettings()
            }
        }

    private fun isMediaImagesPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMediaImagesPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }

    private fun showPermissionExplanationOrNavigateToSettings() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_MEDIA_IMAGES
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to access media images.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestMediaImagesPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    }else{
                        requestMediaImagesPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            val settingsIntent = Intent().apply {
                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
            startActivity(settingsIntent)
        }
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            // Handle the exception as needed
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun uploadImage(uris: List<Uri>) {
        val files = mutableListOf<MultipartBody.Part>()
        val userId = preferences.getStringValue(Constant.USER_ID)
        // Convert each selected image Uri to a file and create a MultipartBody.Part
        for (uri in uris) {
            val file = File(getRealPathFromURI(requireContext(), uri))
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaType())
            val part: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo[]", file.name, requestFile)
            files.add(part)
        }
        lifecycleScope.launch(Dispatchers.IO + ExceptionHandlerCoroutine.handler) {
            val response: ResultType<ResponseSignUp> = safeApiCall {
                ApiService.retrofitService.uploadImage(
                    file = files,
                    userId = userId.toRequestBody()
                )
            }
            withContext(Dispatchers.Main) {
                when (response) {
                    is ResultType.Success -> {
                        checkValidAndSave()
                    }

                    is ResultType.Error -> {
                        checkValidAndSave()
                    }
                }
            }

        }
    }

}