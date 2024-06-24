package com.example.journalapp

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.journalapp.database.Journal
import com.example.journalapp.databinding.JournalRowBinding

class MyRecyclerViewAdapter(
    private val context: Context,
    private val journalList: List<Journal>,
    private val clickListener: (Journal) -> Unit
) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal, context: Context, clickListener: (Journal) -> Unit) {
            binding.journalRowUsername.text = journal.userName

            // Using Glide to load the image
            Glide.with(context)
                .load(journal.imageUrl)
                .fitCenter()
                .into(binding.journalImageList)

            binding.journalTitleList.text = journal.title
            binding.journalThoughtList.text = journal.thoughts

            val timeAgo = DateUtils.getRelativeTimeSpanString(journal.timeAdded!!.seconds * 1000)
            binding.journalTimestampList.text = timeAgo

            binding.journalRowShareButton.setOnClickListener {
                shareData(context , journal.title , journal.thoughts)
            }

            binding.contentList.setOnClickListener {
                clickListener(journal)
            }
        }

        private fun shareData(context: Context, title: String?, desc: String?) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, desc)
            }
            context.startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: JournalRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.journal_row, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal, context, clickListener)
    }
    }
