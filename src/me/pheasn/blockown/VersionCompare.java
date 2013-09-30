package me.pheasn.blockown;

public abstract class VersionCompare {
	public static int compare(String version1, String version2)
			throws NumberFormatException {
		version1 = version1.replace(".", ""); //$NON-NLS-1$ //$NON-NLS-2$
		version2 = version2.replace(".", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (version1.length() < 3) {
			version1 += "0";
		}
		if (version2.length() < 3) {
			version2 += "0";
		}
		if (Integer.valueOf(version1) < Integer.valueOf(version2)) {
			return -1;
		} else if (Integer.valueOf(version1) > Integer.valueOf(version2)) {
			return 1;
		} else {
			return 0;
		}
	}
}
