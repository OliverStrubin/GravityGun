package me.imedia.gravityGun;

import me.imedia.gravityGun.Utils.LicenseValidator;
import me.imedia.gravityGun.Utils.TNTListener;
import me.imedia.gravityGun.commands.GravityGunCmd;
import me.imedia.gravityGun.commands.ReloadConfigCmd;
import me.imedia.gravityGun.settings.Dropping;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class GravityGun extends JavaPlugin implements CommandExecutor {
    private GravityGunManager gravityGunManager;
    private boolean isLicenseValid;

    @Override
    public void onEnable() {

        String licenseKey = getConfig().getString("license.token");

        LicenseValidator licenseValidator = new LicenseValidator(this, licenseKey);
         isLicenseValid = licenseValidator.isLicenseValid();

        /*
         if(!isLicenseValid) {

             String invMessage =
                     "\n" +  // Start on the second line to prevent cutting off
                             "+--------------------------------------------+\n" +
                             "|      INPUT VALID LICENSE IN CONFIG        |\n" +
                             "+--------------------------------------------+\n" +
                             "+--------------------------------------------+\n" +
                             "|      DISABLING PLUGIN...                 |\n" +
                             "+--------------------------------------------+\n";

             getLogger().severe(invMessage);
             getServer().getPluginManager().disablePlugin(this);
             return;
         }*/ //DO NOT FORGET TO RE ENABLE BEFORE RELEASE

        //bugs to fix
        //tnt not exploding correctly
        //messages not showing
        //impliment the license system

        saveDefaultConfig();
        gravityGunManager = new GravityGunManager(this);
        getServer().getPluginManager().registerEvents(gravityGunManager, this);
        getServer().getPluginManager().registerEvents(new Dropping(this), this);
        getServer().getPluginManager().registerEvents(new TNTListener(this), this);
        this.getCommand("gravitygun").setExecutor(new GravityGunCmd(this));
        getCommand("gravityreload").setExecutor(new ReloadConfigCmd(this, gravityGunManager));

        String message =
                "\n" +
                        "  TTTTT  H   H   A   N   N  K   K    Y   Y  OOO  U   U\n" +
                        "    T    H   H  A A  NN  N  K  K      Y Y  O   O U   U\n" +
                        "    T    HHHHH AAAAA N N N  KKK        Y   O   O U   U\n" +
                        "    T    H   H A   A N  NN  K  K       Y   O   O U   U\n" +
                        "    T    H   H A   A N   N  K   K      Y    OOO   UUU\n" +
                        "\n" +
                        "  FFFFF  OOO  RRRR      PPPP  U   U  RRRR   CCCC  H   H  A   SSSS  III  N   N  GGG\n" +
                        "  F     O   O R   R     P   P U   U  R   R C      H   H  A A S      I   NN  N G\n" +
                        "  FFFF  O   O RRRR      PPPP  U   U  RRRR  C      HHHHH AAAAA  SSS   I   N N N G  GG\n" +
                        "  F     O   O R  R      P     U   U  R  R  C      H   H A   A     S  I   N  NN G   G\n" +
                        "  F      OOO  R   R     P      UUU   R   R  CCCC  H   H A   A SSSS  III N   N  GGGG\n";

        getLogger().info(message);
        getLogger().info("Gravity Gun plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (gravityGunManager != null) {
            gravityGunManager.stopCarryTask();
        }
        getLogger().info("Gravity Gun plugin disabled!");
    }
}
