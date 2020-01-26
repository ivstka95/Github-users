package ivan.karpiuk.githubusers.ui

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import ivan.karpiuk.githubusers.repository.NetworkState
import ivan.karpiuk.githubusers.R
import ivan.karpiuk.githubusers.ServiceLocator
import ivan.karpiuk.githubusers.model.GithubUsersViewModel
import ivan.karpiuk.githubusers.ui.usersList.UsersAdapter
import ivan.karpiuk.githubusers.dto.User
import kotlinx.android.synthetic.main.activity_github_users.*

class GithubUsersActivity : AppCompatActivity() {

    private val model: GithubUsersViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance()
                    .getRepository()
                @Suppress("UNCHECKED_CAST")
                return GithubUsersViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_users)
        initAdapter()
        initSwipeToRefresh()
        if (savedInstanceState == null)
            model.loadInitial()
    }

    private fun initAdapter() {
        val adapter =
            UsersAdapter({ model.retry() },
                { view: View, user: User? ->
                    user?.let {
                        startActivity(
                            UserDetailsActivity.intentFor(this, user.login, user.avatarUrl),
                            ActivityOptions.makeSceneTransitionAnimation(
                                this,
                                view.findViewById(R.id.ivAvatar),
                                "ivAvatar"
                            ).toBundle()
                        )
                    }
                })
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<User>> {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.loadInitial()
        }
    }
}
