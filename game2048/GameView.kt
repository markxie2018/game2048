package markxie.game.game2048

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.MutableLiveData
import markxie.game.game2048.extension.debug
import kotlin.math.min


class GameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TILE_COUNT = 4
    private val WIN_NUMBER = 2048

    private lateinit var textPaint: Paint
    private lateinit var rectPaint: Paint

    private var mTextSize = 0
    private var mTileSize = 0

    //2D array
    private var tileArray = arrayOf<Array<Tile>>()

    val gameStatus = MutableLiveData<GameStatus>().apply {
        value = GameStatus.IDLE
    }


    init {
        initPaint()
    }

    private fun initPaint() {
        debug("initPaint")

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        with(textPaint) {
            color = resources.getColor(R.color.color_text)
            strokeWidth = 10f
            textAlign = Paint.Align.CENTER
            setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        }

        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun initView(width: Int, height: Int) {

        val tileSize = (min(width, height) - 10) / TILE_COUNT

        mTileSize = tileSize
        mTextSize = tileSize

        for (i in 0 until TILE_COUNT) {
            var array = arrayOf<Tile>()
            for (j in 0 until TILE_COUNT) {
                val point = Point(i * tileSize + 10, j * tileSize + 10)
                val rect =
                    RectF(
                        point.x.toFloat(),
                        (point.y).toFloat(),
                        (point.x + tileSize).toFloat(),
                        (point.y + tileSize).toFloat()
                    )

                array += Tile(0, point, rect)
            }
            tileArray += array
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        initView(w, h)
    }

    fun initGame() {

        initTileData()
        addNewNumber(true)
        addNewNumber(true)
        postInvalidate()
        gameStatus.value = GameStatus.GAMING
    }

    private fun initTileData() {

        for (i in 0 until TILE_COUNT) {
            for (j in 0 until TILE_COUNT) {
                tileArray[i][j].number = 0
            }
        }
    }

    /**
     * 直向比較邏輯
     *
     * 每個值(a)都需跟該列(y)上的下一個值(b)做比較一次
     * 1. b == 0 時彈出， b != 0 時
     *   若 a == 0 則 a b 交換位置
     *   若 a == b 則 a*2 , b = 0
     *   若 a != b 則什麼都不做
     *
     * 2. 若該列同時存在需要交換與增加數值兩種情況，如下舉例，則該列需要做兩動
     *
     *    舉例 - 若情況為 [0,0,4,4] or [0,4,0,4] 則移動完會變成 [4,4,0,0] 與預期 [8,0,0,0] 不符
     *    該列需要在比對一次，做法為將 y 值減 1
     *
     *
     */
    fun up() {
        if (gameStatus.value != GameStatus.GAMING) return

        var doAction = false
        for (x in 0 until TILE_COUNT) {
            var y = 0
            while (y < TILE_COUNT) {
                debug("y = $y")
                var y1 = y + 1
                while (y1 < TILE_COUNT) {
                    debug("y1 = $y1")
                    //如果第 y 個位置不為0
                    if (!tileArray[x][y1].isZero()) {

                        //如果第一個位置為0
                        if (tileArray[x][y].isZero()) {
                            //將第一個位置=第二個位置的值，第二個位置設為0

                            debug("x = $x , y = $y , y1 = $y1")

                            tileArray[x][y].number = tileArray[x][y1].number
                            tileArray[x][y1].number = 0
                            y--
                            doAction = true
                        } else if (tileArray[x][y1].number == tileArray[x][y].number) {

                            tileArray[x][y].number *= 2
                            tileArray[x][y1].number = 0
                            doAction = true
                        }
                        break
                    }
                    y1++
                }
                y++
            }
        }

        addNewNumber(doAction)
    }

    fun down() {
        if (gameStatus.value != GameStatus.GAMING) return

        var doAction = false
        for (x in 0 until TILE_COUNT) {
            var y = TILE_COUNT - 1
            while (y >= 0) {
                debug("y = $y")
                var y1 = y - 1
                while (y1 >= 0) {
                    debug("y1 = $y1")
                    //如果第 y 個位置不為0
                    if (!tileArray[x][y1].isZero()) {

                        //如果第一個位置為0
                        if (tileArray[x][y].isZero()) {
                            //將第一個位置=第二個位置的值，第二個位置設為0

                            debug("x = $x , y = $y , y1 = $y1")

                            tileArray[x][y].number = tileArray[x][y1].number
                            tileArray[x][y1].number = 0
                            y++
                            doAction = true
                        } else if (tileArray[x][y1].number == tileArray[x][y].number) {

                            tileArray[x][y].number *= 2
                            tileArray[x][y1].number = 0
                            doAction = true
                        }
                        break
                    }
                    y1--
                }
                y--
            }
        }

        addNewNumber(doAction)
    }

    fun right() {
        if (gameStatus.value != GameStatus.GAMING) return

        var doAction = false
        for (y in 0 until TILE_COUNT) {
            var x = TILE_COUNT - 1
            while (x >= 0) {
                debug("x = $x")
                var x1 = x - 1
                while (x1 >= 0) {
                    debug("x1 = $x1")
                    //如果第 y 個位置不為0
                    if (!tileArray[x1][y].isZero()) {

                        //如果第一個位置為0
                        if (tileArray[x][y].isZero()) {
                            //將第一個位置=第二個位置的值，第二個位置設為0

                            debug("y = $y , x = $x , x1 = $x1")

                            tileArray[x][y].number = tileArray[x1][y].number
                            tileArray[x1][y].number = 0
                            x++
                            doAction = true
                        } else if (tileArray[x][y].number == tileArray[x1][y].number) {

                            tileArray[x][y].number *= 2
                            tileArray[x1][y].number = 0
                            doAction = true
                        }
                        break
                    }
                    x1--
                }
                x--
            }
        }

        addNewNumber(doAction)
    }

    fun left() {
        if (gameStatus.value != GameStatus.GAMING) return

        var doAction = false
        for (y in 0 until TILE_COUNT) {
            var x = 0
            while (x < TILE_COUNT) {
                debug("x = $x")
                var x1 = x + 1
                while (x1 < TILE_COUNT) {
                    debug("x1 = $x1")
                    //如果第 y 個位置不為0
                    if (!tileArray[x1][y].isZero()) {

                        //如果第一個位置為0
                        if (tileArray[x][y].isZero()) {
                            //將第一個位置=第二個位置的值，第二個位置設為0

                            debug("y = $y , x = $x , x1 = $x1")

                            tileArray[x][y].number = tileArray[x1][y].number
                            tileArray[x1][y].number = 0
                            x--
                            doAction = true
                        } else if (tileArray[x][y].number == tileArray[x1][y].number) {

                            tileArray[x][y].number *= 2
                            tileArray[x1][y].number = 0
                            doAction = true
                        }
                        break
                    }
                    x1++
                }
                x++
            }
        }

        addNewNumber(doAction)
    }

    /**
     * 在空白處加一個 2 or 4 (9:1)
     */
    private fun addNewNumber(doAction: Boolean) {

        if (!doAction) {
            if (isLose()) {
                gameStatus.value = GameStatus.LOST
                return
            }
        }

        val tmp = mutableListOf<Point>()

        for (i in 0 until TILE_COUNT) {
            for (j in 0 until TILE_COUNT) {
                if (tileArray[i][j].isZero())
                    tmp.add(Point(i, j))
            }
        }

        if (tmp.size == 0) {
            debug("no space")
            if (isLose()) {
                gameStatus.value = GameStatus.LOST
            }
            return
        }

        val r = (Math.random() * tmp.size).toInt()
        tileArray[tmp[r].x][tmp[r].y].number = if (Math.random() > 0.1) 2 else 4

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTileAndNumber(canvas)
    }

    private fun drawTileAndNumber(canvas: Canvas?) {
        canvas?.run {

            textPaint.textSize = mTextSize * 0.8.toFloat()

            for (i in 0 until TILE_COUNT) {
                for (j in 0 until TILE_COUNT) {
                    val tile = tileArray[i][j]

                    if (isWin(tile)) {
                        gameStatus.value = GameStatus.WIN
                    }
                    drawTile(canvas, tile)
                    drawNumber(canvas, tile)
                }
            }
        }
    }

    private fun drawTile(canvas: Canvas?, tile: Tile) {

        when (tile.number) {
            in 2..8 -> {
                rectPaint.color = resources.getColor(R.color.color2)
            }
            in 9..64 -> {
                rectPaint.color = resources.getColor(R.color.color3)
            }
            in 65..512 -> {
                rectPaint.color = resources.getColor(R.color.color4)
            }
            in 513..2048 -> {
                rectPaint.color = resources.getColor(R.color.color5)
            }
            else -> {
                rectPaint.color = resources.getColor(R.color.color1)
            }
        }

        canvas?.drawRect(
            Rect(
                (tile.rect.left + 20).toInt(),
                (tile.rect.top + 20).toInt(),
                (tile.rect.right - 20).toInt(),
                (tile.rect.bottom - 20).toInt()
            ),
            rectPaint
        )
    }

    private fun drawNumber(canvas: Canvas?, tile: Tile) {

        if (tile.number > 0) {

            textPaint.textSize = mTextSize * 0.3f

            canvas?.drawText(
                tile.number.toString(),
                (tile.point.x + (mTileSize / 2)).toFloat(),
                (tile.point.y + mTileSize * 0.6).toFloat(),
                textPaint
            )
        }
    }

    private fun isWin(tile: Tile): Boolean = tile.number >= WIN_NUMBER

    private fun isLose(): Boolean {
        var isLoseB = true
        for (x in 0 until TILE_COUNT) {
            for (y in 0 until TILE_COUNT) {

                if (tileArray[x][y].number > 0) {
                    if (x > 0 && tileArray[x][y].number == tileArray[x - 1][y].number ||
                        x < TILE_COUNT - 1 && tileArray[x][y].number == tileArray[x + 1][y].number ||
                        y > 0 && tileArray[x][y].number == tileArray[x][y - 1].number ||
                        y < TILE_COUNT - 1 && tileArray[x][y].number == tileArray[x][y + 1].number
                    ) {
                        //有一格跟相鄰的相等就還沒結束
                        return false
                    }
                } else {
                    //有一格= 0就還沒結束
                    isLoseB = false
                }
            }
        }
        return isLoseB
    }
}