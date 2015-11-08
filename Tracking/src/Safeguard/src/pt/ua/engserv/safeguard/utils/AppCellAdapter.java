package pt.ua.engserv.safeguard.utils;

import java.util.List;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.settings.ApplicationStatus;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppCellAdapter extends BaseAdapter {

	private Context context;
	private List<ApplicationStatus> appsList;
 
	public AppCellAdapter(Context context, List<ApplicationStatus> appsList) {
		
		this.context = context;
		this.appsList = appsList;
	}
	
	@Override
	public int getCount() {
		
		return appsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View cell;
		
		if(convertView == null) {
			
			cell = new View(context);
			
			cell = inflater.inflate(R.layout.apps_grid_cell, null);
			
			TextView tvAppName = (TextView) cell.findViewById(R.id.app_name);
			
			tvAppName.setText(appsList.get(position).getLabel());
			
			ImageView ivAppImage = (ImageView) cell.findViewById(R.id.app_image);
			
			ivAppImage.setImageDrawable(appsList.get(position).getIcon());
			
		}
		else {
			
			cell = convertView;
		}
			
		
		
		return cell;
	}

	
	
	
}
