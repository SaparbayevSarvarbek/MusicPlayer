package com.example.navigationmenu.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.navigationmenu.R
import com.example.navigationmenu.adapters.RecycleAdapter
import com.example.navigationmenu.databinding.FragmentHomeBinding
import com.example.navigationmenu.models.Audio
import com.example.navigationmenu.service.MyService
import com.example.navigationmenu.ui.slideshow.SlideshowFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.apply {
            if (checkPermission()) {
                binding.apply {
                    val list = loadAudioFiles()
                    recycle.adapter =
                        RecycleAdapter(requireContext(), list, object : RecycleAdapter.onClick {
                            override fun onItemClick(position: Int) {
                                val fm = SlideshowFragment()
                                val bundle = Bundle()
                                bundle.putSerializable("audio", list[position])
                                fm.arguments = bundle
                                findNavController().navigate(R.id.playMusicFragment, bundle)
                                val intent= Intent(requireContext(), MyService::class.java)
                                intent.putExtra("musicList",list[position])
                                requireActivity().startService(intent)
                            }
                        })
                    recycle.layoutManager = GridLayoutManager(requireContext(), 2)
                    val images = list[0].data?.let { getAlbumArt(it) }
                    Glide.with(requireContext()).asBitmap().load(images).into(image)
                    musicName.text=list[0].musicName
                }
            }
        }
        return binding.root
    }
    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            loadAudioFiles()
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAudioFiles()
        }
    }

    private fun loadAudioFiles(): List<Audio> {
        val audioList = mutableListOf<Audio>()
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE
        )
        val cursor = activity?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            while (it.moveToNext()) {
                val data = it.getString(dataIndex)
                val name = it.getString(nameIndex)
                val artist = it.getString(artistIndex)
                val duration = it.getString(durationIndex)
                val title = it.getString(titleIndex)
                val audio = Audio(data, name, artist, duration, title)
                audioList.add(audio)
            }
            cursor.close()
        }
        return audioList
    }
    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}