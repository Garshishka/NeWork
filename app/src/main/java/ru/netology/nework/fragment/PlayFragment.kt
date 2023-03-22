package ru.netology.nework.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentPlayBinding
import ru.netology.nework.utils.BooleanArg
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.StringArg


class PlayFragment : Fragment() {
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlayBinding.inflate(inflater,container,false)

        val player = mediaObserver.mediaPlayer
        val videoPlayer = binding.video
        if (arguments?.isVideoArg == true){
            binding.picture.isVisible = false
            binding.video.isVisible = true
            arguments?.urlArg?.let { playerPlayVideo(videoPlayer, it) }
        } else{
            binding.picture.isVisible = true
            binding.video.isVisible = false
            arguments?.urlArg?.let { playerPlaySound(player, it) }
        }


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            player?.stop()
            player?.reset()
            videoPlayer.stopPlayback()
            findNavController().navigateUp()
        }

        return binding.root
    }

    fun playerPlayVideo(video: VideoView, url: String){
        video.apply {
            setMediaController(MediaController(requireContext()))
            setVideoURI(Uri.parse(url))
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                stopPlayback()
            }
        }
    }

    fun playerPlaySound(player: MediaPlayer?, url: String){
        player?.apply {
            setDataSource(url)

            setOnPreparedListener {
                it.start()
            }

            setOnCompletionListener {
                player.stop()
            }
            prepareAsync()
        }
    }

    companion object {
        var Bundle.urlArg by StringArg
        var Bundle.isVideoArg by BooleanArg
    }
}