package el.ka.someapp.viewmodel

import android.app.Application
import android.media.Image
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.ImageType

class ChangeImageViewModel(application: Application) : AndroidViewModel(application) {
  private val _currentChangeImageType = MutableLiveData<ImageType?>()
  val currentChangeImageType: LiveData<ImageType?>
    get() = _currentChangeImageType

  fun setCurrentChangeImageType(type: ImageType) {
    _currentChangeImageType.value = type
    setAspectRation()
  }


  private val _aspectRatioX = MutableLiveData(0)
  val aspectRationX: LiveData<Int> get() = _aspectRatioX

  private val _aspectRatioY = MutableLiveData(0)
  val aspectRationY: LiveData<Int> get() = _aspectRatioY

  private fun setAspectRation() {
    _aspectRatioX.value = when (_currentChangeImageType.value) {
      ImageType.PROFILE -> 1
      ImageType.BACKGROUND -> 3
      else -> 0
    }

    _aspectRatioY.value = when (_currentChangeImageType.value) {
      ImageType.PROFILE -> 1
      ImageType.BACKGROUND -> 1
      else -> 0
    }
  }
}