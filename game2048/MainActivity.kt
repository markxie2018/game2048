package markxie.game.game2048

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import markxie.game.game2048.extension.toast

class MainActivity : AppCompatActivity() {


    private lateinit var mGestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGestureDetector = GestureDetectorCompat(this, InputListener(gameView))

        bind()
        initLiveData()

    }

    private fun bind() {
        b_start.setOnClickListener {
            gameView.initGame()
        }

        gameView.setOnTouchListener { _, event ->
            mGestureDetector.onTouchEvent(event)
        }
    }

    private fun initLiveData() {
        gameView.gameStatus.observe(this, Observer {
            it?.run {
                when (it) {
                    GameStatus.WIN -> {
                        toast("你贏惹")
                    }
                    GameStatus.LOST -> {
                        toast("你輸惹")
                    }
                    GameStatus.START -> {

                    }
                    else -> {
                    }
                }
            }
        })
    }
}
