package org.quantumbadger.redreader.ui.settings;

import android.view.View;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.quantumbadger.redreader.R;
import org.quantumbadger.redreader.common.General;
import org.quantumbadger.redreader.settings.RRPreference;
import org.quantumbadger.redreader.settings.RRPreferenceBoolean;
import org.quantumbadger.redreader.settings.RRPreferenceEnum;
import org.quantumbadger.redreader.settings.RRPreferenceFloat;
import org.quantumbadger.redreader.ui.RRContext;
import org.quantumbadger.redreader.ui.frag.RRUriHandler;

public final class PreferenceView extends FrameLayout implements RRPreference.Listener {

	protected RRPreference preference;

	protected TextView textView, subtitleView;
	protected CheckBox checkBox;
	private View divider;

	protected final RRContext context;

	public PreferenceView(final RRContext context) {

		super(context.activity);
		this.context = context;

		final LinearLayout ll = (LinearLayout)inflate(context.activity, R.layout.preference_view, null);
		divider = ll.findViewById(R.id.list_item_divider);
		textView = (TextView)ll.findViewById(R.id.list_item_text);
		subtitleView = (TextView)ll.findViewById(R.id.list_item_subtitle);
		checkBox = (CheckBox)ll.findViewById(R.id.list_item_checkbox);
		addView(ll);

		final int hPadding = General.dpToPixels(context.activity, 12);
		setPadding(hPadding, 0, hPadding, 0);

		setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
	}

	public void reset(final RRPreference preference, final boolean hideDivider) {

		if(this.preference != null) {
			this.preference.removeListener(this);
		}

		this.preference = preference;

		preference.addListener(this);

		textView.setText(preference.titleString);
		divider.setVisibility(hideDivider ? GONE : VISIBLE);

		if(preference instanceof RRPreferenceBoolean) {
			checkBox.setVisibility(VISIBLE);
			checkBox.setChecked(((RRPreferenceBoolean) preference).get());
			subtitleView.setVisibility(GONE);

		} else if(preference instanceof RRPreferenceEnum || preference instanceof RRPreferenceFloat) {
			checkBox.setVisibility(GONE);
			subtitleView.setVisibility(VISIBLE);
			subtitleView.setText(getSubtitle());


		} else {
			checkBox.setVisibility(GONE);
			subtitleView.setVisibility(GONE);
		}
	}

	public final void onClick() {
		if(preference instanceof RRPreferenceBoolean) {
			((RRPreferenceBoolean) preference).set(!((RRPreferenceBoolean) preference).get());

		} else {
			context.fragmentLayout.handleUri(context.fragment, preference.getUri(), RRUriHandler.Mode.ANY, null);
		}
	}

	public void onPreferenceChanged(final RRPreference preference) {

		General.runOnUiThread(new Runnable() {
			public void run() {
				if(preference != PreferenceView.this.preference) return;

				if(preference instanceof RRPreferenceBoolean) {
					checkBox.setChecked(((RRPreferenceBoolean) preference).get());

				} else if(preference instanceof RRPreferenceEnum || preference instanceof RRPreferenceFloat) {
					subtitleView.setText(getSubtitle());
				}
			}
		});
	}

	private String getSubtitle() {

		if(preference instanceof RRPreferenceEnum) {
			final RRPreferenceEnum pref = (RRPreferenceEnum) preference;
			return pref.getItem(pref.get()).getName(context.activity);

		}

		if(preference instanceof RRPreferenceFloat) {
			final RRPreferenceFloat pref = (RRPreferenceFloat) preference;
			return pref.getItem(pref.getRaw()).getName(context.activity);
		}

		return "[SUBTITLE NOT FOUND]";
	}
}