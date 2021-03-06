package de.unibayreuth.bayeosloggerapp.android.tools;

import android.view.Gravity;
import android.widget.Toast;
import de.unibayreuth.bayeosloggerapp.android.main.MainActivity;

public class ToastMessage {

	public static void toastConnectionFailed(MainActivity mainActivity) {
		CharSequence text = "Connection failed!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mainActivity, text, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
		toast.show();
	}

	public static void toastConnectionSuccessful(MainActivity mainActivity) {
		CharSequence text = "Connection successful!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mainActivity, text, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
		toast.show();
	}

	public static void toastDisconnected(MainActivity mainActivity) {
		CharSequence text = "Disconnected!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mainActivity, text, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
		toast.show();
	}


	public static void toastMessageTop(MainActivity mainActivity, String string) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mainActivity, string, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
		toast.show();

	}
	
	public static void toastMessageBottom(MainActivity mainActivity, String string) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(mainActivity, string, duration);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 100);
		toast.show();

	}
}
