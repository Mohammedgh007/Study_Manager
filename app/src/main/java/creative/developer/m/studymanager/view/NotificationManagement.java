/*
###############################################################################
Class name : NotificationManagement
purpose: This is a view class that handles creating and showing notifications (for assignments)
    and alarms(for reminders).
Methods:
    - setNotify() -> it creates the timer's background service with selecting the proper receiver.
    - getNotifyTime(assignment) -> this method determines the notification
    time based on the AssignmentEntity field.
    - getNotifyTime(reminder) -> get the alarm time from the reminder instance.
Inner class:
    - AlarmReceiver: It is used to receive the service from the background for notifiying.
        method: showNotificationView() -> It creates and shows the the notification for the user.
###############################################################################
 */
package creative.developer.m.studymanager.view;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;


import androidx.core.app.NotificationCompat;


import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;
import creative.developer.m.studymanager.model.modelCoordinators.AssignmentCoordinator;
import creative.developer.m.studymanager.model.modelCoordinators.ReminderCoordinator;


public class NotificationManagement {

    private int timerID; // each alarm/notification needs a unique integer identifier.
    private Calendar dateTime; // the time and date of the notification.
    private boolean isNotify; // true if it's used by assignment; false for reminders.
    private boolean isRepeating; // true for reminders that repeat weekly.

    /*
    it creates an instance that has a special id and time for the timer background service..
    @param: assignment is the object for that needs to utilize notification service.
    */
    public NotificationManagement(AssignmentEntity assignment) {
        this.timerID = assignment.getAssignmentID() * -1;
        this.dateTime = getNotifyTime(assignment);
        isNotify = true;
        isRepeating = false;
    }


    /*
    * it creates an instance that has a special id and time for the timer background service.
    * @param: reminder is the object for that needs to utilize alarming service.
    * @param: alarmID is id that will be used to identify the alarm.
    * @param: day is string three letters format text(like mon) that represents the day of a reminder.
    */
    public NotificationManagement(ReminderEntity reminder, int alarmID, String day) {
        this.timerID = alarmID;
        this.dateTime = getNotifyTime(reminder, day);
        isNotify = false;
        isRepeating = reminder.getIsRepeated();
    }


    /*
    this method determines the notification time based on the AssignmentEntity field.
    @param assignment is the AssignmentEntity
    */
    private static Calendar getNotifyTime(AssignmentEntity assignment) {
        Calendar cal = Calendar.getInstance();
        // month - 1 because months start from zero at Calendar.
        cal.set(assignment.getYearNum(), assignment.getMonthNum() - 1, assignment.getDayNum(),
                assignment.getHourNum(), assignment.getMinuteNum());
        String notifyTime = assignment.getNotificationTime();
        if (notifyTime.contains("N")) { // when the user does not want to be notified.
            return null;
        } else if(notifyTime.contains("d")) { // d for days
            String days = notifyTime.substring(0, notifyTime.indexOf(" "));
            int daysNum = 0 - Integer.parseInt(days);
            cal.add(Calendar.DAY_OF_MONTH, daysNum);
        } else {
            System.out.println("time is " + notifyTime);
            String hours = notifyTime.substring(0, notifyTime.indexOf(" "));
            int hoursNum = 0 - Integer.parseInt(hours);
            cal.add(Calendar.HOUR_OF_DAY, hoursNum);
        }
        return cal;
    }


