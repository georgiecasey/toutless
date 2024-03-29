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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.api.Status
import com.tapadoo.pagerpushpoc.arch.BaseFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_events_list.*
import timber.log.Timber
import javax.inject.Inject
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.georgiecasey.toutless.room.entities.Event

class EventsListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, EventsRecyclerAdapter.OnEventClickListener{
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: EventsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel::class.java)
    }

    private val eventsRecyclerAdapter by lazy { EventsRecyclerAdapter(this) }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_events_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvEvents.adapter = eventsRecyclerAdapter
        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        rvEvents.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        val animator = DefaultItemAnimator()
        animator.moveDuration = 500
        rvEvents.itemAnimator = animator
        srSwipe.setOnRefreshListener(this)

        val sizeProvider =
            FixedPreloadSizeProvider<Event>(640, 640)
        val modelProvider = eventsRecyclerAdapter.GlidePreloadModelProvider(requireContext())
        val preloader = RecyclerViewPreloader(
            Glide.with(this), modelProvider, sizeProvider, 50 /*maxPreload*/
        )
        rvEvents.addOnScrollListener(preloader)

        viewModel.eventsListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    srSwipe.isRefreshing = false
                    rvEvents.visibility = View.VISIBLE
                    eventsRecyclerAdapter.setItems(it.data!!)
                }
                Status.ERROR -> {
                    srSwipe.isRefreshing = false
                    Toast.makeText(requireContext(), it.message!!, Toast.LENGTH_LONG).show()
                }
            }
        })
        eventsRecyclerAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                rvEvents.scrollToPosition(toPosition)
            }
        })
        viewModel.getEvents()
    }

    override fun onEventClicked(toutlessThreadId: String) {
        Timber.d("event clicked")
        view?.let {
            val action = EventsListFragmentDirections.actionEventsListToEventPostsList(toutlessThreadId)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onEventFavouriteClicked(toutlessThreadId: String, isFavourite: Boolean) {
        viewModel.toggleEventFavourite(toutlessThreadId, isFavourite)
    }

    override fun onDestroyView() {
        rvEvents.adapter = null
        super.onDestroyView()
    }

    override fun onRefresh() {
        updateListFromServer()
    }

    private fun updateListFromServer() {
        srSwipe.isRefreshing = true
        viewModel.getEventsRemote()
    }
}
