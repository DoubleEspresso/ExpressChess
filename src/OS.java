
public class OS {
	private static String os = null;

	public static String getOsName() {
		if (os == null) {
			os = System.getProperty("os.name");
		}
		return os;
	}

	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	public static boolean isUnix() {
		return getOsName().startsWith("Linux");
	}

	public static boolean isMac() {
		return getOsName().startsWith("Mac");
	}
}

