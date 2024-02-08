package com.thinkgas.heatapp.data.remote.paging

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.api.TpiApiService
import com.thinkgas.heatapp.data.remote.model.Agent
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class RfcPagingSource(
    private val apiService: TpiApiService,
    private val sessionId: String?,
    private val query: String?,
    private val status: String?,
) : PagingSource<Int, Agent>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Agent> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        return try {

            val tempData = HashMap<String,String>()
            tempData["session_id"]=sessionId.toString()
            tempData["limit_per_page"]="5"
            tempData["next_page_offset"]=pageIndex.toString()
            tempData["is_tpi"] = if(AppCache.isTpi) "1" else "0"


            val response = when (status) {
                "pending" -> apiService.getRfcPendingList(tempData)
                "done" -> apiService.getRfcDoneList(tempData)
                "hold" -> apiService.getRfcHoldList(tempData)
                "unclaimed"->apiService.getRfcUnclaimedList(tempData)
                "approved"->apiService.getRfcApprovedList(tempData)
                "failed"->apiService.getRfcFailedList(tempData)
                "declined"->apiService.getRfcDeclinedList(tempData)
                else -> apiService.getRfcPendingList(tempData)
            }

            var list = response.body()?.agentList ?: emptyList()
            if (query != null) {
                if(query.isDigitsOnly()){
                    list = list.filter {
                        it.mobileNo.contains(query, true)
                    } as MutableList<Agent>
                }else {
                    list = list.filter {
                        it.customerName.contains(query, true)
                    } as MutableList<Agent>
                }

            }
            val nextKey =
                if (list.isEmpty()) {
                    null
                } else {
                    // By default, initial load size = 3 * NETWORK PAGE SIZE
                    // ensure we're not requesting duplicating items at the 2nd request
                    pageIndex + 1
                }
            LoadResult.Page(
                data = list,
                prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            Log.d("pagingdata:5 ", exception.message!!)

            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("pagingdata:6", exception.message!!)
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Agent>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}