package kimetsu.no.yaiba.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kimetsu.no.yaiba.data.VolumeManager
import kimetsu.no.yaiba.databinding.ItemVolumeBinding
import kimetsu.no.yaiba.models.Volume

class VolumeAdapter(
    private var volumes: List<Volume>,
    private val onVolumeClick: (Volume) -> Unit
) : RecyclerView.Adapter<VolumeAdapter.VolumeViewHolder>() {

    class VolumeViewHolder(val binding: ItemVolumeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolumeViewHolder {
        val binding = ItemVolumeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolumeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VolumeViewHolder, position: Int) {
        val volume = volumes[position]
        val volumeManager = VolumeManager(holder.itemView.context)
        val progressPercent = volumeManager.getProgressPercent(volume.id)

        holder.binding.volumeTitle.text = "Volume ${volume.volumeNumber}"
        holder.binding.volumeSubtitle.text = volume.title
        holder.binding.volumeProgress.progress = progressPercent

        // In a real app, we would load the actual image using Glide
        // Glide.with(holder.itemView.context).load(volume.coverImageUrl).into(holder.binding.volumeCover)

        holder.itemView.setOnClickListener { onVolumeClick(volume) }
    }

    override fun getItemCount() = volumes.size

    fun updateVolumes(newVolumes: List<Volume>) {
        volumes = newVolumes
        notifyDataSetChanged()
    }
}
