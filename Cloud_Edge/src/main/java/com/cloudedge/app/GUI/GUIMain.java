package com.cloudedge.app.GUI;

// Local imports 
import com.cloudedge.app.GUI.GUIMain;
import com.cloudedge.app.Client.ClientAuthView;
import com.cloudedge.app.Client.FileOperationView;
import com.cloudedge.app.Client.ServerStatusView;
import com.cloudedge.app.Webserver.ServerMain;

// UI imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
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
                    try {
                        main_page(frame);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
                    try {
                        main_page(frame);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        // back button event listner
        backButton.addActionListener(e -> {
            login_sign_up_page(frame); // back to login page
        });
    }

    public static void main_page(JFrame frame) throws IOException {
        FileOperationView fileOperationView = new FileOperationView();
        boolean localbox = fileOperationView.Check_local_Box(); // initialise CloudEdgeBox folder

        // main page design and code which will call upon sub function to make post
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // remove last page content
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
        localFilesPanel.setLayout(new BoxLayout(localFilesPanel, BoxLayout.Y_AXIS)); // set vertical layout
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
            System.out.println("Local Files: " + files);

            List<String> remoteFiles = fileOperationView.getRemoteFiles(); // get remote files
            System.out.println("Remote files: " + remoteFiles);

            // Download button
            JButton downloadButton = new JButton("Download");
            downloadButton.setBounds(430, 480, 100, 30); // Adjust position as
                                                         // needed
            // Upload button
            JButton uploadButton = new JButton(
                    "Upload");
            uploadButton.setBounds(250, 480, 80, 30);

            // Delete button
            JButton deleteButton = new JButton("Delete");
            deleteButton.setBounds(50, 120, 100, 30);
            frame.add(deleteButton);
            deleteButton.setVisible(true);

            // Local Files Panel
            List<JCheckBox> checkBoxes = new ArrayList<>();
            for (Path file : files) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setActionCommand(file.toString());
                checkBox.setOpaque(false);
                checkBox.setHorizontalAlignment(SwingConstants.LEFT);
                checkBoxes.add(checkBox);

                JPanel filePanel = createFilePanel(checkBox, uploadButton); // Pass checkbox and upload button
                localFilesPanel.add(filePanel);
            }

            // Remote Files Panel
            List<JCheckBox> remoteCheckBoxes = new ArrayList<>();
            for (String remoteFile : remoteFiles) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setActionCommand(remoteFile);
                checkBox.setOpaque(false);
                checkBox.setHorizontalAlignment(SwingConstants.LEFT);
                remoteCheckBoxes.add(checkBox);

                JPanel filePanel = createFilePanel(checkBox, downloadButton); // Pass checkbox and download button
                cloudFilesPanel.add(filePanel);
            }

            // After adding all file panels
            uploadButton.setVisible(true);
            downloadButton.setVisible(true);

            frame.add(downloadButton);
            frame.add(uploadButton);

            // Action listener for download button
            downloadButton.addActionListener(e -> {
                List<String> selectedFilesToDownload = new ArrayList<>();
                for (int i = 0; i < remoteCheckBoxes.size(); i++) {
                    JCheckBox checkBox = remoteCheckBoxes.get(i);
                    if (checkBox.isSelected()) {
                        String remoteFile = remoteFiles.get(i);
                        System.out.println("GUI selected remote file: " + remoteFile);
                        selectedFilesToDownload.add(remoteFile);
                    }
                }
                if (!selectedFilesToDownload.isEmpty()) {
                    // Perform download operation
                    Boolean result;
                    try {
                        result = fileOperationView.downloadFiles(selectedFilesToDownload);
                        if (result) {
                            JOptionPane.showMessageDialog(frame, "Files downloaded sucessfully"); // display success
                                                                                                  // message
                        }
                        System.out.println("Download operation: " + result);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(frame, "Error while downloading files"); // display error message
                        e1.printStackTrace();
                    }

                }

                // Deselect all checkboxes and hide download button
                for (JCheckBox checkBox : remoteCheckBoxes) {
                    checkBox.setSelected(false);
                }
                try {
                    refreshFileDisplay(localFilesPanel, cloudFilesPanel, fileOperationView, downloadButton,
                            downloadButton);
                    // After adding all file panels
                    uploadButton.setVisible(true);
                    downloadButton.setVisible(true);

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                downloadButton.setVisible(false);
            });

            frame.add(uploadButton);

            // Add action listener to upload button
            uploadButton.addActionListener(e -> {
                // create hash map to store file names and checksums
                Map<String, String> filesum = new HashMap<>();

                // get hash for selected files
                List<Path> selectedFiles = new ArrayList<>();
                for (JCheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        String filePath = checkBox.getActionCommand(); // Use getActionCommand() instead of getText()
                        if (!filePath.isEmpty()) {
                            System.out.println("text from check box: " + filePath);
                            Path file = Paths.get(filePath);
                            selectedFiles.add(file);
                            System.out.print("selected local file path: " + file);
                            String hash = fileOperationView.calculateChecksum(file);
                            filesum.put(file.getFileName().toString(), hash); // add files to hash map to send off
                        }
                    }
                }

                // send hash map for comparison
                List<String> desyncfiles = fileOperationView.fileSyncCheck(filesum);

                if (desyncfiles != null && !desyncfiles.isEmpty()) {
                    // Here, you need to convert 'desyncfiles' to a list of file paths to upload
                    List<Path> filesToUpload = convert_ListString_To_ListPath(desyncfiles);
                    boolean upload = fileOperationView.uploadfiles(filesToUpload);
                } else {
                    // Upload selected files
                    boolean upload = fileOperationView.uploadfiles(selectedFiles);
                }

                // Deselect all checkboxes and hide upload button
                for (JCheckBox checkBox : checkBoxes) {
                    checkBox.setSelected(false);
                }
                try {
                    refreshFileDisplay(localFilesPanel, cloudFilesPanel, fileOperationView, uploadButton,
                            downloadButton);
                    // After adding all file panels
                    uploadButton.setVisible(true);
                    downloadButton.setVisible(true);
                    JOptionPane.showMessageDialog(frame, "Upload sucessful");

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                uploadButton.setVisible(true);
            });

            // Add action listener to delete button
            deleteButton.addActionListener(e -> {
                // Get the selected files from both the local and remote panels
                List<String> selectedFilesToDelete = new ArrayList<>();

                // Get selected files from local panel
                for (JCheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        String localFile = checkBox.getActionCommand();
                        System.out.println("GUI selected local file: " + localFile);
                        selectedFilesToDelete.add(localFile);
                    }
                }

                // Get selected files from remote panel
                for (JCheckBox checkBox : remoteCheckBoxes) {
                    if (checkBox.isSelected()) {
                        String remoteFile = checkBox.getActionCommand();
                        System.out.println("GUI selected remote file: " + remoteFile);
                        selectedFilesToDelete.add(remoteFile);
                    }
                }

                // Confirm the deletion
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete the selected files?");
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // Delete the selected files from both the local and remote machines
                try {
                    boolean delete = fileOperationView.deleteFiles(selectedFilesToDelete);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // Refresh the file display
                try {
                    refreshFileDisplay(localFilesPanel, cloudFilesPanel, fileOperationView, uploadButton,
                            downloadButton);
                    // After adding all file panels
                    uploadButton.setVisible(true);
                    downloadButton.setVisible(true);
                    JOptionPane.showMessageDialog(frame, "Deletion sucessful");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                deleteButton.setVisible(true);
            });

            syncButton.addActionListener(e -> {
                // create hash map to store file names and checksums
                Map<String, String> filesum = new HashMap<>();

                // get hash for all files
                for (Path file : files) {
                    String hash = fileOperationView.calculateChecksum(file);
                    filesum.put(file.getFileName().toString(), hash); // add files to hash map to send off
                }

                // send hash map for comparison
                List<String> desyncfiles = fileOperationView.fileSyncCheck(filesum);

                if (desyncfiles != null && !desyncfiles.isEmpty()) {
                    // convert 'desyncfiles' to a list of file paths to upload
                    List<Path> filesToUpload = convert_ListString_To_ListPath(desyncfiles);
                    boolean upload = fileOperationView.uploadfiles(filesToUpload);

                    try {
                        refreshFileDisplay(localFilesPanel, cloudFilesPanel, fileOperationView, uploadButton,
                                uploadButton);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }
    }

    private static List<Path> convert_ListString_To_ListPath(List<String> desyncfiles) {
        System.out.println("Pre-conversion: " + desyncfiles);
        List<Path> filesToUpload = new ArrayList<>();
        for (String desyncfile : desyncfiles) {
            String[] fileNames = desyncfile.split(",");
            System.out.println("Filenames: " + fileNames);
            for (String fileName : fileNames) {
                filesToUpload.add(Paths.get(fileName.trim()));
            }
        }
        System.out.println("Paths for returned files: " + filesToUpload);
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

    private static JPanel createFilePanel(JCheckBox checkBox, JButton button) {
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        filePanel.setBackground(Color.lightGray);
        filePanel.setPreferredSize(new Dimension(180, 20));
        filePanel.setMaximumSize(filePanel.getPreferredSize());

        checkBox.setOpaque(false);
        checkBox.setHorizontalAlignment(SwingConstants.LEFT);

        filePanel.add(checkBox, BorderLayout.WEST);

        JLabel fileNameLabel = new JLabel(checkBox.getActionCommand()); // Set label text from checkBox action command
        fileNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fileNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        filePanel.add(fileNameLabel, BorderLayout.CENTER);

        // Add the download button after the file panel
        filePanel.add(button, BorderLayout.EAST);

        return filePanel;
    }

    public static void refreshFileDisplay(JPanel localFilesPanel, JPanel cloudFilesPanel,
            final FileOperationView fileOperationView,
            JButton uploadButton, JButton downloadButton) throws IOException {
        // Clear existing content
        localFilesPanel.removeAll();
        cloudFilesPanel.removeAll();

        // Repopulate local files panel
        List<Path> localFiles = fileOperationView.getFilesFromLocalBox();
        for (Path localFile : localFiles) {
            JCheckBox checkBox = new JCheckBox(localFile.getFileName().toString());
            checkBox.setActionCommand(localFile.toString());
            JPanel filePanel = createFilePanel(checkBox, uploadButton);
            localFilesPanel.add(filePanel);
        }

        // Repopulate cloud files panel
        List<String> remoteFiles = fileOperationView.getRemoteFiles();
        for (String remoteFile : remoteFiles) {
            Path filePath = Paths.get(remoteFile);
            JCheckBox checkBox = new JCheckBox(filePath.getFileName().toString());
            checkBox.setActionCommand(remoteFile);
            JPanel filePanel = createFilePanel(checkBox, downloadButton); // Added download button to the file panel
            cloudFilesPanel.add(filePanel);
        }

        // Refresh panels
        localFilesPanel.revalidate();
        localFilesPanel.repaint();
        cloudFilesPanel.revalidate();
        cloudFilesPanel.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cloud-Edge"); // Create new object of Jfrmae this is the main window which will be
                                                 // used

        ServerStatusView serverStatusView = new ServerStatusView(); // create object of serverstatus view
        serverStatusView.connect(frame);

    }

}