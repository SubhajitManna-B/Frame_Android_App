package com.ovaku.ui.main.like

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ovaku.data.models.event.eventImage.EventImagePayload
import com.ovaku.data.models.event.eventImage.EventImageUpdate
import com.ovaku.data.models.item.MainItem
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.databinding.FragmentLikeBinding
import com.ovaku.ui.main.MainViewModel
import com.ovaku.ui.main.adapter.EventImageAdapter
import com.ovaku.ui.main.home.HomeViewModel
import com.ovaku.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikeFragment : Fragment() {
    private lateinit var binding: FragmentLikeBinding
    private val likeViewModel by viewModels<LikeViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private var eventImageAdapter: EventImageAdapter? = null
    private val eventImageList = mutableListOf<EventImagePayload>()
    private val eventImageUpdateList = mutableListOf<EventImageUpdate>()
    private var userId = -1
    private var authToken = ""
    private var eventId = -1
    private var click = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initObserver()
    }

    private fun initObserver() {

        mainViewModel.preferenceUserStatus.observe(this){
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        it.data?.let {user->
                            userId = user.id
                            authToken = user.accessToken
                        }
                    }
                }
                is Resource.Error -> {
                    toast(it.message!!)
                }
            }
        }
        mainViewModel.eventId.observe(viewLifecycleOwner){id->
            eventId = id
            likeViewModel.fetchEventImages(userId = userId, eventId = eventId, authToken = authToken)
        }

        likeViewModel.fetchEventImageStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    lifecycleScope.launch {
                        it.data?.let {data->
                            eventImageList.clear()
                            eventImageList.addAll(data.payload)
                            eventImageAdapter?.updateData(eventImageList)
                        }
                    }
                }
                is Resource.Error -> {
                    eventImageList.clear()
                    eventImageAdapter?.updateData(eventImageList)
                    mainViewModel.progressView.value = false
                }
            }
        }
    }

    private fun initAdapter() {
        eventImageAdapter = EventImageAdapter(eventImageList, object: EventImageAdapter.OnInteraction{
            override fun onLike(eventImagePayload: EventImagePayload, position: Int) {}

            override fun onFavourite(eventImagePayload: EventImagePayload, position: Int) {
                eventImageUpdateList.clear()
                eventImageUpdateList.add(EventImageUpdate(eventImagePayload.id, "LOVE"))
                lifecycleScope.launch {
                    likeViewModel.updateEventImages(
                        userId = userId, eventId = eventId, authToken = authToken, eventImageUpdateList
                    )
                    click = true
                    imageUpdateObserver(position)
                }
            }

            override fun onDislike(eventImagePayload: EventImagePayload, position: Int) {
                eventImageUpdateList.clear()
                eventImageUpdateList.add(EventImageUpdate(eventImagePayload.id, "REJECT"))
                lifecycleScope.launch {
                    likeViewModel.updateEventImages(
                        userId = userId, eventId = eventId, authToken = authToken, eventImageUpdateList
                    )
                    click = true
                    imageUpdateObserver(position)
                }
            }
        })

        binding.rvItem.adapter = eventImageAdapter
    }

    private fun imageUpdateObserver(position: Int) {

        likeViewModel.updateEventImageStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    if(click) {
                        click = false
                        eventImageAdapter?.notifyItemRemoved(position)
                        eventImageList.removeAt(position)
                    }
                }
                is Resource.Error -> {
                    mainViewModel.progressView.value = false
                }
            }
        }
    }
}