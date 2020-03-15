/*
###############################################################################
Class name : AlarmManagement
purpose: This is a view class that handles creating and showing notifications
Methods:
    - setAlarm(context) -> it creates and shows the notification on the given time and date.
    - getNotificationTime(assignment) -> this method determines the notification
    time based on the AssignmentEntity field.
Inner class:
    - AlramReciever: It is used to receive the service(alarm) from the background.
###############################################################################
 */
package creative.developer.m.studymanager.view;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;

public class AlarmManagement {

    private String notifyID;
    private Calendar dateTime;

    /*
    it creates an instance that has a special id for the notification with its time of notifying.
    @param: notifyID is the the unique identifier for the notification.
    @param: dateTime stores the date and the time of the notification.
    @param: title is the tile of the notification shown to the user.
    @param: disc is the discribtion of the notification shown to the user.
    */
    public AlarmManagement(AssignmentsEntity assignment) {
        this.notifyID = assignment.getNotificationID();
        this.dateTime = getNotifyTime(assignment);
    }


    /*
    it creates and shows the notification on the given time and date.
    @param: context is the context of the class that calls this method.
     */
    public void setAlarm(Context context) {
        // check if the user does not want to be notified
        if (dateTime == null) {return;}

        // if the user wants to be notified
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("notifyID", notifyID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.valueOf(notifyID),
                alarmIntent, 0);
        AlarmManager alaramManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alaramManager.set(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(), pendingIntent);
    }

    /*
    this method determines the notification time based on the AssignmentEntity field.
    @param assignment is the AssignmentEntity
    */
    private static Calendar getNotifyTime(AssignmentsEntity assignment) {
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


    // It handles the event of service(alarm) receiving.
    public static class AlarmReceiver extends BroadcastReceiver{
        /*
        It is called when the alarm time is elapsed.
        */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null &&
                    intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // on device boot completed, reset the alarm
                handleBooting(context);
            } else { // for regular cases.
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> handleReceiveNormal(context, intent));
            }
        }


        /*
        this method is called to when onReceive is called for booting
        @param: context is the context from onReceive()
        */
        private void handleBooting(Context context) {

        }


        /*
        this method is called to when onReceive is called for normal case.
        @param: context is the context from onReceive()
        @param: intent holds the data that is sent by the notification creator like notification
            title and distribution.
         */
        private void handleReceiveNormal(Context context, Intent intent) {
            // getting the assignment that correspondence to the  notification.
            String notifyID = intent.getStringExtra("notifyID");
            DataRepository repository = DataRepository.getInstance(
                    AppDatabase.getInstance(context));
            AssignmentsEntity notifyAss = repository.getAssignmentByNotifyID(notifyID);

            // checking if the user has deleted the assignment or not
            if (notifyAss == null) {
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
                return;
            }

            // making sure that the notification is done for unchecked assignment.
            if (notifyAss.getIsMarked()) {
                return;
            }


            // prepare to show the notification.
            String title = "Assignment for " + notifyAss.getCourse();
            String disc = notifyAss.getDisc();
            int importanceLevel = NotificationManager.IMPORTANCE_HIGH;
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(notifyID
                        , title, importanceLevel);
                channel.setDescription(disc);
                NotificationManager notificationManager = context.
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(context,
                        notifyID);
            } else {
                builder = new NotificationCompat.Builder(context);
            }
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setAutoCancel(true)
                    .setOnlyAlertOnce(false)
                    .setPriority(Notification.PRIORITY_HIGH);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(disc);
            builder.setStyle(bigTextStyle);
            builder.setSmallIcon(R.mipmap.app_icon);
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(notifyID), builder.build());


        }
    }

}
