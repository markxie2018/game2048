package markxie.game.game2048

import android.graphics.Point
import android.graphics.RectF

data class Tile(
    var number: Int,
    var point: Point,
    var rect: RectF
) {

    fun isZero(): Boolean = number == 0
}