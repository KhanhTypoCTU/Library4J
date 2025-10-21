package ctu.cict.khanhtypo.forms;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import ctu.cict.khanhtypo.users.User;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class StartupScreen {
    private static final MessageDigest DIGESTER;
    private static final HexFormat HEX_FORMAT;
    private JPanel basePanel;
    private JLabel title;
    private JTextField usernameField;
    private JLabel loginLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JRadioButton registerButton;
    private JRadioButton loginButton;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPwField;
    private JButton confirmButton;
    private Operation currentOperation;

    public JPanel getBasePanel() {
        return basePanel;
    }

    public StartupScreen() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(loginButton);
        buttonGroup.add(registerButton);
        buttonGroup.setSelected(loginButton.getModel(), true);
        this.currentOperation = Operation.LOGIN;

        this.confirmPasswordLabel.setVisible(false);
        this.confirmPwField.setVisible(false);
        this.title.setText("Login");
        this.title.setFont(this.title.getFont().deriveFont(Font.BOLD, 15));

        this.confirmButton.addActionListener(e -> this.onConfirm());

        this.loginButton.addActionListener(e -> changeOperation(loginButton));
        this.registerButton.addActionListener(e -> changeOperation(registerButton));
    }

    private void onConfirm() {

    }

    private void changeOperation(JRadioButton button) {
        if (button.isSelected()) {
            if (button == registerButton) {
                this.currentOperation = Operation.REGISTER;
                this.confirmPasswordLabel.setText("Confirm Password:");
                this.confirmPwField.setVisible(true);
                this.confirmPasswordLabel.setVisible(true);
            } else if (button == loginButton) {
                this.currentOperation = Operation.LOGIN;
                this.confirmPwField.setVisible(false);
                this.confirmPasswordLabel.setVisible(false);
            }
            ScreenUtils.packFrame();
        }
        this.title.setText(this.currentOperation.getTitle());
    }

    private void tryLogin() {
        lockAll();
        if (areFieldsOccupied()) {
            FindIterable<User> username = DatabaseUtils.getAccounts().find(Filters.eq("username", this.usernameField.getText()));
            MongoCursor<User> iterator = username.iterator();
            if (iterator.hasNext()) {
                DIGESTER.update(this.usernameField.getText().getBytes(StandardCharsets.UTF_8));
                DIGESTER.update(new String(this.passwordField.getPassword()).getBytes(StandardCharsets.UTF_8));

                if (iterator.next().password().equals(HEX_FORMAT.formatHex(DIGESTER.digest()))) {
                    System.out.println("OKAY");
                    //TODO
                } else System.out.println("Wrong password.");

                DIGESTER.reset();
            } else System.out.println("No user found with username : " + this.usernameField.getText());
            iterator.close();
        }
        unlockAll();
    }

    private void tryRegister() {
        lockAll();
        if (areFieldsOccupied()) {
            FindIterable<User> username = DatabaseUtils.getAccounts().find(Filters.eq("username", this.usernameField.getText()));
            MongoCursor<User> cursor = username.cursor();
            if (cursor.hasNext()) {
                System.out.println("Username already exists, can not register.");
            }
            cursor.close();
        }
        unlockAll();
    }

    private boolean areFieldsOccupied() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.isBlank()) {
            System.out.println("Username is blank.");
            return false;
        }
        if (password.isBlank()) {
            System.out.println("Password is blank.");
            return false;
        }
        return true;
    }

    private void unlockAll() {
        this.loginButton.setEnabled(true);
        this.registerButton.setEnabled(true);
        this.usernameField.setEnabled(true);
        this.passwordField.setEnabled(true);
        this.confirmPasswordLabel.setEnabled(true);
        this.confirmPwField.setEnabled(true);
    }

    private void lockAll() {
        this.loginButton.setEnabled(false);
        this.registerButton.setEnabled(false);
        this.usernameField.setEnabled(false);
        this.passwordField.setEnabled(false);
        this.confirmPasswordLabel.setEnabled(false);
        this.confirmPwField.setEnabled(false);
    }

    static {
        HEX_FORMAT = HexFormat.of();
        try {
            DIGESTER = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private enum Operation {
        LOGIN("Login"),
        REGISTER("Register"),
        UPDATE("Change Password");

        private final String title;

        Operation(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
