/*
###############################################################################
Author: Mohammed Alghamdi
Class name : RemarksActivity
purpose: This is model view class that is responsible for remark fragment
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from AddRemarkActivity that
     holds the data of the added remark.
  updateDateText() -> It updates the shown text that represent the selected day.
  createRemarksView() -> It creates the view that show all remarks.
  getViewedDate(date) -> It returns the string that is used to output the date to the user. date is
    a string on the format yyyy:mm:dd
  doEditing() -> it handles the event of editing, which is modifying the view to fit editing mode.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.EntityListFiles.RemarksList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;
import creative.developer.m.studymanager.model.modelCoordinators.RemarkCoordinator;
import creative.developer.m.studymanager.view.StringMaker;

import static android.app.Activity.RESULT_OK;


public class RemarksActivity extends Fragment implements Observer {

    private RemarkCoordinator model; // used to access the model
    private final int ADDING_CODE = 55; // used as requestCode for startActivityForResult()
    private final int EDITING_CODE = 66; // used as requestCode for startActivityForResult()
    private boolean isEditing = false; // true when the user click on edit button
    // represent the inputted date to show its remarks
    private int selectedYear, selectedMonth, selectedDay;
    private HashMap<CalendarDay, Integer> highlightedDates;// the highlighted dates on calendarView

    // declaring views; the first two map depends on remarkId as a key while the third uses dueDate
    private LinearLayout remarksLayout; // Layout that's holds all added assignments
    private HashMap<Integer, LinearLayout> oneRemarkLayouts; // layout of a single assignment
    private HashMap<Integer, LinearLayout>  buttonsLayouts; //it stores all the layout of edit and delete buttons.
    private Button addBtn;
    private TextView remarkDate;
    private MaterialCalendarView calendarView;
    private Activity activityMain;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityMain = (Activity) context;
    }

    @Override
    public void onDetach () {
        super.onDetach();
        activityMain = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // initializing the set of highlighted dates
        highlightedDates = new HashMap<>();

        // Accessing database to retrieve the data then show it in the view
        model = RemarkCoordinator.getInstance(activityMain.getBaseContext());
        model.addObserver(this);

        // initializing views
        View root = inflater.inflate(R.layout.remarks, container, false);
        remarksLayout = root.findViewById(R.id.remarks_layout);
        addBtn = root.findViewById(R.id.add_remark);
        remarkDate = root.findViewById(R.id.selected_day_remarks_view);
        Calendar todayDate = Calendar.getInstance();
        selectedYear = todayDate.get(Calendar.YEAR);
        selectedMonth = todayDate.get(Calendar.MONTH);
        selectedDay = todayDate.get(Calendar.DAY_OF_MONTH);
        calendarView = ((MaterialCalendarView ) root.findViewById(R.id.calnder_remarks_layout));

        // taking user to AddAssignmentActivity to input details of an added assignment.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    Intent intent = new Intent(activityMain,
                            AddRemarkActivity.class);
                    intent.putExtra("porpuse", "adding");
                    startActivityForResult(intent, ADDING_CODE);
                } else {
                    doEditing(); // to close editing mode.
                }
            }
        });

        // showing the remarks
        if (model.getRemarks() != null) { // to avoid race condition; It will be shown by update() if null
            activityMain.runOnUiThread(() -> {
                selectedYear = CalendarDay.today().getYear();
                selectedMonth = CalendarDay.today().getMonth();
                selectedDay = CalendarDay.today().getDay();
                createRemarksView(model.getRemarks(), selectedYear, selectedMonth, selectedDay);
                updateDateText(selectedMonth, selectedDay);
            });
        }


        // create event handling for selecting a day from the calender
        calendarView.setOnDateChangedListener((view, date, useless) -> {
            // Managing the highlighted dates.
            calendarView.clearSelection();
            for (CalendarDay highlighted : highlightedDates.keySet()) {
                calendarView.setDateSelected(highlighted, true);
            }




            // show the remakrs of the selected date
            updateDateText(date.getMonth(), date.getDay());
            createRemarksView(model.getRemarks(), date.getYear(),date.getMonth() , date.getDay());
            if (isEditing) {
                addBtn.setBackgroundResource(R.drawable.add_btn_icon);
                isEditing = false;
            }

            //updating inputted year, month, day
            selectedYear = date.getYear();
            selectedMonth = date.getMonth();
            selectedDay = date.getDay();
        });

        return root;
    }


    /*
    this method changes the text of remark's date view.
    @param: day is the day in the new date.
    @param: month is the month in the selected date.
    */
    private void updateDateText(int month, int day) {
        // for RTL languages
        if(getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            remarkDate.setText(getResources().getString(R.string.remarkForDate) + " " + day + " " +
                    new DateFormatSymbols(Locale.getDefault()).
                            getShortMonths()[month - 1] // January = 1 as month, but it indexes 0
                     );
        } else {
            remarkDate.setText(getResources().getString(R.string.remarkForDate) + " " +
                    new DateFormatSymbols(Locale.getDefault()).
                            getShortMonths()[month - 1] // January = 1 as month, but it indexes 0
                    + " " + day);
        }
    }


    /*
    This method create the view for remarks that is below the calendar.
    It also initialize the map highlightedDates that will be used outside of this method to paint
    the dates that contain a remark.
    @PARAM: remarks is an object that contains all retrieved remarks from the database.
    @param: year is the selected year by the user.
    @param: month is the selected month by the user. (month starts from 1)
    @param: day is the selected day by the user.
     */
    private void createRemarksView (List<RemarkEntity> remarks, int year, int month, int day) {
        TextView RemarkIfno;
        String info;
        remarksLayout.removeAllViews(); // clear the previous views.
        calendarView.clearSelection();
        int lastHeight = 120; // used to leave an empty at the the bottom of the remarks layout

        // each remark is in a single layout that is oneRemarkLayout, and it has 2
        // layouts, infoLayout for the textView, buttonsLayout for the buttons.
        LinearLayout oneRemarkLayout;
        LinearLayout infoLayout;
        oneRemarkLayouts = new HashMap<>();
        buttonsLayouts = new HashMap<>();
        highlightedDates = new HashMap<>();

        // this is used for the layout of text view
        LinearLayout.LayoutParams layoutParamsInfo = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsInfo.weight = 0.75f;
        // this is used for the layout of buttons
        LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(
                0, (int)getResources().getDimension(R.dimen.size_edit_btn) + 10);
        layoutParamsButtons.weight = 0.25f;
        layoutParamsButtons.setLayoutDirection(LinearLayout.VERTICAL);
        // this is used for the dueDate views
        LinearLayout.LayoutParams layoutParamsDueDate = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsDueDate.topMargin = 30;
        // this is used for the layout of a single assignment
        LinearLayout.LayoutParams layoutParamsRemark = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRemark.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutParamsRemark.setMargins(20, 20, 20, 0);

        for (RemarkEntity remark : remarks) {
            // adding a remark only if its date is the same as the user's input
            // otherwise: highlight its date
            if (remark.getDayNum() == day && remark.getMonthNum() == month &&
            remark.getYearNum() == year) {
                // setting text
                info = remark.getTitle() + "\n";
                info += getResources().getString(R.string.dueTime) + " " + StringMaker.getViewedTime(remark.getHourNum(),
                        remark.getMinuteNum()) + "\n";
                info += getResources().getString(R.string.discText) + " " + remark.getDisc();

                // setting the remark layout
                oneRemarkLayout = new LinearLayout(activityMain.getBaseContext());
                remarksLayout.addView(oneRemarkLayout);
                oneRemarkLayout.setLayoutParams(layoutParamsRemark);

                // setting layout of the remark's information
                infoLayout = new LinearLayout(activityMain.getBaseContext());
                infoLayout.setOnLongClickListener( (v) -> {
                    doEditing();
                    return false;
                });
                oneRemarkLayout.addView(infoLayout);
                infoLayout.setLayoutParams(layoutParamsInfo);

                // setting layout of the remark's buttons
                buttonsLayouts.put(remark.getRemarkID(), new LinearLayout(activityMain.getBaseContext()));
                oneRemarkLayout.addView(buttonsLayouts.get(remark.getRemarkID()));
                buttonsLayouts.get(remark.getRemarkID()).setLayoutParams(layoutParamsButtons);

                // setting TextView
                RemarkIfno = new TextView(activityMain.getBaseContext());
                RemarkIfno.setText(info);

                // adding textview to their layouts
                infoLayout.addView(RemarkIfno);


                // adding oneRemarksLayout to the hashMap
                oneRemarkLayouts.put(remark.getRemarkID(), oneRemarkLayout);
                lastHeight = Math.max(lastHeight, oneRemarkLayout.getHeight());
                // adding remark's date to the set of highlighted dates
                CalendarDay remarkDay = CalendarDay.from(remark.getYearNum(), remark.getMonthNum(),
                        remark.getDayNum());
                if (!highlightedDates.containsKey(remarkDay)) {
                    highlightedDates.put(remarkDay, 0);
                }
                highlightedDates.put(remarkDay, highlightedDates.get(remarkDay) + 1);
                calendarView.setDateSelected(remarkDay, true);
            } else {
                // adding remark's date to the set of highlighted dates
                CalendarDay remarkDay = CalendarDay.from(remark.getYearNum(), remark.getMonthNum(),
                        remark.getDayNum());
                if (!highlightedDates.containsKey(remarkDay)) {
                    highlightedDates.put(remarkDay, 0);
                }
                highlightedDates.put(remarkDay, highlightedDates.get(remarkDay) + 1);
                calendarView.setDateSelected(remarkDay, true);
            }
        }

        // this section is to append a transparent layout, so that there is an empty space below the
        // lowest remark to prevent an overlap between add's button and the lowest remark.
        LinearLayout.LayoutParams layoutParamsTran = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT   , lastHeight);
        LinearLayout tranLayout = new LinearLayout(activityMain);
        tranLayout.setLayoutParams(layoutParamsTran);
        remarksLayout.addView(tranLayout);

        // highlight the current date if it has a remark.
        if (highlightedDates.keySet().contains(CalendarDay.today())) {
            calendarView.setDateSelected(CalendarDay.today(), true);
        }
    }


    // this method receives intent from AddRemarkActivity that store details of an added
    // remark.
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDING_CODE && resultCode == RESULT_OK) {
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.remarkAdded), Toast.LENGTH_LONG).show();
            Calendar cal = Calendar.getInstance();
            selectedDay = cal.get(Calendar.DAY_OF_MONTH);
            selectedMonth =  cal.get(Calendar.MONTH) + 1;
            selectedYear = cal.get(Calendar.YEAR);
            createRemarksView(model.getRemarks(), selectedYear, selectedMonth, selectedDay);
            updateDateText( selectedMonth, cal.get(Calendar.DAY_OF_MONTH));
        } else if (requestCode == EDITING_CODE && resultCode == RESULT_OK) {
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.remarkEdited), Toast.LENGTH_LONG).show();
            createRemarksView(model.getRemarks(), selectedYear, selectedMonth, selectedDay);
            updateDateText( selectedMonth, selectedDay);
        }
    }

    // this method handles the event of editing that is trigged by clicking the button edit or
    // by holding on a remark
    private void doEditing() {
        if (!isEditing) {
            // changing the appearance of add button.
            addBtn.setBackgroundResource(R.drawable.done_editing_icon);
            isEditing = true;

            Button changeBtn;
            Button deleteBtn;
            LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.size_edit_btn) ,// for buttons
                    (int) getResources().getDimension(R.dimen.size_edit_btn));
            for (RemarkEntity remark : model.getRemarks()) {
                // showing editing button only for the shows remarks
                if (buttonsLayouts.get(remark.getRemarkID()) != null) {
                    // setting change button; on the left
                    layoutParamsBtn.leftMargin = 0;
                    changeBtn = new Button(activityMain.getBaseContext());
                    changeBtn.setLayoutParams(layoutParamsBtn);
                    changeBtn.setBackgroundResource(R.drawable.edit_icon);
                    changeBtn.setOnClickListener((changingBtn) -> {
                        Intent intent = new Intent(activityMain,
                                AddRemarkActivity.class);
                        Gson formattedAssignment = new Gson();
                        String stringRemark = formattedAssignment.toJson(remark);
                        intent.putExtra("remarkObj", stringRemark);
                        intent.putExtra("porpuse", "editing");
                        startActivityForResult(intent, EDITING_CODE);
                    });

                    // setting delete button; on the right
                    layoutParamsBtn.leftMargin = 10;
                    deleteBtn = new Button(activityMain.getBaseContext());
                    deleteBtn.setLayoutParams(layoutParamsBtn);
                    deleteBtn.setBackgroundResource(R.drawable.delete_icon);
                    deleteBtn.setOnClickListener((deletingBtn) -> {
                        // removing from remarksView
                        remarksLayout.removeView(oneRemarkLayouts.
                                get(remark.getRemarkID()));
                        model.removeRemark(remark);
                        // removing from the calendar's highlighted dates if that date has one remark
                        CalendarDay remarkDay = CalendarDay.from(remark.getYearNum(), remark.getMonthNum(),
                                remark.getDayNum());
                        if (highlightedDates.get(remarkDay) == 1) {
                            highlightedDates.remove(remarkDay);
                            calendarView.setDateSelected(remarkDay, false);
                        } else {
                            highlightedDates.put(remarkDay, highlightedDates.get(remarkDay) - 1);
                        }
                    });

                    // setting the layout by replacing check button with change and delete
                    buttonsLayouts.get(remark.getRemarkID()).addView(changeBtn, 0);
                    buttonsLayouts.get(remark.getRemarkID()).addView(deleteBtn, 1);
                }

            }

        } else {
            // bring back the adding button
            addBtn.setBackgroundResource(R.drawable.add_btn_icon);
            isEditing = false;

            // recreating assignments view so that they are sorted
            remarksLayout.removeAllViews();
            createRemarksView(model.getRemarks(), selectedYear, selectedMonth, selectedDay);
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        activityMain.runOnUiThread(() -> {
            selectedYear = CalendarDay.today().getYear();
            selectedMonth = CalendarDay.today().getMonth();
            selectedDay = CalendarDay.today().getDay();
            createRemarksView(model.getRemarks(), selectedYear, selectedMonth, selectedDay);
            updateDateText(selectedMonth, selectedDay);
        });
    }
}
