package com.skateboard.skcalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by skateboard on 16-6-15.
 */
public class CalendarView extends LinearLayout
{
    private OnCellClickListener onCellClickListener;
    private TextView displayMonth;
    private ViewPager viewPager;
    private ArrayList<CalendarCard> calendarCards;



    public void setNumTextColor(int color)
    {
        if (calendarCards != null)
        {
            for (CalendarCard card : calendarCards)
            {
                card.setNumTextColor(color);
            }
        }
        invalidate();
    }

    public void setCellBackgroundColor(int color)
    {
        if (calendarCards != null)
        {
            for (CalendarCard card : calendarCards)
            {
                card.setCellBackgroundColor(color);
            }
        }
    }

    public void setYear(int year)
    {
        if (calendarCards != null)
        {
            for (CalendarCard card : calendarCards)
            {
                card.setYear(year);
            }
        }
    }

    public void setMonth(int month)
    {
        if (calendarCards != null)
        {
            for (CalendarCard card : calendarCards)
            {
                card.setYear(month);
            }
        }
    }


    public void setOnCellClickListener(OnCellClickListener listener)
    {
        this.onCellClickListener = listener;
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initCalendarCard(context);
        initView(context);
    }

    public CalendarView(Context context)
    {
        super(context);
        initCalendarCard(context);
        initView(context);
    }

