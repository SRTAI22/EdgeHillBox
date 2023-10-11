package com.cloudedge.app.GUI;

import com.cloudedge.app.GUI.GUIMain;
import com.cloudedge.app.GUI.ClientAuthView;

import javax.swing.*;

public class GUIMain {
    // login page
    public static void login_page(JFrame frame) {
        // add login page to frame
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        // ask for username and password append to frame
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 10, 80, 25);
        frame.add(usernameLabel);
        // add input field
        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(100, 10, 160, 25);
        frame.add(usernameText);

        // add password
        JLabel passwordLabel = new JLabel("password");
        passwordLabel.setBounds(10, 40, 80, 25);
        frame.add(passwordLabel);

        // add input field
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(100, 40, 160, 25);
        frame.add(passwordText);

        JButton confirmLogin = new JButton("Log in");
        confirmLogin.setBounds(90, 100, 170, 40);
        frame.add(confirmLogin);

        confirmLogin.addActionListener(d -> {
            System.out.println(passwordText.getText()); // debug
            System.out.println(usernameText.getText()); // debug
            // the method to validate details
            ClientAuthView clientAuthView = new ClientAuthView();
            boolean isValid = clientAuthView.validate_credentials(usernameText.getText(), passwordText.getText());
            System.out.println(isValid);
        });
    }

    // signup page

    public static void signup_page(JFrame frame) {
        // add login page to frame
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        // ask for username and password append to frame
        JLabel usernameLabel = new JLabel("Create username");
        usernameLabel.setBounds(10, 10, 80, 25);
        frame.add(usernameLabel);
        // add input field
        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(100, 10, 160, 25);
        frame.add(usernameText);

        // add password
        JLabel passwordLabel = new JLabel("Create password");
        passwordLabel.setBounds(10, 40, 80, 25);
        frame.add(passwordLabel);

        // add input field
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(100, 40, 160, 25);
        frame.add(passwordText);

        JButton confirmLogin = new JButton("Sign up");
        confirmLogin.setBounds(90, 100, 170, 40);
        frame.add(confirmLogin);

        confirmLogin.addActionListener(d -> {
            System.out.println(passwordText.getText()); // debug
            System.out.println(usernameText.getText()); // debug
            // the method to validate details
            ClientAuthView clientAuthView = new ClientAuthView();
            boolean isValid = clientAuthView.create_user(usernameText.getText(), passwordText.getText());
            System.out.println(isValid);
        });
    }

    // MAIN|login in and sign up page

    public static void login_sign_up_page(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        frame.setTitle("Cloud-Edge");
        frame.setSize(300, 300);
        frame.setVisible(true);

        // add log in button
        JButton loginButton = new JButton("Log In");
        loginButton.setBounds(90, 100, 140, 40);
        frame.add(loginButton);

        // add sign up button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(90, 200, 140, 40);
        frame.add(signupButton);

        // add action listeners
        loginButton.addActionListener(e -> {
            login_page(frame);
        });

        signupButton.addActionListener(e -> {
            signup_page(frame);
        });

        // set auto layout
        frame.setLayout(null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cloud-Edge");

        login_sign_up_page(frame);

    }
}