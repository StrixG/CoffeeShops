package com.obrekht.coffeeshops.cart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.databinding.ItemCartItemBinding

class CartItemAdapter(
    private val interactionListener: CartItemInteractionListener? = null
) : ListAdapter<CartMenuItem, CartItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ItemCartItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartItemViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
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

class CartItemViewHolder(
    private val binding: ItemCartItemBinding,
    private val interactionListener: CartItemInteractionListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cartMenuItem: CartMenuItem) {
        with(binding) {
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
