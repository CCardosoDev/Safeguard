package pt.ua.engserv.safeguard.settings;

import java.util.List;

import pt.ua.engserv.safeguard.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAppsArrayAdapter extends ArrayAdapter {

    protected final Context context;
    protected final List<ApplicationStatus> apps;

    public MyAppsArrayAdapter(Context context, List<ApplicationStatus> apps) {

        super(context, R.layout.packages_listview_row, apps);

        this.context = context;
        this.apps = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder viewHolder = new ViewHolder();

        View rowView = inflator.inflate(R.layout.packages_listview_row, parent, false);
        TextView tvAppLabel = (TextView) rowView.findViewById(R.id.appsrow_textViewLabel);
        ImageView ivIcon = (ImageView) rowView.findViewById(R.id.appsrow_icon);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.appsrow_checkbox);

        viewHolder.cbAppEnabled = checkBox;

        /* Atribuir os valores aos campos da linha */
        tvAppLabel.setText(apps.get(position).getLabel());
        ivIcon.setImageDrawable(apps.get(position).getIcon());
        checkBox.setChecked(apps.get(position).isEnabled());

        viewHolder.cbAppEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                /* Obter a referência para o ApplicationStatus correspondente */
                ApplicationStatus as = (ApplicationStatus) viewHolder.cbAppEnabled.getTag();
                /* Guardar estado da checkbox */
                as.setEnabled(!as.isEnabled());
            }
        });

        rowView.setTag(viewHolder);
        viewHolder.cbAppEnabled.setTag(apps.get(position));

        return rowView;
    }

    static class ViewHolder {

        protected CheckBox cbAppEnabled;
    }
}
