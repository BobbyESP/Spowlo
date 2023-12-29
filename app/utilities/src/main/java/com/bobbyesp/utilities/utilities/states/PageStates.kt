package com.bobbyesp.utilities.utilities.states

sealed class PageState {
    data object Loading : PageState()
    data object Error : PageState()
    data object Success : PageState()
}

sealed class PageStateWithThrowable {
    data object Loading : PageStateWithThrowable()
    data class Error(val exception: Throwable) : PageStateWithThrowable()
    data object Success : PageStateWithThrowable()
}

sealed class PageStateWithData<T>(open val data: T?) {
    data object Loading : PageStateWithData<Nothing>(data = null)
    class Error(override val data: Exception) : PageStateWithData<Exception>(data)
    class Success<T>(override val data: T) : PageStateWithData<T>(data)
}
