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

class MenuItemAdapter(
    private val interactionListener: MenuItemInteractionListener? = null
) : ListAdapter<CartMenuItem, CartMenuItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartMenuItemViewHolder {
        val binding = ItemMenuPositionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartMenuItemViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: CartMenuItemViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(
        holder: CartMenuItemViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        val item = getItem(position)

        if (payloads.isEmpty()) {
            holder.bind(item)
        } else {
            val payloadList = payloads.map { it as CartMenuItemPayload }
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
        ): CartMenuItemPayload? {
            val payload = CartMenuItemPayload(
                count = newItem.count.takeIf { it != oldItem.count }
            )
            return payload.takeIf { it != CartMenuItemPayload.EMPTY }
        }
    }
}

class CartMenuItemViewHolder(
    private val binding: ItemMenuPositionBinding,
    private val interactionListener: MenuItemInteractionListener? = null
) : RecyclerView.ViewHolder(binding.root) {

    private var item: CartMenuItem? = null

    init {
        with(binding) {
            itemView.setOnClickListener {
                item?.let {
                    interactionListener?.onClick(it, bindingAdapterPosition)
                }
            }
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
            image.load(cartMenuItem.imageUrl) {
                crossfade(true)
            }

            name.text = cartMenuItem.name
            price.text = itemView.context.getString(R.string.price, cartMenuItem.price)
            updateCount(cartMenuItem.count)
        }
    }

    fun bind(cartMenuItem: CartMenuItem, payloads: List<CartMenuItemPayload>) {
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

data class CartMenuItemPayload(
    val count: Int? = null
) {
    companion object {
        val EMPTY = CartMenuItemPayload()
    }
}
