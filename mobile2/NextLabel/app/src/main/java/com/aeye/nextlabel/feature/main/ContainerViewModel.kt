package com.aeye.nextlabel.feature.main

import android.net.Uri
import androidx.lifecycle.ViewModel

/**
 * viewModel로 child fragment 상태 보관
 * 라벨링 정보 보내기
 */
class ContainerViewModel : ViewModel() {
    var imageSavedUri: Uri? = null
}