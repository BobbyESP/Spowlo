package com.bobbyesp.spowlo.domain.spotify.model

data class APICallState(
    val isLoading: Boolean = false,
    val APIResponse: APIResponse = APIResponse(
        Regular_Latest = "",
        Amoled_Latest = "",
        RC_Latest = "",
        ABC_Latest = "",
        Lite_Latest = "",
        Regular = emptyList(),
        Amoled = emptyList(),
        Regular_Cloned = emptyList(),
        Amoled_Cloned = emptyList(),
        Lite = emptyList()
    )
)
