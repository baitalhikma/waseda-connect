/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 * 
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 * 
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.bai.android.ui;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.ImageView;

import com.bai.android.data.Application;
import com.bai.android.data.LogManager;
import com.bai.android.data.account.OnAccountChangedListener;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.extension.avatar.AvatarManager;
import com.bai.android.data.extension.vcard.OnVCardListener;
import com.bai.android.data.extension.vcard.VCardManager;
import com.bai.android.data.intent.AccountIntentBuilder;
import com.bai.android.data.intent.EntityIntentBuilder;
import com.bai.android.data.roster.OnContactChangedListener;
import com.bai.android.data.roster.RosterContact;
import com.bai.android.data.roster.RosterManager;
import com.bai.xmpp.address.Jid;
import com.bai.xmpp.vcard.VCard;
import com.bai.xmpp.vcard.VCardProperty;
import com.bai.xmpp.vcard.VCardProvider;
import com.bai.androiddev.R;

public class ContactViewer extends PreferenceActivity implements
		OnVCardListener, OnContactChangedListener, OnAccountChangedListener {

	private static final String SAVED_VCARD = "com.bai.android.ui.ContactViewer.SAVED_VCARD";
	private static final String SAVED_VCARD_ERROR = "com.bai.android.ui.ContactViewer.SAVED_VCARD_ERROR";

	private String account;
	private String bareAddress;
	private VCard vCard;
	private boolean vCardError;

	private MyPreferenceFragment preferenceSegment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferenceSegment = new MyPreferenceFragment();

		getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceSegment).commit();
//		addPreferencesFromResource(R.xml.contact_viewer);
//		addresses = new ArrayList<PreferenceCategory>();
//		telephones = new ArrayList<PreferenceCategory>();
//		emails = new ArrayList<PreferenceCategory>();

		if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
			// View information about contact from system contact list
			Uri data = getIntent().getData();
			if (data != null && "content".equals(data.getScheme())) {
				List<String> segments = data.getPathSegments();
				if (segments.size() == 2 && "data".equals(segments.get(0))) {
					Long id;
					try {
						id = Long.valueOf(segments.get(1));
					} catch (NumberFormatException e) {
						id = null;
					}
					if (id != null)
						// FIXME: Will be empty while application is loading
						for (RosterContact rosterContact : RosterManager
								.getInstance().getContacts())
							if (id.equals(rosterContact.getViewId())) {
								account = rosterContact.getAccount();
								bareAddress = rosterContact.getUser();
								break;
							}
				}
			}
		} else {
			account = getAccount(getIntent());
			bareAddress = Jid.getBareAddress(getUser(getIntent()));
		}
		if (account == null || bareAddress == null) {
			Application.getInstance().onError(R.string.ENTRY_IS_NOT_FOUND);
			finish();
			return;
		}
		vCard = null;
		vCardError = false;
		if (savedInstanceState != null) {
			vCardError = savedInstanceState
					.getBoolean(SAVED_VCARD_ERROR, false);
			String xml = savedInstanceState.getString(SAVED_VCARD);
			if (xml != null)
				try {
					XmlPullParser parser = XmlPullParserFactory.newInstance()
							.newPullParser();
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
							true);
					parser.setInput(new StringReader(xml));
					int eventType = parser.next();
					if (eventType != XmlPullParser.START_TAG)
						throw new IllegalStateException(
								String.valueOf(eventType));
					if (!VCard.ELEMENT_NAME.equals(parser.getName()))
						throw new IllegalStateException(parser.getName());
					if (!VCard.NAMESPACE.equals(parser.getNamespace()))
						throw new IllegalStateException(parser.getNamespace());
					vCard = (VCard) (new VCardProvider()).parseIQ(parser);
				} catch (Exception e) {
					LogManager.exception(this, e);
				}
		}
		setTitle(getString(R.string.contact_viewer_for, bareAddress));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Application.getInstance().addUIListener(OnVCardListener.class, this);
		Application.getInstance().addUIListener(OnContactChangedListener.class,
				this);
		Application.getInstance().addUIListener(OnAccountChangedListener.class,
				this);
		if (vCard == null && !vCardError)
			VCardManager.getInstance().request(account, bareAddress, null);
		updateContact();
		updateVCard();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Application.getInstance().removeUIListener(OnVCardListener.class, this);
		Application.getInstance().removeUIListener(
				OnContactChangedListener.class, this);
		Application.getInstance().removeUIListener(
				OnAccountChangedListener.class, this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_VCARD_ERROR, vCardError);
		if (vCard != null)
			outState.putString(SAVED_VCARD, vCard.getChildElementXML());
	}

	@Override
	public void onVCardReceived(String account, String bareAddress, VCard vCard) {
		if (!this.account.equals(account)
				|| !this.bareAddress.equals(bareAddress))
			return;
		this.vCard = vCard;
		this.vCardError = false;
		updateVCard();
	}

	@Override
	public void onVCardFailed(String account, String bareAddress) {
		if (!this.account.equals(account)
				|| !this.bareAddress.equals(bareAddress))
			return;
		this.vCard = null;
		this.vCardError = true;
		updateVCard();
		Application.getInstance().onError(R.string.XMPP_EXCEPTION);
	}

	@Override
	public void onContactsChanged(Collection<BaseEntity> entities) {
		for (BaseEntity entity : entities)
			if (entity.equals(account, bareAddress)) {
				updateContact();
				break;
			}
	}

	@Override
	public void onAccountsChanged(Collection<String> accounts) {
		if (accounts.contains(account))
			updateContact();
	}

	/**
	 * Sets value for the preference by its id.
	 * 
	 * @param resourceId
	 * @param value
	 */
	private void setValue(int resourceId, String value) {
		if (value == null)
			value = "";
//		findPreference(getString(resourceId)).setSummary(value);
		preferenceSegment.findPreference(getString(resourceId)).setSummary(value);
	}

	/**
	 * @param source
	 * @param value
	 * @param splitter
	 * @return Concatenated source and value with splitter if necessary.
	 */
