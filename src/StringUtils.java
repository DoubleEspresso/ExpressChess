
public class StringUtils {
	public static Boolean isOK(String result) {
		return result != null;
	}

	public static String extension(String file) {
		if (file == null)
			return null;
		try {
			return file.substring(file.lastIndexOf('.'));
		} catch (Exception any) {
			System.out.println("..parse file extension exception: " + any.getMessage());
			return null;
		}
	}

	public static String dirSeparator() {
		try {
			if (OS.isWindows()) {
				return "\\";
			} else if (OS.isUnix() || OS.isMac()) {
				return "/";
			} else {
				System.out.println("..parse file basename error: unknown OS type");
				return null;
			}
		} catch (Exception any) {
			System.out.println("..parse file basename exception: " + any.getMessage());
			return null;
		}
	}

	public static String basename(String file) {
		if (file == null)
			return null;
		String base = null;
		try {
			if (OS.isWindows()) {
				base = file.substring(0, file.lastIndexOf('\\'));
			} else if (OS.isUnix() || OS.isMac()) {
				base = file.substring(0, file.lastIndexOf('/'));
			} else {
				System.out.println("..parse file basename error: unknown OS type");
				return null;
			}
		} catch (Exception any) {
			System.out.println("..parse file basename exception: " + any.getMessage());
			base = null;
		}
		return base;
	}
}
