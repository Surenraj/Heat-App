package com.thinkgas.heatapp.data.remote.paging

import androidx.core.text.isDigitsOnly
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.api.TpiApiService
import com.thinkgas.heatapp.data.remote.model.Agent
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class FeasibilityPagingSource(
    private val apiService: TpiApiService,
    private val query: String?,
    private val status: String?,
    private val sessionId:String?
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
                "pending" -> apiService.getFeasibilityList(tempData)
                "done" -> apiService.getFeasibilityDoneList(tempData)
                "hold" -> apiService.getFeasibilityHoldList(tempData)
                "unclaimed"->apiService.getFeasibilityUnclaimedList(tempData)
                "failed"->apiService.getFeasibilityFailedList(tempData)
                "approved"->apiService.getFeasibilityApprovedList(tempData)
                "declined"->apiService.getFeasibilityDeclinedList(tempData)
                else -> apiService.getFeasibilityList(tempData)
            }
            val data = response.body()?.agentList ?: emptyList()
            var list = mutableListOf<Agent>()
            list.addAll(data)
            if (query != null) {
                list = if(query.isDigitsOnly()){
                    list.filter {
                        it.mobileNo.contains(query, true)
                    } as MutableList<Agent>
                }else {
                    list.filter {
                        it.customerName.contains(query, true)
                    } as MutableList<Agent>
                }
            }
            val nextKey =
                if (list.isNullOrEmpty()) {
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
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
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