//	private String addString(String source, String value, String splitter) {
//		if (value == null || "".equals(value))
//			return source;
//		if (source == null || "".equals(source))
//			return value;
//		return source + splitter + value;
//	}

	private void updateContact() {
	}

	private void updateVCard() {
		if (vCard == null)
			return;
		String displayUserName = null;
		if (bareAddress != null && bareAddress.indexOf("@") > 0) {
			displayUserName = bareAddress.substring(0, bareAddress.indexOf("@"));
		} else {
			displayUserName = bareAddress;
		}
		setValue(R.string.contact_viewer_id , displayUserName);
		setValue(R.string.contact_viewer_name, vCard.getFormattedName());
		setValue(R.string.contact_viewer_email , vCard.getField(VCardProperty.EMAIL));
		setValue(R.string.contact_viewer_nationality , vCard.getField(VCardProperty.NATIONALITY));
		setValue(R.string.contact_viewer_graduation_year , vCard.getField(VCardProperty.GRAD_YEAR));
		setValue(R.string.contact_viewer_status , vCard.getField(VCardProperty.STATUS));
		setValue(R.string.contact_viewer_faculty , vCard.getField(VCardProperty.FACULTY));
		
		ImageView avatarImage = (ImageView) findViewById(R.id.contact_avatar);
		avatarImage.setImageDrawable(AvatarManager.getInstance().getUserAvatarForContactList(bareAddress));
//		setValue(R.string.vcard_nick_name,
//				vCard.getField(VCardProperty.NICKNAME));
//		setValue(R.string.vcard_formatted_name, vCard.getFormattedName());
//		setValue(R.string.vcard_prefix_name,
//				vCard.getField(NameProperty.PREFIX));
//		setValue(R.string.vcard_given_name, vCard.getField(NameProperty.GIVEN));
//		setValue(R.string.vcard_middle_name,
//				vCard.getField(NameProperty.MIDDLE));
//		setValue(R.string.vcard_family_name,
//				vCard.getField(NameProperty.FAMILY));
//		setValue(R.string.vcard_suffix_name,
//				vCard.getField(NameProperty.SUFFIX));
//		setValue(R.string.vcard_birth_date, vCard.getField(VCardProperty.BDAY));
//		setValue(R.string.vcard_title, vCard.getField(VCardProperty.TITLE));
//		setValue(R.string.vcard_role, vCard.getField(VCardProperty.ROLE));
//		List<Organization> organizations = vCard.getOrganizations();
//		String organization;
//		if (organizations.isEmpty())
//			organization = null;
//		else {
//			organization = organizations.get(0).getName();
//			for (String unit : organizations.get(0).getUnits())
//				organization = addString(organization, unit, "\n");
//		}
//		setValue(R.string.vcard_organization, organization);
//		setValue(R.string.vcard_url, vCard.getField(VCardProperty.URL));
//		String categories = null;
//		for (String category : vCard.getCategories())
//			categories = addString(categories, category, "\n");
//		setValue(R.string.vcard_categories, categories);
//		setValue(R.string.vcard_note, vCard.getField(VCardProperty.NOTE));
//		setValue(R.string.vcard_decsription, vCard.getField(VCardProperty.DESC));
//		PreferenceScreen screen = getPreferenceScreen();
//		PreferenceScreen screen = preferenceSegment.getPreferenceScreen();
//		for (PreferenceCategory category : addresses)
//			screen.removePreference(category);
//		for (PreferenceCategory category : telephones)
//			screen.removePreference(category);
//		for (PreferenceCategory category : emails)
//			screen.removePreference(category);
//		for (Address address : vCard.getAddresses()) {
//			String types = null;
//			for (AddressType type : address.getTypes())
//				types = addString(types, getString(ADDRESS_TYPE_MAP.get(type)),
//						", ");
//			String value = null;
//			for (AddressProperty property : AddressProperty.values())
//				value = addString(value, address.getProperties().get(property),
//						"\n");
//			PreferenceScreen addressScreen = createTypedCategory(
//					R.string.vcard_address, types, value);
//			for (AddressProperty property : AddressProperty.values())
//				addPreferenceScreen(addressScreen,
//						ADDRESS_PROPERTY_MAP.get(property), address
//								.getProperties().get(property));
//		}
//		for (Telephone telephone : vCard.getTelephones()) {
//			String types = null;
//			for (TelephoneType type : telephone.getTypes())
//				types = addString(types,
//						getString(TELEPHONE_TYPE_MAP.get(type)), ", ");
//			createTypedCategory(R.string.vcard_telephone, types,
//					telephone.getValue());
//		}
//		for (Email email : vCard.getEmails()) {
//			String types = null;
//			for (EmailType type : email.getTypes())
//				types = addString(types, getString(EMAIL_TYPE_MAP.get(type)),
//						", ");
//			createTypedCategory(R.string.vcard_email, types, email.getValue());
//		}
	}

