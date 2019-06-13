package com.example.balanceview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.balanceview.volumebalance.PointInfo
import com.example.balanceview.volumebalance.VolumeBalance
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), VolumeBalance.Callback {
    override fun updatePointInfo(pointInfo: PointInfo?) {
    }

    private var mPointInfo: PointInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPointInfo = PointInfo(0, 0, 9, -9)
        balance.setCallback(this)
        balance.init(mPointInfo)
    }
}
