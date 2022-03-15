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

  override fun stop() {
    super.stop()
    try {
      detector.close()
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
  private fun checkResult(result: DetectedObject) {
    if(trackingId != result.trackingId) {
      Log.d(TAG, "checkResult: TrackingId Changed")
      updateResult(result)
      stopTimer()
      startTimer()
    } else {
      if(result.labels.isNotEmpty() && result.labels[0].text == label) {
        Log.d(TAG, "checkResult: keep going")
        isResultChanged = false
      } else {
        Log.d(TAG, "checkResult: Label Changed")
        updateResult(result)
        stopTimer()
        startTimer()
      }
    }
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

  private fun startTimer() {
    job = CoroutineScope(Dispatchers.Default).launch {
      isResultChanged = true
      delay(1000L)
      if(!isResultChanged) {
        Log.d(TAG, "detect success")
        detectSuccessListener.detectSuccess(label)
      }
    }
  }

  private fun stopTimer() {
    job?.cancel()
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Object detection failed!", e)
  }

  companion object {
    private const val TAG = "ObjectDetectorProcessor"
  }

  interface DetectSuccessListener {
    fun detectSuccess(label: String)
  }
}
