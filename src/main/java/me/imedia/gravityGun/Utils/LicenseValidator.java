package me.imedia.gravityGun.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.plugin.java.JavaPlugin;

public class LicenseValidator {
    private static final String LICENSEX_API_URL = "https://REPLACE_CHECL/license/check"; // REPLACE WITH WORKING LICENSE
    private final String licenseKey;
    private final JavaPlugin plugin;

    public LicenseValidator(JavaPlugin plugin, String licenseKey) {
        this.plugin = plugin;
        this.licenseKey = licenseKey;
    }

    public boolean isLicenseValid() {
        try {
            URL url = new URL(LICENSEX_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + licenseKey);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString().contains("\"valid\":true");
            } else {
                plugin.getLogger().severe("License validation failed: " + responseCode);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error validating license: " + e.getMessage());
        }
        return false;
    }
}
