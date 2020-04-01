package petrovskyi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public SecurityService() {
    }

    public boolean login(String login, String password) {
        LOG.info("run login({})", login);
        LOG.debug("run login({},{})", login, password);

        if ("admin".equals(login) && "pass".equals(password)) {
            LOG.info("Login and password are correct");

            return true;
        } else {
            LOG.error("Cannot find user with login " + login + " and password " + password + ". " +
                    "Make sure you typed the login and the password correctly and then try again!");
            return false;
        }
    }

}