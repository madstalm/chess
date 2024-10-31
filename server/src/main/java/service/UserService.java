package service;

import dataaccess.UserDAO;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;

import java.util.Collection;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData registerUser(UserData user) throws Exception {
        /*Steps
        1. Check if the user already exists
        2. If not, create the user and add their info to the db
        3. get authentication info for the new user
        */
        String username = user.username();
        if ((username == null) || (username.isEmpty()) || (user.password() == null) || (user.password().isEmpty())) {
            throw new InvalidInputException("Error: bad request");
        }
        if (dataAccess.getUserData(username) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        // uncomment to reactivate email validation functionality
        /*
        if (invalidEmail(user.email())) {
            throw new InvalidInputException("Error: invalid email");
        }
        */
        String hash = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        dataAccess.addUserData(new UserData(user.username(), hash, user.email()));
        return user;
    }

    public UserData checkLogin(UserData user) throws Exception {
        String username = user.username();
        String password = user.password();
        if ((username == null) || (username.isEmpty()) || (password == null) || (password.isEmpty())) {
            throw new InvalidInputException("Error: bad request");
        }
        UserData checkUser = dataAccess.getUserData(username);
        if (checkUser == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String checkPassword = checkUser.password();
        if (!BCrypt.checkpw(password, checkPassword)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return checkUser;
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllUsers();
    }

    private boolean invalidEmail(String email) {
        String emailREGEX = "^[a-zA-Z0-9_%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailREGEX);
        if (email == null || email.isEmpty()) {
            return true;
        }
        return !pattern.matcher(email).matches();
    }
    
}
