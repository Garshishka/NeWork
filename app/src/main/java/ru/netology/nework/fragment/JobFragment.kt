package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nework.R
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.databinding.FragmentJobsBinding
import ru.netology.nework.dto.FeedModelState
import ru.netology.nework.viewmodel.JobViewModel

class JobFragment : Fragment() {
    private val viewModel: JobViewModel by activityViewModels()

    private val adapter = JobAdapter()
    lateinit var binding: FragmentJobsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        //viewModel.loadPersonJobs(155)
        viewModel.load()

        subscribe()

        return binding.root
    }

    private fun subscribe() {
        binding.apply {
            jobList.adapter = adapter
            feedButton.setOnClickListener {
                findNavController().navigateUp()
            }
            addJobButton.setOnClickListener {
                addJobContainer.isVisible = true
            }
            sendJobButton.setOnClickListener {
                viewModel.saveNewJob(jobName.text.toString(),jobPosition.text.toString())
            }
        }

        viewModel.apply {
            jobsData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            dataState.observe(viewLifecycleOwner){
                when(it){
                    FeedModelState.Error -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.load_jobs_error),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Retry") {
                                load()
                            }
                            .show()
                    }
                    else -> {}
                }
                binding.loading.isVisible = it == FeedModelState.Loading
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(binding.addJobContainer.isVisible == false){
                findNavController().navigateUp()
            } else{
             binding.addJobContainer.isVisible = false
            }
        }
    }
}