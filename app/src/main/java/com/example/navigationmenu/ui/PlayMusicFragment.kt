package com.example.navigationmenu.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.navigationmenu.R
import com.example.navigationmenu.databinding.FragmentPlayMusicBinding
import com.example.navigationmenu.models.Audio

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PlayMusicFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentPlayMusicBinding
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayMusicBinding.inflate(layoutInflater)
        binding.apply {
            mediaPlayer = MediaPlayer()
            val audio=arguments?.getSerializable("audio") as Audio
            val uri=stringToUri(audio.data)
            mediaPlayer.setDataSource(requireContext(),uri!!)
            mediaPlayer.prepare()
            mediaPlayer.start()
            seekBar.max = mediaPlayer.duration
            val handler = Handler(Looper.getMainLooper())
            handler.post(object : Runnable {
                override fun run() {
                    try {
                        seekBar.progress = mediaPlayer.currentPosition
                        handler.postDelayed(this,1000)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            seekBar.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2){
                        mediaPlayer.seekTo(p1)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
            Glide.with(requireContext()).load(audio.data).into(playMusicImage)
            musicName.text=audio.title
            musicAfter.text=audio.artist
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayMusicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun stringToUri(string: String?): Uri? {
        return try {
            if (!string.isNullOrEmpty()) {
                Uri.parse(string)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}