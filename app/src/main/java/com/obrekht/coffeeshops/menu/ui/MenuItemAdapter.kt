package com.obrekht.coffeeshops.menu.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.databinding.ItemMenuPositionBinding

class CartMenuItemAdapter(
    private val interactionListener: MenuItemInteractionListener? = null
) : ListAdapter<CartMenuItem, CartMenuItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartMenuItemViewHolder {
        val binding = ItemMenuPositionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartMenuItemViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: CartMenuItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<CartMenuItem>() {
        override fun areItemsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
            oldItem == newItem
    }
}

class CartMenuItemViewHolder(
    private val binding: ItemMenuPositionBinding,
    private val interactionListener: MenuItemInteractionListener? = null
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cartMenuItem: CartMenuItem) {
        itemView.setOnClickListener {
            interactionListener?.onClick(cartMenuItem, bindingAdapterPosition)
        }
        with(binding) {
            image.load(cartMenuItem.imageUrl) {
                crossfade(true)
            }

            name.text = cartMenuItem.name
            price.text = itemView.context.getString(R.string.price, cartMenuItem.price)
            count.text = cartMenuItem.count.toString()

            buttonRemove.isEnabled = cartMenuItem.count > 0

            buttonRemove.setOnClickListener {
                interactionListener?.onRemove(cartMenuItem, bindingAdapterPosition)
            }
            buttonAdd.setOnClickListener {
                interactionListener?.onAdd(cartMenuItem, bindingAdapterPosition)
            }
        }
    }
}

