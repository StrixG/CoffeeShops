package com.obrekht.coffeeshops.coffeeshops.ui.nearby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.databinding.ItemCoffeeShopBinding

class NearbyCoffeeShopsAdapter(
    private val clickListener: OnCoffeeShopClickListener? = null
) : ListAdapter<CoffeeShop, CoffeeShopViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeShopViewHolder {
        val binding = ItemCoffeeShopBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CoffeeShopViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CoffeeShopViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<CoffeeShop>() {
        override fun areItemsTheSame(oldItem: CoffeeShop, newItem: CoffeeShop): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CoffeeShop, newItem: CoffeeShop): Boolean =
            oldItem == newItem
    }
}

class CoffeeShopViewHolder(
    private val binding: ItemCoffeeShopBinding,
    private val clickListener: OnCoffeeShopClickListener? = null
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(coffeeShop: CoffeeShop) {
        itemView.setOnClickListener {
            clickListener?.onClick(coffeeShop, bindingAdapterPosition)
        }

        with(binding) {
            name.text = coffeeShop.name
            distance.text = coffeeShop.distance?.let {
                itemView.context.getString(R.string.distance_to_me_meters, it)
            } ?: itemView.context.getString(R.string.unknown_distance)
        }
    }
}

fun interface OnCoffeeShopClickListener {
    fun onClick(coffeeShop: CoffeeShop, position: Int)
}
