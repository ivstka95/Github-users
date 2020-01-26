package ivan.karpiuk.githubusers.repository

import androidx.lifecycle.Transformations
import androidx.annotation.MainThread
import androidx.paging.toLiveData
import ivan.karpiuk.githubusers.api.GithubApi
import ivan.karpiuk.githubusers.dto.User
import ivan.karpiuk.githubusers.dto.UserDetails
import retrofit2.Call
import java.util.concurrent.Executor

class GitHubUsersRepository(
    private val githubApi: GithubApi,
    private val networkExecutor: Executor
) : GithubUsersRepository {
    @MainThread
    override fun getGitHubUsers(pageSize: Int): Listing<User> {
        val sourceFactory =
            GithubUsersDataSourceFactory(
                githubApi,
                networkExecutor
            )

        val livePagedList = sourceFactory.toLiveData(
            pageSize = pageSize,
            fetchExecutor = networkExecutor
        )

        val refreshState =
            Transformations.switchMap(sourceFactory.sourceLiveData) { it.initialLoad }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.networkState },
            retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
            refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
            refreshState = refreshState
        )
    }

    @MainThread
    override fun getUserDetails(login: String?): Call<UserDetails> = githubApi.getUserDetails(login)
}

