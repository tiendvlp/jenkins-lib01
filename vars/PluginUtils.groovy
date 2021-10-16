import jenkins.model.Jenkins
import hudson.util.VersionNumber

class Plugin {
    private String shortName;
    private String version;
    private Boolean isInstalled = false;
    private Boolean isLatestVersion = false;

    Plugin(String shortName) {
        println(shortName);
        this.shortName = shortName;
        def internalPlugin = new ArrayList<String>(Jenkins.instance.pluginManager.plugins).find {x -> x.shortName == shortName};
        // find in center
        if (internalPlugin != null) {
            version = internalPlugin.version;
            isInstalled = true;
        } else {
            def pluginOnCenter = Jenkins.instance.updateCenter.getPlugin(shortName);
            if (pluginOnCenter != null) {
                version = pluginOnCenter.version;
            } else {
                throw new RuntimeException ("Your plugin does not exist in current Jenkins instance and Jenkins plugin center !");
            }
        }
    }

    Boolean isInstalled () {
        return this.isInstalled;
    }

    Boolean isInstalledLatestVersion () {
        def pluginOnCenter = Jenkins.instance.updateCenter.getPlugin(shortName);
        return this.isInstalled() && this.version == pluginOnCenter.version;
    }

    String shortName () {
        return this.shortName;
    }

    String version () {
        return this.version;
    }
}

class PluginUtils {
    PluginUtils () {

    }

    ArrayList<PluginWrapper> getAllInstalledPlugins () {
        return new ArrayList(Jenkins.instance.pluginManager.plugins);
    }

    Boolean isInstalled (String shortName) {
        def internalPlugin = getAllInstalledPlugins().find {x -> x.shortName == shortName}
        if (internalPlugin != null) {
            return true;
        }
    }

    Boolean isValid (String shortName) {
        if (isInstalled(shortName)) {
            return true;
        }
        def target = Jenkins.instance.updateCenter.getPlugin(artifactId);
        if (target != null) {
            return true;
        }
        return false;
    }

    Plugin install (String shortName) {
       def internalPlugin = getAllInstalledPlugins().find {x -> x.shortName == shortName}
       // if plugin is not installed => install
       if (internalPlugin == null) {
            def target = Jenkins.instance.updateCenter.getPlugin(shortName);
            // plugin not found
            if (target != null) {
               target.deploy();
               return new Plugin(shortName); 
            }
            return null;
       }
       return new Plugin(internalPlugin.shortName);
    }
}
