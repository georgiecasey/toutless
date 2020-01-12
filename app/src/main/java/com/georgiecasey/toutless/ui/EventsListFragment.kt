package com.georgiecasey.toutless.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.georgiecasey.toutless.R
import com.tapadoo.pagerpushpoc.arch.BaseFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_events_list.*
import timber.log.Timber
import javax.inject.Inject

class EventsListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, EventsRecyclerAdapter.OnEventClickListener {
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
        srSwipe.setOnRefreshListener(this)
        viewModel.eventsListLiveData.observe(viewLifecycleOwner, Observer { events ->
            srSwipe.isRefreshing = false
            rvEvents.visibility = View.VISIBLE
            eventsRecyclerAdapter.submitList(events)
        })
        viewModel.getEvents()
    }

    override fun onEventClicked(messageId: String) {
        Timber.d("event clicked")
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