    /*
     * It determine the exact time and date for the first alarm on the week for the reminder object.
     * @param reminder is the object that will utilize the alarming service.
     * @param alarmDay string three letters format text(like mon) that represents the day of a reminder.
     */
    private static Calendar getNotifyTime(ReminderEntity reminder, String alarmDay) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, reminder.getHourNum());
        alarmTime.set(Calendar.MINUTE, reminder.getMinuteNum());
        alarmTime.set(Calendar.SECOND, 0);

        int i = 0;
        // in case the reminder has the same day of week as the current day.
        if (!alarmTime.after(Calendar.getInstance()) && alarmDay.equals(alarmTime.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.SHORT, Locale.getDefault()).toLowerCase())) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 7);
            return alarmTime;
        } else if (alarmTime.after(Calendar.getInstance()) && !(alarmDay.equals(alarmTime.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.SHORT, Locale.getDefault()).toLowerCase()))) {
            return alarmTime;
        }
        // for the rest of days.
        while (i < 6 && !(alarmDay.equals(alarmTime.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.SHORT, Locale.getDefault()).toLowerCase()))) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            i++;
        }
        return alarmTime;
    }


    /*
    it creates and shows the notification on the given time and date.
    @param: context is the context of the class that calls this method.
     */
    public void setNotify(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("notifyID", timerID);
        alarmIntent.putExtra("isNotify", isNotify);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, timerID,  alarmIntent, 0);
        System.out.println(timerID + " the dateTime is " + dateTime);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (!isRepeating) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, dateTime.getTimeInMillis(), pendingIntent);
        }
    }

    // It handles the event of service(AlarmManager) receiving. Used for alarming reminders
    public static class AlarmReceiver extends BroadcastReceiver{
        /*
        It is called when the alarm time is elapsed.
        */
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("in receive");
            if (intent.getAction() != null &&
                    intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // on device boot completed, reset the alarm
                handleBooting(context);
            } else { // for regular cases.
                Executor thread = Executors.newSingleThreadExecutor();
                boolean isNotify = intent.getBooleanExtra("isNotify", true);
                if (isNotify)
                    thread.execute(() -> handleReceiveNormalAssignment(context, intent));
                else
                    thread.execute(() -> handleReceiveNormalReminder(context, intent));
            }
        }


        /*
        this method is called to when onReceive is called for booting
        @param: context is the context from onReceive()
        */
        private void handleBooting(Context context) {
            System.out.println("in booting");
        }


        /*
        this method is called to when onReceive is called for normal case reminders.
        @param: context is the context from onReceive()
        @param: intent holds the data that is sent by the notification creator like notification
            title and distribution.
        */
        private void handleReceiveNormalReminder(Context context, Intent intent){
            int timerID = intent.getIntExtra("notifyID", 1);
            System.out.println("in receive normal reminders " + timerID);
            int reminderID = (timerID > 6) ? timerID - (timerID % 7) : 0;
            ReminderCoordinator model = ReminderCoordinator.getInstance(context);
            ReminderEntity notifyReminder = model.getreminderByID(String.valueOf(reminderID));

            // checking if the user has deleted the reminder or not
            if (notifyReminder == null) {
                System.out.println("id is " + reminderID);
                return;
            }

            // making sure that the reminder should be notified at this time to avoid the case
            // that a user has changed the notification time.
            Calendar currTime = Calendar.getInstance();
            Calendar notifyTime = getNotifyTime(notifyReminder,
                    currTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            if (notifyTime.get(Calendar.MONTH) != currTime.get(Calendar.MONTH) &&
                    notifyTime.get(Calendar.DAY_OF_MONTH) != currTime.get(Calendar.DAY_OF_MONTH) &&
                    notifyTime.get(Calendar.HOUR_OF_DAY) != currTime.get(Calendar.HOUR_OF_DAY) &&
                    notifyTime.get(Calendar.MINUTE) != currTime.get(Calendar.MINUTE) &&
                    notifyTime.get(Calendar.YEAR) != currTime.get(Calendar.YEAR)) {
                System.out.println("in if");
                return;
            }

            // check if the user has not turned the reminder off.
            if (!notifyReminder.getIsOn()) {
                if (notifyReminder.getIsRepeated()) {
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, timerID,
                            alarmIntent, PendingIntent.FLAG_NO_CREATE);
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
                return;
            }



            // prepare to show the notification.
            showNotificationView(reminderID, "alarm", notifyReminder.getTitle(),
                    notifyReminder.getDisc(), context);
        }


        /*
        this method is called to when onReceive is called for normal case for assignments.
        @param: context is the context from onReceive()
        @param: intent holds the data that is sent by the notification creator like notification
            title and distribution.
         */
        private void handleReceiveNormalAssignment(Context context, Intent intent) {
            // getting the assignment that correspondence to the  notification.
            int assignmentID = intent.getIntExtra("notifyID", 1) * -1;
            AssignmentCoordinator model = AssignmentCoordinator.getInstance(context);
            AssignmentEntity notifyAss = model.getAssignmentbyID(assignmentID);
            System.out.println("in receive normal assignments");
            // checking if the user has deleted the assignment or not
            if (notifyAss == null) {
                System.out.println("in null");
                return;
            }

            // making sure that the assignment should be notified at this time to avoid the case
            // that a user has changed the notification time.
            Calendar notifyTime = getNotifyTime(notifyAss);
            Calendar currTime = Calendar.getInstance();
            if (notifyTime.get(Calendar.MONTH) != currTime.get(Calendar.MONTH) &&
                    notifyTime.get(Calendar.DAY_OF_MONTH) != currTime.get(Calendar.DAY_OF_MONTH) &&
                    notifyTime.get(Calendar.HOUR_OF_DAY) != currTime.get(Calendar.HOUR_OF_DAY) &&
                    notifyTime.get(Calendar.MINUTE) != currTime.get(Calendar.MINUTE) &&
                    notifyTime.get(Calendar.YEAR) != currTime.get(Calendar.YEAR)) {
                System.out.println("in if");
                return;
            }

            // making sure that the notification is done for unchecked assignment.
            if (notifyAss.getIsMarked()) {
                System.out.println("is market");
                return;
            }


            // prepare to show the notification.
            showNotificationView(assignmentID, "notify", notifyAss.getCourse(),
                    notifyAss.getDisc(), context);
        }

        /*
         * It creates and shows the notification for the user.
         * @param id is the unique int used to create a notification.
         * @param appearance is either "notify" for assignment(show only on notifications' bar) or "alarm"
         *  to fill the screen.
         * @param title is the shown text as a title.
         * @param disc is the description of the notification.
         * @param context is the context that will be used by NotificationManager
         */
        private void showNotificationView(int id, String appearance, String title, String disc,
                                                 Context context) {
            System.out.println("in notify View!!");
            int importanceLevel = NotificationManager.IMPORTANCE_HIGH;
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(String.valueOf(id)
                        , title, importanceLevel);
                channel.setDescription(disc);
                NotificationManager notificationManager = context.
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(context,
                        String.valueOf(id));
            } else {
                builder = new NotificationCompat.Builder(context);
            }
            builder.setPriority(Notification.PRIORITY_HIGH);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(disc);
            builder.setStyle(bigTextStyle);
            builder.setSmallIcon(R.mipmap.app_icon);
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (appearance.equals("notify")) { // for Assignments' notifications
                System.out.println("notify");
                builder.setOnlyAlertOnce(true);
                notificationManager.notify(id, builder.build());
            } else { // for reminders' alarms
                System.out.println("alarm");
                Uri defaultUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
                if (defaultUri != null) {
                    builder.setSound(defaultUri);
                } else {
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                }
                Notification notification = builder.build();
                notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(id, notification);
            }
        }
    }

}

