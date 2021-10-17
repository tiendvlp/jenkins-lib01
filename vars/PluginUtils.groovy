import jenkins.model.Jenkins
import hudson.util.VersionNumber

class PluginManager {
    private def updateCenter;
    private def pluginManager;

    PluginManager () {
        updateCenter = Jenkins.instance.updateCenter;
        pluginManager = Jenkins.instance.pluginManager;
    }

    Boolean isLatestVersion (PluginWrapper plugin) {
        def pluginOnCenter = updateCenter.getPlugin(shortName);
        return this.isInstalled() && plugin.version == pluginOnCenter.version;
    }

    PluginWrapper getInstalledPlugin (String shortName) {
        return new ArrayList<String>(pluginManager.plugins).find {x -> x.shortName == shortName};
    }

    ArrayList<PluginWrapper> getAllInstalledPlugins () {
        return new ArrayList(pluginManager.plugins);
    }

    Boolean isInstalled (String shortName) {
        return getAllInstalledPlugins().find {x -> x.shortName == shortName} != null
    }

    Boolean isValid (String shortName) {
        if (isInstalled(shortName)) {
            return true;
        }
        def target = updateCenter.getPlugin(artifactId);
        if (target != null) {
            return true;
        }
        return false;
    }

    void disablePlugin (PluginWrapper plugin) {
        if (plugin.isEnabled()) {
            plugin.disable();
        }
    }

    void disablePlugin (String shortName) {
        def plugin = pluginManager.getPlugin(shortName);
        if (plugin == null) {
            throw new RuntimeException("Plugin ${shortname} is not installed or does not exist");
        }
        disablePlugin(plugin);
    }

    void enablePlugin (String shortName) {
        def plugin = pluginManager.getPlugin(shortName);
        if (plugin == null) {
            throw new RuntimeException("Plugin ${shortname} is not installed or does not exist");
        }
        enablePlugin(plugin);
    }

    void enablePlugin(PluginWrapper plugin) {
        if (!plugin.isEnabled()) {
            plugin.enable();
        }
        plugin.getDependencies().each { 
           enablePlugin(it.shortName);
        }
    }

    Boolean isPluginEnabled (String shortName) {
        def plugin = pluginManager.getPlugin(shortName);
        if (plugin == null) {
            throw new RuntimeException("Plugin ${shortname} is not installed or does not exist");
        }
        return isPluginEnabled(plugin);
    }

    Boolean isPluginEnabled (PluginWrapper plugin) {
        return plugin.isEnabled();
    }

    Boolean install (String shortName) {
       def internalPlugin = getAllInstalledPlugins().find {x -> x.shortName == shortName}
       // if plugin is not installed => install
       if (internalPlugin == null) {
            def target =  updateCenter.getPlugin(shortName);
            // plugin not found
            if (target == null) {
                throw new RuntimeException("Your plugin does not exist on plugins center");
            }
         	target.deploy ();
			return true;
        }
       return false;
    }
  
    Boolean isRestartRequired () {
    	return updateCenter.isRestartRequiredForCompletion();
    }
}

PluginManager u = new PluginManager();
u.install('subversion');
println(u.isInstalled('subversion'));
u.isRestartRequired()
u.disablePlugin('jobConfigHistory');
u.isPluginEnabled('jobConfigHistory')
