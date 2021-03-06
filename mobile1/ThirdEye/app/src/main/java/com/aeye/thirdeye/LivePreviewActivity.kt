/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeye.thirdeye

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aeye.thirdeye.objectdetector.ObjectDetectorProcessor
import com.aeye.thirdeye.preference.PreferenceUtils
import com.aeye.thirdeye.sound.SoundAlarmUtil
import com.aeye.thirdeye.tts.TextToSpeechUtil
import com.aeye.thirdeye.vibrator.VibratorUtil
import com.google.android.gms.common.annotation.KeepName
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import java.io.IOException
import java.util.ArrayList
import java.util.regex.Pattern

/** Live preview demo for ML Kit APIs. */
@KeepName
class LivePreviewActivity :
    AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback, ObjectDetectorProcessor.DetectSuccessListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var selectedModel = OBJECT_DETECTION_CUSTOM
    lateinit var resultTextView: TextView
    private var resultLast = ""
    lateinit var tsUtil: TextToSpeechUtil
    lateinit var actionBar: ActionBar
    lateinit var blackPlate: FrameLayout

    // firebase ?????? ?????????
    lateinit var remoteConfig: FirebaseRemoteConfig
    // OBJECT_DETECTION_CUSTOM LocalModel
    private val aEyeLocalModel =
