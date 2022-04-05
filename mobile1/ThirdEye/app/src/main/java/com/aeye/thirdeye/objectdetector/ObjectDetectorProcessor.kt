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

package com.aeye.thirdeye.objectdetector

import android.content.Context
import android.util.Log
import com.aeye.thirdeye.GraphicOverlay
import com.aeye.thirdeye.VisionProcessorBase
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase
import kotlinx.coroutines.*
import java.io.IOException

/** A processor to run object detector.  */
class ObjectDetectorProcessor(context: Context, options: ObjectDetectorOptionsBase, alertListener: DetectSuccessListener) :
  VisionProcessorBase<List<DetectedObject>>(context) {

  private val detector: ObjectDetector = ObjectDetection.getClient(options)
  private var trackingId: Int = -1
  private var label: String = ""
  private var job: Job? = null
  private var isResultChanged = true
  private val detectSuccessListener = alertListener
  private var id = 0
  private var timer = Timer()

  override fun stop() {
    super.stop()
    try {
      detector.close()
      timer.stop()
    } catch (e: IOException) {
      Log.e(
        TAG,
        "Exception thrown while trying to close object detector!",
        e
      )
    }
  }

  override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
    return detector.process(image)
  }

  override fun onSuccess(results: List<DetectedObject>, graphicOverlay: GraphicOverlay) {
    for (result in results) {
      graphicOverlay.add(ObjectGraphic(graphicOverlay, result))
      Log.d(TAG, "onSuccess: labelsize: ${result.labels.size} id: ${result.trackingId}")
      checkResult(result)
    }
  }

  /**
   * trackingId가 변했다면 결과 업데이트 후, 타이머 초기화 실행
   * 변하지 않았다면 label 비교
   *
   * label이 같다면 isResultChanged = false
   * 다르거나 없다면
   */
  private fun oldCheckResult(result: DetectedObject) {
    Log.d(TAG, "id: ${id++}, trackingId: ${result.trackingId}, label: ${result.labels[0].text}, isResultChanged: $isResultChanged")
    if(trackingId != result.trackingId || result.labels.size > 1) {
      Log.d(TAG, "checkResult: TrackingId Changed")
      stopTimer()
      updateResult(result)
      startTimer()
    } else {
      if(result.labels.isNotEmpty() && result.labels[0].text == label) {
        Log.d(TAG, "checkResult: keep going")
        isResultChanged = false
      } else {
        Log.d(TAG, "checkResult: Label Changed")
        stopTimer()
        updateResult(result)
        startTimer()
      }
    }
  }

  // TODO: Single Mode일 때 id 변경해야함
  private fun checkResult(result: DetectedObject) {
    val id = result.trackingId!!
    val labels = result.labels

    // tracking id 변화 없음
    if(id == trackingId) {
      // 라벨 값이 존재하고 변화했다면 타이머 스타트
      if(labels.isNotEmpty() && labels[0].text != label) {
        timer.start()
      } else if(labels.isEmpty()) {
        timer.stop()
      }
    }
    // tracking id 변화 있음
    else {
      timer.stop()
      if(labels.size > 0) {
        timer.start()
      }
    }
    updateResult(result)
  }

  private fun updateResult(result: DetectedObject) {
    result.trackingId?.let {
      trackingId = it
    }?: run {
      trackingId = -1
    }

    label = if(result.labels.isEmpty()) {
      ""
    } else {
      result.labels[0].text
    }
  }

  /**
   * 1초 후에도 isResultChange 가 true인 상황 예외 처리 방법
   *  trackingId, label 초기화 하여 다시 타이머 실행하게 함
   */
  private fun startTimer() {
    Log.d(TAG, "startTimer: start")
    job = CoroutineScope(Dispatchers.Default).launch {
      isResultChanged = true
      delay(1000L)
      Log.d(TAG, "afterTimer: $isResultChanged")
      if(!isResultChanged) {
        Log.d(TAG, "detect success")
        detectSuccessListener.detectSuccess(label)
      }
      isResultChanged = true
      trackingId = -1
      label = ""
    }
    Log.d(TAG, "startTimer: $job")
  }

  private fun stopTimer() {
    Log.d(TAG, "stopTimer: stop")
    Log.d(TAG, "stopTimer: ${job}")
    job?.cancel()
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Object detection failed!", e)
  }

  companion object {
    private const val TAG = "ObjectDetectorProcessor"
  }

  inner class Timer {
    lateinit var job: Job

    fun start() {
      Log.d(TAG, "start: ")
      stop()
      job = CoroutineScope(Dispatchers.Default).launch {
        delay(1000L)
        detectSuccessListener.detectSuccess(label)
        label = ""
        trackingId = -1
      }
    }

    fun stop() {
      if(::job.isInitialized) job.cancel()
    }
  }

  interface DetectSuccessListener {
    fun detectSuccess(label: String)
  }
}
