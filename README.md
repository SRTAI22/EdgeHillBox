# CloudEdgeBox

CloudEdgeBox is a cloud storage application allowing secure file management over HTTP, built with Java and featuring a Swing-based GUI for client interactions. It's designed to meet the specific needs for secure, private file storage and transfer.

## Features

- **Secure Authentication**: Users can securely log in to access their files.
- **File Upload/Download**: Support for uploading and downloading files through a Java-based HTTP server.
- **File Management**: Users can manage their files within a dedicated `EdgeHillBox` folder on their local machine.
- **Swing-based GUI**: Easy-to-use interface built with Java Swing.
- **Error Handling**: Provides user feedback based on HTTP response codes.

## Installation

To get CloudEdgeBox up and running on your local machine, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/SRTAI22/EdgeHillBox.git
   ```
2. Navigate to the cloned directory:
   ```
   cd CloudEdgeBox
   ```
3. Compile the Java files (make sure you have JDK installed):
   ```
   javac -d . src/*.java
   ```
4. Run the server:
   ```
   java com.cloudedge.app.Webserver.ServerMain
   ```
5. In a separate terminal, run the client:
   ```
   java com.cloudedge.app.GUI.ClientMain
   ```

## Usage

After starting the server and client:

1. Log in with your credentials or sign up for a new account.
2. Use the GUI to upload files to the server or download files from the server.
3. Manage your local `EdgeHillBox` folder directly from the GUI.

## Contributing

We welcome contributions to the EdgeHillBox project. Please read `CONTRIBUTING.md` for details on our code of conduct, and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the `LICENSE.md` file for details.


