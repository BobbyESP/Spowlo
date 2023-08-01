package com.bobbyesp.spowlo.utils.databases

import com.bobbyesp.spowlo.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.data.local.db.searching.SearchingHistoryDatabase
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchingDbHelper @Inject constructor(
    searchDb: SearchingHistoryDatabase
) {

    private val dao = searchDb.searchingDao()

    suspend fun insertSearch(
        search: String,
        filterType: MediaStoreFilterType?,
        spotifySearch: Boolean = false
    ) {
        val searchEntity = SearchEntity(
            id = 0,
            search = search,
            spotifySearch = spotifySearch,
            date = System.currentTimeMillis(),
            filter = filterType
        )

        dao.insert(searchEntity)
    }

    suspend fun getAllSearches(): List<SearchEntity> {
        return dao.getAll()
    }

    fun getAllSearchesWithFlow(): Flow<List<SearchEntity>> {
        return dao.getAllWithFlow()
    }

    suspend fun getSearchById(searchId: Int): SearchEntity? {
        return dao.getById(searchId)
    }

    suspend fun deleteSearch(searchId: Int) {
        dao.deleteById(searchId)
    }

    suspend fun deleteAllSearches() {
        dao.deleteAll()
    }

    suspend fun getSearchesBySearch(search: String): List<SearchEntity> {
        return dao.getBySearch(search)
    }

    suspend fun getSearchesBySearchAndSpotifySearch(
        search: String,
        spotifySearch: Boolean
    ): List<SearchEntity> {
        return dao.getBySearchAndSpotifySearch(search, spotifySearch)
    }
}