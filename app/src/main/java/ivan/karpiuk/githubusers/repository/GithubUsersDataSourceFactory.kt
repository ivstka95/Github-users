package ivan.karpiuk.githubusers.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ivan.karpiuk.githubusers.api.GithubApi
import ivan.karpiuk.githubusers.dto.User
import java.util.concurrent.Executor

class GithubUsersDataSourceFactory(
    private val githubApi: GithubApi,
    private val retryExecutor: Executor
) : DataSource.Factory<Int, User>() {
    val sourceLiveData = MutableLiveData<PageKeyedGitHubUsersDataSource>()
    override fun create(): DataSource<Int, User> {
        val source =
            PageKeyedGitHubUsersDataSource(
                githubApi,
                retryExecutor
            )
        sourceLiveData.postValue(source)
        return source
    }
}
