package ru.netology.nework.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.LayoutJobBinding
import ru.netology.nework.dto.Job
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class JobViewHolder(
    private val binding: LayoutJobBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            name.text = job.name
            position.text = job.position
            start.text = getJobDay(job.start)
            if (job.finish != null) {
                finish.text = getJobDay(job.finish)
            } else {
                finish.setText(R.string.this_day)
            }
            if (job.link != null) {
                link.isVisible = true
                link.text = job.link
            } else {
                link.isVisible = false
            }
        }
    }

    private fun getJobDay(time: String): String {
        val publishedTime = OffsetDateTime.parse(time).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
        return publishedTime.format(formatter)
    }
}