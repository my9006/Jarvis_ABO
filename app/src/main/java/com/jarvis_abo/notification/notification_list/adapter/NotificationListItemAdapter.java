package com.jarvis_abo.notification.notification_list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jarvis_abo.JA_application;
import com.jarvis_abo.R;
import com.jarvis_abo.entities.Notification;
import com.jarvis_abo.entities.NotificationDao;
import com.jarvis_abo.model.Type;
import com.joanzapata.iconify.widget.IconButton;

import java.util.List;

/**
 * Created by Mxo on 20-May-16.
 */
public class NotificationListItemAdapter extends BaseAdapter {

    private List<Notification> notifications;
    private Context context;
    private LayoutInflater inflater;

    public NotificationListItemAdapter( Context context) {

        reloadItems();

        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    private void reloadItems(){

        this.notifications = JA_application.getInstance().getDaoSession().getNotificationDao().queryBuilder().where(NotificationDao.Properties.Fk_user.eq(JA_application.getInstance().loggedUser().getPk_id())).list();


    }



    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        IconButton type;
        TextView message;
        IconButton edit;
        IconButton delete;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Notification notification= notifications.get(position);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_notifications_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.type = (IconButton) convertView.findViewById(R.id.ib_type);
            viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
            viewHolder.edit = (IconButton) convertView.findViewById(R.id.ib_editNotification);
            viewHolder.delete = (IconButton) convertView.findViewById(R.id.ib_deleteNotification);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.type.setText("{" + notification.getType() +"}");
        viewHolder.type.setTextColor(context.getResources().getColor(Type.colors.get(notification.getType())));

        viewHolder.message.setText(notification.getMessage());
        viewHolder.edit.setText("{fa-edit}");
        viewHolder.delete.setText("{fa-trash-o}");

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notification.delete();
                reloadItems();
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

}
