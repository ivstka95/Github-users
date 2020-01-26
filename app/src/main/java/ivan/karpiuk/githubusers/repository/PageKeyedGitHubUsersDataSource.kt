package ivan.karpiuk.githubusers.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import ivan.karpiuk.githubusers.api.GithubApi
import ivan.karpiuk.githubusers.dto.User
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class PageKeyedGitHubUsersDataSource(
    private val githubApi: GithubApi,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, User>() {

    private val TAG = "PageKeyedDataSource"
    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        networkState.postValue(NetworkState.LOADING)
        githubApi.getUserSince(after = params.key).enqueue(
            object : retrofit2.Callback<List<User>> {
                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                }

                override fun onResponse(
                    call: Call<List<User>>,
                    response: Response<List<User>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        val items = data?.map { it } ?: emptyList()
                        val after = items[items.size - 1].id
                        retry = null
                        callback.onResult(items, after)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(
                            NetworkState.error("error code: ${response.code()}")
                        )
                    }
                }
            }
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {

        Log.e(TAG, "loadInitial")
        val request = githubApi.getUsers()
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()


            val data = response.body()
            val items = data?.map { it } ?: emptyList()

            if (items.isEmpty()) {
                retry = { loadInitial(params, callback) }
                val error = NetworkState.error("unknown error")
                networkState.postValue(error)
                initialLoad.postValue(error)
            } else {
                retry = null

                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                val before = items[0].id
                val after = items[items.size - 1].id
                callback.onResult(data as MutableList<User>, before, after)
            }
        } catch (ioException: IOException) {
            retry = { loadInitial(params, callback) }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}