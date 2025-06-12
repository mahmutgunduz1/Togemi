package com.mahmutgunduz.togemi.service

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahmutgunduz.togemi.R

class AnimatedBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private val indicatorPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val glowPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = context.getColor(R.color.accent_green)
        alpha = 80
    }
    
    private val highlightPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val path = Path()
    private val rect = RectF()
    private var selectedItemPosition = 0
    private var indicatorX = 0f
    private var indicatorRadius = 8f
    private var maxIndicatorRadius = 12f
    private var isAnimating = false
    private var pulseAnimator: ValueAnimator? = null
    
    // For the active item highlight
    private var activeItemWidth = 0f
    private var activeItemLeft = 0f
    
    init {
        setOnItemSelectedListener { item ->
            animateIndicator(item)
            true
        }
        
        // Set elevation for shadow
        elevation = 24f
        
        // Start pulse animation
        startPulseAnimation()
    }
    
    private fun startPulseAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(indicatorRadius, maxIndicatorRadius, indicatorRadius).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                indicatorRadius = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
    
    private fun animateIndicator(item: MenuItem): Boolean {
        if (isAnimating) return false
        isAnimating = true
        
        val newPosition = getMenuItemPosition(item.itemId)
        
        // Find the view at the new position
        val itemView = getChildAt(0).findViewById<View>(item.itemId)
        val targetX = itemView.x + itemView.width / 2
        
        // Update active item position for highlight
        activeItemWidth = itemView.width.toFloat()
        activeItemLeft = itemView.x
        
        // Scale animation for selected item
        val scaleUpX = ObjectAnimator.ofFloat(itemView, "scaleX", 1f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(itemView, "scaleY", 1f, 1.2f)
        val moveUp = ObjectAnimator.ofFloat(itemView, "translationY", 0f, -8f)
        
        // Scale down animation for previously selected item
        val oldItemView = getChildAt(0).findViewById<View>(getMenu().getItem(selectedItemPosition).itemId)
        val scaleDownX = ObjectAnimator.ofFloat(oldItemView, "scaleX", 1.2f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(oldItemView, "scaleY", 1.2f, 1f)
        val moveDown = ObjectAnimator.ofFloat(oldItemView, "translationY", -8f, 0f)
        
        // Indicator animation
        val indicatorAnim = ObjectAnimator.ofFloat(this, "indicatorX", indicatorX, targetX)
        indicatorAnim.interpolator = DecelerateInterpolator(1.5f)
        
        // Create animation set
        val animSet = AnimatorSet()
        animSet.playTogether(scaleUpX, scaleUpY, moveUp, scaleDownX, scaleDownY, moveDown, indicatorAnim)
        animSet.duration = 400
        animSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                selectedItemPosition = newPosition
                isAnimating = false
            }
        })
        animSet.start()
        
        return true
    }
    
    private fun getMenuItemPosition(itemId: Int): Int {
        for (i in 0 until menu.size()) {
            if (menu.getItem(i).itemId == itemId) {
                return i
            }
        }
        return 0
    }
    
    // Custom property for animation
    fun setIndicatorX(x: Float) {
        indicatorX = x
        invalidate()
    }
    
    fun getIndicatorX(): Float {
        return indicatorX
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Set initial indicator position
        post {
            val firstItem = getChildAt(0).findViewById<View>(selectedItemId)
            if (firstItem != null) {
                indicatorX = firstItem.x + firstItem.width / 2
                activeItemWidth = firstItem.width.toFloat()
                activeItemLeft = firstItem.x
                invalidate()
            }
        }
        
        // Create gradient for indicator
        updateGradients()
    }
    
    private fun updateGradients() {
        // Create radial gradient for the indicator
        indicatorPaint.shader = RadialGradient(
            0f, 0f, maxIndicatorRadius * 1.5f,
            intArrayOf(Color.parseColor("#4CAF50"), Color.parseColor("#388E3C")),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        
        // Create linear gradient for the highlight
        highlightPaint.shader = LinearGradient(
            0f, 0f, 0f, 40f,
            intArrayOf(Color.parseColor("#404CAF50"), Color.parseColor("#00388E3C")),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (indicatorX > 0) {
            // Draw highlight for active item
            if (activeItemWidth > 0) {
                canvas.save()
                rect.set(activeItemLeft + 10, 0f, activeItemLeft + activeItemWidth - 10, 40f)
                path.reset()
                path.addRoundRect(rect, 20f, 20f, Path.Direction.CW)
                canvas.clipPath(path)
                canvas.drawRect(rect, highlightPaint)
                canvas.restore()
            }
            
            // Save canvas state for indicator drawing
            canvas.save()
            canvas.translate(indicatorX, 12f)
            
            // Draw glow effect
            canvas.drawCircle(0f, 0f, indicatorRadius + 8, glowPaint)
            
            // Draw indicator dot
            canvas.drawCircle(0f, 0f, indicatorRadius, indicatorPaint)
            canvas.restore()
        }
    }
    
    override fun onDetachedFromWindow() {
        pulseAnimator?.cancel()
        super.onDetachedFromWindow()
    }
} 