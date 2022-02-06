package com.georgiecasey.toutless.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.api.Status
import com.tapadoo.pagerpushpoc.arch.BaseFragment
import dagger.android.support.AndroidSupportInjection
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.fragment_event_posts_list.*
import kotlinx.android.synthetic.main.fragment_events_list.*
import kotlinx.android.synthetic.main.fragment_events_list.srSwipe
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import javax.inject.Inject


class EventPostsListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, EventPostsRecyclerAdapter.OnEventPostClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: EventPostsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(EventPostsViewModel::class.java)
    }

    lateinit private var buyingOrSelling: BuyingOrSellingField
    private val args: EventPostsListFragmentArgs by navArgs()
    private val eventPostsRecyclerAdapter by lazy { EventPostsRecyclerAdapter(this) }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_event_posts_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCurrentEvent(args.toutlessThreadId)
        viewModel.currentEvent.observe(viewLifecycleOwner, Observer { event ->
            when (event.buyingOrSelling) {
                is BuyingOrSellingField.Buying -> {
                    ssBuyingOrSelling.setDirection(StickySwitch.Direction.LEFT, false, false)
                }
                is BuyingOrSellingField.Selling -> {
                    ssBuyingOrSelling.setDirection(StickySwitch.Direction.RIGHT, false, false)
                }
            }
            buyingOrSelling = event.buyingOrSelling
            viewModel.getEventPosts(args.toutlessThreadId, event.buyingOrSelling)
        })
        ssBuyingOrSelling.onSelectedChangeListener = object : StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(@NotNull direction: StickySwitch.Direction, @NotNull text: String) {
                if (direction.name == "LEFT") {
                    buyingOrSelling = BuyingOrSellingField.Buying
                } else if (direction.name == "RIGHT") {
                    buyingOrSelling = BuyingOrSellingField.Selling
                }
                viewModel.updateEventBuyingOrSelling(args.toutlessThreadId, buyingOrSelling)
                viewModel.getEventPosts(args.toutlessThreadId, buyingOrSelling)
                Timber.d("Now Selected : ${direction.name}, Current Text : $text")
            }
        }

        rvPosts.adapter = eventPostsRecyclerAdapter
        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        srSwipe.setOnRefreshListener(this)

        viewModel.eventPostsListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    srSwipe.isRefreshing = false
                    rvPosts.visibility = View.VISIBLE
                    eventPostsRecyclerAdapter.submitList(it.data)
                }
                Status.ERROR -> {
                    srSwipe.isRefreshing = false
                    Toast.makeText(requireContext(), it.message!!, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onEventPostClicked(toutlessPostId: String) {
        Timber.d("event post clicked")
    }

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

    override fun onRefresh() {
        updateListFromServer()
    }

    private fun updateListFromServer() {
        srSwipe.isRefreshing = true
        viewModel.getEventPostsRemote(args.toutlessThreadId, buyingOrSelling)
    }
}
