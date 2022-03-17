package com.aeye.nextlabel.feature.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aeye.nextlabel.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture

class CameraFragment: Fragment() {

    val PERMISSIONS_REQUEST_CODE = 1
    val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    val binding by lazy { FragmentCameraBinding.inflate(layoutInflater) }
    lateinit var activity: MainActivity
    lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = context as MainActivity

        if (!hasPermissions(activity)) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            startCamera()
        }

        return binding.root
    }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                startCamera()
            } else {
                activity.finish()
            }
        }
    }

    fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = getPreview()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(activity))
    }

    fun getPreview(): Preview {
        val preview: Preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.pvCamera.getSurfaceProvider())

        return preview
    }
}