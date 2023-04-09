package ru.netology.nework.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
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
        var startString = ""
        var finishString: String? = null
        binding.apply {
            jobList.adapter = adapter
            feedButton.setOnClickListener {
                findNavController().navigateUp()
            }
            addJobButton.setOnClickListener {
                addJobContainer.isVisible = true
                addJobButton.isVisible = false
            }
            jobStartDate.setOnClickListener {
                val dateDialog = DatePickerDialog(requireContext())
                dateDialog.setOnDateSetListener { datePicker, y, m, d ->
                    startString = setDate(y,m,d,jobStartDate)
                }
                dateDialog.show()
            }
            jobFinishDate.setOnClickListener {
                val dateDialog = DatePickerDialog(requireContext())
                dateDialog.apply {
                    setOnDateSetListener { datePicker, y, m, d ->
                        finishString = setDate(y,m,d,jobFinishDate)
                    }
                    setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.remove)) { _, _ ->
                        jobFinishDate.setText(R.string.job_finish_date)
                        finishString = null
                    }
                }
                dateDialog.show()
            }

            sendJobButton.setOnClickListener {
                if (jobName.text?.isBlank() == true || jobPosition.text?.isBlank() == true || startString.isBlank()) {
                    Toast.makeText(
                        context,
                        getString(R.string.job_not_filled),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    viewModel.changeContent(
                        jobName.text.toString(),
                        jobPosition.text.toString(),
                        startString,
                        finishString,
                        jobLink.text.toString()
                    )
                    viewModel.saveNewJob()
                    addJobButton.isVisible = true
                    addJobContainer.isVisible = false
                }
            }
        }

        viewModel.apply {
            jobsData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            dataState.observe(viewLifecycleOwner) {
                when (it) {
                    FeedModelState.Error -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.load_jobs_error),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Retry") {
                                viewModel.saveNewJob()
                            }
                            .show()
                    }
                    else -> {}
                }
                binding.loading.isVisible = it == FeedModelState.Loading
            }
            newJobLoadError.observe(viewLifecycleOwner) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.new_job_error),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Retry") {
                        load()
                    }
                    .show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.apply {
                if (addJobContainer.isVisible == true) {
                    addJobContainer.isVisible = false
                    addJobButton.isVisible = true
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setDate(y: Int, m: Int, d: Int, button: MaterialButton) : String{
        //"yyyy-mm-ddT15:40:11.996Z" making this type of string
        val yString = y.toString().padStart(4, '0')
        val mString = (m + 1).toString().padStart(2, '0')
        val dString = d.toString().padStart(2, '0')
        val date = "$dString $mString $yString"
        button.text = date
        return "$yString-$mString-${dString}T00:00:01.000Z"
    }
}