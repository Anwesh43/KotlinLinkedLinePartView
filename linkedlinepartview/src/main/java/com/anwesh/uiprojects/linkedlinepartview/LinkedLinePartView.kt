package com.anwesh.uiprojects.linkedlinepartview

/**
 * Created by anweshmishra on 13/06/18.
 */

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent

val LLP_NODES = 6
class LinkedLinePartView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

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
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = (h / (2 * LLP_NODES))
            val ox : Float = w * (i %2)
            val dx : Float = w/2
            val x : Float = ox + (dx - ox) * state.scale
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
    }
}