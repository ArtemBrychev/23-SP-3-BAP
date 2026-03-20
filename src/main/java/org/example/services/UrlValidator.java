package org.example.services;


import java.net.*;
import java.util.regex.Pattern;

public class UrlValidator {

    private static final Pattern SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://.*");

    public static boolean isValidExternalUrl(String input) {
        try {

            if (input == null || input.isBlank())
                return false;

            input = input.trim();

            if (!SCHEME.matcher(input).matches()) {
                input = "https://" + input;
            }

            URI uri = new URI(input);

            if (!uri.isAbsolute())
                return false;

            String scheme = uri.getScheme();
            if (scheme == null)
                return false;

            scheme = scheme.toLowerCase();
            if (!scheme.equals("http") && !scheme.equals("https"))
                return false;

            String host = uri.getHost();
            if (host == null || host.isBlank())
                return false;

            if (isLocalAddress(host))
                return false;

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isLocalAddress(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);

            if (addr.isAnyLocalAddress()) return true;
            if (addr.isLoopbackAddress()) return true;
            if (addr.isLinkLocalAddress()) return true;
            if (addr.isSiteLocalAddress()) return true;

            String ip = addr.getHostAddress();

            if (ip.startsWith("127.")) return true;
            if (ip.startsWith("10.")) return true;
            if (ip.startsWith("192.168.")) return true;

            if (ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*"))
                return true;

            return false;

        } catch (Exception e) {
            return true;
        }
    }
}