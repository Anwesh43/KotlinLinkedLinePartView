package com.anwesh.uiprojects.linkedlinepartview

/**
 * Created by anweshmishra on 13/06/18.
 */

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent

val LLP_NODES = 6
class LinkedLinePartView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var scale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LLPNode (var i : Int, val state : State = State()) {

        private var next : LLPNode? = null

        private var prev : LLPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < LLP_NODES - 1) {
                next = LLPNode(i + 1)
                next?.prev = this
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            prev?.draw(canvas, paint)
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            paint.style = Paint.Style.STROKE
            val gap : Float = (h / (2 * LLP_NODES))
            val ox : Float = w * (i %2)
            val dx : Float = w/2
            var deg : Float = 360f / LLP_NODES
            val x : Float = ox + (dx - ox) * state.scale
            canvas.save()
            canvas.translate(w/2, h/8)
            val r : Float = Math.min(w, h) / 12
            if (state.scale != 0f) {
                canvas.drawArc(RectF(-r, -r, r, r), deg * i, deg * state.scale, false, paint)
            }
            canvas.restore()
            canvas.save()
            canvas.translate(x, h/4 + gap * i)
            canvas.drawLine(0f, 0f, 0f, gap, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit): LLPNode {
            var curr : LLPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedLinePart (var i : Int) {

        private var curr : LLPNode = LLPNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedLinePartView) {

        private val animator : Animator = Animator(view)

        private val llp : LinkedLinePart = LinkedLinePart(0)

        fun render(canvas: Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#ecf0f1")
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = 11f
            llp.draw(canvas, paint)
            animator.animate {
                llp.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            llp.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : LinkedLinePartView {
            val view : LinkedLinePartView = LinkedLinePartView(activity)
            activity.setContentView(view)
            return view
        }
    }
}