package com.along.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridLayout;

/**
 * TODO: document your custom view class.
 */
public class GameView extends GridLayout {

    private GestureDetector myGestureDetector;
    private int columns = 4;
    private int margin = 10;

    public ItemVIEW[][] cards = new ItemVIEW[columns][columns];

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Load attributes
        myGestureDetector = new GestureDetector(this.getContext(), new gameGestureDetector());
    }

    boolean once = false;

    @Override

    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (!once) {
            startGame();
        }
        once = true;
    }

    boolean hasStartedGame = false;

    public void startGame() {
        if (!hasStartedGame) {
            addCards();
            hasStartedGame = true;
        }

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < columns; y++) {
                cards[x][y].setNum(0);
            }
        }
        addRandomNum();
        addRandomNum();
        addRandomNum();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        myGestureDetector.onTouchEvent(event);
        return true;
    }


    public void addCards() {
        ItemVIEW card;
        int num = 0;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < columns; y++) {
                card = new ItemVIEW(getContext());
                card.setNum(num);
                GridLayout.LayoutParams lp = new LayoutParams();
                lp.setMargins(0, margin, margin, 0);
                lp.width = (getMeasuredWidth() - margin * (columns - 1) - getContext().getResources().getInteger(R.integer.activity_paddingInt) * 2) / columns;
                lp.height = (getMeasuredWidth() - margin * (columns - 1) - getContext().getResources().getInteger(R.integer.activity_paddingInt) * 2) / columns;
                addView(card, lp);
                cards[x][y] = card;
            }
        }
    }

    private void addRandomNum() {
        int addLine;
        int addRow;
        while (true) {
            addLine = (int) (Math.random() * 4);
            addRow = (int) (Math.random() * 4);
            if (cards[addLine][addRow].getNum() == 0) {
                cards[addLine][addRow].setNum(Math.random() > 0.75 ? 4 : 2);
                break;
            }
        }

    }

    private boolean isFull() {
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < columns; y++) {
                if (cards[x][y].getNum() == 0) return false;
            }
        }
        return true;
    }

    private boolean isMovedOrMerged = false;

    class gameGestureDetector extends GestureDetector.SimpleOnGestureListener {
        final int FLING_MIN_DISTANCE = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            if (x > FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                action(Action.RIGHT);
            } else if (x < -FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                action(Action.LEFT);
            } else if (y > FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                action(Action.DOWN);
            } else if (y < -FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                action(Action.UP);
            }
            if (!isFull() && isMovedOrMerged) {
                addRandomNum();
                isMovedOrMerged = false;
            }
            if (checkOver()) {
                MainActivity.getMainActivity().gameOver();
            }
            return true;
        }
    }

    private boolean checkOver() {
        if (!isFull()) {
            return false;
        }
        for (int x = 0; x < columns - 1; x++) {
            for (int y = 0; y < columns - 1; y++) {
                if (cards[x][y].getNum() == cards[x][y + 1].getNum() && x < columns && y < columns) {
                    return false;
                }
                if (cards[x][y].getNum() == cards[x + 1][y].getNum() && x < columns && y < columns) {
                    return false;
                }
            }
        }
        if (cards[columns - 1][columns - 1].getNum() == cards[columns - 2][columns - 1].getNum() ||
                cards[columns - 1][columns - 1].getNum() == cards[columns - 1][columns - 2].getNum())
            return false;
        return true;
    }

    public enum Action {
        LEFT, RIGHT, UP, DOWN
    }

    public void action(Action action) {
        if (action.toString().equals("LEFT")) {
            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < columns; y++) {
                    for (int nextYs = y + 1; nextYs < columns; nextYs++) {
                        if (cards[x][y].getNum() == cards[x][nextYs].getNum() && cards[x][y].getNum() != 0) {
                            cards[x][y].setNum(cards[x][nextYs].getNum() * 2);
                            cards[x][nextYs].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            isMovedOrMerged = true;
                            y--;
                            break;
                        }
                        if (cards[x][y].getNum() != cards[x][nextYs].getNum() && cards[x][nextYs].getNum() != 0 && cards[x][y].getNum() != 0) {
                            break;
                        }
                        if (cards[x][y].getNum() == 0 && cards[x][nextYs].getNum() != 0) {
                            cards[x][y].setNum(cards[x][nextYs].getNum());
                            cards[x][nextYs].setNum(0);
                            isMovedOrMerged = true;

                            y--;
                            break;
                        }
                    }
                }
            }
        }
        if (action.toString().equals("RIGHT")) {
            for (int x = 0; x < columns; x++) {
                for (int y = columns - 1; y >= 0; y--) {
                    for (int nextYs = y - 1; nextYs >= 0; nextYs--) {
                        if (cards[x][y].getNum() == cards[x][nextYs].getNum() && cards[x][y].getNum() != 0) {
                            cards[x][y].setNum(cards[x][nextYs].getNum() * 2);
                            cards[x][nextYs].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            isMovedOrMerged = true;

                            y++;

                            break;
                        }
                        if (cards[x][y].getNum() != cards[x][nextYs].getNum() && cards[x][nextYs].getNum() != 0 && cards[x][y].getNum() != 0) {
                            break;
                        }
                        if (cards[x][y].getNum() == 0 && cards[x][nextYs].getNum() != 0) {
                            cards[x][y].setNum(cards[x][nextYs].getNum());
                            cards[x][nextYs].setNum(0);
                            isMovedOrMerged = true;

                            y++;
                            break;
                        }
                    }
                }
            }
        }
        if (action.toString().equals("UP")) {
            for (int y = 0; y < columns; y++) {
                for (int x = 0; x < columns; x++) {
                    for (int nextXs = x + 1; nextXs < columns; nextXs++) {
                        if (cards[x][y].getNum() == cards[nextXs][y].getNum() && cards[x][y].getNum() != 0) {
                            cards[x][y].setNum(cards[nextXs][y].getNum() * 2);
                            cards[nextXs][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            isMovedOrMerged = true;

                            x--;

                            break;
                        }
                        if (cards[x][y].getNum() != cards[nextXs][y].getNum() && cards[nextXs][y].getNum() != 0 && cards[x][y].getNum() != 0) {
                            break;
                        }
                        if (cards[x][y].getNum() == 0 && cards[nextXs][y].getNum() != 0) {
                            cards[x][y].setNum(cards[nextXs][y].getNum());
                            cards[nextXs][y].setNum(0);
                            isMovedOrMerged = true;

                            x--;
                            break;
                        }
                    }
                }
            }
        }
        if (action.toString().equals("DOWN")) {
            for (int y = 0; y < columns; y++) {
                for (int x = columns - 1; x >= 0; x--) {
                    for (int nextXs = x - 1; nextXs >= 0; nextXs--) {
                        if (cards[x][y].getNum() == cards[nextXs][y].getNum() && cards[x][y].getNum() != 0) {
                            cards[x][y].setNum(cards[nextXs][y].getNum() * 2);
                            cards[nextXs][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            isMovedOrMerged = true;

                            x++;
                            break;
                        }
                        if (cards[x][y].getNum() != cards[nextXs][y].getNum() && cards[nextXs][y].getNum() != 0 && cards[x][y].getNum() != 0) {
                            break;
                        }
                        if (cards[x][y].getNum() == 0 && cards[nextXs][y].getNum() != 0) {
                            cards[x][y].setNum(cards[nextXs][y].getNum());
                            cards[nextXs][y].setNum(0);
                            isMovedOrMerged = true;

                            x++;
                            break;
                        }
                    }
                }
            }
        }
    }
}