package kr.co.mash_up.a5afe.alert;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationUtil {

    /**
     * 노티피케이션 발생
     * @param context 컨텍스트
     * @param iconRes 아이콘 리소스 id
     * @param ticker 알림 상단 문구
     * @param title 알림 제목 ex)회의
     * @param body 알림 내용 ex)2016 10월 21일 (월) _ 11시 55분
     */
    @RequiresPermission(value = Manifest.permission.VIBRATE)
    private void createNotification(Context context, int iconRes, String ticker, String title, String body){

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconRes)
                        .setTicker(ticker)  //알림 상단 문구
                        .setWhen(System.currentTimeMillis())  //알람 출력 시간
                        .setAutoCancel(true)  //터치시 반응 후 알림 삭제 여부
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  //보안잠금화면에 표시
                        .setPriority(NotificationCompat.PRIORITY_MAX)  //우선 순위
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)  //사운드, 진동, 불빛 설정
                        .setContentTitle(title)  //제목
                        .setContentText(body);  //내용

//        Intent resultIntent = new Intent(context, TasksActivity.class);
//
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
//        taskStackBuilder.addParentStack(TasksActivity.class);
//        taskStackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(
//                id,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        builder.setContentIntent(resultPendingIntent);  //터치시 반응

        //다른 액션을 추가할 수도 있다.
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 34, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.ic_2, "Share", pendingIntent);

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //고유 id로 생성
//        notificationManager.notify(id, builder.build());
    }


}
