package com.example.rubiksolver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*
import kotlin.Comparator

class MainActivity : AppCompatActivity() {
    companion object {

        val TAG = MainActivity::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable { RubikSolver_v2.main(null) }).start()
        /*val queue = PriorityQueue<Int>(MyMinComparator())
        queue.add(10)
        queue.add(20)
        queue.add(30)
        queue.add(3)
        queue.add(5)
        queue.add(7)
        while (!queue.isEmpty()) {
            Log.e(TAG, "Item : " + queue.poll())
        }*/
    }


    class MyMaxComparator : Comparator<Int> {
        override fun compare(o1: Int, o2: Int): Int {
            if (o1 > o2) {
                return -1
            } else if (o2 > o1) {
                return 1
            } else {
                return 0
            }
        }
    }


    class MyMinComparator : Comparator<Int> {
        override fun compare(o1: Int, o2: Int): Int {
            if (o1 > o2) {
                return 1
            } else if (o2 > o1) {
                return -1
            } else {
                return 0
            }
        }
    }

}