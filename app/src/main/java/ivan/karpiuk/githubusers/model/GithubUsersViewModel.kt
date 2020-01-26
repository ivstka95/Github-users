package ivan.karpiuk.githubusers.model

import androidx.lifecycle.ViewModel
import ivan.karpiuk.githubusers.repository.GithubUsersRepository

class GithubUsersViewModel(repository: GithubUsersRepository) : ViewModel() {
    private val PAGE_SIZE = 30
    private val repoResult = repository.getGitHubUsers(PAGE_SIZE)
    val posts = repoResult.pagedList
    val networkState = repoResult.networkState
    val refreshState = repoResult.refreshState

    fun loadInitial() = repoResult.refresh.invoke()

    fun retry() = repoResult.retry.invoke()
}
