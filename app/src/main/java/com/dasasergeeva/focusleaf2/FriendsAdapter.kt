package com.dasasergeeva.focusleaf2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dasasergeeva.focusleaf2.databinding.ItemFriendBinding

class FriendsAdapter(
    private val friends: List<FriendsSearchActivity.Friend>,
    private val onFriendClick: (FriendsSearchActivity.Friend) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendsSearchActivity.Friend) {
            binding.tvFriendName.text = friend.name
            binding.tvFriendAge.text = "${friend.age}"

            // Установка аватара в зависимости от пола
            val avatarRes = when (friend.gender) {
                "Male" -> R.drawable.ic_male_avatar
                "Female" -> R.drawable.ic_female_avatar
                else -> R.drawable.ic_default_avatar
            }
            binding.ivAvatar.setImageResource(avatarRes)

            // Статус онлайн
            binding.vOnlineStatus.visibility = if (friend.isOnline) View.VISIBLE else View.GONE

            // Обработка клика на элемент
            binding.root.setOnClickListener {
                onFriendClick(friend)
            }

            // Обработка клика на иконку чата
            binding.ibChat.setOnClickListener {
                onFriendClick(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    override fun getItemCount(): Int = friends.size
}

