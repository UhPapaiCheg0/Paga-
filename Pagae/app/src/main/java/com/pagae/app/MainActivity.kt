package com.pagae.app

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.pagae.app.ui.ProfilesFragment
import com.pagae.app.ui.SaleFragment
import com.pagae.app.ui.TotalsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    private lateinit var tabs: List<MaterialButton>

    // progress[i] vai de 0 (aba inativa: cinza, ícone 100%) até 1 (aba ativa: dourada, ícone 115%)
    private val progress = FloatArray(TAB_COUNT)
    private val animators = arrayOfNulls<ValueAnimator>(TAB_COUNT)
    private val colorEvaluator = ArgbEvaluator()
    private var baseIconPx = 0
    private var selectedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseIconPx = (ICON_SIZE_DP * resources.displayMetrics.density).toInt()

        pager = findViewById(R.id.pager)
        pager.adapter = MainPagerAdapter(this)

        tabs = listOf<MaterialButton>(
            findViewById(R.id.nav_profiles),
            findViewById(R.id.nav_sale),
            findViewById(R.id.nav_totals)
        )

        // Tocar em um botão troca de página; tocar no botão da página aberta não faz nada.
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                if (pager.currentItem != index) pager.setCurrentItem(index, false)
            }
        }

        // Arrastar a tela para o lado também muda de página; o mesmo callback anima os botões
        // (tanto para toque quanto para arrasto).
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabs(position, animate = true)
            }
        })

        // Página de venda é a primeira que o usuário vê ao entrar no app.
        if (savedInstanceState == null) {
            pager.setCurrentItem(TAB_SALE, false)
            updateTabs(TAB_SALE, animate = false)
        }
    }

    override fun onResume() {
        super.onResume()
        // Garante o visual correto depois de girar a tela ou voltar de outra tela.
        updateTabs(pager.currentItem, animate = false)
    }

    private fun updateTabs(index: Int, animate: Boolean) {
        if (index == selectedIndex) return
        selectedIndex = index
        tabs.forEachIndexed { i, tab ->
            tab.isSelected = i == index
            animateTab(i, if (i == index) 1f else 0f, animate)
        }
    }

    private fun animateTab(index: Int, target: Float, animate: Boolean) {
        animators[index]?.cancel()
        if (!animate) {
            applyProgress(index, target)
            return
        }
        val animator = ValueAnimator.ofFloat(progress[index], target)
        animator.duration = 220L
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { applyProgress(index, it.animatedValue as Float) }
        animators[index] = animator
        animator.start()
    }

    /** Aplica cor (cinza -> dourado) e tamanho do ícone (100% -> 115%) do botão. */
    private fun applyProgress(index: Int, p: Float) {
        progress[index] = p
        val tab = tabs[index]
        val color = colorEvaluator.evaluate(p, NAV_GRAY, NAV_GOLD) as Int
        tab.setTextColor(color)
        tab.iconTint = ColorStateList.valueOf(color)
        tab.iconSize = (baseIconPx * (1f + ICON_GROWTH * p)).toInt()
    }

    private class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = TAB_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            TAB_PROFILES -> ProfilesFragment()
            TAB_SALE -> SaleFragment()
            else -> TotalsFragment()
        }
    }

    companion object {
        private const val TAB_COUNT = 3
        private const val TAB_PROFILES = 0
        private const val TAB_SALE = 1
        private const val ICON_SIZE_DP = 24
        private const val ICON_GROWTH = 0.15f
        private val NAV_GOLD = Color.parseColor("#FFC107")
        private val NAV_GRAY = Color.parseColor("#888888")
    }
}