//            LocalModel.Builder().setAssetFilePath("custom_models/snack_01_410.tflite").build()
        LocalModel.Builder().setAssetFilePath("custom_models/snack_02_17.tflite").build()
    lateinit var aEyeRemoteModel: CustomRemoteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_live_preview)

        init()

        if(!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
            showToast("????????? ??????????????????")
            tsUtil.speakTxt("????????? ??????????????????")
       } else {
            // ?????? ???????????? ?????? & ?????? ????????????
            getModelName()
        }
    }

    private fun init() {
        tsUtil = TextToSpeechUtil(this)
        supportActionBar?.let {
            actionBar = it
        }

        blackPlate = findViewById(R.id.frame_layout_plate)

        preview = findViewById(R.id.preview_view)
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }

        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        val refreshButton = findViewById<Button>(R.id.button_live_preview_refresh).apply {
            setOnClickListener {
                // TODO: ?????????
                cameraSource?.release()
                if(this@LivePreviewActivity::aEyeRemoteModel.isInitialized) {
                    createCameraSource(OBJECT_DETECTION_CUSTOM)
                } else {
                    createCameraSource(OBJECT_DETECTION_CUSTOM, true)
                }
                resultLast = ""
                resultTextView.text = resultLast
                dismissBlackPlate()
            }
        }

        // object detection??? ??????
        resultTextView = findViewById<TextView>(R.id.tv_detection_result)

        val voiceButton = findViewById<Button>(R.id.button_live_preview_voice).apply {
            setOnClickListener {
                tsUtil.speakTxt(resultTextView.text.toString())
            }
        }

    }

    private fun createCameraSource(model: String, isLocalModel: Boolean = false) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
        }
        try {
            when (model) {
                OBJECT_DETECTION -> {
                    Log.i(TAG, "Using Object Detector Processor")
                    val objectDetectorOptions = PreferenceUtils.getObjectDetectorOptionsForLivePreview(this)
                    cameraSource!!.setMachineLearningFrameProcessor(
                        ObjectDetectorProcessor(this, objectDetectorOptions, this)
                    )
                }
                OBJECT_DETECTION_CUSTOM -> {
                    Log.i("??????", "Using Custom Object Detector Processor")

                    // local model??? ????????? ??????
                    // isLocalModel??? ????????? objectDetectorOption ??????
                    val objectDetectorOption = if(isLocalModel) {
                        PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, aEyeLocalModel)
                    } else {
                        PreferenceUtils.getCustomObjectDetectorOptionsForLivePreviewWithRemoteModel(this, aEyeRemoteModel)
                    }
                    cameraSource!!.setMachineLearningFrameProcessor(
                        ObjectDetectorProcessor(this, objectDetectorOption, this)
                    )
                    startCameraSource()
                }

                else -> Log.e(TAG, "Unknown model: $model")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $model", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * ?????? ????????? ?????? ?????? -> ?????? ????????? ????????? ?????? ?????? ?????? ??????
     * ?????? -> ?????? ????????? createCameraSource
     */
    private fun getModelName() {
        var modelName: String
        configureRemoteConfig()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    modelName = remoteConfig.getString("model_name")
                    Log.d("??????", "modelName: $modelName")
                    // ?????? ???????????? ?????? & ????????????
                    checkRemoteModel(modelName)
                } else {
                    createCameraSource(selectedModel, isLocalModel = true)
                    showToast("Failed to fetch model name.")
                    Log.d("??????", "?????? ??????")
                }
            }
    }

    /**
     * ?????? ???????????? ????????? ????????? ????????? ??????
     * ????????? -> ?????? ????????? createCameraSource()
     * ????????? ???????????? ??????
     *
     * ?????? ?????? -> ??????????????? createCameraSource()
     */

    private fun checkRemoteModel(modelName: String) {

        aEyeRemoteModel =
            CustomRemoteModel
                .Builder(FirebaseModelSource.Builder(modelName).build())
                .build()
        Log.d("??????", "localModel: $aEyeLocalModel")
        Log.d("??????", "remoteModel: $aEyeRemoteModel")
        val remoteModelManager = RemoteModelManager.getInstance()

        remoteModelManager.isModelDownloaded(aEyeRemoteModel)
            .addOnSuccessListener {
                Log.d("??????", "???????????? ??????: $it")
                if (!it) {
                    // false?????? ?????? ????????????
                    Log.d("??????", "???????????? ??????")
                    downloadRemoteModel(remoteModelManager, aEyeRemoteModel)
                } else {
                    Log.d("??????", "???????????? RemoteModel??? ??????(checkRemoteModel)")
                    createCameraSource(selectedModel)
                }
            }
            .addOnFailureListener {
                Log.d("??????", "???????????? ?????? ??????")
                createCameraSource(selectedModel, isLocalModel = true)
            }
    }

    /**
     * todo: ???????????? ?????? ???????????? check
     * ???????????? ?????? -> ???????????? ????????? createCameraSource()
     * ?????? -> ??????????????? createCameraSource()
     */
    private fun downloadRemoteModel(manager: RemoteModelManager, remoteModel: CustomRemoteModel) {
        val downloadConditions = DownloadConditions.Builder()
            .build()
        manager.download(remoteModel, downloadConditions)
            .addOnSuccessListener {
                Log.d("??????", "???????????? ??????")
                // ??????????????? ????????? ???????????? ???????????? RemoteModel??? ??????
                Log.d("??????", "???????????? RemoteModel??? ??????(downloadRemoteModel)")
                createCameraSource(selectedModel)
            }
            .addOnFailureListener {
                Log.d("??????", "???????????? ??????")
                createCameraSource(selectedModel, isLocalModel = true)
            }
    }

    // ?????? ???????????? ?????? &  ??????
    private fun checkModelAndStart() {
        if (::aEyeRemoteModel.isInitialized) {
            val remoteModelManager = RemoteModelManager.getInstance()

            remoteModelManager.isModelDownloaded(aEyeRemoteModel)
                .addOnSuccessListener {
                    Log.d("??????", "???????????? ??????: $it")
                    if (it) {
                        // ??????
                        Log.d("??????", "???????????? RemoteModel??? ??????(checkModelAndStart)")
                        startObjectDetectorWithRemoteModel(aEyeRemoteModel)
                    }
                }
                .addOnFailureListener {
                    Log.d("??????", "???????????? ?????? ??????")
                }
        } else {
            Log.d("??????", "LocalModel??? ??????")
            startObjectDetectorWithLocalModel(aEyeLocalModel)
        }

    }

    // ?????? ????????? ?????? ?????? ????????????
    // Firebase ?????? ??????
    private fun configureRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            // ???????????? ????????? ?????? ???????????? ?????? ????????? ?????? ???????????? ????????? ??????
            // https://firebase.google.com/docs/remote-config/get-started?platform=android&hl=ko#throttling
            // ?????? ???????????? ???????????? ???????????? ?????? ????????? ????????? ????????? ???????????? ?????? ???????????????
            minimumFetchIntervalInSeconds = 5
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    // RemoteModel ???????????? ??????
    private fun startObjectDetectorWithRemoteModel(remoteModel: CustomRemoteModel) {
        val customObjectDetectorOptions =
            PreferenceUtils.getCustomObjectDetectorOptionsForLivePreviewWithRemoteModel(this, remoteModel)
        Log.d("??????", "??????: ${customObjectDetectorOptions}")
        Log.d("??????", "Remote ????????????")
        cameraSource!!.setMachineLearningFrameProcessor(
            ObjectDetectorProcessor(this, customObjectDetectorOptions, this)
        )
    }

    // LocalModel ???????????? ??????
    private fun startObjectDetectorWithLocalModel(localModel: LocalModel) {
        val customObjectDetectorOptions =
            PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, localModel)
        Log.d("??????", "??????: ${customObjectDetectorOptions}")
        Log.d("??????", "Local ????????????")
        cameraSource!!.setMachineLearningFrameProcessor(
            ObjectDetectorProcessor(this, customObjectDetectorOptions, this)
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        SoundAlarmUtil.load(this)
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        //createCameraSource(selectedModel)
        startCameraSource()
    }

    /** Stops the camera. */
    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
        tsUtil.ttsCustom.shutdown()
        SoundAlarmUtil.release()
    }

    companion object {
        private const val OBJECT_DETECTION = "Object Detection"
        private const val OBJECT_DETECTION_CUSTOM = "Custom Object Detection"

        private const val TAG = "LivePreviewActivity"

        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(permission in permissions) {
            if(permission == Manifest.permission.CAMERA && grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED) {
                getModelName()
            }
        }
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    override fun detectSuccess(label: String) {
        Log.d("ObjectDetectorProcessor", "detectSuccess: ")
        runOnUiThread {
            requestPreviewStop(label)
        }
    }

    private fun requestPreviewStop(label: String) {
//        preview?.stopForAlert(object: CameraStopListener {
//            override fun onStop() {
//                if(blackPlate.visibility == View.GONE) {
//                    alert()
//                    resultTextView.text = getLabel(label)
//                    showBlackPlate()
//                }
//            }
//        })

        preview?.stop()
        if(blackPlate.visibility == View.GONE) {
            alert()
            resultTextView.text = getLabel(label)
            showBlackPlate()
            cameraSource?.release()
        }
    }

    private fun showBlackPlate() {
        if(this::blackPlate.isInitialized) {
            blackPlate.visibility = View.VISIBLE
        }
    }

    private fun dismissBlackPlate() {
        if(this::blackPlate.isInitialized) {
            blackPlate.visibility = View.GONE
        }
    }

    private fun alert() {
        SoundAlarmUtil.play()
        VibratorUtil.vibrate(this)
    }

    private fun getLabel(string: String): String {
        val sb = StringBuffer()
//        val pattern = Pattern.compile("[\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F]")
//        val matcher = pattern.matcher(string)
//        while (matcher.find()) {
//            sb.append(matcher.group())
//
//        }
        val a = string.split("_")
        if(a.size > 1) {
            sb.append(a[0]).append(" ").append(a[1])
        }
        return sb.toString()
    }

    // actionbar ?????? ??????

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_more -> {
                startActivity(Intent(this, LicenseActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
