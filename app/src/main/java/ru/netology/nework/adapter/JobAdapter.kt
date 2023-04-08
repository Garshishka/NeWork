package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.LayoutJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewholder.JobDiffCallBack
import ru.netology.nework.viewholder.JobViewHolder

class JobAdapter : ListAdapter<Job, JobViewHolder>(JobDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = LayoutJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position) ?: return
        holder.bind(job)
    }
}