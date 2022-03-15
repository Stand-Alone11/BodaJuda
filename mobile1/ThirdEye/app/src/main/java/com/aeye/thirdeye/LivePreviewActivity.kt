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
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aeye.thirdeye.objectdetector.ObjectDetectorProcessor
import com.aeye.thirdeye.preference.PreferenceUtils
import com.aeye.thirdeye.sound.SoundAlarmUtil
import com.aeye.thirdeye.vibrator.TextToSpeechUtil
import com.aeye.thirdeye.vibrator.VibratorUtil
import com.google.android.gms.common.annotation.KeepName
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
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

    // firebase 관련 변수들
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
            showToast("권한을 허용해주세요")
            TextToSpeechUtil(this, "권한을 허용해주세요")
        } else {
            // 모델 다운로드 확인 & 모델 다운로드
            getModelName()
        }
    }

    private fun isCameraPermissionAccepted(): Boolean = isPermissionGranted(this, Manifest.permission.CAMERA)

    private fun init() {
        preview = findViewById(R.id.preview_view)
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }

        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        val detailButton = findViewById<Button>(R.id.button_live_preview_detail).apply {
            setOnClickListener {
                // TODO: 상세 정보 음성 안내
            }
        }

        val refreshButton = findViewById<Button>(R.id.button_live_preview_refresh).apply {
            setOnClickListener {
                // TODO: 재인식
                startCameraSource()
                resultTextView.text = ""
            }
        }

        // object detection의 결과
        resultTextView = findViewById<TextView>(R.id.tv_detection_result)

        val voiceButton = findViewById<Button>(R.id.button_live_preview_voice).apply {
            setOnClickListener {
                TextToSpeechUtil(this@LivePreviewActivity, resultTextView.text.toString())
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
                    Log.i("확인", "Using Custom Object Detector Processor")

                    // local model은 상단에 있음
                    // isLocalModel의 값으로 objectDetectorOption 생성
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
                CUSTOM_AUTOML_OBJECT_DETECTION -> {
                    Log.i(TAG, "Using Custom AutoML Object Detector Processor")
                    val customAutoMLODTLocalModel =
                        LocalModel.Builder().setAssetManifestFilePath("automl/manifest.json").build()
                    val customAutoMLODTOptions =
                        PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(
                            this,
                            customAutoMLODTLocalModel
                        )
                    cameraSource!!.setMachineLearningFrameProcessor(
                        ObjectDetectorProcessor(this, customAutoMLODTOptions, this)
                    )
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
     * 모델 이름을 로딩 성공 -> 해당 이름의 리모트 모델 존재 여부 확인
     * 실패 -> 로컬 모델로 createCameraSource
     */
    private fun getModelName() {
        var modelName: String
        configureRemoteConfig()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    modelName = remoteConfig.getString("model_name")
                    Log.d("확인", "modelName: $modelName")
                    // 모델 다운로드 확인 & 다운로드
                    checkRemoteModel(modelName)
                } else {
                    createCameraSource(selectedModel, isLocalModel = true)
                    showToast("Failed to fetch model name.")
                    Log.d("확인", "연결 실패")
                }
            }
    }

    /**
     * 모델 이름으로 기기에 모델이 있는지 확인
     * 있다면 -> 해당 모델로 createCameraSource()
     * 없다면 다운로드 실행
     *
     * 확인 불가 -> 로컬모델로 createCameraSource()
     */

    private fun checkRemoteModel(modelName: String) {

        aEyeRemoteModel =
            CustomRemoteModel
                .Builder(FirebaseModelSource.Builder(modelName).build())
                .build()
        Log.d("확인", "localModel: $aEyeLocalModel")
        Log.d("확인", "remoteModel: $aEyeRemoteModel")
        val remoteModelManager = RemoteModelManager.getInstance()

        remoteModelManager.isModelDownloaded(aEyeRemoteModel)
            .addOnSuccessListener {
                Log.d("확인", "다운로드 확인: $it")
                if (!it) {
                    // false이면 모델 다운로드
                    Log.d("확인", "다운로드 시작")
                    downloadRemoteModel(remoteModelManager, aEyeRemoteModel)
                } else {
                    Log.d("확인", "다운받은 RemoteModel로 실행(checkRemoteModel)")
                    createCameraSource(selectedModel)
                }
            }
            .addOnFailureListener {
                Log.d("확인", "다운로드 확인 불가")
                createCameraSource(selectedModel, isLocalModel = true)
            }
    }

    /**
     * todo: 와이파이 연결 필수인지 check
     * 다운로드 성공 -> 다운받은 모델로 createCameraSource()
     * 실패 -> 로컬모델로 createCameraSource()
     */
    private fun downloadRemoteModel(manager: RemoteModelManager, remoteModel: CustomRemoteModel) {
        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        manager.download(remoteModel, downloadConditions)
            .addOnSuccessListener {
                Log.d("확인", "다운로드 성공")
                // 다운로드가 느려서 완료되면 다운받은 RemoteModel로 실행
                Log.d("확인", "다운받은 RemoteModel로 실행(downloadRemoteModel)")
                createCameraSource(selectedModel)
            }
            .addOnFailureListener {
                Log.d("확인", "다운로드 실패")
                createCameraSource(selectedModel, isLocalModel = true)
            }
    }

    // 모델 다운로드 확인 &  실행
    private fun checkModelAndStart() {
        if (::aEyeRemoteModel.isInitialized) {
            val remoteModelManager = RemoteModelManager.getInstance()

            remoteModelManager.isModelDownloaded(aEyeRemoteModel)
                .addOnSuccessListener {
                    Log.d("확인", "다운로드 확인: $it")
                    if (it) {
                        // 실행
                        Log.d("확인", "다운받은 RemoteModel로 실행(checkModelAndStart)")
                        startObjectDetectorWithRemoteModel(aEyeRemoteModel)
                    }
                }
                .addOnFailureListener {
                    Log.d("확인", "다운로드 확인 불가")
                }
        } else {
            Log.d("확인", "LocalModel로 실행")
            startObjectDetectorWithLocalModel(aEyeLocalModel)
        }

    }

    // 원격 구성을 통해 모델 선택하기
    // Firebase 원격 구성
    private fun configureRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            // 업데이트 간격을 너무 길게해서 변수 변경이 즉시 반영되지 않았던 문제
            // https://firebase.google.com/docs/remote-config/get-started?platform=android&hl=ko#throttling
            // 개발 도중에는 짧게해서 사용해도 문제 없지만 서비스 중에는 업데이트 시간 고려해야함
            minimumFetchIntervalInSeconds = 5
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    // RemoteModel 적용하여 실행
    private fun startObjectDetectorWithRemoteModel(remoteModel: CustomRemoteModel) {
        val customObjectDetectorOptions =
            PreferenceUtils.getCustomObjectDetectorOptionsForLivePreviewWithRemoteModel(this, remoteModel)
        Log.d("확인", "옵션: ${customObjectDetectorOptions}")
        Log.d("확인", "Remote 실행함수")
        cameraSource!!.setMachineLearningFrameProcessor(
            ObjectDetectorProcessor(this, customObjectDetectorOptions, this)
        )
    }

    // LocalModel 적용하여 실행
    private fun startObjectDetectorWithLocalModel(localModel: LocalModel) {
        val customObjectDetectorOptions =
            PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, localModel)
        Log.d("확인", "옵션: ${customObjectDetectorOptions}")
        Log.d("확인", "Local 실행함수")
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

    override fun onStop() {
        super.onStop()
        SoundAlarmUtil.release()
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
    }

    companion object {
        private const val OBJECT_DETECTION = "Object Detection"
        private const val OBJECT_DETECTION_CUSTOM = "Custom Object Detection"
        private const val CUSTOM_AUTOML_OBJECT_DETECTION = "Custom AutoML Object Detection (Flower)"

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
        runOnUiThread {
            preview?.stop()
            alert()
            resultTextView.text = label
        }
    }

    private fun alert() {
        SoundAlarmUtil.play()
        VibratorUtil.vibrate(this)
    }
}
