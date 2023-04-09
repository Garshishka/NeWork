package ru.netology.nework.utils

import ru.netology.nework.dto.Job

interface JobInteractionListener {
    fun onEdit(job: Job)
    fun onRemove(job: Job)
}