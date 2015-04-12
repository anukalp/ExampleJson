
package com.android.flightscheduler.filter;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.flightscheduler.FlightListActivity;
import com.android.flightscheduler.R;

/**
 * Manager class for managing child view's listeners and callback
 * {@link TimePickerDialog} to input time associated changes
 * @author Anukalp
 *
 */
public class FilterManager extends LinearLayout implements OnClickListener {

    private ContentValues mContentValues = new ContentValues();

    private EditText mMinFare;

    private EditText mMaxFare;

    private TextView mDepartStartTime;

    private TextView mDetartEndTime;

    private TextView mArriveStartTime;

    private TextView mArriveEndTime;

    private CheckBox mBizCheckBox;

    private CheckBox mEcoCheckBox;

    private Button mClear;

    private Button mApply;

    public FilterManager(Context context) {
        super(context);
    }

    public FilterManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMinFare = (EditText)findViewById(R.id.min_fare);
        mMaxFare = (EditText)findViewById(R.id.max_fare);
        mDepartStartTime = (TextView)findViewById(R.id.depart_from);
        mDetartEndTime = (TextView)findViewById(R.id.depart_till);
        mArriveStartTime = (TextView)findViewById(R.id.arrive_from);
        mArriveEndTime = (TextView)findViewById(R.id.arrive_till);
        mBizCheckBox = (CheckBox)findViewById(R.id.button);
        mEcoCheckBox = (CheckBox)findViewById(R.id.button1);
        mClear = (Button)findViewById(R.id.clear);
        mApply = (Button)findViewById(R.id.apply);
        mDepartStartTime.setOnClickListener(this);
        mDetartEndTime.setOnClickListener(this);
        mDepartStartTime.setOnClickListener(this);
        mArriveStartTime.setOnClickListener(this);
        mArriveEndTime.setOnClickListener(this);
        findViewById(R.id.business_container).setOnClickListener(this);
        findViewById(R.id.economy_container).setOnClickListener(this);
        mClear.setOnClickListener(this);
        mApply.setOnClickListener(this);
        initValues();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.depart_from:
                createDatePickerDialog(mDepartStartTime);
                break;
            case R.id.depart_till:
                createDatePickerDialog(mDetartEndTime);
                break;
            case R.id.arrive_from:
                createDatePickerDialog(mArriveStartTime);
                break;
            case R.id.arrive_till:
                createDatePickerDialog(mArriveEndTime);
                break;
            case R.id.business_container:
                mBizCheckBox.toggle();
                break;
            case R.id.economy_container:
                mEcoCheckBox.toggle();
                break;
            case R.id.clear:
                clearFilter();
                break;
            case R.id.apply:
                buildFilter();
                break;

            default:
                break;
        }
    }

    public void initValues() {
        ContentValues cv = CustomFilter.getInstance(getContext()).getCurrentFilter();
        for (String key : cv.keySet()) {
            switch (key) {
                case FilterUtils.TAG_FARE_START:
                    mMinFare.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_FARE_END:
                    mMaxFare.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_DEPART_START:
                    mDepartStartTime.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_DEPART_END:
                    mDetartEndTime.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_ARRIVE_START:
                    mArriveStartTime.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_ARRIVE_END:
                    mArriveEndTime.setText(cv.getAsString(key));
                    break;
                case FilterUtils.TAG_BUSINESS:
                    mBizCheckBox.setChecked(cv.getAsBoolean(key));
                    break;
                case FilterUtils.TAG_ECONOMY:
                    mBizCheckBox.setChecked(cv.getAsBoolean(key));
                    break;

                default:
                    break;
            }
        }
    }

    private void clearFilter() {
        CustomFilter.getInstance(getContext()).applyDefaultFilter();
        if (getContext() instanceof FlightListActivity) {
            ((FlightListActivity)getContext()).restFilters();
        }
    }

    private void buildFilter() {
        mContentValues.clear();
        mContentValues.put(FilterUtils.TAG_FARE_START, mMinFare.getText().toString());
        mContentValues.put(FilterUtils.TAG_FARE_END, mMaxFare.getText().toString());
        mContentValues.put(FilterUtils.TAG_DEPART_START, mDepartStartTime.getText().toString());
        mContentValues.put(FilterUtils.TAG_DEPART_END, mDetartEndTime.getText().toString());
        mContentValues.put(FilterUtils.TAG_ARRIVE_START, mArriveStartTime.getText().toString());
        mContentValues.put(FilterUtils.TAG_ARRIVE_END, mArriveEndTime.getText().toString());
        mContentValues.put(FilterUtils.TAG_ECONOMY, mEcoCheckBox.isChecked());
        mContentValues.put(FilterUtils.TAG_BUSINESS, mBizCheckBox.isChecked());
        CustomFilter.getInstance(getContext()).buildFilter(mContentValues);
        if (getContext() instanceof FlightListActivity) {
            ((FlightListActivity)getContext()).restFilters();
        }
    }

    private void createDatePickerDialog(final TextView view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour,
                            int selectedMinute) {
                        StringBuilder timezone = new StringBuilder();
                        if (selectedHour / 10 == 0)
                            timezone.append("0");
                        timezone.append(selectedHour).append(":");
                        if (selectedMinute / 10 == 0)
                            timezone.append("0");
                        timezone.append(selectedMinute);
                        view.setText(timezone.toString());
                    }
                }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

}
