package me.pheasn.blockown;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "messages";//$NON-NLS-1$
	private static final String INCLUDED_BUNDLE_NAME = "me.pheasn.blockown.messages"; //$NON-NLS-1$
	private static boolean initialized = false;
	private static ResourceBundle RESOURCE_BUNDLE;

	public static InputStream getPropertiesFileStream(Locale locale) {
		try {
			if (locale == null) {
				return BlockOwn.class.getResourceAsStream(BUNDLE_NAME
						+ ".properties"); //$NON-NLS-1$
			} else if (locale.equals(Locale.GERMANY)) {
				return BlockOwn.class.getResourceAsStream(BUNDLE_NAME
						+ "_de_DE.properties"); //$NON-NLS-1$
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	public static String getString(String key) {
		if (!initialized) {
			initialize();
		}
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getString(String key, Object... params) {
		if (!initialized) {
			initialize();
		}
		try {
			return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	private static void initialize() {
		try {
			File file = new File("./plugins/BlockOwn/"); //$NON-NLS-1$
			URL[] urls = { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME,
					Locale.getDefault(), loader);
			ResourceBundle included = ResourceBundle
					.getBundle(INCLUDED_BUNDLE_NAME);
			for (String key : included.keySet()) {
				if (!RESOURCE_BUNDLE.containsKey(key)) {
					// TODO
				}
			}
		} catch (Exception e) {
			RESOURCE_BUNDLE = ResourceBundle.getBundle(INCLUDED_BUNDLE_NAME);
		}

		initialized = true;
	}

	private Messages() {

	}
}
