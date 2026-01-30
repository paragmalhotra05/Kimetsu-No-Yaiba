package kimetsu.no.yaiba

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kimetsu.no.yaiba.adapters.VolumeAdapter
import kimetsu.no.yaiba.data.VolumeManager
import kimetsu.no.yaiba.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var volumeManager: VolumeManager
    private lateinit var adapter: VolumeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volumeManager = VolumeManager(this)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Refresh progress indicators when returning to library
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        val volumes = volumeManager.getAllVolumes()
        adapter = VolumeAdapter(volumes) { volume ->
            val intent = Intent(this, ReaderActivity::class.java).apply {
                putExtra("volume_id", volume.id)
            }
            startActivity(intent)
        }

        binding.volumesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.volumesRecyclerView.adapter = adapter
    }
}
