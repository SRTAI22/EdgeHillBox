package com.cloudedge.app.GUI;

import com.cloudedge.app.GUI.GUIMain;
import com.cloudedge.app.GUI.ClientAuthView;
import com.cloudedge.app.Webserver.ServerMain;

import javax.swing.*;

public class GUIMain {
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
            // the method to validate details
            ClientAuthView clientAuthView = new ClientAuthView();
            boolean isValid = clientAuthView.validate_credentials(usernameText.getText(), passwordText.getText());
            System.out.println(isValid);

            if (isValid) {
                main_page(frame);
            }
        });

        // back button event listner
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
        usernameLabel.setBounds(200, 10, 80, 25);
        frame.add(usernameLabel);
        // add input field
        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(20, 30, 160, 30);
        frame.add(usernameText);

        // add password
        JLabel passwordLabel = new JLabel("Create password");
        passwordLabel.setBounds(200, 60, 80, 25);
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
            // the method to validate details
            ClientAuthView clientAuthView = new ClientAuthView();
            boolean isValid = clientAuthView.create_user(usernameText.getText(), passwordText.getText());
            System.out.println(isValid);
            if (isValid) {
                main_page(frame);
            }
        });

        // back button event listner
        backButton.addActionListener(e -> {
            login_sign_up_page(frame); // back to login page
        });
    }

    // Main page

    public static void main_page(JFrame frame) {
        // main page desin and code which will call upon sub function to make post
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        System.out.println("Main page");
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
                                                 // manipulated to match user's input

        login_sign_up_page(frame); // create frame
    }

}