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
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        val item = getItem(position)

        if (payloads.isEmpty()) {
            holder.bind(item)
        } else {
            val payloadList = payloads.map { it as CartItemPayload }
            holder.bind(item, payloadList)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartMenuItem>() {
        override fun areItemsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: CartMenuItem,
            newItem: CartMenuItem
        ): CartItemPayload? {
            val payload = CartItemPayload(
                count = newItem.count.takeIf { it != oldItem.count }
            )
            return payload.takeIf { it != CartItemPayload.EMPTY }
        }
    }
}

class CartItemViewHolder(
    private val binding: ItemCartItemBinding,
    private val interactionListener: CartItemInteractionListener?
) : RecyclerView.ViewHolder(binding.root) {

    private var item: CartMenuItem? = null

    init {
        with(binding) {
            buttonRemove.setOnClickListener {
                item?.let {
                    interactionListener?.onRemove(it, bindingAdapterPosition)
                }
            }
            buttonAdd.setOnClickListener {
                item?.let {
                    interactionListener?.onAdd(it, bindingAdapterPosition)
                }
            }
        }
    }

    fun bind(cartMenuItem: CartMenuItem) {
        item = cartMenuItem

        with(binding) {
            name.text = cartMenuItem.name
            price.text = itemView.context.getString(R.string.price, cartMenuItem.price)
            updateCount(cartMenuItem.count)
        }
    }

    fun bind(cartMenuItem: CartMenuItem, payloads: List<CartItemPayload>) {
        bind(cartMenuItem)

        payloads.forEach { payload ->
            payload.count?.let(::updateCount)
        }
    }

    private fun updateCount(count: Int) {
        binding.count.text = count.toString()
        binding.buttonRemove.isEnabled = count > 0
    }
}

data class CartItemPayload(
    val count: Int? = null
) {
    companion object {
        val EMPTY = CartItemPayload()
    }
}
