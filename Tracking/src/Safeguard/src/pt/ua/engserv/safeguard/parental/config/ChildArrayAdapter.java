package pt.ua.engserv.safeguard.parental.config;

import java.io.InputStream;
import java.util.List;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.utils.Child;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildArrayAdapter extends ArrayAdapter {

	protected final Context context;
    protected final List<Child> child;
    
	public ChildArrayAdapter(Context context, List<Child> child) {

        super(context, R.layout.child_listview_row, child);

        this.context = context;
        this.child = child;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflator.inflate(R.layout.child_listview_row, parent, false);
        TextView tvChildName = (TextView) rowView.findViewById(R.id.childsrow_name);
        ImageView ivPhoto = (ImageView) rowView.findViewById(R.id.childsrow_photo);

        /* Atribuir os valores aos campos da linha */
        tvChildName.setText(child.get(position).getName());
        new DownloadImageTask(ivPhoto).execute(child.get(position).getPhoto());

        return rowView;
    }
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    
		ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        
	        try {
	        	
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	            
	            try {
	            	
		            InputStream in = new java.net.URL("http://safeguardtracking.no-ip.org/avatar").openStream();
		            mIcon11 = BitmapFactory.decodeStream(in);
		        } 
	            catch (Exception ex) {

		            ex.printStackTrace();
		        }
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	
}
