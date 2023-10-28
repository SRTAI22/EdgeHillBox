package com.cloudedge.app.GUI;

// Local imports 
import com.cloudedge.app.GUI.GUIMain;
import com.cloudedge.app.GUI.ClientAuthView;
import com.cloudedge.app.Webserver.ServerMain;
import com.cloudedge.app.GUI.ServerStatusView;
import com.cloudedge.app.GUI.FileOperationView;

// UI imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

// other
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIMain {
    // helper style
    public static class RoundJPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.setColor(getForeground());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        }
    }

    // login page
    public static void login_page(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // add login page to frame
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        // ask for username and password append to frame
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(200, 10, 80, 25);
        frame.add(usernameLabel);
        // add input field
        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(200, 30, 160, 25);
        frame.add(usernameText);

        // add password
        JLabel passwordLabel = new JLabel("password");
        passwordLabel.setBounds(200, 60, 80, 30);
        frame.add(passwordLabel);

        // add input field
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(200, 80, 160, 30);
        frame.add(passwordText);

        JButton confirmLogin = new JButton("Log in");
        confirmLogin.setBounds(200, 120, 170, 40);
        frame.add(confirmLogin);

        // back button
        JButton backButton = new JButton("Back");
        backButton.setBounds(200, 170, 170, 40);
        frame.add(backButton);

        confirmLogin.addActionListener(d -> {
            System.out.println(passwordText.getText()); // debug
            System.out.println(usernameText.getText()); // debug

            if ((usernameText.getText().equals("") || passwordText.getText().equals(""))) {
                JOptionPane.showMessageDialog(frame, "Username and Password, cannot be blank.", "Details Error", 0);
            } else {
                // the method to validate details
                ClientAuthView clientAuthView = new ClientAuthView();
                boolean isValid = clientAuthView.validate_credentials(frame, usernameText.getText(),
                        passwordText.getText());
                System.out.println(isValid);
                if (isValid) {
                    main_page(frame);
                }
            }
        });

        backButton.addActionListener(e -> {
            login_sign_up_page(frame); // back to login page
        });

    }

    // signup page
    public static void signup_page(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // add login page to frame
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        // ask for username and password append to frame
        JLabel usernameLabel = new JLabel("Create username");
        usernameLabel.setBounds(200, 10, 120, 25);
        frame.add(usernameLabel);
        // add input field
        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(200, 30, 160, 30);
        frame.add(usernameText);

        // add password
        JLabel passwordLabel = new JLabel("Create password");
        passwordLabel.setBounds(200, 60, 120, 25);
        frame.add(passwordLabel);

        // add input field
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(200, 80, 160, 30);
        frame.add(passwordText);

        JButton confirmSignUP = new JButton("Sign up");
        confirmSignUP.setBounds(200, 120, 170, 40);
        frame.add(confirmSignUP);

        // back button
        JButton backButton = new JButton("Back");
        backButton.setBounds(200, 170, 170, 40);
        frame.add(backButton);

        confirmSignUP.addActionListener(d -> {
            System.out.println(passwordText.getText()); // debug
            System.out.println(usernameText.getText()); // debug

            if ((usernameText.getText().equals("") || passwordText.getText().equals(""))) {
                JOptionPane.showMessageDialog(frame, "Username and Password, cannot be blank.", "Details Error", 0);
            } else {
                // the method to validate details
                ClientAuthView clientAuthView = new ClientAuthView();
                boolean isValid = clientAuthView.create_user(frame, usernameText.getText(), passwordText.getText());
                System.out.println(isValid);
                if (isValid) {
                    main_page(frame);
                }
            }
        });

        // back button event listner
        backButton.addActionListener(e -> {
            login_sign_up_page(frame); // back to login page
        });
    }

    // Main page
    public static void main_page(JFrame frame) {

        FileOperationView fileOperationView = new FileOperationView();
        boolean localbox = fileOperationView.Check_local_Box(); // initialise EdgeHillBox folder

        // main page desin and code which will call upon sub function to make post
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // remove laat page content
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();

        // Set layout to null for custom positioning
        frame.setLayout(null);

        // Synced status label
        JLabel syncedStatusLabel = new JLabel("Synced status");
        syncedStatusLabel.setBounds(50, 50, 100, 30); // x, y, width, height
        frame.add(syncedStatusLabel);

        // Sync button
        JButton syncButton = new JButton("Sync");
        syncButton.setBounds(50, 90, 80, 30);
        frame.add(syncButton);

        // Local Files label and panel
        JLabel localFilesLabel = new JLabel("Local Files");
        localFilesLabel.setBounds(200, 50, 100, 30);
        frame.add(localFilesLabel);

        JPanel localFilesPanel = new RoundJPanel();
        localFilesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        localFilesPanel.setBounds(200, 90, 180, 380);
        frame.add(localFilesPanel);

        // Cloud Files label and panel
        JLabel cloudFilesLabel = new JLabel("Cloud files");
        cloudFilesLabel.setBounds(400, 50, 100, 30);
        frame.add(cloudFilesLabel);

        JPanel cloudFilesPanel = new RoundJPanel();
        cloudFilesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cloudFilesPanel.setBounds(400, 90, 180, 380);
        frame.add(cloudFilesPanel);

        // Cloud Edge label at the bottom
        JLabel cloudEdgeLabel = new JLabel("Cloud Edge");
        cloudEdgeLabel.setBounds(20, 430, 270, 120);
        frame.add(cloudEdgeLabel);

        // Size and visibility settings
        frame.setSize(700, 540); // Width, height
        frame.setVisible(true);

        if (localbox) {
            // run code to add and delete files
            List<Path> files = fileOperationView.getFilesFromLocalBox(); // get local files
            System.out.println(files);

            syncButton.addActionListener(e -> {
                // create hash map to store file names and checksums
                Map<String, String> filesum = new HashMap<>();

                // get hash for files
                for (Path file : files) {
                    String hash = fileOperationView.calculateChecksum(file);
                    filesum.put(file.getFileName().toString(), hash); // add files to hash map to send off
                }

                // send hash map for comparison
                String desyncfiles = fileOperationView.fileSyncCheck(filesum);

                if (desyncfiles != null && !desyncfiles.isEmpty()) {
                    // Here, you need to convert 'desyncfiles' to a list of file paths to upload
                    List<Path> filesToUpload = convertDesyncFilesToPath(desyncfiles);
                    boolean upload = fileOperationView.uploadfiles(filesToUpload);
                }
            });
        }

    }

    private static List<Path> convertDesyncFilesToPath(String desyncfiles) {
        List<Path> filesToUpload = new ArrayList<>();
        String[] fileNames = desyncfiles.split(",");
        for (String fileName : fileNames) {
            filesToUpload.add(Paths.get(fileName.trim()));
        }
        return filesToUpload;
    }

    private static List<String> convertDesyncFilesToList(String desyncfiles) {
        List<String> filesToUpload = new ArrayList<>();
        String[] fileNames = desyncfiles.split(",");
        for (String fileName : fileNames) {
            filesToUpload.add(fileName.trim());
        }
        return filesToUpload;
    }

    // MAIN|login in and sign up page

    public static void login_sign_up_page(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // set close behaviour
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        frame.setTitle("Cloud-Edge");
        frame.setSize(550, 550);
        frame.setVisible(true);

        // add log in button
        JButton loginButton = new JButton("Log In");
        loginButton.setBounds(200, 200, 140, 40);
        frame.add(loginButton);

        // add sign up button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(200, 300, 140, 40);
        frame.add(signupButton);

        // add action listeners
        loginButton.addActionListener(e -> {
            login_page(frame); // login page
        });

        signupButton.addActionListener(e -> {
            signup_page(frame); // sign up page
        });

        // set auto layout
        frame.setLayout(null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cloud-Edge"); // Create new object of Jfrmae this is the main window which will be

        ServerStatusView serverStatusView = new ServerStatusView();
        serverStatusView.connect(frame);

    }

}