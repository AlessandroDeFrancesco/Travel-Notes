package com.capraraedefrancescosoft.progettomobidev.utilities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.activities.MapsActivity;
import com.capraraedefrancescosoft.progettomobidev.activities.WallActivity;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Ianfire on 01/07/2016.
 */
public class ListenerUtility {

    public static View.OnClickListener getSharingOnClickListener(final Context context, final Element element) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);

                Log.d("Share", "Sharing " + element.getType() + ": " + element.getContent());
                // aggiungo l'oggetto associato all'element
                switch (element.getType()){
                    case NOTE:
                        intent.setType("text/plain");
                        String text = element.getContent();
                        text += "\n" + context.getResources().getString(R.string.app_link);
                        intent.putExtra(Intent.EXTRA_TEXT, text);
                        break;
                    case IMAGE:
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(element.getContent()));
                        intent.setType("image/png");
                        break;
                    case VIDEO:
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(element.getContent()));
                        intent.setType("video/mp4");
                        break;
                }

                // aggiungo il nome dell'app nel soggetto
                String appName = context.getString(R.string.app_link);
                intent.putExtra(Intent.EXTRA_SUBJECT, appName);

                if(intent.getType() != null) {
                    context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.send_to)));
                } else {
                    Log.d("Share", "Sharing failed");
                    Toast.makeText(context, context.getResources().getText(R.string.sharing_failed), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public static View.OnClickListener getPlaceOnClickListener(final Context context, final Element element, final Journal journal) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra(WallActivity.EXTRA_ELEMENT, element);
                intent.putExtra(WallActivity.EXTRA_JOURNAL, journal.getJournalID());
                context.startActivity(intent);
            }
        };
    }

    public static void showYesNoDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("YES", onClickListener);
        builder.setNegativeButton("NO", onClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static DatePickerDialog getDatePickerDialog(final Context activity, DatePickerDialog.OnDateSetListener listener){
        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(activity, listener, year, month, day);
    }

    public static AlertDialog.Builder getAbortAddElementDialogConfirmation(final Activity activity){
        return new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(activity.getString(R.string.abortingAddElementDialogTitle))
                .setMessage(activity.getString(R.string.abortingAddElementDialogText))
                .setPositiveButton(activity.getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }

                })
                .setNegativeButton(activity.getString(R.string.no), null);
    }
}
