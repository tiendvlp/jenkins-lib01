import hudson.model.User.*;
import hudson.security.*;

class UserManager {
    Set getAllUsers () {
        return User.getAll();
    }

    void createUser (String userName, String password) {
        if (!this.isUserExists(userName)) {
            throw new RuntimeException("Your user ${userName} already exist !")
        }
        def realm = new HudsonPrivateSecurityRealm(false);
        realm.createAccount(userName, password);
        Jenkins.instance.setSecurityRealm(realm);
        Jenkins.instance.save();
    }

    Boolean isUserExists (String userName) {
        return User.get(userName);
    }

    void deleteUser (String userName) {
        if (!this.isUserExists(userName)) {
            throw new RuntimeException("Your user ${userName} already exist !")
        }
        User u = User.get(userName);
        u.delete();
    }
}
}