//	private PreferenceScreen createTypedCategory(int title, String types,
//			String value) {
//		PreferenceCategory preferenceCategory = new PreferenceCategory(this);
//		preferenceCategory.setTitle(title);
//		getPreferenceScreen().addPreference(preferenceCategory);
//
//		addPreferenceScreen(preferenceCategory, R.string.vcard_type, types);
//		return addPreferenceScreen(preferenceCategory, title, value);
//	}
//
//	private PreferenceScreen addPreferenceScreen(PreferenceGroup container,
//			int title, String summary) {
//		PreferenceScreen preference = getPreferenceManager()
//				.createPreferenceScreen(this);
//		preference.setLayoutResource(R.layout.info_preference);
//		preference.setTitle(title);
//		preference.setSummary(summary == null ? "" : summary);
//		container.addPreference(preference);
//		return preference;
//	}

	public static Intent createIntent(Context context, String account,
			String user) {
		return new EntityIntentBuilder(context, ContactViewer.class)
				.setAccount(account).setUser(user).build();
	}

	private static String getAccount(Intent intent) {
		return AccountIntentBuilder.getAccount(intent);
	}

	private static String getUser(Intent intent) {
		return EntityIntentBuilder.getUser(intent);
	}

	public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.contact_viewer);
            
        }
    }
}
