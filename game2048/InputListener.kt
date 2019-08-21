package markxie.game.game2048

import android.view.GestureDetector
import android.view.MotionEvent
import markxie.game.game2048.extension.debug
import kotlin.math.abs

class InputListener(private val gameView: GameView) : GestureDetector.OnGestureListener {

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean = false

    override fun onDown(e: MotionEvent?): Boolean = true


    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

        val minMove = 120   // 最小滑動距離
        val minVelocity = 0 // 最小滑動速度
        val beginX = e1.x
        val endX = e2.x
        val beginY = e1.y
        val endY = e2.y

        debug("$beginX $endX  $beginY   $endY")

        when {
            beginX - endX > minMove && abs(velocityX) > minVelocity -> {
                // 左滑
                gameView.left()
            }
            endX - beginX > minMove && abs(velocityX) > minVelocity -> {
                // 右滑
                gameView.right()
            }

            beginY - endY > minMove && abs(velocityY) > minVelocity -> {
                // 上滑
                gameView.up()
            }
            endY - beginY > minMove && abs(velocityY) > minVelocity -> {
                // 下滑
                gameView.down()
            }
        }
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

    override fun onLongPress(e: MotionEvent?) {
    }
}