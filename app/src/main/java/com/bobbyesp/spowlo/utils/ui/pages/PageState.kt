package com.bobbyesp.spowlo.utils.ui.pages

sealed class PageState() {
    object Loading : PageState()
    object Error : PageState()
    object Success : PageState()
}

sealed class PageStateWithThrowable {
    object Loading : PageStateWithThrowable()
    data class Error(val exception: Throwable) : PageStateWithThrowable()
    object Success : PageStateWithThrowable()
}

//page state with custom data on success
sealed class PageStateWithData<T>(open val data: T?) {
    object Loading : PageStateWithData<Nothing>(data = null)
    class Error(override val data: Exception) : PageStateWithData<Exception>(data)
    class Success<T>(override val data: T) : PageStateWithData<T>(data)
}
