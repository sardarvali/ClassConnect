package com.syed.classconnect.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.databinding.ItemOnboardingPageBinding

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

class OnboardingAdapter(private val pages: List<OnboardingPage>) :
    RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

<<<<<<< HEAD
    inner class ViewHolder(private val b: ItemOnboardingPageBinding) : RecyclerView.ViewHolder(b.root) {
=======
    class ViewHolder(private val b: ItemOnboardingPageBinding) :
        RecyclerView.ViewHolder(b.root) {
>>>>>>> final
        fun bind(page: OnboardingPage) {
            b.tvTitle.text = page.title
            b.tvDescription.text = page.description
            b.ivImage.setImageResource(page.imageRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
<<<<<<< HEAD
        ViewHolder(ItemOnboardingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
=======
        ViewHolder(
            ItemOnboardingPageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
>>>>>>> final

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(pages[position])
    override fun getItemCount() = pages.size
}