    private void initView(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_layout, this, false);
        displayMonth = (TextView) view.findViewById(R.id.display_month);
        displayMonth.setText((Calendar.getInstance().get(Calendar.MONTH) + 1) + "Mon");
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new CardAdapter());
        viewPager.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                displayMonth.setText(position + 1 + "Mon");
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        this.addView(view);
        this.invalidate();
    }

    private void initCalendarCard(Context context)
    {
        calendarCards = new ArrayList<>();
        for (int i = 1; i <= 12; i++)
        {
            CalendarCard calendarCard = new CalendarCard(context);
            calendarCard.setMonth(i);
            calendarCards.add(calendarCard);
        }
    }


    private class CardAdapter extends PagerAdapter
    {


        @Override
        public int getCount()
        {
            return calendarCards.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(calendarCards.get(position));
            return calendarCards.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(calendarCards.get(position));
        }
    }


    private class CalendarCard extends View
    {

        private int width;
        private int height;
        private int cellWidth;
        private int cellHeight;
        private int totalRow;
        private int firstDayPos;
        private int totalDays;
        private int toDrawNum;
        private Paint bgPaint;
        private Paint divPaint;
        private Paint numPaint;
        private Calendar calendar = Calendar.getInstance();
        private int textOffset;

        private int numTextColor = -1;
        private int cellBackgroundColor = -1;


        private void setNumTextColor(int color)
        {
            this.numTextColor = color;
            invalidate();
        }

        private void setCellBackgroundColor(int color)
        {
            this.cellBackgroundColor = color;
            invalidate();
        }


        public CalendarCard(Context context)
        {
            super(context);
            initPaint();
        }

        public CalendarCard(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            initPaint();
        }

        private void initPaint()
        {
            initBgPaint();
            initDivPaint();
            initNumPaint();
        }

        private void initBgPaint()
        {
            bgPaint = new Paint();
            bgPaint.setAntiAlias(true);
            bgPaint.setStyle(Paint.Style.FILL);

        }

        private void initDivPaint()
        {
            divPaint = new Paint();
            divPaint.setAntiAlias(true);
            divPaint.setStyle(Paint.Style.FILL);
            divPaint.setColor(Color.WHITE);
        }

        private void initNumPaint()
        {
            numPaint = new Paint();
            numPaint.setTextAlign(Paint.Align.CENTER);
            numPaint.setAntiAlias(true);
            numPaint.setStyle(Paint.Style.FILL);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST)
            {
                width = 300;
                height = 300;
                setMeasuredDimension(width, height);
            } else
            {
                width = MeasureSpec.getSize(widthMeasureSpec);
                height = MeasureSpec.getSize(heightMeasureSpec);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            cellWidth = width / 7;
            cellHeight = cellWidth;

        }


        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            resetToDrawNum();
            calculateDate();
            drawBg(canvas);
            drawHorLine(canvas);
            drawVerLine(canvas);
        }

        public void setYear(int year)
        {
            calendar.set(Calendar.YEAR, year);
            invalidate();
        }

        public void setMonth(int month)
        {
            calendar.set(Calendar.MONTH, month - 1);
            invalidate();
        }

        private void calculateDate()
        {
            calendar.get(Calendar.DAY_OF_MONTH);
            totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            firstDayPos = calendar.get(Calendar.DAY_OF_WEEK);
            if ((firstDayPos - 1 + totalDays) % 7 == 0)
                totalRow = (firstDayPos - 1 + totalDays) / 7;
            else
                totalRow = (firstDayPos - 1 + totalDays) / 7 + 1;

        }

        private void resetToDrawNum()
        {
            toDrawNum = 1;
        }

        private void drawBg(Canvas canvas)
        {
            if (cellBackgroundColor != -1)
            {
                bgPaint.setColor(cellBackgroundColor);
            }
            else
            {
                bgPaint.setColor(Color.GRAY);
            }
            canvas.drawRect(0, 0, width, totalRow * cellHeight, bgPaint);
        }


        private void drawHorLine(Canvas canvas)
        {

            for (int i = 0; i < totalRow; i++)
            {
                canvas.drawRect(0, cellHeight * (i + 1), width, cellHeight * (i + 1) + 2, divPaint);
            }
        }

        private void drawVerLine(Canvas canvas)
        {

            for (int i = 0; i <= 6; i++)
            {
                canvas.drawRect(i * cellWidth, 0, i * cellWidth + 2, totalRow * cellHeight, divPaint);
            }
            drawNumber(canvas);
        }

        private void drawNumber(Canvas canvas)
        {
            wrapNumpaint(cellHeight/2);
            textOffset = getTextOffset();
            drawFirstLine(canvas);
            drawMidLine(canvas);
            drawBottomLine(canvas);
        }

        private void wrapNumpaint(int size)
        {
            setNumPaintTextSize(size);
            if(numTextColor!=-1)
            {
                numPaint.setColor(numTextColor);
            }
            else
            {
                numPaint.setColor(Color.WHITE);
            }
        }

        private void setNumPaintTextSize(int size)
        {
            numPaint.setTextSize(size);
        }

        private int getTextOffset()
        {
            Paint.FontMetricsInt fontMetricsInt = numPaint.getFontMetricsInt();
            int ascent = fontMetricsInt.ascent;
            int descent = fontMetricsInt.descent;
            int offset = (descent - ascent) / 2 - descent;
            return offset;
        }

        private void drawFirstLine(Canvas canvas)
        {

            for (int i = firstDayPos; i <= 7; i++)
            {
                canvas.drawText((i - firstDayPos + 1) + "", i * cellWidth - cellWidth / 2, cellHeight / 2 + textOffset, numPaint);
                toDrawNum++;
            }
        }

        private void drawMidLine(Canvas canvas)
        {

            if ((totalDays + firstDayPos - 1 - 7) % 7 == 0)
            {
                for (int i = 1; i <= totalRow - 1; i++)
                {
                    for (int j = 0; j < 7; j++)
                    {
                        canvas.drawText(toDrawNum + "", j * cellWidth + cellWidth / 2, i * cellHeight + cellHeight / 2 + textOffset, numPaint);
                        toDrawNum++;
                    }
                }
            } else
            {
                for (int i = 1; i <= totalRow - 2; i++)
                {
                    for (int j = 0; j < 7; j++)
                    {
                        canvas.drawText(toDrawNum + "", j * cellWidth + cellWidth / 2, i * cellHeight + cellHeight / 2 + textOffset, numPaint);
                        toDrawNum++;
                    }
                }
            }
        }

        private void drawBottomLine(Canvas canvas)
        {

            if ((totalDays + firstDayPos - 1 - 7) % 7 != 0)
            {
                int leftDays = totalDays - toDrawNum + 1;
                for (int i = 0; i < leftDays; i++)
                {
                    canvas.drawText(toDrawNum + "", i * cellWidth + cellWidth / 2, (totalRow - 1) * cellHeight + cellHeight / 2 + textOffset, numPaint);
                    toDrawNum++;
                }
            }
        }


        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_UP:
                    float touchX = event.getX();
                    float touchY = event.getY();
                    int cellX = (int) (touchX / cellWidth);
                    int cellY = (int) (touchY / cellHeight);
                    if (cellY > totalRow)
                    {
                        return true;
                    }

                    if (onCellClickListener != null)
                        onCellClickListener.onCellClisk(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), cellY * 7 + cellX + 1 - firstDayPos + 1);
                    break;
            }
            return true;
        }

    }

    public interface OnCellClickListener
    {
        void onCellClisk(int year, int month, int day);
    }

}
