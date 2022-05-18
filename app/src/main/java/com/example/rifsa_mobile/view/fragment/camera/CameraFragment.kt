package com.example.rifsa_mobile.view.fragment.camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rifsa_mobile.R
import com.example.rifsa_mobile.databinding.FragmentCameraBinding
import com.example.rifsa_mobile.utils.Utils
import com.example.rifsa_mobile.view.fragment.inventory.insert.InvetoryInsertFragment
import com.example.rifsa_mobile.view.fragment.inventory.insert.InvetoryInsertFragment.Companion.camera_key
import com.example.rifsa_mobile.view.fragment.inventory.insert.InvetoryInsertFragment.Companion.invetory_camera_key
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


class CameraFragment : Fragment() {
    private lateinit var binding : FragmentCameraBinding
    private var imageCapture : ImageCapture? = null


    private var type = ""
    private lateinit var fragment : Fragment

    private val launchIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ respon ->
        if (respon.resultCode == Activity.RESULT_OK){
            val uriImage : Uri = respon.data?.data as Uri
            val file = Utils.uriToFile(uriImage,requireContext())
            showImageCapture(file)
        }
    }
    private fun allPermissionGranted() = required_permission.all {
        ContextCompat.checkSelfPermission(requireContext(),it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                showToast("tidak ada izin")
                requireActivity().finishAffinity()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater)

        type = requireArguments().getString(camera_key).toString()
        if (type == "inventory"){
            fragment = InvetoryInsertFragment()
        }else{

        }

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(requireActivity(), required_permission, REQUEST_CODE_PERMISSIONS)
        }

        startCamera()

        binding.btncameraCapture.setOnClickListener {
            capturePhoto()
        }


        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            launchIntentGallery.launch(intent)
        }

        return binding.root
    }

    private fun startCamera(){
        val bottomMenu = requireActivity().findViewById<BottomNavigationView>(R.id.main_bottommenu)
        bottomMenu.visibility = View.GONE

        val cameraProvider = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider.addListener({
            val provider : ProcessCameraProvider = cameraProvider.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder().build()
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }catch (e : Exception){
                Toast.makeText(
                    context,
                    e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(camera_fragment,e.message.toString())
            }

        },ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto(){
        val imageResult = imageCapture?:return
        val imageFile = Utils.createFile(requireActivity().application)

        val outputFiles = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        imageResult.takePicture(
            outputFiles,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    showToast("berhasil mengambil gambar")
                    showImageCapture(imageFile)
                }

                override fun onError(exception: ImageCaptureException) {
                   showToast("gagal mengambil gambar")
                }

            }
        )
    }

    private fun showImageCapture(data : File){
        val bundle = Bundle()
        val imageFiles = ArrayList<File>()
        imageFiles.add(data)
        bundle.putSerializable(invetory_camera_key,imageFiles)
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainnav_framgent,fragment)
            .addToBackStack(null)
            .commit()

    }

    private fun showToast(title : String){
        Toast.makeText(requireContext(),title,Toast.LENGTH_SHORT).show()
    }

    companion object{
        private val required_permission = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val camera_fragment = "camera_fragment"
    }


}