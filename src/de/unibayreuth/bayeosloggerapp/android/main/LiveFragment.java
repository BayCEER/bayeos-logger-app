package de.unibayreuth.bayeosloggerapp.android.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ToggleButton;
import de.unibayreuth.bayeosloggerapp.frames.bayeos.DataFrame;
import de.unibayreuth.bayeosloggerapp.frames.serial.SerialFrame;

public class LiveFragment extends Fragment {
	// private static final String TAG = "LiveFragment";

	private ToggleButton toggleButton;
	private LinearLayout linearLayout, linLay_charts;

	private LayoutParams layoutParams = new LayoutParams(
			LayoutParams.MATCH_PARENT, 300);
	private float labelTextSize;

	private Hashtable<Short, TimeSeries> charts;

	public LiveFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		labelTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, metrics);

		layoutParams.setMargins(5, 0, 5, 5);

		charts = new Hashtable<>();

		if (this.getView() != null) {
			((ViewGroup) this.getView().getParent()).removeView(this.getView());
			return this.getView();
		}

		View view = inflater.inflate(R.layout.fragment_live, container, false);

		toggleButton = (ToggleButton) view.findViewById(R.id.live_toggleButton);

		toggleButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							((MainActivity) getActivity())
									.addToQueue(SerialFrame.startLiveData);
						} else {
							((MainActivity) getActivity())
									.addToQueue(SerialFrame.modeStop);
						}
					}
				});
		linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

		linLay_charts = (LinearLayout) view.findViewById(R.id.linLay_charts);

		disableContent();

		return view;
	}

	/**
	 * Handles a new Data Frame received in live mode.
	 * 
	 * @param receivedFrame
	 */
	public void handle_DataFrame(DataFrame receivedFrame) {
		Date date = new Date();
		Set<Short> keys = receivedFrame.getValues().keySet();
		for (Short channel : keys) {
			addNewValue(date, channel, receivedFrame.getValues().get(channel));
		}

		refreshGraphs(linearLayout);
	}

	/**
	 * Runs recursively through all children of a ViewGroup. If the child found
	 * is a GraphicalView it will be repainted.
	 * 
	 * @param layout
	 */
	private static void refreshGraphs(ViewGroup layout) {
		if (layout.getChildAt(0) instanceof GraphicalView) {
			((GraphicalView) layout.getChildAt(0)).repaint();
		}
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ViewGroup) {
				refreshGraphs((ViewGroup) child);
			} else {
				if (child instanceof GraphicalView) {
					((GraphicalView) child).repaint();
				}
			}
		}
	}

	/**
	 * Adds a new value to the channel related graph.
	 * 
	 * @param date
	 *            Date of the new value
	 * @param channel
	 *            Channel of the new value
	 * @param value
	 *            Value
	 */
	private void addNewValue(Date date, Short channel, Float value) {
		if (!charts.containsKey(channel)) {

			TimeSeries values = new TimeSeries("Values");
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			dataset.addSeries(values);

			XYSeriesRenderer valuesRenderer = new XYSeriesRenderer();
			valuesRenderer.setColor(Color.BLACK);
			valuesRenderer.setLineWidth(2);
			valuesRenderer.setDisplayChartValues(true);

			XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

			multiRenderer.setChartTitle("Channel " + channel + "\n\n");
			multiRenderer.setXTitle("\n\n\n\nTime");
			multiRenderer.setYTitle("Value");
			multiRenderer.setLabelsColor(Color.BLACK);
			multiRenderer.setXLabelsColor(Color.DKGRAY);
			multiRenderer.setYLabelsColor(0, Color.DKGRAY);
			multiRenderer.setAxesColor(Color.BLACK);

			multiRenderer.setPanEnabled(true, true);
			multiRenderer.setZoomEnabled(true, true);
			multiRenderer.setApplyBackgroundColor(true);
			multiRenderer.setBackgroundColor(Color.WHITE);
			multiRenderer.setMarginsColor(Color.LTGRAY);

			multiRenderer.setGridColor(Color.DKGRAY);
			multiRenderer.setShowGrid(true);

			multiRenderer.addSeriesRenderer(valuesRenderer);
			multiRenderer.setLabelsTextSize(labelTextSize);

			// Creating a Time Chart

			GraphicalView mChart = ChartFactory.getTimeChartView(
					((MainActivity) getActivity()), dataset, multiRenderer,
					"EEE',' dd.MM.yyyy '\nat' HH:mm:ss z");

			multiRenderer.setClickEnabled(true);
			multiRenderer.setSelectableBuffer(10);

			LinearLayout LL = new LinearLayout(((MainActivity) getActivity()));
			LL.setOrientation(LinearLayout.VERTICAL);
			LL.setLayoutParams(layoutParams);

			LL.addView(mChart);
			linLay_charts.addView(LL);

			charts.put(channel, values);
		}

		// add new values
		charts.get(channel).add(date, round(value, 4));

	}

	public static double round(float value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void resetView() {
		charts = new Hashtable<>();

		linLay_charts.removeAllViews();
		linLay_charts.invalidate();
	}

	public void disableContent() {
		MainActivity.disable(linearLayout);
	}

	public void enableContent() {
		MainActivity.enable(linearLayout);
	}

	public ToggleButton getToggleButton() {
		return toggleButton;
	}

}
